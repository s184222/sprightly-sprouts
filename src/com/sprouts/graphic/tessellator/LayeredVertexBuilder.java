package com.sprouts.graphic.tessellator;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.UniqueIDSupplier;

public class LayeredVertexBuilder implements AutoCloseable {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private final VertexLayerInfo rootLayer;
	
	private final Map<VertexLayerID, VertexLayerInfo> idToLayer;
	private final UniqueIDSupplier<VertexLayerID> layerIdSupplier;
	
	private final VertexAttribBuilder builder;
	private VertexLayerInfo buildingLayer;
	private VertexLayerInfo currentLayer;

	private ByteBuffer builtBuffer;

	public LayeredVertexBuilder(int vertexSize) {
		this(vertexSize, DEFAULT_INITIAL_CAPACITY);
	}
	
	public LayeredVertexBuilder(int vertexSize, int initialCapacity) {
		if (initialCapacity <= 0)
			throw new IllegalArgumentException("Initial capacity must be positive");
		
		rootLayer = new VertexLayerInfo(null, 0, 0);

		idToLayer = new HashMap<VertexLayerID, VertexLayerInfo>();
		layerIdSupplier = new UniqueIDSupplier<VertexLayerID>(VertexLayerID::new);
		
		builder = new VertexAttribBuilder(vertexSize);
		buildingLayer = null;
		currentLayer = null;

		builtBuffer = MemoryUtil.memAlloc(initialCapacity * vertexSize);
	}
	
	private void checkNotClosed() throws IllegalStateException {
		if (builtBuffer == null)
			throw new IllegalStateException("Tessellator closed.");
	}
	
	private void checkBuilding() {
		if (!isBuilding())
			throw new IllegalStateException("Must be building to perform this operation");
	}

	private void checkNotBuilding() {
		if (isBuilding())
			throw new IllegalStateException("Currently building layer");
	}
	
	public void removeLayer(VertexLayerID layerId) {
		checkNotClosed();
		checkNotBuilding();
		
		VertexLayerInfo layerInfo = idToLayer.remove(layerId);
		if (layerInfo != null) {
			if (layerInfo.prevSibling != null) {
				int deltaOffset = layerInfo.offset - layerInfo.prevSibling.offset;
				moveBufferBlock(layerInfo.prevSibling.offset, deltaOffset);
				incrementLeftOffsets(layerInfo, deltaOffset);
			} else {
				// In this case we do not have to worry about moving
				// the buffer block since there is no trailing data.
				builtBuffer.position(layerInfo.offset);
			}
			
			removeAndDeleteLayerInfo(layerInfo);
		}
	}

	public void rebuildLayer(VertexLayerID layerId) {
		checkNotClosed();
		checkNotBuilding();
		
		if (layerId == null) {
			buildingLayer = rootLayer;
		} else {
			buildingLayer = idToLayer.get(layerId);
			if (buildingLayer == null)
				throw new IllegalArgumentException("Invalid layer ID");
		}
		
		deleteChildrenLayerInfo(buildingLayer);

		// This should already be the case, but just to be sure.
		builder.clear();
		currentLayer = null;
	}
	
	public void finishRebuilding() {
		checkNotClosed();
		checkBuilding();
		
		if (currentLayer != null)
			throw new IllegalStateException("Layers were not popped properly");
		
		int oldPosition;
		if (buildingLayer.prevSibling != null) {
			int deltaOffset = getBuildingEndOffset() - buildingLayer.prevSibling.offset;
			moveBufferBlock(buildingLayer.prevSibling.offset, deltaOffset);
			incrementLeftOffsets(buildingLayer, deltaOffset);
			
			oldPosition = builtBuffer.position();
		} else {
			ensureCapacity(oldPosition = getBuildingEndOffset());
		}
		
		builtBuffer.position(buildingLayer.offset);
		builder.writeBuffer(builtBuffer);
		builder.clear();
		builtBuffer.position(oldPosition);
		
		// We are no longer building.
		buildingLayer = currentLayer = null;
	}

	public VertexLayerID pushLayer() {
		checkNotClosed();
		checkBuilding();

		if (currentLayer == null) {
			currentLayer = buildingLayer;
		} else {
			int depth = currentLayer.depth + 1;
			
			VertexLayerInfo layerInfo = new VertexLayerInfo(layerIdSupplier.get(), getBuildingEndOffset(), depth);

			if (currentLayer.nextLayer != null) {
				currentLayer.nextLayer.prevSibling = layerInfo;
				layerInfo.nextSibling = currentLayer.nextLayer;
			
				currentLayer.nextLayer.prevLayer = null;
			}

			currentLayer.nextLayer = layerInfo;
			layerInfo.prevLayer = currentLayer;
			currentLayer = layerInfo;
			
			idToLayer.put(layerInfo.layerId, layerInfo);
		}
		
		return currentLayer.layerId;
	}
	
	public void popLayer() {
		checkNotClosed();
		checkBuilding();
		
		if (currentLayer == null)
			throw new IllegalStateException("No more layers to pop");
		
		currentLayer = currentLayer.prevLayer;
	}
	
	public int getBuildingStartOffset() {
		checkNotClosed();
		checkBuilding();
		
		return buildingLayer.offset;
	}
	
	public int getBuildingEndOffset() {
		return getBuildingStartOffset() + builder.getPosition();
	}
	
	private void incrementLeftOffsets(VertexLayerInfo layerInfo, int deltaOffset) {
		if (deltaOffset != 0 && layerInfo.prevSibling != null) {
			layerInfo.prevSibling.offset += deltaOffset;
			incrementBelowOffsets(layerInfo.prevSibling, deltaOffset);
			incrementLeftOffsets(layerInfo.prevSibling, deltaOffset);
		}
	}
	
	private void incrementRightOffsets(VertexLayerInfo layerInfo, int deltaOffset) {
		if (deltaOffset != 0 && layerInfo.nextSibling != null) {
			layerInfo.nextSibling.offset += deltaOffset;
			incrementBelowOffsets(layerInfo.nextSibling, deltaOffset);
			incrementRightOffsets(layerInfo.nextSibling, deltaOffset);
		}
	}
	
	private void incrementBelowOffsets(VertexLayerInfo layerInfo, int deltaOffset) {
		if (deltaOffset != 0 && layerInfo.nextLayer != null) {
			layerInfo.nextLayer.offset += deltaOffset;
			incrementRightOffsets(layerInfo.nextLayer, deltaOffset);
		}
	}
	
	private void removeAndDeleteLayerInfo(VertexLayerInfo li) {
		if (li.prevSibling != null) {
			li.prevSibling.nextSibling = li.nextSibling;
		} else if (li.prevLayer != null) {
			li.prevLayer.nextLayer = li.nextSibling;
		}
		
		if (li.nextSibling != null) {
			li.nextSibling.prevSibling = li.prevSibling;
			li.nextSibling.prevLayer = li.prevLayer;
		}
		
		li.prevSibling = li.nextSibling = li.prevLayer = null;
		
		deleteLayerInfo(li);
	}
	
	private void deleteChildrenLayerInfo(VertexLayerInfo li) {
		if (li.nextLayer != null) {
			deleteLayerInfo(li.nextLayer);
			li.nextLayer = null;
		}
	}
	
	private void deleteLayerInfo(VertexLayerInfo li) {
		// Assume li is the first node of its layer.
		
		if (li.layerId != null) {
			layerIdSupplier.recycle(li.layerId);
			idToLayer.remove(li.layerId);
		}
		
		li.prevLayer = li.prevSibling = null;
		
		if (li.nextLayer != null) {
			deleteLayerInfo(li.nextLayer);
			li.nextLayer = null;
		}

		if (li.nextSibling != null) {
			deleteLayerInfo(li.nextSibling);
			li.nextSibling = null;
		}
	}
	
	private void ensureCapacity(int minimumCapacity) {
		if (builtBuffer.capacity() < minimumCapacity) {
			int newCapacity = Math.max(builtBuffer.capacity() << 1, minimumCapacity);
			builtBuffer = MemoryUtil.memRealloc(builtBuffer, newCapacity);
		}
	}
	
	private void moveBufferBlock(int offset, int deltaOffset) {
		if (deltaOffset != 0) {
			ensureCapacity(builtBuffer.position() + deltaOffset);
			
			ByteBuffer tmpBuffer = builtBuffer.asReadOnlyBuffer();
			tmpBuffer.position(offset).limit(builtBuffer.position());

			// Note: that the memory might be overlapping, but since
			//       DirectFloatBufferU uses the Unsafe.copyMemory
			//       method, which does "conjoint" (known overlap)
			//       memory copying we should be fine.
			// See: jdk.internal.misc.Unsafe#copyMemory
			builtBuffer.position(offset + deltaOffset);
			builtBuffer.put(tmpBuffer);
		}
	}
	
	public void clear() {
		checkNotClosed();
		checkNotBuilding();
		
		deleteLayerInfo(rootLayer);
		builtBuffer.clear();
	}

	public VertexAttribBuilder getBuilder() {
		checkNotClosed();
		checkBuilding();
		
		return builder;
	}
	
	public ByteBuffer getBuiltBuffer() {
		checkNotClosed();
		checkNotBuilding();
		
		ByteBuffer readOnlyBuffer = builtBuffer.asReadOnlyBuffer();
		readOnlyBuffer.flip();
		return readOnlyBuffer;
	}
	
	public boolean isBuilding() {
		return (buildingLayer != null);
	}

	public int getLayerDepth(VertexLayerID layerId) {
		if (layerId == null) {
			// We are at the root layer which has
			// depth zero.
			return 0;
		}
		
		VertexLayerInfo layer = idToLayer.get(layerId);
		return (layer == null) ? -1 : layer.depth;
	}
	
	@Override
	public void close() {
		idToLayer.clear();

		builder.close();
		
		buildingLayer = null;
		currentLayer = null;

		if (builtBuffer != null) {
			MemoryUtil.memFree(builtBuffer);
			builtBuffer = null;
		}
	}

	private class VertexLayerInfo {
		
		private final VertexLayerID layerId;
		
		private int offset;
		private int depth;

		private VertexLayerInfo prevLayer;
		private VertexLayerInfo nextLayer;

		private VertexLayerInfo nextSibling;
		private VertexLayerInfo prevSibling;
		
		public VertexLayerInfo(VertexLayerID layerId, int initialOffset, int depth) {
			this.layerId = layerId;
			
			offset = initialOffset;
			this.depth = depth;
		}
	}
}
