package com.sprouts.composition.border;

import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

public interface IBorder {

	public IMaterialState createMaterialState();
	
	public void draw(IMaterialState state, ITessellator2D tessellator, int x, int y, int width, int height);
	
	public Margin getMargin();
	
}
