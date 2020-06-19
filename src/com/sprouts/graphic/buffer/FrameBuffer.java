package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL30.glRenderbufferStorageMultisample;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

import java.nio.ByteBuffer;

import com.sprouts.IResource;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureRegion;

public class FrameBuffer implements IResource {
	
	/* Number of samples for multisampled framebuffers */
	private static final int SAMPLES = 4;
	
	private final FrameBufferType type;
	private final boolean multisampled;

	private int width;
	private int height;
	
	private int frameBufferID;
	private int colorBufferID;
	private int depthBufferID;

	/* A way to get access to the color buffer if it is a texture. */
	private final FrameBufferTexture textureInstance;
	
	public FrameBuffer(FrameBufferType type, int width, int height) {
		this.type = type;
		this.width = width;
		this.height = height;
		
		frameBufferID = glGenFramebuffers();
		
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
		
		if (type == FrameBufferType.MULTISAMPLED_DEPTH_AND_COLOR) {
			colorBufferID = createRenderBufferAttachment(GL_COLOR_ATTACHMENT0);
			depthBufferID = createRenderBufferAttachment(GL_DEPTH_ATTACHMENT);
			multisampled = true;

			textureInstance = null;
		} else {
			if (type.hasColorAttachment())
				colorBufferID = createTextureAttachment(GL_COLOR_ATTACHMENT0);
			if (type.hasDepthAttachment())
				depthBufferID = createRenderBufferAttachment(GL_DEPTH_ATTACHMENT);
			multisampled = false;
			
			textureInstance = new FrameBufferTexture(colorBufferID);
		}

		updateAttachmentSize();
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private static int createTextureAttachment(int attachment) {
		// Create it as a texture that can be sampled.
		int textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glFramebufferTexture(GL_FRAMEBUFFER, attachment, textureID, 0);
		glDrawBuffer(attachment);
		glBindTexture(GL_TEXTURE_2D, 0);
		return textureID;
	}
	
	private static int createRenderBufferAttachment(int attachment) {
		int bufferID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, bufferID);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, bufferID);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		return bufferID;
	}

	private void updateAttachmentSize() {
		if (type.hasColorAttachment()) {
			if (multisampled) {
				updateRenderBufferSize(colorBufferID, width, height, true, GL_RGBA8);
			} else {
				updateTextureSize(colorBufferID, width, height, GL_RGBA8, GL_RGBA);
				textureInstance.setSize(width, height);
			}
		}
		
		if (type.hasDepthAttachment())
			updateRenderBufferSize(depthBufferID, width, height, multisampled, GL_DEPTH_COMPONENT24);
	}
	
	private static void updateTextureSize(int bufferID, int width, int height, int internalType, int type) {
		glBindTexture(GL_TEXTURE_2D, bufferID);
		glTexImage2D(GL_TEXTURE_2D, 0, internalType, width, height, 0, type, GL_UNSIGNED_BYTE, (ByteBuffer)null);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private static void updateRenderBufferSize(int bufferID, int width, int height, boolean multisampled, int type) {
		glBindRenderbuffer(GL_RENDERBUFFER, bufferID);
		if (multisampled) {
			glRenderbufferStorageMultisample(GL_RENDERBUFFER, SAMPLES, type, width, height);
		} else {
			glRenderbufferStorage(GL_RENDERBUFFER, type, width, height);
		}
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
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
	
	public void resolve(FrameBuffer target) {
		if (!multisampled)
			throw new IllegalStateException("Resolving is only allowed for multisampled framebuffers!");
		
		int flags = 0;
		if (target.type.hasColorAttachment())
			flags |= GL_COLOR_BUFFER_BIT;
		if (target.type.hasDepthAttachment())
			flags |= GL_DEPTH_BUFFER_BIT;
		
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, target.frameBufferID);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferID);
		
		glBlitFramebuffer(0, 0, width, height, 0, 0, target.width, target.height, flags, GL_NEAREST);
	
		unbind();
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
		if (!type.hasColorAttachment())
			throw new IllegalStateException("FrameBuffer does not have a color attachment!");
		if (multisampled)
			throw new IllegalStateException("Multisampled frame buffers can not be sampled!");
		
		return textureInstance;
	}
	
	@Override
	public void dispose() {
		if (colorBufferID != -1) {
			if (multisampled) {
				glDeleteRenderbuffers(colorBufferID);
			} else {
				glDeleteTextures(colorBufferID);
			}
			
			colorBufferID = -1;
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
