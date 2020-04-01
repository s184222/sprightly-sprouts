package com.sprouts.graphic.texture;

import org.lwjgl.opengl.GL11;

public class Texture {
	
	private final int id;
	
	Texture() {
		id = GL11.glGenTextures();
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void dispose() {
		GL11.glDeleteTextures(id);
	}
}
