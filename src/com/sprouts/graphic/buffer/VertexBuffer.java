package com.sprouts.graphic.buffer;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class VertexBuffer {

	private int bufferHandle;
	private final int componentCount;
	private int size;
	
	/**
	 * Creates a new OpenGL vertex buffer object.
	 * 
	 * @param data - the data to be transferred to the VBO.
	 * @param componentCount - the number of elements for each vertex.
	 */
	public VertexBuffer(float[] data, int componentCount) {
		bufferHandle = GL30.glGenBuffers();
		this.componentCount = componentCount;
		size = -1;
		
		storeData(data);
	}
	
	public void storeData(float[] data) {
		bind();
		if (size != data.length) {
			GL30.glBufferData(GL20.GL_ARRAY_BUFFER, data, GL20.GL_STATIC_DRAW);
			size = data.length;
		} else {
			GL30.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, data);
		}
		unbind();
	}
	
	public void bind() {
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, bufferHandle);
	}

	public void unbind() {
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
	}
	
	public int getComponentCount() {
		return componentCount;
	}

	public int getSize() {
		return size;
	}
	
	public void dispose() {
		if (bufferHandle != -1) {
			GL30.glDeleteBuffers(bufferHandle);
			bufferHandle = -1;
		}
	}
}
