package com.sprouts.graphic.buffer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBuffer {
	
	
	public int frameBufferID;
	public int textureID;
	public int depthBufferID;
	
	public FrameBuffer() {
		initializeFrameBuffer();
	}

	private void initializeFrameBuffer() {
		frameBufferID = GL30.glGenFramebuffers();
		bindFrameBuffer();
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		unbindCurrentFrameBuffer();
	}
	
	public void bindFrameBuffer(){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferID);
	}
	
	public void unbindCurrentFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void createTextureAttachment(int width, int height) {
		bindFrameBuffer();
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
				0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
				textureID, 0);
		unbindCurrentFrameBuffer();
	}

	public void createDepthBufferAttachment(int width, int height) {
		bindFrameBuffer();
		depthBufferID = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBufferID);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
				height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
				GL30.GL_RENDERBUFFER, depthBufferID);
		unbindCurrentFrameBuffer();
	}
}
