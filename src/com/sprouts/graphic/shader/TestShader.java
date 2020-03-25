package com.sprouts.graphic.shader;

public class TestShader extends ShaderProgram {

	private static final String VERTEX_SHADER_PATH = "/shader/testShader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader/testShader.frag";
	
	public static final int POSITION_ATTRIB_INDEX = 0;
	
	public TestShader() {
		super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(POSITION_ATTRIB_INDEX, "pos");
	}
}
