package com.sprouts.math;

public class Vec2d {

	public double x;
	public double y;
	
	public Vec2d() {
		this(0,0);
	}
	
	public Vec2d(double c) {
		this(c, c);
	}

	public Vec2d(double x, double y) {
		set(x, y);
	}

	public Vec2d(Vec2d other) {
		set(other);
	}

	public Vec2d set(Vec2d other) {
		return set(other.x, other.y);
	}

	public Vec2d set(double c) {
		return set(c, c);
	}

	public Vec2d set(double x, double y) {
		this.x = x;
		this.y = y;
		
		return this;
	}

	public Vec2d add(Vec2d other) {
		x += other.x;
		y += other.y;

		return this;
	}

	public Vec2d sub(Vec2d other) {
		x -= other.x;
		y -= other.y;

		return this;
	}

	public Vec2d mul(Vec2d other) {
		x *= other.x;
		y *= other.y;

		return this;
	}

	public Vec2d div(Vec2d other) {
		x /= other.x;
		y /= other.y;

		return this;
	}
	
	public Vec2d add(double x1, double y1) {
		x += x1;
		y += y1;
		
		return this;
	}

	public Vec2d add(double c) {
		x += c;
		y += c;

		return this;
	}

	public Vec2d sub(double c) {
		x -= c;
		y -= c;

		return this;
	}
	
	public Vec2d sub(double x1, double y1) {
		x -= x1;
		y -= y1;

		return this;
	}

	public Vec2d mul(double c) {
		x *= c;
		y *= c;

		return this;
	}

	public Vec2d div(double c) {
		x /= c;
		y /= c;

		return this;
	}

	public double dot(Vec2d other) {
		return x * other.x +
		       y * other.y;
	}

	public double lengthSqr() {
		return dot(this);
	}

	public double length() {
		return (double)Math.sqrt(lengthSqr());
	}

	public Vec2d normalize() {
		double len = length();
		//return (len < LinMath.EPSILON) ? set(0.0d) : div(len);
		return div(len);
	}
	
	public Vec2d setAngleRad(double radians) {
		set(length(), 0d);
		rotateRad(radians);

		return this;
	}
	
	public Vec2d setAngle(double degrees) {
		setAngleRad((double)Math.toRadians(degrees));
		
		return this;
	}
	
	public Vec2d rotateRad(double radians) {
		double cos = (double)Math.cos(radians);
		double sin = (double)Math.sin(radians);

		double newX = this.x * cos - this.y * sin;
		double newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}
	
	public Vec2d rotate(double degrees) {
		rotateRad((double)Math.toRadians(degrees));
		
		return this;
	}
	
	public double angle() {
		double angle = Math.toDegrees(Math.atan2(y, x));
		return (angle < 0.0d) ? (angle + 360.0d) : angle;
	}

	public double dist(double x, double y) {
		final double dx = x - this.x;
		final double dy = y - this.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public Vec2d copy() {
		return new Vec2d(this);
	}
}
