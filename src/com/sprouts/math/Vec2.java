package com.sprouts.math;

public class Vec2 {

	public float x;
	public float y;
	
	public Vec2() {
		this(0.0f);
	}
	
	public Vec2(float c) {
		this(c, c);
	}

	public Vec2(float x, float y) {
		set(x, y);
	}

	public Vec2(Vec2 other) {
		set(other);
	}

	public Vec2 set(Vec2 other) {
		return set(other.x, other.y);
	}

	public Vec2 set(float c) {
		return set(c, c);
	}

	public Vec2 set(float x, float y) {
		this.x = x;
		this.y = y;
		
		return this;
	}

	public Vec2 add(Vec2 other) {
		x += other.x;
		y += other.y;

		return this;
	}

	public Vec2 sub(Vec2 other) {
		x -= other.x;
		y -= other.y;

		return this;
	}

	public Vec2 mul(Vec2 other) {
		x *= other.x;
		y *= other.y;

		return this;
	}

	public Vec2 div(Vec2 other) {
		x /= other.x;
		y /= other.y;

		return this;
	}
	
	public Vec2 add(float x1, float y1) {
		x += x1;
		y += y1;
		
		return this;
	}

	public Vec2 add(float c) {
		x += c;
		y += c;

		return this;
	}

	public Vec2 sub(float c) {
		x -= c;
		y -= c;

		return this;
	}
	
	public Vec2 sub(float x1, float y1) {
		x -= x1;
		y -= y1;

		return this;
	}

	public Vec2 mul(float c) {
		x *= c;
		y *= c;

		return this;
	}

	public Vec2 div(float c) {
		x /= c;
		y /= c;

		return this;
	}

	public float dot(Vec2 other) {
		return x * other.x +
		       y * other.y;
	}

	public float lengthSqr() {
		return dot(this);
	}

	public float length() {
		return (float)Math.sqrt(lengthSqr());
	}

	public Vec2 normalize() {
		float len = length();
		return (len < LinMath.EPSILON) ? set(0.0f) : div(len);
	}
	
	public Vec2 setAngleRad(float radians) {
		set(length(), 0f);
		rotateRad(radians);

		return this;
	}
	
	public Vec2 setAngle(float degrees) {
		setAngleRad((float)Math.toRadians(degrees));
		
		return this;
	}
	
	public Vec2 rotateRad(float radians) {
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}
	

	public Vec2 rotate(float degrees) {
		rotateRad((float)Math.toRadians(degrees));
		
		return this;
	}
	
	public float angle() {
		float angle = (float)Math.toDegrees(Math.atan2(y, x));
		return (angle < 0.0f) ? (angle + 360.0f) : angle;
	}

	public float dist(float x, float y) {
		final float dx = x - this.x;
		final float dy = y - this.y;
		return (float)Math.sqrt(dx * dx + dy * dy);
	}

	public Vec2 copy() {
		return new Vec2(this);
	}
}
