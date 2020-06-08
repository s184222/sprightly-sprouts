package com.sprouts.graphic.tessellator;

import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.math.Vec2;
import com.sprouts.math.Vec3;
import com.sprouts.math.Vec4;

public class BasicTessellator implements AutoCloseable {

	public static final int POSITION_COMPONENT_COUNT = 3;
	public static final int COLOR_COMPONENT_COUNT = 4;
	public static final int TEX_COORD_COMPONENT_COUNT = 2;
	
	private final VertexAttribBuilder positionAttrib;
	private final VertexAttribBuilder colorAttrib;
	private final VertexAttribBuilder texCoordAttrib;
	
	private Vec3 currentPos;
	private Vec4 currentCol;
	private Vec2 currentTexCoord;

	public BasicTessellator() {
		positionAttrib = new VertexAttribBuilder(POSITION_COMPONENT_COUNT);
		colorAttrib = new VertexAttribBuilder(COLOR_COMPONENT_COUNT);
		texCoordAttrib = new VertexAttribBuilder(TEX_COORD_COMPONENT_COUNT);
		
		currentPos = new Vec3();
		currentCol = new Vec4();
		currentTexCoord = new Vec2();
	}
	
	public BasicTessellator next() {
		positionAttrib.put(currentPos);
		colorAttrib.put(currentCol);
		texCoordAttrib.put(currentTexCoord);
		return this;
	}
	
	public BasicTessellator position(Vec3 pos) {
		return position(pos.x, pos.y, pos.z);
	}
	
	public BasicTessellator position(float x, float y, float z) {
		currentPos.set(x, y, z);
		return this;
	}
	
	public BasicTessellator color(Vec3 col) {
		return color(col.x, col.y, col.z);
	}

	public BasicTessellator color(Vec4 col) {
		return color(col.x, col.y, col.z, col.w);
	}

	public BasicTessellator clearColor() {
		return color(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public BasicTessellator color(float red, float green, float blue) {
		return color(red, green, blue, 1.0f);
	}
	
	public BasicTessellator color(float red, float green, float blue, float alpha) {
		currentCol.set(red, green, blue, alpha);
		return this;
	}

	public BasicTessellator texCoord(Vec2 tc) {
		return texCoord(tc.x, tc.y);
	}
	
	public BasicTessellator texCoord(float u, float v) {
		currentTexCoord.set(u, v);
		return this;
	}
	
	public VertexBuffer writePositionBuffer(VertexBuffer positionBuffer) {
		positionAttrib.writeBuffer(positionBuffer);
		return positionBuffer;
	}

	public VertexBuffer writeColorBuffer(VertexBuffer colorBuffer) {
		colorAttrib.writeBuffer(colorBuffer);
		return colorBuffer;
	}
	
	public VertexBuffer writeTexCoordBuffer(VertexBuffer texCoordBuffer) {
		texCoordAttrib.writeBuffer(texCoordBuffer);
		return texCoordBuffer;
	}
	
	@Override
	public void close() {
		positionAttrib.close();
		colorAttrib.close();
	}
}
