package com.sprouts.graphic.tessellator2d;

import java.nio.ByteBuffer;

import com.sprouts.graphic.tessellator.LayeredVertexBuilder;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator.VertexLayerID;

public class LayeredTessellator2D extends AbstractTessellator2D implements ILayeredTessellator2D {

	protected final LayeredVertexBuilder layeredBuilder;
	
	private int vertexCount;
	
	private boolean dirty;
	private int dirtyStartPos;
	private int dirtyEndPos;
	
	public LayeredTessellator2D(Tessellator2DShader shader) {
		super(shader);
		
		layeredBuilder = new LayeredVertexBuilder(shader.getVertexByteSize());
		
		vertexCount = 0;
		
		dirty = false;
		dirtyStartPos = 0;
		dirtyEndPos = 0;
	}

	@Override
	public void rebuildLayer(VertexLayerID layerId) {
		layeredBuilder.rebuildLayer(layerId);
	}

	@Override
	public void finishRebuilding() {
		int startPos = layeredBuilder.getBuildingStartOffset();
		int endPos = layeredBuilder.getBuildingEndOffset();

		layeredBuilder.finishRebuilding();

		if (dirty) {
			// FIXME: fix this check. It might not be accurate.
			
			if (startPos < dirtyStartPos)
				dirtyStartPos = startPos;
			if (endPos < dirtyEndPos)
				dirtyEndPos = endPos;
		} else {
			dirty = true;

			dirtyStartPos = startPos;
			dirtyEndPos = endPos;
		}
	}

	@Override
	public VertexLayerID pushLayer() {
		return layeredBuilder.pushLayer();
	}

	@Override
	public void popLayer() {
		layeredBuilder.popLayer();
	}
	
	protected void checkNotBuilding() {
		if (layeredBuilder.isBuilding())
			throw new IllegalStateException("Tessellator is currently building.");
	}

	@Override
	public void drawLayers() {
		checkNotBuilding();
		
		if (dirty) {
			dirty = false;

			ByteBuffer builtBuffer = layeredBuilder.getBuiltBuffer();
			vertexCount = builtBuffer.remaining() / shader.getVertexByteSize();
			
			// Update buffer. If possible only update a portion. This is
			// only possible if the VBO size remains the same.
			if (vertexBuffer.getSize() == builtBuffer.remaining()) {
				builtBuffer.position(dirtyStartPos).limit(dirtyEndPos);
				vertexBuffer.bufferSubData(builtBuffer, dirtyStartPos);
			} else {
				vertexBuffer.bufferData(builtBuffer);
			}
			
			dirtyStartPos = dirtyEndPos = 0;
		}
		
		drawBuffer(vertexCount);
	}
	
	@Override
	public boolean isBuilding() {
		return layeredBuilder.isBuilding();
	}
	
	@Override
	public int getLayerDepth(VertexLayerID layerId) {
		return layeredBuilder.getLayerDepth(layerId);
	}
	
	@Override
	protected VertexAttribBuilder getBuilder() {
		return layeredBuilder.getBuilder();
	}
	
	@Override
	public void dispose() {
		layeredBuilder.close();
		
		super.dispose();
	}
}
