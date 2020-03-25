package com.sprouts.graphic.buffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexArray {
	private int vaoID;
	
	public VertexArray() {
		vaoID = GL30.glGenVertexArrays();
	}
	
	public void storeDataInAttributeList(int attributeNumber, VertexBuffer vbo) {
		vbo.bind();
		GL20.glVertexAttribPointer(attributeNumber, vbo.getComponentCount(), GL11.GL_FLOAT, false, 0, 0);
		vbo.unbind();
		
	}
	
	public void unbind() {
		GL30.glBindVertexArray(0);
	}
	
	public void bind() {
		GL30.glBindVertexArray(vaoID);
	}
	
	public void dispose() {
		if (vaoID != -1) {
			GL30.glDeleteVertexArrays(vaoID);;
			vaoID = -1;
		}
	}
}
