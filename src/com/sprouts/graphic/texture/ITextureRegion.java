package com.sprouts.graphic.texture;

public interface ITextureRegion {

	public Texture getTexture();
	
	public ITextureRegion getRegion(float u0, float v0, float u1, float v1);
	
	public float getU0();

	public float getV0();
	
	public float getU1();

	public float getV1();
}
