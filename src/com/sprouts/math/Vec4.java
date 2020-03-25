package com.sprouts.math;

public class Vec4 {

	public float x;
	public float y;
	public float z;
	public float w;

	public Vec4() {
		this(0.0f);
	}
	
	public Vec4(float c) {
		this(c, c, c, c);
	}

	public Vec4(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	public Vec4(Vec4 other) {
		set(other);
	}

	public Vec4 set(Vec4 other) {
		return set(other.x, other.y, other.z, other.w);
	}

	public Vec4 set(float c) {
		return set(c, c, c, c);
	}
	
	public Vec4 set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		
		return this;
	}

	public Vec4 add(Vec4 other) {
		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;

		return this;
	}

	public Vec4 sub(Vec4 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		w -= other.w;

		return this;
	}

	public Vec4 mul(Vec4 other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;

		w *= other.w;

		return this;
	}

	public Vec4 div(Vec4 other) {
		x /= other.x;
		y /= other.y;
		z /= other.z;
		w /= other.w;

		return this;
	}

	public Vec4 add(float c) {
		x += c;
		y += c;
		z += c;
		w += c;

		return this;
	}

	public Vec4 sub(float c) {
		x -= c;
		y -= c;
		z -= c;
		w -= c;

		return this;
	}

	public Vec4 mul(float c) {
		x *= c;
		y *= c;
		z *= c;
		w *= c;

		return this;
	}

	public Vec4 div(float c) {
		x /= c;
		y /= c;
		z /= c;
		w /= c;

		return this;
	}

	public float dot(Vec4 other) {
		return x * other.x + 
		       y * other.y + 
		       z * other.z + 
		       w * other.w;
	}

	public float lengthSqr() {
		return dot(this);
	}

	public float length() {
		return (float)Math.sqrt(lengthSqr());
	}

	public Vec4 normalize() {
		float len = length();
		return (len < LinMath.EPSILON) ? set(0.0f) : div(len);
	}
	
	public Vec4 copy() {
		return new Vec4(this);
	}
}
