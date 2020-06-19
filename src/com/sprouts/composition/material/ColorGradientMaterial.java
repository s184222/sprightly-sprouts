package com.sprouts.composition.material;

import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;

/**
 * @author Christian
 */
public class ColorGradientMaterial implements IColorMaterial {

	private final ColorGradient2D colorGradient;
	
	public ColorGradientMaterial(VertexColor color) {
		this(new ConstantColorGradient2D(color));
	}
	
	public ColorGradientMaterial(ColorGradient2D colorGradient) {
		if (colorGradient == null)
			throw new IllegalArgumentException("colorGradient is null!");
		
		this.colorGradient = colorGradient;
	}
	
	@Override
	public void apply(IMaterialState state, ITessellator2D tessellator) {
		tessellator.setColorGradient(colorGradient);
	}
	
	public ColorGradient2D getColorGradient() {
		return colorGradient;
	}
}
