package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
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

import com.sprouts.IResource;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureRegion;

public class FrameBuffer implements IResource {
	
	private final FrameBufferType type;
	private int width;
	private int height;
	
	private int frameBufferID;
	private FrameBufferTexture texture;
	private int depthBufferID;

	public FrameBuffer(FrameBufferType type, int width, int height) {
		this.type = type;
		this.width = width;
		this.height = height;
		
		frameBufferID = glGenFramebuffers();
		
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		if (type.hasTextureAttachment())
			texture = new FrameBufferTexture(createTextureAttachment());
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
			texture.bind();
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			texture.unbind();
			
			texture.setSize(width, height);
		}
		
		if (type.hasDepthAttachment()) {
			glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
		}
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		glViewport(0, 0, width, height);
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
	
	public Texture getColorTexture() {
		if (!type.hasTextureAttachment())
			throw new IllegalStateException("FrameBuffer does not have a color attachment!");
		return texture;
	}
	
	@Override
	public void dispose() {
		if (texture != null) {
			texture.dispose();
			texture = null;
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
	
	private static class FrameBufferTexture extends Texture {
		
		public FrameBufferTexture(int texId) {
			super(texId);
		}
		
		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		@Override
		public ITextureRegion getRegion(float u0, float v0, float u1, float v1) {
			// NOTE: FrameBuffer objects are flipped relative to our other
			// textures that were loaded using TextureLoader. This is caused
			// by the origin being bottom left in OpenGL for framebuffers,
			// but when we load the image, the origin will be whichever byte
			// comes first to glTexImage2D. This is in our framework the
			// top left pixel. Therefore there is a conflict in convension.
			// We flip the texture here to compensate for this.
			return new TextureRegion(this, u0, 1.0f - v0, u1, 1.0f - v1);
		}

		@Override
		public float getU0() {
			return 0.0f;
		}

		@Override
		public float getV0() {
			return 1.0f;
		}

		@Override
		public float getU1() {
			return 1.0f;
		}

		@Override
		public float getV1() {
			return 0.0f;
		}
	}
}
