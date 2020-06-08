package com.sprouts.graphic.tessellator2d.clip;

public abstract class ClipShape {

	public abstract ClipShapeBounds getClipBounds();

	public int getPlaneCount() {
		return getPlanes().length;
	}

	public abstract ClipPlane[] getPlanes();
	
}
