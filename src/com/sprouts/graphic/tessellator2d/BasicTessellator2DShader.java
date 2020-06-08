package com.sprouts.graphic.tessellator2d;

public class BasicTessellator2DShader extends Tessellator2DShader {

	private static final String VERTEX_SHADER_PATH = "/shader/basicTessellator2DShader.vert";
	private static final String FRAGMENT_SHADER_PATH = "/shader/basicTessellator2DShader.frag";
	
	public BasicTessellator2DShader() {
		super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
	}
}
