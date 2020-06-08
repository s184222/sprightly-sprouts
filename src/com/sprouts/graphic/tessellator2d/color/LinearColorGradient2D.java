package com.sprouts.graphic.tessellator2d.color;

import com.sprouts.graphic.color.VertexColor;
import com.sprouts.math.LinMath;
import com.sprouts.math.Mat3;
import com.sprouts.math.Vec2;

public class LinearColorGradient2D extends ColorGradient2D {

	private static final float EPSILON = 0.01f;
	
	private final Vec2 startPoint;
	private final VertexColor startColor;
	private final Vec2 endPoint;
	private final VertexColor endColor;
	
	public LinearColorGradient2D(Vec2 startPoint, VertexColor startColor, Vec2 endPoint, VertexColor endColor) {
		this.startPoint = startPoint;
		this.startColor = startColor;
		this.endPoint = endPoint;
		this.endColor = endColor;
	}

	@Override
	public VertexColor getColor(float x, float y, Mat3 transform) {
		// Apply transform to gradient.
		Vec2 p0 = transform.mul(startPoint, new Vec2());
		Vec2 p1 = transform.mul(endPoint, new Vec2());
		
		// Define a line orthogonal to the gradient going through
		// the start point, where nx * x + ny * y = c.
		float nx = p1.x - p0.x;
		float ny = p1.y - p0.y;
		float c  = nx * p0.x + ny * p0.y;

		// We can find the interpolation value as the distance to
		// our line divided by the total distance between the start
		// and end point. The distance can be calculated as follows:
		//     dist = (nx * x + ny * y - c) / sqrt(nx * nx + ny * ny)
		// Since the length of the normal vector is also the total
		// distance between the start and end points we can simply
		// divide by the distance squared to avoid the square root.
		float totalDistSq = nx * nx + ny * ny;

		float d = nx * x + ny * y - c;
		if (totalDistSq < EPSILON * EPSILON) {
			// The two points are the same.
			return (d > 0.0f) ? endColor : startColor;
		}
		
		float t = LinMath.clamp(d / totalDistSq, 0.0f, 1.0f);
		return startColor.interpolate(endColor, t);
	}
}
