package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.math.Mat3;

public interface ITessellator2D {

	public void setViewport(float x0, float y0, float x1, float y1);
	
	public void drawQuad(float x0, float y0, float x1, float y1);

	default public void drawTexturedQuad(float x0, float y0, float x1, float y1, ITextureRegion textureRegion) {
		setTexture(textureRegion.getTexture());
		
		drawTexturedQuad(x0, y0, textureRegion.getU0(), textureRegion.getV0(), 
		                 x1, y1, textureRegion.getU1(), textureRegion.getV1());
	}
	
	public void drawTexturedQuad(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1);

	default public void setColor(VertexColor color) {
		setColorGradient(new ConstantColorGradient2D(color));
	}

	public ColorGradient2D getColorGradient();
	
	public void setColorGradient(ColorGradient2D colorGradient);
	
	public void setTexture(Texture texture);

	public void clearTransform();
	
	default public void translate(float tx, float ty) {
		translate(tx, ty, 0.0f);
	}

	public void translate(float tx, float ty, float tz);
	
	public void rotateZ(float radians);
	
	public Mat3 getTransform();

	public void setTransform(Mat3 transform);

	public float getZOffset();

	public void setZOffset(float zOffset);
	
}
