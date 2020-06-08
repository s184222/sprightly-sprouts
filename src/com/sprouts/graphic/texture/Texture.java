package com.sprouts.graphic.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture implements ITextureRegion {
	
	private final int texId;

	public Texture() {
		texId = GL11.glGenTextures();
		
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
	
	public void setTextureData(ByteBuffer pixels, int width, int height, int channels) throws InvalidFormatException {
		int format;
		int alignment = 4;
		
		switch (channels) {
		case 3:
			if ((width & 3) != 0) {
				// Pixel rows are not divisible by 4.
				alignment = 2 - (width & 1);
			}

			format = GL_RGB;
			break;
		case 4:
			format = GL_RGBA;
			break;
		default:
			throw new InvalidFormatException("Must be either RGB or RGBA.");
		}
		
		bind();
		glPixelStorei(GL_UNPACK_ALIGNMENT, alignment);
		glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, pixels);
		unbind();
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
	
	public void dispose() {
		GL11.glDeleteTextures(texId);
	}
}
