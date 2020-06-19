package com.sprouts.graphic.obj.shader;

public class BasicObjShader extends ObjShader {
	private static final String VERTEX_SHADER_PATH = "/shader/basicObjShader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader/basicObjShader.frag";
	
	public BasicObjShader() {
		super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
	}
}
