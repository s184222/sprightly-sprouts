package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import com.sprouts.IResource;

public class VertexArray implements IResource {
	
	private int vaoHandle;
	
	public VertexArray() {
		vaoHandle = glGenVertexArrays();
	}

	public void storeBuffer(int attributeIndex, VertexBuffer buffer) {
		storeBuffer(attributeIndex, buffer, GL_FLOAT, false);
	}

	public void storeBuffer(int attributeIndex, VertexBuffer buffer, int type, boolean normalized) {
		storeBuffer(attributeIndex, buffer, type, normalized, buffer.getVertexSize(), 0, 0);
	}

	public void storeBuffer(int attributeIndex, VertexBuffer buffer, int type, boolean normalized, int size, int stride, int offset) {
		bind();
		buffer.bind();
		glVertexAttribPointer(attributeIndex, size, type, normalized, stride, (long)offset);
		buffer.unbind();

		glEnableVertexAttribArray(attributeIndex);
		unbind();
	}
	
	public void bind() {
		glBindVertexArray(vaoHandle);
	}

	public void unbind() {
		glBindVertexArray(0);
	}
	
	@Override
	public void dispose() {
		if (vaoHandle != -1) {
			glDeleteVertexArrays(vaoHandle);
			vaoHandle = -1;
		}
	}
}
