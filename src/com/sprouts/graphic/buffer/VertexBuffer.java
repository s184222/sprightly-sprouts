package com.sprouts.graphic.buffer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.ByteBuffer;

public class VertexBuffer {

	private int bufferHandle;
	private final int vertexSize;
	private final int minimumBufferSize;
	
	private int bufferSize;

	/**
	 * Creates a new empty OpenGL vertex buffer object.
	 * 
	 * @param vertexSize - the number of elements for each vertex.
	 */
	public VertexBuffer(int vertexSize) {
		this(vertexSize, 0);
	}

	/**
	 * Creates a new empty OpenGL vertex buffer object.
	 * 
	 * @param vertexSize - the number of elements for each vertex.
	 * @param minVertexAllocation - the minimum number of vertices
	 *                              that should be preallocated.
	 */
	public VertexBuffer(int vertexSize, int minVertexAllocation) {
		bufferHandle = glGenBuffers();
		this.vertexSize = vertexSize;
		this.minimumBufferSize = vertexSize * minVertexAllocation;
		
		initBuffer();
	}
	
	private void initBuffer() {
		if (minimumBufferSize != 0) {
			bind();
			glBufferData(GL_ARRAY_BUFFER, minimumBufferSize, GL_DYNAMIC_DRAW);
			bufferSize = minimumBufferSize;
			unbind();
		}
	}
	
	public void bufferData(ByteBuffer buffer) {
		int numBytes = buffer.remaining();
		
		if (bufferSize > minimumBufferSize && numBytes <= minimumBufferSize) {
			// In this case we can free some memory. This vertex
			// buffer implementation will try to keep the memory
			// usage as low as possible, but at least use up the
			// minimumBufferSize number of bytes for performance.
			initBuffer();
		}
		
		if (numBytes <= bufferSize) {
			bufferSubData(buffer, 0);
		} else {
			bind();
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
			bufferSize = numBytes;
			unbind();
		}
	}

	public void bufferSubData(ByteBuffer buffer, int vboOffset) {
		if (buffer.remaining() + vboOffset > bufferSize)
			throw new IndexOutOfBoundsException("VBO offset out of bounds: " + bufferSize);

		bind();
		glBufferSubData(GL_ARRAY_BUFFER, vboOffset, buffer);
		unbind();
	}
	
	public void bind() {
		glBindBuffer(GL_ARRAY_BUFFER, bufferHandle);
	}

	public void unbind() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public int getVertexSize() {
		return vertexSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}
	
	public void dispose() {
		if (bufferHandle != -1) {
			glDeleteBuffers(bufferHandle);
			bufferHandle = -1;
		}
	}
}
