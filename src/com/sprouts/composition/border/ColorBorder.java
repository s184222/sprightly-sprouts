package com.sprouts.composition.border;

import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;

public class ColorBorder implements IBorder {

	private final Margin margin;
	private final IColorMaterial color;
	
	public ColorBorder(Margin margin, VertexColor color) {
		this(margin, new ConstantColorGradient2D(color));
	}

	public ColorBorder(Margin margin, ColorGradient2D colorGradient) {
		this(margin, new ColorGradientMaterial(colorGradient));
	}
	
	public ColorBorder(Margin margin, IColorMaterial color) {
		if (color == null)
			throw new IllegalArgumentException("color is null!");
		
		this.margin = margin;
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
		tessellator.drawQuad(x, y, x + margin.left, y + height - margin.bottom);
		tessellator.drawQuad(x + width - margin.right, y + margin.top, x + width, y + height);

		tessellator.drawQuad(x + margin.left, y, x + width, y + margin.top);
		tessellator.drawQuad(x, y + height - margin.bottom, x + width - margin.right, y + height);
	}
	
	@Override
	public Margin getMargin() {
		return margin;
	}

	public IColorMaterial getColor() {
		return color;
	}
}
