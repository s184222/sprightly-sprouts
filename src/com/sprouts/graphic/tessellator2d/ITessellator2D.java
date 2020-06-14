package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.clip.ClipShape;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.math.Mat3;
import com.sprouts.math.Vec2;

/**
 * @author Christian
 */
public interface ITessellator2D {

	public void setViewport(float x0, float y0, float x1, float y1);

	default public void drawQuad(Vec2 p0, Vec2 p1) {
		drawQuad(p0.x, p0.y, p1.x, p1.y);
	}
	
	public void drawQuad(float x0, float y0, float x1, float y1);

	default public void drawQuadRegion(Vec2 p0, float u0, float v0, Vec2 p1, float u1, float v1) {
		drawQuadRegion(p0.x, p0.y, p1.x, p1.y, u0, v0, u1, v1);
	}
	
	public void drawQuadRegion(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1);

	default public void drawLine(Vec2 p0, Vec2 p1) {
		drawLine(p0.x, p0.y, p1.x, p1.y);
	}
	
	default public void drawLine(float x0, float y0, float x1, float y1) {
		drawLine(x0, y0, x1, y1, 1.0f);
	}

	default public void drawLine(Vec2 p0, Vec2 p1, float lineWidth) {
		drawLine(p0.x, p0.y, p1.x, p1.y, lineWidth);
	}

	public void drawLine(float x0, float y0, float x1, float y1, float lineWidth);
	
	public void clearMaterial();
	
	default public void setColor(VertexColor color) {
		setColorGradient(new ConstantColorGradient2D(color));
	}

	public ColorGradient2D getColorGradient();
	
	public void setColorGradient(ColorGradient2D colorGradient);
	
	public ITextureRegion getTextureRegion();
	
	public void setTextureRegion(ITextureRegion textureRegion);
	
	public void clearTransform();
	
	public Mat3 getTransform();

	public void setTransform(Mat3 transform);
	
	public void translate(float tx, float ty);

	public void rotateZ(float radians);

	public void clearClipShape();
	
	public ClipShape getClipShape();

	public void setClipRect(float x0, float y0, float x1, float y1);
	
	public void setClipShape(ClipShape shape);
	
}
