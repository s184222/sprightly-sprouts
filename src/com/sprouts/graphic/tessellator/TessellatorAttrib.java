package com.sprouts.graphic.tessellator;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;
import com.sprouts.math.Vec3;
import com.sprouts.math.Vec4;

public class TessellatorAttrib implements AutoCloseable {

	private static final int DEFAULT_INITIAL_CAPACITY = 64;
	
	private final int componentCount;
	private FloatBuffer attribData;
	
	public TessellatorAttrib(int componentCount) {
		this(componentCount, DEFAULT_INITIAL_CAPACITY);
	}
	
	public TessellatorAttrib(int componentCount, int initialCapacity) {
		if (componentCount <= 0)
			throw new IllegalArgumentException("Component count must be positive!");
		if (initialCapacity <= 0)
			throw new IllegalArgumentException("Initial capacity must be positive!");
		
		this.componentCount = componentCount;
		
		attribData = MemoryUtil.memAllocFloat(componentCount * initialCapacity);
	}

	private void checkClosed() throws IllegalStateException {
		if (attribData == null)
			throw new IllegalStateException("Buffer closed.");
	}
	
	private void ensureCapacity(int amount) {
		checkClosed();

		if (attribData.remaining() < amount) {
			// Double the buffer capacity.
			int newCapacity = attribData.capacity() << 1;
			
			int minimumCapacity = attribData.capacity() + amount;
			int compOffset = (amount % componentCount);
			if (compOffset != 0)
				minimumCapacity += componentCount - compOffset;
			
			// We still do not have enough space with the
			// newly doubled capacity.
			if (newCapacity < minimumCapacity)
				newCapacity = minimumCapacity;
			
			attribData = MemoryUtil.memRealloc(attribData, newCapacity);
		}
	}
	
	public void put(float data) {
		ensureCapacity(1);
		
		attribData.put(data);
	}

	public void put(Vec2 vec) {
		put(vec.x, vec.y);
	}

	public void put(float x, float y) {
		ensureCapacity(2);
		
		attribData.put(x);
		attribData.put(y);
	}
	
	public void put(Vec3 vec) {
		put(vec.x, vec.y, vec.z);
	}
	
	public void put(float x, float y, float z) {
		ensureCapacity(3);
		
		attribData.put(x);
		attribData.put(y);
		attribData.put(z);
	}
	
	public void put(Vec4 vec) {
		put(vec.x, vec.y, vec.z, vec.w);
	}
	
	public void put(float x, float y, float z, float w) {
		ensureCapacity(4);
		
		attribData.put(x);
		attribData.put(y);
		attribData.put(z);
		attribData.put(w);
	}
	
	public void put(Mat4 mat) {
		ensureCapacity(16);
		
		mat.writeBuffer(attribData, false);
	}
	
	public void writeBuffer(VertexBuffer buffer) {
		checkClosed();
		
		if (buffer.getComponentCount() != componentCount)
			throw new IllegalArgumentException("Component count does not match!");
		if ((attribData.position() % componentCount) != 0)
			throw new IllegalStateException("Attribute has missing components!");
		
		FloatBuffer readOnlyBuff = attribData.asReadOnlyBuffer();
		readOnlyBuff.flip();
		buffer.storeData(readOnlyBuff);
	}
	
	public int getComponentCount() {
		return componentCount;
	}
	
	@Override
	public void close() {
		if (attribData != null) {
			MemoryUtil.memFree(attribData);
			attribData = null;
		}
	}
}
