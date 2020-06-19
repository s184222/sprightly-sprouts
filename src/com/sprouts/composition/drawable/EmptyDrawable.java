package com.sprouts.composition.drawable;

import com.sprouts.composition.material.EmptyMaterialState;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public final class EmptyDrawable implements IDrawable {

	public static final EmptyDrawable INSTANCE = new EmptyDrawable();
	
	private EmptyDrawable() {
	}
	
	@Override
	public IMaterialState createMaterialState() {
		return EmptyMaterialState.INSTANCE;
	}

	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
	}
}
