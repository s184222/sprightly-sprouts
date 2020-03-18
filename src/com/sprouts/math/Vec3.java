package com.sprouts.math;

public class Vec3 {

	public float x;
	public float y;
	public float z;
	
	public Vec3(float c) {
		this(c, c, c);
	}

	public Vec3(float x, float y, float z) {
		set(x, y, z);
	}

	public Vec3(Vec3 other) {
		set(other);
	}

	public Vec3 set(Vec3 other) {
		return set(other.x, other.y, other.z);
	}

	public Vec3 set(float c) {
		return set(c, c, c);
	}

	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		return this;
	}

	public Vec3 add(Vec3 other) {
		x += other.x;
		y += other.y;
		z += other.z;

		return this;
	}

	public Vec3 sub(Vec3 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;

		return this;
	}

	public Vec3 mul(Vec3 other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;

		return this;
	}

	public Vec3 div(Vec3 other) {
		x /= other.x;
		y /= other.y;
		z /= other.z;

		return this;
	}

	public Vec3 add(float c) {
		x += c;
		y += c;
		z += c;

		return this;
	}

	public Vec3 sub(float c) {
		x -= c;
		y -= c;
		z -= c;

		return this;
	}

	public Vec3 mul(float c) {
		x *= c;
		y *= c;
		z *= c;

		return this;
	}

	public Vec3 div(float c) {
		x /= c;
		y /= c;
		z /= c;

		return this;
	}

	public float dot(Vec3 other) {
		return x * other.x +
		       y * other.y +
		       z * other.z;
	}

	public float lengthSqr() {
		return dot(this);
	}

	public float length() {
		return (float)Math.sqrt(lengthSqr());
	}

	public Vec3 normalize() {
		float len = length();
		return (len < LinMath.EPSILON) ? set(0.0f) : div(len);
	}

	public Vec3 cross(Vec3 right) {
		return new Vec3(y * right.z - z * right.y, 
		                z * right.x - x * right.z, 
		                x * right.y - y * right.x);
	}
}
