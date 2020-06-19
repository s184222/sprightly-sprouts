package com.sprouts.graphic.post;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import com.sprouts.IResource;
import com.sprouts.graphic.buffer.FrameBuffer;
import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.texture.Texture;

import static com.sprouts.graphic.tessellator.VertexAttribBuilder.FLOAT_BYTE_SIZE;

public class PostManager implements IResource {

	private final VertexArray quad;
	private final VertexBuffer quadBuffer;
	
	private final HorizontalBlurPost horizontalBlur;
	private final VerticalBlurPost verticalBlur;
	
	public PostManager() {
		quad = new VertexArray();
		quadBuffer = new VertexBuffer(2 * FLOAT_BYTE_SIZE, 6);

		horizontalBlur = new HorizontalBlurPost(quad);
		verticalBlur = new VerticalBlurPost(quad);
		
		initQuad();
	}
	
	private void initQuad() {
		quad.storeBuffer(0, quadBuffer, GL_FLOAT, false, 2, 0, 0);
		
		VertexAttribBuilder builder = new VertexAttribBuilder(quadBuffer.getVertexSize());
		
		builder.put(-1.0f,  1.0f);
		builder.put(-1.0f, -1.0f);
		builder.put( 1.0f,  1.0f);
		
		builder.put( 1.0f, -1.0f);
		builder.put( 1.0f,  1.0f);
		builder.put(-1.0f, -1.0f);
		
		builder.writeBuffer(quadBuffer);
		builder.close();
	}

	public void setSize(int width, int height) {
		width >>= 2;
		height >>= 2;
		
		horizontalBlur.setSize(width, height);
		verticalBlur.setSize(width, height);
	}
	
	public void process(FrameBuffer source) {
		horizontalBlur.process(source);
		verticalBlur.process(horizontalBlur.getTargetBuffer());
	}

	public Texture getOutputTexture() {
		return verticalBlur.getTargetBuffer().getColorTexture();
	}
	
	@Override
	public void dispose() {
		
	}
}
