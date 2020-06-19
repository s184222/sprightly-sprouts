package com.sprouts.composition.drawable;

import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;

/**
 * @author Christian
 */
public class ColorDrawable implements IDrawable {

	private final IColorMaterial color;
	
	public ColorDrawable(VertexColor color) {
		this(new ConstantColorGradient2D(color));
	}

	public ColorDrawable(ColorGradient2D colorGradient) {
		this(new ColorGradientMaterial(colorGradient));
	}
	
	public ColorDrawable(IColorMaterial color) {
		if (color == null)
			throw new IllegalArgumentException("color is null!");
		
		this.color = color;
	}
	
	@Override
	public IMaterialState createMaterialState() {
		return color.createState();
	}

	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
		tessellator.clearMaterial();
		
		color.apply(materialState, tessellator);
		tessellator.drawQuad(x, y, x + width, y + height);
	}
	
	public IColorMaterial getColor() {
		return color;
	}
}
