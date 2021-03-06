package com.sprouts.math;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Mat3 {

	/** Element m[c][r], where c is column and r is row. */
	public float m00, m10, m20,
	             m01, m11, m21,
	             m02, m12, m22;
	
	public Mat3() {
		toIdentity();
	}

	public Mat3(float d) {
		toDiagonal(d);
	}

	public Mat3(Mat3 other) {
		set(other);
	}

	public Mat3 toIdentity() {
		return toDiagonal(1.0f);
	}

	public Mat3 toDiagonal(float d) {
		m00 = m11 = m22 = d;

		m10 = m20 = 0.0f; // r = 0
		m01 = m21 = 0.0f; // r = 1
		m02 = m12 = 0.0f; // r = 2

		return this;
	}

	public Mat3 rotateZ(float radians) {
		float c = (float)Math.cos(radians);
		float s = (float)Math.sin(radians);

		float n00 = m00 * c + m10 * s;
		float n01 = m01 * c + m11 * s;
		float n02 = m02 * c + m12 * s;

		m10 = m10 * c - m00 * s;
		m11 = m11 * c - m01 * s;
		m12 = m12 * c - m02 * s;

		m00 = n00;
		m01 = n01;
		m02 = n02;

		return this;
	}

	public Mat3 translate(Vec2 trs) {
		return translate(trs.x, trs.y);
	}

	public Mat3 translate(float xt, float yt) {
		m20 += m00 * xt + m10 * yt;
		m21 += m01 * xt + m11 * yt;

		// This should always yield 1.0 but leave
		// it anyway for mathematical correctness.
		m22 += m02 * xt + m12 * yt;

		return this;
	}

	public Mat3 scale(Vec2 scl) {
		return scale(scl.x, scl.y);
	}

	public Mat3 scale(float xs, float ys) {
		m00 *= xs;
		m01 *= xs;
		m02 *= xs;

		m10 *= ys;
		m11 *= ys;
		m12 *= ys;

		return this;
	}

	public Mat3 add(Mat3 right) {
		return add(right, this);
	}

	public Mat3 add(Mat3 right, Mat3 dest) {
		dest.m00 = m00 + right.m00;
		dest.m01 = m01 + right.m01;
		dest.m02 = m02 + right.m02;

		dest.m10 = m10 + right.m10;
		dest.m11 = m11 + right.m11;
		dest.m12 = m12 + right.m12;

		dest.m20 = m20 + right.m20;
		dest.m21 = m21 + right.m21;
		dest.m22 = m22 + right.m22;
		
		return dest;
	}

	public Mat3 sub(Mat3 right) {
		return sub(right, this);
	}
	
	public Mat3 sub(Mat3 right, Mat3 dest) {
		dest.m00 = m00 - right.m00;
		dest.m01 = m01 - right.m01;
		dest.m02 = m02 - right.m02;

		dest.m10 = m10 - right.m10;
		dest.m11 = m11 - right.m11;
		dest.m12 = m12 - right.m12;

		dest.m20 = m20 - right.m20;
		dest.m21 = m21 - right.m21;
		dest.m22 = m22 - right.m22;

		return dest;
	}

	public Mat3 mul(Mat3 right) {
		return mul(right, this);
	}

	public Mat3 mul(Mat3 right, Mat3 dest) {
		float n00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02;
		float n01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02;
		float n02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02;

		float n10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12;
		float n11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12;
		float n12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12;

		float n20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22;
		float n21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22;
		float n22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22;

		dest.m00 = n00;
		dest.m01 = n01;
		dest.m02 = n02;

		dest.m10 = n10;
		dest.m11 = n11;
		dest.m12 = n12;

		dest.m20 = n20;
		dest.m21 = n21;
		dest.m22 = n22;

		return dest;
	}

	public Vec3 mul(Vec3 right) {
		return mul(right, right);
	}

	public Vec3 mul(Vec3 right, Vec3 dest) {
		float nx = m00 * right.x + m10 * right.y + m20 * right.z;
		float ny = m01 * right.x + m11 * right.y + m21 * right.z;
		dest.z   = m02 * right.x + m12 * right.y + m22 * right.z;

		dest.x = nx;
		dest.y = ny;

		return dest;
	}

	public Vec2 mul(Vec2 right) {
		return mul(right, right);
	}

	public Vec2 mul(Vec2 right, Vec2 dest) {
		float nx = m00 * right.x + m10 * right.y + m20;
		dest.y   = m01 * right.x + m11 * right.y + m21;
		
		dest.x = nx;

		return dest;
	}
	
	public Mat3 copy() {
		return copy(new Mat3());
	}
	
	public Mat3 copy(Mat3 dest) {
		return dest.set(m00, m10, m20,
		                m01, m11, m21,
		                m02, m12, m22);
	}

	public Mat3 set(Mat3 other) {
		return set(other.m00, other.m10, other.m20,
		           other.m01, other.m11, other.m21,
		           other.m02, other.m12, other.m22);
	}
	
	public Mat3 set(float m00, float m10, float m20,
	                float m01, float m11, float m21, 
	                float m02, float m12, float m22) {
		
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;

		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;

		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	
		return this;
	}

	public void writeBuffer(FloatBuffer buf, boolean rowMajor) {
		if (rowMajor) {
			buf.put(m00).put(m10).put(m20)
			   .put(m01).put(m11).put(m21)
			   .put(m02).put(m12).put(m22);
		} else {
			buf.put(m00).put(m01).put(m02)
			   .put(m10).put(m11).put(m12)
			   .put(m20).put(m21).put(m22);
		}
	}

	public void writeBuffer(ByteBuffer buf, boolean rowMajor) {
		if (rowMajor) {
			buf.putFloat(m00).putFloat(m10).putFloat(m20)
			   .putFloat(m01).putFloat(m11).putFloat(m21)
			   .putFloat(m02).putFloat(m12).putFloat(m22);
		} else {
			buf.putFloat(m00).putFloat(m01).putFloat(m02)
			   .putFloat(m10).putFloat(m11).putFloat(m12)
			   .putFloat(m20).putFloat(m21).putFloat(m22);
		}
	}
}
