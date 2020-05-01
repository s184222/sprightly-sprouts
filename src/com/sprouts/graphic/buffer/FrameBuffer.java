package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.nio.ByteBuffer;

public class FrameBuffer {
	
	private final FrameBufferType type;
	private int width;
	private int height;
	
	private int frameBufferID;
	private int textureID;
	private int depthBufferID;

	public FrameBuffer(FrameBufferType type, int width, int height) {
		this.type = type;
		this.width = width;
		this.height = height;
		
		frameBufferID = glGenFramebuffers();
		
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		textureID = type.hasTextureAttachment() ? createTextureAttachment() : -1;
		depthBufferID = type.hasDepthAttachment() ? createDepthAttachment() : -1;

		updateAttachmentSize();
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private static int createTextureAttachment() {
		int textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureID, 0);
		glDrawBuffer(GL_COLOR_ATTACHMENT0);
		glBindTexture(GL_TEXTURE_2D, 0);
		return textureID;
	}
	
	private static int createDepthAttachment() {
		int depthBufferID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBufferID);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		return depthBufferID;
	}

	private void updateAttachmentSize() {
		if (type.hasTextureAttachment()) {
			glBindTexture(GL_TEXTURE_2D, textureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
		if (type.hasDepthAttachment()) {
			glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
		}
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
	}
	
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void setSize(int width, int height) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("Dimensions must be positive.");
		
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			
			bind();
			updateAttachmentSize();
			unbind();
		}
	}

	public FrameBufferType getType() {
		return type;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void dispose() {
		if (textureID != -1) {
			glDeleteTextures(textureID);
			textureID = -1;
		}

		if (depthBufferID != -1) {
			glDeleteRenderbuffers(depthBufferID);
			depthBufferID = -1;
		}
		
		if (frameBufferID != -1) {
			glDeleteFramebuffers(frameBufferID);
			frameBufferID = -1;
		}
	}
}
