package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.LinkedList;

public class VertexArray {
	
	private int vaoHandle;
	
	private LinkedList<VertexBuffer> buffers;
	
	public VertexArray() {
		vaoHandle = glGenVertexArrays();
	
		buffers = new LinkedList<VertexBuffer>();
	}
	
	public void storeAttributeBuffer(int attributeIndex, VertexBuffer buffer) {
		bind();
		buffer.bind();
		glVertexAttribPointer(attributeIndex, buffer.getComponentCount(), GL_FLOAT, false, 0, 0);
		buffer.unbind();

		glEnableVertexAttribArray(attributeIndex);
		unbind();
		
		buffers.add(buffer);
	}
	
	public void bind() {
		glBindVertexArray(vaoHandle);
	}

	public void unbind() {
		glBindVertexArray(0);
	}
	
	public void dispose() {
		if (vaoHandle != -1) {
			glDeleteVertexArrays(vaoHandle);
			vaoHandle = -1;
		}
		
		for (VertexBuffer buffer : buffers)
			buffer.dispose();
		buffers.clear();
	}
}
