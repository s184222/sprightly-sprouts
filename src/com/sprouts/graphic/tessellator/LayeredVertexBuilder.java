package com.sprouts.graphic.tessellator;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.UniqueIDSupplier;
import com.sprouts.graphic.buffer.VertexBuffer;

/**
 * @author Christian
 */
public class LayeredVertexBuilder implements AutoCloseable {
	
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private final VertexLayerInfo rootLayer;
	
	private final Map<VertexLayerID, VertexLayerInfo> idToLayer;
	private final UniqueIDSupplier<VertexLayerID> layerIdSupplier;
	
	private final VertexAttribBuilder builder;
	private VertexLayerInfo buildingLayer;
	private VertexLayerInfo currentLayer;
	private boolean rebuildAscendingLayers;

	private ByteBuffer builtBuffer;
	
	private boolean dirty;
	private int dirtyStartOffset;
	private int dirtyEndOffset;

	/**
	 * Constructs a new {@code LayeredVertexBuilder} for vertices of size {@code vertexSize}
	 * bytes and with the initial buffer capacity of 16 bytes.
	 * 
	 * @param vertexSize - the vertex size in bytes.
	 */
	public LayeredVertexBuilder(int vertexSize) {
		this(vertexSize, DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Constructs a new {@code LayeredVertexBuilder} for vertices of size {@code vertexSize}
	 * bytes and with the initial buffer capacity of {@code initialCapacity} bytes.
	 * 
	 * @param vertexSize - the vertex size in bytes.
	 * @param initialCapacity - the initial capacity of the built buffer.
	 */
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
	
		dirty = false;
		dirtyStartOffset = dirtyEndOffset = 0;
	}
	
	/*
	 * Checks if the builder is currently closed and throws an exception if this is the
	 * case.
	 * 
	 * @throws IllegalStateException if the builder is closed.
	 */
	private void checkNotClosed() throws IllegalStateException {
		if (builtBuffer == null)
			throw new IllegalStateException("Tessellator closed.");
	}
	
	/*
	 * Checks if the builder is currently not building a layer and throws an exception
	 * if this is the case.
	 * 
	 * @throws IllegalStateException if the builder is not currently building.
	 */
	private void checkBuilding() {
		if (!isBuilding())
			throw new IllegalStateException("Must be building to perform this operation");
	}

	/*
	 * Checks if the builder is currently building and throws and exception if this
	 * is the case.
	 * 
	 * @throws IllegalStateException if the builder is currently building.
	 */
	private void checkNotBuilding() {
		if (isBuilding())
			throw new IllegalStateException("Currently building layer");
	}
	
	/**
	 * Rebuilds the layer pointed to by the given {@code layerId}. If the second parameter
	 * {@code rebuildAscendingLayers}, is true then all ascending layers that have been
	 * added by the {@link #pushLayer()} method on the given layer will be removed. If
	 * these layers are required to be rebuilt then one must call the {@link #pushLayer()}
	 * and {@link #popLayer()} methods for each ascending layer that should be rebuilt.
	 * If {@code rebuildAscendingLayers} is false, then only the vertices on the layer
	 * pointed to by {@code layerId} will be removed. In this case it is only allowed to
	 * call the {@link #pushLayer()} method once, before a call to {@link #popLayer()}
	 * is made.
	 * <br><br>
	 * If {@code layerId} is {@code null} then the root layer will be rebuilt.
	 * 
	 * @param layerId - the id that points to the layer that should be rebuilt.
	 * @param rebuildAscendingLayers - whether the ascending layers should also be rebuilt.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if a layer is already
	 *                               being rebuilt at the moment of calling this method.
	 * @throws IllegalArgumentException if no layer is pointed to by the given id.
	 * 
	 * @see #finishRebuilding()
	 * @see #pushLayer()
	 * @see #popLayer()
	 */
	public void rebuildLayer(VertexLayerID layerId, boolean rebuildAscendingLayers) {
		checkNotClosed();
		checkNotBuilding();
		
		if (layerId == null) {
			buildingLayer = rootLayer;
		} else {
			buildingLayer = idToLayer.get(layerId);
			if (buildingLayer == null)
				throw new IllegalArgumentException("Invalid layer ID");
		}
		
		this.rebuildAscendingLayers = rebuildAscendingLayers;
		
		if (rebuildAscendingLayers)
			deleteChildrenLayerInfo(buildingLayer);
		
		// This should already be the case, but just to be sure.
		builder.clear();
		currentLayer = null;
	}
	
	/**
	 * Finishes rebuilding the layer that should be rebuilt. When invoked this method will
	 * update any memory that changed during of the building process. If the builder is
	 * currently rebuilding ascending layers then any vertices provided by the ascending
	 * layers will be overridden by the new data. If the builder is not rebuilding the
	 * ascending layers then this data will stay intact.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building, or if the layers were not popped
	 *                               properly.
	 */
	public void finishRebuilding() {
		checkNotClosed();
		checkBuilding();
		
		if (currentLayer != null)
			throw new IllegalStateException("Layers were not popped properly");
		
		int invalidSize;
		if (!rebuildAscendingLayers && buildingLayer.nextLayer != null) {
			invalidSize = getFirstSiblingOffset(buildingLayer.nextLayer);
			incrementChildrenOffsets(buildingLayer, builder.getPosition() - invalidSize);
		} else {
			invalidSize = buildingLayer.size;
		}
		
		int deltaSize = builder.getPosition() - invalidSize;
		moveBufferBlock(buildingLayer.offset + invalidSize, deltaSize);
		incrementLayerSizesAndOffsets(buildingLayer, deltaSize);
		
		int oldPosition = builtBuffer.position();
		builtBuffer.position(buildingLayer.offset);
		builder.writeBuffer(builtBuffer);
		builder.clear();
		
		updateDirtyRange(buildingLayer.offset, builtBuffer.position(), deltaSize);
		
		builtBuffer.position(oldPosition);
		
		// We are no longer building.
		buildingLayer = currentLayer = null;
	}

	/**
	 * Pushes a new layer to the building layer stack. If the current layer is null then
	 * the building layer itself will be added to the stack. Note that any layer that is
	 * added to the building layer stack must also be popped with {@link #popLayer()}.
	 * If any vertices have been added to the {@code VertexAttribBuilder}, given by the
	 * {@link #getBuilder()} method, and the current layer has any building ascending
	 * layers then it is not considered tightly packed and an exception is thrown.
	 * 
	 * @return The layer id of the layer that was added to the building layer stack, or
	 *         null if that layer is the root layer.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building, or if the current layer is not
	 *                               tightly packed, or if an attempt is made to rebuild
	 *                               an ascending layer when it is not permitted.
	 * 
	 * @see #popLayer()
	 */
	public VertexLayerID pushLayer() {
		checkNotClosed();
		checkBuilding();

		if (currentLayer == null) {
			currentLayer = buildingLayer;
		} else {
			if (!rebuildAscendingLayers)
				throw new IllegalStateException("Not rebuilding ascending layers!");
			
			// See #checkTightlyPacked(VertexLayerInfo).
			checkTightlyPacked(currentLayer);

			VertexLayerID layerId = layerIdSupplier.get();
			int offset = getBuildingEndOffset();
			int depth = currentLayer.depth + 1;
			
			VertexLayerInfo nextLayer = new VertexLayerInfo(layerId, offset, depth);
			
			if (currentLayer.nextLayer != null) {
				currentLayer.nextLayer.prevSibling = nextLayer;
				nextLayer.nextSibling = currentLayer.nextLayer;
			
				currentLayer.nextLayer.prevLayer = null;
			}

			currentLayer.nextLayer = nextLayer;
			nextLayer.prevLayer = currentLayer;
			currentLayer = nextLayer;
			
			idToLayer.put(layerId, nextLayer);
		}

		return currentLayer.layerId;
	}
	
	/**
	 * Pops the current layer from the building layer stack. If the current layer is the
	 * building layer itself, then the current layer will be set to null and the building
	 * can be finished. If the current layer is not the building layer and there are no
	 * layers to be popped, then an exception is thrown.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building, or if there is no layer to pop,
	 *                               or if the current layer is not tightly packed.
	 * 
	 * @see #pushLayer()
	 */
	public void popLayer() {
		checkNotClosed();
		checkBuilding();
		
		if (currentLayer == null)
			throw new IllegalStateException("No more layers to pop");
		
		if (rebuildAscendingLayers) {
			// See #checkTightlyPacked(VertexLayerInfo).
			checkTightlyPacked(currentLayer);
		}
		
		if (currentLayer == buildingLayer) {
			// The prevLayer of our building layer might not be null.
			// Ensure that we actually set currentLayer to null so
			// the user of this builder can not build invalid data.
			currentLayer = null;
		} else {
			// Note that the size of the building layer is calculated
			// in the #finishRebuilding() method, when moving the data.
			currentLayer.size = getBuildingEndOffset() - currentLayer.offset;
			
			currentLayer = currentLayer.prevLayer;
		}
	}
	
	/*
	 * Checks if any vertices have been written to the buffer since the previous layer was
	 * popped and if that is the case throws an IllegalStateException. If the given layer
	 * has no ascending layers then it is considered tightly packed and no exception will
	 * be thrown. Note that this should be called before any new layer is added to the
	 * given layer, and when the layer itself is popped. This will ensure that the layer
	 * is tightly packed.
	 * 
	 * @param layer - the layer which should be checked.
	 * 
	 * @throws IllegalStateException if the layer is not tightly packed.
	 */
	private void checkTightlyPacked(VertexLayerInfo layer) throws IllegalStateException {
		if (layer.nextLayer != null && layer.nextLayer.offset + layer.nextLayer.size != getBuildingEndOffset())
			throw new IllegalStateException("Layers must be tightly packed!");
	}
	
	/**
	 * Removes the layer pointed to by the given {@code layerId} and all ascending layers
	 * if there are any. This will immediately move and invalidate any data that is after
	 * the data of this layer. It is advised to make a call to this method as low in the
	 * hierarchy as possible, since it would otherwise require multiple copy operations.
	 * <br><br>
	 * If there exists no layer with the given {@code layerId}, then no operation occurs.
	 * 
	 * @param layerId - the id of the layer that should be removed, or null if that layer
	 *                  is the root layer.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is
	 *                               currently building
	 * 
	 * @see #rebuildLayer(VertexLayerID, boolean)
	 */
	public void removeLayer(VertexLayerID layerId) {
		if (layerId == null) {
			clear();
			return;
		}
		
		checkNotClosed();
		checkNotBuilding();
		
		VertexLayerInfo layer = idToLayer.remove(layerId);
		if (layer != null) {
			int deltaSize = -layer.size;
			moveBufferBlock(layer.offset + layer.size, deltaSize);
			incrementLayerSizesAndOffsets(layer, deltaSize);
			
			updateDirtyRange(layer.offset, layer.offset, deltaSize);
			
			removeAndDeleteLayerInfo(layer);
		}
	}
	
	/**
	 * @return The offset of the currently building layer.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building.
	 */
	public int getBuildingStartOffset() {
		checkNotClosed();
		checkBuilding();
		
		return buildingLayer.offset;
	}
	
	/**
	 * @return The end offset of the currently building layer. This is simply the first
	 *         offset that is not part of the layer that is being rebuilt.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building.
	 */
	public int getBuildingEndOffset() {
		return getBuildingStartOffset() + builder.getPosition();
	}
	
	/*
	 * Updates the dirty range given by {@code startOffset} and {@code endOffset}, if it
	 * is outside of the current dirty range. If data has been removed or added then the
	 * amount of data that was moved should be specified by {@code deltaEndOffset}. This
	 * will assume that the previous end offset was {@code endOffset - deltaEndOffset}
	 * and increment the {@code dirtyEndOffset} if it was above this position. This will
	 * ensure that the previously dirty range is moved along with the new dirty range.
	 * 
	 * @param startOffset - the start offset of the dirty range.
	 * @param endOffset - the end offset of the dirty range.
	 * @param deltaEndOffset - the amount of bytes that the end offset has moved or zero
	 *                         if the new end offset spans till the end of the buffer.
	 */
	private void updateDirtyRange(int startOffset, int endOffset, int deltaEndOffset) {
		if (startOffset < dirtyStartOffset)
			dirtyStartOffset = startOffset;

		if (dirtyEndOffset >= endOffset - deltaEndOffset) {
			dirtyEndOffset += deltaEndOffset;
		} else {
			dirtyEndOffset = endOffset;
		}
		
		dirty = true;
	}
	
	/*
	 * Increments the sizes and offsets any layers that might be affected by the given layer changing
	 * size. This ensures that the layer data structure contains the correct offsets. Note that
	 * this function will not actually offset the built buffer. That must be done separately either
	 * before or after a call to this method.
	 * 
	 * @param layer - the layer whose size has changed.
	 * @param deltaSize - the size difference of the layer.
	 */
	private void incrementLayerSizesAndOffsets(VertexLayerInfo layer, int deltaSize) {
		if (deltaSize != 0) {
			while (layer != null) {
				layer.size += deltaSize;

				while (layer.prevSibling != null) {
					layer = layer.prevSibling;
					layer.offset += deltaSize;
					incrementChildrenOffsets(layer, deltaSize);
				}

				layer = layer.prevLayer;
			}
		}
	}
	
	/*
	 * Increments offsets of all children in the specified layer, if there are any.
	 * 
	 * @param layerInfo - the layer who's children have changed offset.
	 * @param deltaOffset - the amount of bytes which the offset has changed.
	 */
	private void incrementChildrenOffsets(VertexLayerInfo layer, int deltaOffset) {
		if (layer.nextLayer != null) {
			layer = layer.nextLayer;

			layer.offset += deltaOffset;
			while ((layer = layer.nextSibling) != null) {
				layer.offset += deltaOffset;
				incrementChildrenOffsets(layer, deltaOffset);
			}
		}
	}
	
	/*
	 * Traverses the layer siblings until it finds the sibling with the lowest offset, and returns the
	 * value of that offset. If the given layer has no siblings with a lower offset than the layer itself
	 * then the the offset of the given layer is returned.
	 * 
	 * @param layer - the layer that will be traversed.
	 * 
	 * @return The lowest offset of one of the siblings of the given layer, or the offset of the layer
	 *         itself, if it has no siblings with a lower offset.
	 */
	private int getFirstSiblingOffset(VertexLayerInfo layer) {
		while (layer.nextSibling != null)
			layer = layer.nextSibling;
		return layer.offset;
	}
	
	/*
	 * Removes the given layer {@code li} from its layer and deletes it as well as its
	 * children layers.
	 * 
	 * @param li - the layer to be removed from its layer
	 */
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
		
		deleteAllLayerInfo(li);
	}
	
	/*
	 * Deletes all the children nodes associated with the given layer {@code li} if there
	 * are any.
	 * 
	 * @param li - the layer who's children should be deleted.
	 * 
	 * @see #deleteAllLayerInfo(VertexLayerInfo)
	 */
	private void deleteChildrenLayerInfo(VertexLayerInfo li) {
		if (li.nextLayer != null) {
			deleteAllLayerInfo(li.nextLayer);
			li.nextLayer = null;
		}
	}
	
	/*
	 * Deletes all layers associated with {@code li}. This includes the layer itself, the
	 * layer's children and finally the layer's siblings. If one does not wish to delete
	 * the siblings of the layer, then they should set {@code nextSibling} to null. It is
	 * assumed that {@code li} is the first sibling of its layer and that {@code prevSibling}
	 * is null. Any layer IDs of deleted layers will be recycled by the {@code layerIdSupplier}.
	 * <br><br>
	 * All pointers to other layers within this layer is guaranteed to be null after the
	 * invocation of this method, even the {@code prevSibling} pointer.
	 * <br><br>
	 * <b>WARNING:</b> It is assumed that the layer has already been removed from its previous
	 * layer, or that it will be removed directly after the call. If this is not the case this
	 * method will have undefined behavior.
	 * 
	 * @param li - the layer info that should be deleted.
	 */
	private void deleteAllLayerInfo(VertexLayerInfo li) {
		// Assume li is the first node of its layer.
		
		if (li.layerId != null) {
			layerIdSupplier.recycle(li.layerId);
			idToLayer.remove(li.layerId);
		}
		
		li.prevLayer = li.prevSibling = null;
		
		if (li.nextLayer != null) {
			deleteAllLayerInfo(li.nextLayer);
			li.nextLayer = null;
		}

		if (li.nextSibling != null) {
			deleteAllLayerInfo(li.nextSibling);
			li.nextSibling = null;
		}
	}
	
	/*
	 * Ensures that the size of {@code builtBuffer} is at least {@code minimumCapacity}
	 * and if this is not the case then reallocates the buffer. The new size of the buffer
	 * is either not altered, double the previous size, or equal to {@code minimumCapacity}
	 * depending on how much capacity is required.
	 * 
	 * @param minimumCapacity - the minimum required capacity of the {@code builtBuffer}.
	 */
	private void ensureCapacity(int minimumCapacity) {
		if (builtBuffer.capacity() < minimumCapacity) {
			int newCapacity = Math.max(builtBuffer.capacity() << 1, minimumCapacity);
			builtBuffer = MemoryUtil.memRealloc(builtBuffer, newCapacity);
		}
	}
	
	/*
	 * Moves the buffer data after the specified offset, inclusive, by the given delta
	 * offset amount of bytes. This is done by first ensuring that there is enough space
	 * in the buffer to hold the moved data, and then copying the buffer data to the new
	 * location given by {@code offset + deltaOffset}. 
	 * <br><br>
	 * If the {@code deltaOffset} is positive then the {@code deltaOffset} number of bytes
	 * that are at location {@code offset} will not be overridden or reset to zero, and will
	 * therefore contain garbage data. If one wishes to get rid of this data, it should be
	 * overwritten separately.
	 * <br><br>
	 * If the {@code deltaOffset} is negative then that amount of bytes will be overridden
	 * when the buffer block is moved to {@code offset + deltaOffset}, and can not be
	 * recovered.
	 * <br><br>
	 * If the offset is equal to the current value of {@code builtBuffer.position()} then
	 * no bytes will be moved, but instead the buffer position will be changed by adding
	 * {@code deltaOffset} to it.
	 * 
	 * @param offset - the start offset of the buffer block that should be moved
	 * @param deltaOffset - the number of bytes that the buffer block should be moved
	 * 
	 * @see #ensureCapacity(int)
	 */
	private void moveBufferBlock(int offset, int deltaOffset) {
		if (deltaOffset != 0) {
			ensureCapacity(builtBuffer.position() + deltaOffset);
			
			if (offset < builtBuffer.position()) {
				ByteBuffer tmpBuffer = builtBuffer.asReadOnlyBuffer();
				tmpBuffer.position(offset).limit(builtBuffer.position());
	
				// Note: that the memory might be overlapping, but since
				//       DirectFloatBufferU uses the Unsafe.copyMemory
				//       method, which does "conjoint" (known overlap)
				//       memory copying we should be fine.
				// See: jdk.internal.misc.Unsafe#copyMemory
				builtBuffer.position(offset + deltaOffset);
				builtBuffer.put(tmpBuffer);
			} else {
				// We only need to move the buffer by the specified amount.
				builtBuffer.position(offset + deltaOffset);
			}
		}
	}
	
	/**
	 * Clears the cached layer vertices of this builder, and deletes all the layers that
	 * have previously been acquired by calling the {@link #pushLayer()} method. Any
	 * attempt to rebuild these layers will throw an exception.
	 * <br><br>
	 * All vertex layer IDs distributed by this builder should be considered invalid after
	 * a call to this method.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is
	 *                               currently building.
	 */
	public void clear() {
		checkNotClosed();
		checkNotBuilding();
		
		deleteAllLayerInfo(rootLayer);
		builtBuffer.clear();
	}

	/**
	 * @return The {@link VertexAttribBuilder} that should be used when rebuilding layers
	 *         for this builder.
	 * 
	 * @throws IllegalStateException if the builder is closed, or if the builder is not
	 *                               currently building.
	 */
	public VertexAttribBuilder getBuilder() {
		checkNotClosed();
		checkBuilding();
		
		return builder;
	}

	/**
	 * @return True, if this builder is currently building, false otherwise.
	 */
	public boolean isBuilding() {
		return (buildingLayer != null);
	}

	/**
	 * Returns the depth of the layer pointed to by the given {@code layerId}.
	 * <br><br>
	 * The depth of the root layer is zero. Any ascending layers will have the
	 * depth of their previous layer plus one.
	 * 
	 * @param layerId - the id of the layer to retrieve the depth from, or null
	 *                  if that layer is the root layer.
	 * 
	 * @return The depth of the layer pointed to by {@code layerId}.
	 */
	public int getLayerDepth(VertexLayerID layerId) {
		if (layerId == null) {
			// We are at the root layer.
			return rootLayer.depth;
		}
		
		VertexLayerInfo layer = idToLayer.get(layerId);
		return (layer == null) ? -1 : layer.depth;
	}
	
	/**
	 * Writes all bytes that have changed since the last call to this method, or all bytes
	 * if it is the first call to the method. The bytes that have changed are being tracked
	 * using a dirty range which is reset upon any invocation of this method. This method
	 * should always be called with the same {@link VertexBuffer} and the second parameter
	 * should always be the value returned by the previous call to this method. If any of
	 * this is violated then the effect of this method is undefined.
	 * 
	 * @param vertexBuffer - the vertex buffer which should be updated by this builder.
	 * @param previousVertexCount - the previous value returned by a call to this method, or
	 *                              0 if the method has never been called.
	 * 
	 * @return The new vertex count stored in the {@code vertexBuffer}.
	 */
	public int writeBuffer(VertexBuffer vertexBuffer, int previousVertexCount) {
		checkNotClosed();
		checkNotBuilding();
		
		int vertexCount = previousVertexCount;
		
		if (dirty) {
			ByteBuffer readOnlyBuffer = getBuiltBuffer();

			int bytesToWrite = readOnlyBuffer.remaining();
			if ((bytesToWrite % vertexBuffer.getVertexSize()) != 0)
				throw new IllegalStateException("Vertices in buffer are not complete!");

			vertexCount = bytesToWrite / vertexBuffer.getVertexSize();
			
			if (previousVertexCount == vertexCount) {
				// The number of vertices is the same, only update the dirty range.
				readOnlyBuffer.position(dirtyStartOffset).limit(dirtyEndOffset);
				vertexBuffer.bufferSubData(readOnlyBuffer, dirtyStartOffset);
			} else if (bytesToWrite <= vertexBuffer.getBufferSize()) {
				// We can simply do a sub-data call.
				readOnlyBuffer.position(dirtyStartOffset);
				vertexBuffer.bufferSubData(readOnlyBuffer, dirtyStartOffset);
			} else {
				// We have to expand the vertex buffer, and write the entire buffer.
				vertexBuffer.bufferData(readOnlyBuffer);
			}
			
			dirtyStartOffset = dirtyEndOffset = 0;
			dirty = false;
		}
		
		return vertexCount;
	}
	
	/*
	 * @return A read only version of {@code builtBuffer} that should be used to copy the
	 *         contents of the built layers to a new location. The returned buffer will
	 *         have a location of zero and a limit given by the size of the buffer.
	 */
	public ByteBuffer getBuiltBuffer() {
		ByteBuffer readOnlyBuffer = builtBuffer.asReadOnlyBuffer();
		readOnlyBuffer.flip();
		return readOnlyBuffer;
	}
	
	/**
	 * @return True, if the {@code builtBuffer} is currently not up to date with the previous
	 *         call to the {@link #writeBuffer(VertexBuffer, int)} method, false otherwise.
	 * 
	 * @see #writeBuffer(VertexBuffer, int)
	 */
	public boolean isDirty() {
		return dirty;
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
		private int size;
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
