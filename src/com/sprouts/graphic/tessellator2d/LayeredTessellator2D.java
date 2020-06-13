package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.tessellator.LayeredVertexBuilder;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator.VertexLayerID;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;

/**
 * @author Christian
 */
public class LayeredTessellator2D extends AbstractTessellator2D implements ILayeredTessellator2D {

	protected final LayeredVertexBuilder layeredBuilder;
	
	private int vertexCount;
	
	public LayeredTessellator2D(Tessellator2DShader shader) {
		super(shader);
		
		layeredBuilder = new LayeredVertexBuilder(shader.getVertexByteSize());
		
		vertexCount = 0;
	}

	@Override
	public void rebuildLayer(VertexLayerID layerId, boolean rebuildAscendingLayers) {
		layeredBuilder.rebuildLayer(layerId, rebuildAscendingLayers);
	}

	@Override
	public void finishRebuilding() {
		layeredBuilder.finishRebuilding();
	}

	@Override
	public VertexLayerID pushLayer() {
		return layeredBuilder.pushLayer();
	}

	@Override
	public void popLayer() {
		layeredBuilder.popLayer();
	}
	
	@Override
	public void drawLayers() {
		if (layeredBuilder.isBuilding())
			throw new IllegalStateException("Tessellator is currently building.");
		
		if (layeredBuilder.isDirty())
			vertexCount = layeredBuilder.writeBuffer(vertexBuffer, vertexCount);
		
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
