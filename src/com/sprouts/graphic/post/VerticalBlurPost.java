package com.sprouts.graphic.post;

import com.sprouts.graphic.buffer.FrameBuffer;
import com.sprouts.graphic.buffer.VertexArray;

public class VerticalBlurPost extends BasicPost {

	private static final String VERTEX_SHADER_PATH = "/shader/verticalBlurShader.vert";
	
	private final BlurShader shader;
	
	public VerticalBlurPost(VertexArray quad) {
		super(quad);
	
		shader = new BlurShader(VERTEX_SHADER_PATH);
	}
	
	@Override
	public void prepareAndEnableShader(FrameBuffer source) {
		shader.enable();
		shader.setTargetSize(frameBuffer.getHeight());
		shader.setTextureSampler(0);
	}

	@Override
	public void dispose() {
		super.dispose();

		shader.dispose();
	}
}
