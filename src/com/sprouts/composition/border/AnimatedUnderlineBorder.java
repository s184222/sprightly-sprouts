package com.sprouts.composition.border;

import com.sprouts.composition.material.AnimatedMaterialState;
import com.sprouts.composition.material.AnimationType;
import com.sprouts.composition.material.BiMaterialState;
import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;

public class AnimatedUnderlineBorder implements IBorder {

	private final Margin margin;
	private final IColorMaterial color;

	private final AnimationType type;
	private final int durationMillis;
	
	public AnimatedUnderlineBorder(Margin margin, VertexColor color, AnimationType type, int durationMillis) {
		this(margin, new ConstantColorGradient2D(color), type, durationMillis);
	}

	public AnimatedUnderlineBorder(Margin margin, ColorGradient2D colorGradient, AnimationType type, int durationMillis) {
		this(margin, new ColorGradientMaterial(colorGradient), type, durationMillis);
	}

	public AnimatedUnderlineBorder(Margin margin, IColorMaterial color, AnimationType type, int durationMillis) {
		if (margin == null)
			throw new IllegalArgumentException("margin is null!");
		if (color == null)
			throw new IllegalArgumentException("color is null!");
		if (durationMillis < 0)
			throw new IllegalArgumentException("durationMillis must be non-negative!");
		
		this.margin = margin;
		this.color = color;

		this.type = type;
		this.durationMillis = durationMillis;
	}
	
	@Override
	public IMaterialState createMaterialState() {
		return new BiMaterialState(color.createState(), new AnimatedMaterialState(type, durationMillis));
	}

	@Override
	public void draw(IMaterialState state, ITessellator2D tessellator, int x, int y, int width, int height) {
		BiMaterialState bms = (BiMaterialState)state;
		
		tessellator.clearMaterial();
		
		color.apply(bms.getFirst(), tessellator);
	
		AnimatedMaterialState ams = (AnimatedMaterialState)bms.getSecond();
		if (ams.isDynamic()) {
			int dist = Math.round(ams.getProgress() * (0.5f * width + height));
			if (dist > height) {
				int w = dist - height;
				
				tessellator.drawQuad(x + margin.left, y, x + margin.left + w, y + margin.top);
				tessellator.drawQuad(x + width - margin.right - w, y, x + width - margin.right, y + margin.top);
			
				dist = height;
			}

			tessellator.drawQuad(x, y + height - dist, x + margin.left, y + height);
			tessellator.drawQuad(x + width - margin.right, y + height - dist, x + width, y + height);
		} else if (!type.isFadeOut()) {
			// Assume that the animation has finished.
			tessellator.drawQuad(x, y, x + margin.left, y + height);
			tessellator.drawQuad(x + width - margin.right, y + margin.top, x + width, y + height);

			tessellator.drawQuad(x + margin.left, y, x + width, y + margin.top);
		}

		// Draw bottom underline
		tessellator.drawQuad(x + margin.left, y + height - margin.bottom, x + width - margin.right, y + height);
	}
	
	@Override
	public Margin getMargin() {
		return margin;
	}
	
	public IColorMaterial getColor() {
		return color;
	}

	public AnimationType getType() {
		return type;
	}
	
	public int getDurationMillis() {
		return durationMillis;
	}
}
