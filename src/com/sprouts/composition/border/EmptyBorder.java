package com.sprouts.composition.border;

import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

public final class EmptyBorder implements IBorder {

	public static final EmptyBorder INSTANCE = new EmptyBorder();
	
	private final Margin margin;

	public EmptyBorder() {
		this(new Margin(0));
	}
	
	public EmptyBorder(Margin margin) {
		this.margin = margin;
	}
	
	@Override
	public IMaterialState createMaterialState() {
		return null;
	}

	@Override
	public void draw(IMaterialState state, ITessellator2D tessellator, int x, int y, int width, int height) {
	}

	@Override
	public Margin getMargin() {
		return margin;
	}
}
