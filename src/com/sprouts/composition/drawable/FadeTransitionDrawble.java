package com.sprouts.composition.drawable;

import com.sprouts.composition.material.AnimatedMaterialState;
import com.sprouts.composition.material.AnimationType;
import com.sprouts.composition.material.BiMaterialState;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class FadeTransitionDrawble extends TransitionDrawable {

	private final IDrawable bottom;
	private final IMaterialState bottomState;

	private final VertexColor color;
	private final AnimationType type;
	private final int durationMillis;
	
	public FadeTransitionDrawble(IDrawable bottom, IMaterialState bottomState, VertexColor color, AnimationType type, int durationMillis) {
		if (bottom == null)
			throw new IllegalArgumentException("bottom is null!");
		if (bottomState == null)
			throw new IllegalArgumentException("bottomState is null!");
		if (color == null)
			throw new IllegalArgumentException("color is null!");
		if (type == null)
			throw new IllegalArgumentException("type is null!");
		if (durationMillis < 0)
			throw new IllegalArgumentException("durationMillis must be non-negative!");
		
		this.bottom = bottom;
		this.bottomState = bottomState;
	
		this.color = color;
		this.type = type;
		this.durationMillis = durationMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMaterialState createMaterialState() {
		return new BiMaterialState(bottomState, new AnimatedMaterialState(type, durationMillis));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
		BiMaterialState bms = (BiMaterialState)materialState;
		AnimatedMaterialState ams = (AnimatedMaterialState)bms.getSecond();

		tessellator.clearMaterial();
		tessellator.setColor(color);
		tessellator.drawQuad(x, y, x + width, y + height);
		
		tessellator.multiplyAlpha(1.0f - ams.getProgress());
		bottom.draw(bms.getFirst(), tessellator, x, y, width, height);
		tessellator.clearAlphaMultiplier();
	}
}
