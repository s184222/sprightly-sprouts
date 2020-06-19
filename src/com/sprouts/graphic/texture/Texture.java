package com.sprouts.graphic.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_RG;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.sprouts.IResource;

public class Texture implements ITextureRegion, IResource {
	
	private final int texId;
	
	protected int width;
	protected int height;
	
	public Texture() {
		texId = GL11.glGenTextures();
		
		width = height = 0;
		
		bind();
		
		// Use nearest neighbor for scaling
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		// Clamp texture when wrapped
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		unbind();
	}
	
	public Texture(ByteBuffer pixels, int width, int height, int channels) {
		this();
		
		try {
			setTextureData(pixels, width, height, channels);
		} catch (InvalidFormatException e) {
			dispose();
			throw e;
		}
	}
	
	protected Texture(int texId) {
		this.texId = texId;
		
		width = height = 0;
	}
	
	public void setTextureData(ByteBuffer pixels, int width, int height, int channels) throws InvalidFormatException {
		if (width == 0 || height == 0)
			throw new IllegalArgumentException("Texture size must be positive!");
		
		int format = getFormatFromChannels(channels);

		int alignment = 4;
		if (format != GL_RGBA && (width & 3) != 0) {
			// Pixel rows are not divisible by 4.
			alignment = 2 - (width & 1);
		}
		
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, alignment);
		glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);
		unbind();
		
		this.width = width;
		this.height = height;
	}
	
	private int getFormatFromChannels(int channels) {
		switch (channels) {
		case 1:
			return GL_RED;
		case 2:
			return GL_RG;
		case 3:
			return GL_RGB;
		case 4:
			return GL_RGBA;
		default:
			throw new InvalidFormatException("Must be either R, RG, RGB, or RGBA.");
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texId);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public Texture getTexture() {
		return this;
	}

	@Override
	public ITextureRegion getRegion(float u0, float v0, float u1, float v1) {
		return new TextureRegion(this, u0, v0, u1, v1);
	}

	@Override
	public float getU0() {
		return 0.0f;
	}

	@Override
	public float getV0() {
		return 0.0f;
	}

	@Override
	public float getU1() {
		return 1.0f;
	}

	@Override
	public float getV1() {
		return 1.0f;
	}
	
	@Override
	public void dispose() {
		glDeleteTextures(texId);
	}

	@Override
	public float getAspect() {
		return (height == 0) ? 1.0f : ((float)width / height);
	}
}
