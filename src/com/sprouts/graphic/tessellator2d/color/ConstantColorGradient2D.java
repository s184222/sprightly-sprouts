package com.sprouts.graphic.tessellator2d.color;

import com.sprouts.graphic.color.VertexColor;
import com.sprouts.math.Mat3;

/**
 * @author Christian
 */
public class ConstantColorGradient2D extends ColorGradient2D {

	private final VertexColor color;
	
	public ConstantColorGradient2D(VertexColor color) {
		if (color == null)
			throw new IllegalArgumentException("color is null");
		
		this.color = color;
	}
	
	@Override
	public VertexColor getColor(float x, float y, Mat3 transform) {
		return color;
	}
}
