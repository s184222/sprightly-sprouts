package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.tessellator.VertexLayerID;

public interface ILayeredTessellator2D extends ITessellator2D {

	public void rebuildLayer(VertexLayerID layerId);

	public void finishRebuilding();

	public VertexLayerID pushLayer();

	public void popLayer();

	public void drawLayers();
	
	public boolean isBuilding();
	
	public int getLayerDepth(VertexLayerID layerId);
	
}
