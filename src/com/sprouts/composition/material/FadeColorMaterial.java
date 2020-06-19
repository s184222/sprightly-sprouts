package com.sprouts.composition.material;

import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class FadeColorMaterial implements IColorMaterial {

	private final AnimationType type;
	private final VertexColor color;
	private final int durationMillis;
	
	public FadeColorMaterial(AnimationType type, VertexColor color, int durationMillis) {
		this.type = type;
		this.color = color;
		this.durationMillis = durationMillis;
	}

	@Override
	public IMaterialState createState() {
		return new AnimatedMaterialState(type, durationMillis);
	}
	
	@Override
	public void apply(IMaterialState state, ITessellator2D tessellator) {
		float alpha = ((AnimatedMaterialState)state).getProgress();
		
		tessellator.setColor(color.withAlpha(alpha));
	}
}
