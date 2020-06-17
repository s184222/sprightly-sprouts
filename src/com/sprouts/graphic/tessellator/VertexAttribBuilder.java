package com.sprouts.graphic.tessellator;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import com.sprouts.IResource;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;
import com.sprouts.math.Vec3;
import com.sprouts.math.Vec4;

/**
 * @author Christian
 */
public class VertexAttribBuilder implements AutoCloseable, IResource {

	private static final int DEFAULT_INITIAL_CAPACITY = 64;
	
	private static final int BYTE_SIZE = 1;
	private static final int FLOAT_BYTE_SIZE = 4 * BYTE_SIZE;
	private static final int INT_BYTE_SIZE = 4 * BYTE_SIZE;
	
	private final int vertexSize;
	
	private ByteBuffer attribData;
	
	public VertexAttribBuilder(int vertexSize) {
		this(vertexSize, DEFAULT_INITIAL_CAPACITY);
	}
	
	public VertexAttribBuilder(int vertexSize, int initialCapacity) {
		if (vertexSize <= 0)
			throw new IllegalArgumentException("Vertex size must be positive!");
		if (initialCapacity <= 0)
			throw new IllegalArgumentException("Initial capacity must be positive!");
		
		this.vertexSize = vertexSize;
		
		attribData = MemoryUtil.memAlloc(vertexSize * initialCapacity);
	}

	private void checkClosed() throws IllegalStateException {
		if (attribData == null)
			throw new IllegalStateException("Buffer closed.");
	}
	
	public void ensureCapacity(int numBytes) {
		checkClosed();

		if (attribData.remaining() < numBytes) {
			// Double the buffer capacity.
			int newCapacity = attribData.capacity() << 1;
			
			int minimumCapacity = attribData.position() + numBytes;
			int compOffset = (numBytes % vertexSize);
			if (compOffset != 0)
				minimumCapacity += vertexSize - compOffset;
			
			// We still do not have enough space with the
			// newly doubled capacity.
			if (newCapacity < minimumCapacity)
				newCapacity = minimumCapacity;
			
			attribData = MemoryUtil.memRealloc(attribData, newCapacity);
		}
	}
	
	public void put(byte data) {
		ensureCapacity(1 * BYTE_SIZE);
		
		attribData.put(data);
	}
	
	public void put(float data) {
		ensureCapacity(1 * FLOAT_BYTE_SIZE);
		
		attribData.putFloat(data);
	}

	public void put(Vec2 vec) {
		put(vec.x, vec.y);
	}

	public void put(float x, float y) {
		ensureCapacity(2 * FLOAT_BYTE_SIZE);
		
		attribData.putFloat(x);
		attribData.putFloat(y);
	}
	
	public void put(Vec3 vec) {
		put(vec.x, vec.y, vec.z);
	}
	
	public void put(float x, float y, float z) {
		ensureCapacity(3 * FLOAT_BYTE_SIZE);
		
		attribData.putFloat(x);
		attribData.putFloat(y);
		attribData.putFloat(z);
	}
	
	public void put(Vec4 vec) {
		put(vec.x, vec.y, vec.z, vec.w);
	}
	
	public void put(float x, float y, float z, float w) {
		ensureCapacity(4 * FLOAT_BYTE_SIZE);
		
		attribData.putFloat(x);
		attribData.putFloat(y);
		attribData.putFloat(z);
		attribData.putFloat(w);
	}
	
	public void put(Mat4 mat) {
		ensureCapacity(16);
		
		mat.writeBuffer(attribData, false);
	}
	
	public void put(int data) {
		ensureCapacity(1 * INT_BYTE_SIZE);
		
		attribData.putInt(data);
	}

	public void put(int x, int y) {
		ensureCapacity(2 * INT_BYTE_SIZE);
		
		attribData.putInt(x);
		attribData.putInt(y);
	}

	public void put(int x, int y, int z) {
		ensureCapacity(3 * INT_BYTE_SIZE);
		
		attribData.putInt(x);
		attribData.putInt(y);
		attribData.putInt(z);
	}

	public void put(int x, int y, int z, int w) {
		ensureCapacity(4 * INT_BYTE_SIZE);
		
		attribData.putInt(x);
		attribData.putInt(y);
		attribData.putInt(z);
		attribData.putInt(w);
	}

	public void clear() {
		attribData.clear();
	}
	
	public void writeBuffer(VertexBuffer buffer) {
		checkClosed();
		
		if (buffer.getVertexSize() != vertexSize)
			throw new IllegalArgumentException("Vertex sizes do not match!");
		if ((attribData.position() % vertexSize) != 0)
			throw new IllegalStateException("Attribute has missing data!");
		
		buffer.bufferData(getAsReadOnlyBuffer());
	}
	
	public void writeBuffer(ByteBuffer buffer) throws BufferOverflowException {
		buffer.put(getAsReadOnlyBuffer());
	}
	
	private ByteBuffer getAsReadOnlyBuffer() {
		ByteBuffer tmpBuffer = attribData.asReadOnlyBuffer();
		tmpBuffer.flip();
		return tmpBuffer;
	}
	
	public ByteBuffer getWritableBuffer() {
		return attribData;
	}
	
	public int getVertexSize() {
		return vertexSize;
	}

	public int getPosition() {
		return attribData.position();
	}
	
	@Override
	public void dispose() {
		if (attribData != null) {
			MemoryUtil.memFree(attribData);
			attribData = null;
		}
	}
	
	@Override
	public void close() {
		dispose();
	}
}
