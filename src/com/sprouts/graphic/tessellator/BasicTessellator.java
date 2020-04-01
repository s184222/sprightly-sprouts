package com.sprouts.graphic.tessellator;

import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.math.Vec3;

public class BasicTessellator implements AutoCloseable {

	private static final int POSITION_COMPONENT_COUNT = 3;
	private static final int COLOR_COMPONENT_COUNT = 3;
	
	private final TessellatorAttrib positionAttrib;
	private final TessellatorAttrib colorAttrib;
	
	private Vec3 currentPos;
	private Vec3 currentCol;

	public BasicTessellator() {
		positionAttrib = new TessellatorAttrib(POSITION_COMPONENT_COUNT);
		colorAttrib = new TessellatorAttrib(COLOR_COMPONENT_COUNT);

		currentPos = new Vec3();
		currentCol = new Vec3();
	}
	
	public BasicTessellator next() {
		positionAttrib.put(currentPos);
		colorAttrib.put(currentCol);
		return this;
	}
	
	public BasicTessellator position(Vec3 position) {
		return position(position.x, position.y, position.z);
	}
	
	public BasicTessellator position(float x, float y, float z) {
		currentPos.set(x, y, z);
		return this;
	}
	
	public BasicTessellator color(Vec3 color) {
		return color(color.x, color.y, color.z);
	}
	
	public BasicTessellator color(float r, float g, float b) {
		currentCol.set(r, g, b);
		return this;
	}

	public VertexBuffer writePositionBuffer() {
		return writePositionBuffer(new VertexBuffer(POSITION_COMPONENT_COUNT));
	}

	public VertexBuffer writePositionBuffer(VertexBuffer positionBuffer) {
		positionAttrib.writeBuffer(positionBuffer);
		return positionBuffer;
	}

	public VertexBuffer writeColorBuffer() {
		return writeColorBuffer(new VertexBuffer(COLOR_COMPONENT_COUNT));
	}

	public VertexBuffer writeColorBuffer(VertexBuffer colorBuffer) {
		colorAttrib.writeBuffer(colorBuffer);
		return colorBuffer;
	}
	
	@Override
	public void close() {
		positionAttrib.close();
		colorAttrib.close();
	}
}
