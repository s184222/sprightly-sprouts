package com.sprouts.graphic.buffer;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class VertexArray {
	
	private int vaoHandle;
	
	private LinkedList<VertexBuffer> buffers;
	
	public VertexArray() {
		vaoHandle = GL30.glGenVertexArrays();
	
		buffers = new LinkedList<VertexBuffer>();
	}
	
	public void storeAttributeBuffer(int attributeIndex, VertexBuffer buffer) {
		bind();
		buffer.bind();
		GL30.glVertexAttribPointer(attributeIndex, buffer.getComponentCount(), GL11.GL_FLOAT, false, 0, 0);
		buffer.unbind();

		GL30.glEnableVertexAttribArray(attributeIndex);
		unbind();
		
		buffers.add(buffer);
	}
	
	public void bind() {
		GL30.glBindVertexArray(vaoHandle);
	}

	public void unbind() {
		GL30.glBindVertexArray(0);
	}
	
	public void dispose() {
		if (vaoHandle != -1) {
			GL30.glDeleteVertexArrays(vaoHandle);
			vaoHandle = -1;
		}
		
		for (VertexBuffer buffer : buffers)
			buffer.dispose();
		buffers.clear();
	}
}
