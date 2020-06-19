package com.sprouts.graphic.post;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import com.sprouts.graphic.buffer.FrameBuffer;
import com.sprouts.graphic.buffer.FrameBufferType;
import com.sprouts.graphic.buffer.VertexArray;

public abstract class BasicPost implements IPost {

	private final VertexArray quad;
	
	protected final FrameBuffer frameBuffer;
	
	public BasicPost(VertexArray quad) {
		this.quad = quad;

		frameBuffer = new FrameBuffer(FrameBufferType.COLOR, 0, 0);
	}
	
	@Override
	public void setSize(int width, int height) {
		frameBuffer.setSize(width, height);
	}
	
	@Override
	public void process(FrameBuffer source) {
		prepareAndEnableShader(source);
		
		frameBuffer.bind();

		glActiveTexture(GL_TEXTURE0);
		source.getColorTexture().bind();
		
		quad.bind();
		glDrawArrays(GL_TRIANGLES, 0, 3 * 2);
		
		frameBuffer.unbind();
	}

	public abstract void prepareAndEnableShader(FrameBuffer source);

	@Override
	public FrameBuffer getTargetBuffer() {
		return frameBuffer;
	}

	@Override
	public void dispose() {
		frameBuffer.dispose();
	}
}
