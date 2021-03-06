package com.sprouts.graphic.shader;

import com.sprouts.math.Mat4;

public class TestShader extends ShaderProgram {

	private static final String VERTEX_SHADER_PATH = "/shader/testShader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader/testShader.frag";
	
	public static final int POSITION_ATTRIB_INDEX = 0;
	public static final int COLOR_ATTRIB_INDEX = 1;
	public static final int TEX_COORD_ATTRIB_INDEX = 2;
	
	private final int projMatLocation;
	private final int viewMatLocation;
	private final int modlMatLocation;
	private final int texLocation;
	
	public TestShader() {
		super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
		
		projMatLocation = getUniformLocation("proj_mat");
		viewMatLocation = getUniformLocation("view_mat");
		modlMatLocation = getUniformLocation("modl_mat");
		texLocation = getUniformLocation("tex");
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(POSITION_ATTRIB_INDEX, "position");
		bindAttribute(COLOR_ATTRIB_INDEX, "color");
		bindAttribute(TEX_COORD_ATTRIB_INDEX, "texCoord");
	}

	public void setProjMat(Mat4 projMat) {
		uniformMat4(projMatLocation, projMat);
	}

	public void setViewMat(Mat4 viewMat) {
		uniformMat4(viewMatLocation, viewMat);
	}

	public void setModlMat(Mat4 modlMat) {
		uniformMat4(modlMatLocation, modlMat);
	}
	
	public void setTextureUnit(int tex) {
		uniformInt(texLocation, tex);
	}
}
