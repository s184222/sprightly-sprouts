package com.sprouts.graphic.post;

import com.sprouts.graphic.shader.ShaderProgram;

public class BlurShader extends ShaderProgram {

	private static final String FRAGMENT_SHADER_PATH = "/shader/blurShader.frag";
	
	private static final int POSITION_ATTRIB_INDEX = 0;
	
	private final int textureSamplerLocation;
	private final int targetSizeLocation;
	
	public BlurShader(String vertexShaderPath) {
		super(vertexShaderPath, FRAGMENT_SHADER_PATH);
	
		textureSamplerLocation = getUniformLocation("u_TextureSampler");
		targetSizeLocation = getUniformLocation("u_TargetSize");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(POSITION_ATTRIB_INDEX, "a_Position");
	}

	public void setTextureSampler(int textureSampler) {
		uniformInt(textureSamplerLocation, textureSampler);
	}
	
	public void setTargetSize(float targetSize) {
		uniformFloat(targetSizeLocation, targetSize);
	}
}
