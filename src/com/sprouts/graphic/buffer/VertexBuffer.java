package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

public class VertexBuffer {

	private int bufferHandle;
	private final int componentCount;
	private int size;

	/**
	 * Creates a new empty OpenGL vertex buffer object.
	 * 
	 * @param componentCount - the number of elements for each vertex.
	 */
	public VertexBuffer(int componentCount) {
		bufferHandle = glGenBuffers();
		this.componentCount = componentCount;
		size = -1;
	}
	
	/**
	 * Creates a new OpenGL vertex buffer object.
	 * 
	 * @param data - the data to be transferred to the VBO.
	 * @param componentCount - the number of elements for each vertex.
	 */
	public VertexBuffer(float[] data, int componentCount) {
		this(componentCount);
		
		storeData(data);
	}
	
	public void storeData(FloatBuffer buffer) {
		bind();
		if (size != buffer.remaining()) {
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
			size = buffer.remaining();
		} else {
			glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		}
		unbind();
	}
	
	public void storeData(float[] data) {
		bind();
		if (size != data.length) {
			glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
			size = data.length;
		} else {
			glBufferSubData(GL_ARRAY_BUFFER, 0, data);
		}
		unbind();
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
	}

	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public int getComponentCount() {
		return componentCount;
	}

	public int getSize() {
		return size;
	}
	
	public void dispose() {
		if (bufferHandle != -1) {
			glDeleteBuffers(bufferHandle);
			bufferHandle = -1;
		}
	}
}
