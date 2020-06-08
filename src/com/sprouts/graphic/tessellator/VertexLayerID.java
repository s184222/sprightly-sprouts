package com.sprouts.graphic.tessellator;

import com.sprouts.graphic.UniqueIntegerID;

public final class VertexLayerID implements UniqueIntegerID {

	private final int layerId;
	
	VertexLayerID(int layerId) {
		this.layerId = layerId;
	}
	
	@Override
	public int getId() {
		return layerId;
	}
	
	@Override
	public int hashCode() {
		return layerId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof VertexLayerID)
			return layerId == ((VertexLayerID)other).getId();
		return false;
	}
}
