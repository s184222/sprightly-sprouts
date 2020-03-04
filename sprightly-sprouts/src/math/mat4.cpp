#include "mat4.h"
#include "math_constants.h"

#include <cstring>
#include <math.h>

Mat4::Mat4(float d)
{
	toIdentity(d);
}

Mat4::Mat4(const Mat4& other)
{
	*this = other;
}

Mat4& Mat4::toIdentity(float d)
{
	m00 = m11 = m22 = m33 = d;

	m10 = m20 = m30 = 0.0f; // r = 0
	m01 = m21 = m31 = 0.0f; // r = 1
	m02 = m12 = m32 = 0.0f; // r = 2
	m03 = m13 = m23 = 0.0f; // r = 3

	return *this;
}

Mat4& Mat4::toOrthographic(float left, float right, float top, float bottom, float near, float far)
{
	toIdentity();

	m00 = 2.0f / (right - left);
	m11 = 2.0f / (top - bottom);
	m22 = 2.0f / (near - far);

	m30 = (left + right) / (left - right);
	m31 = (bottom + top) / (bottom - top);
	m32 = (far + near) / (far - near);

	return *this;
}

Mat4& Mat4::toPerspective(float fov, float aspect, float near, float far)
{
	toIdentity();

	float q = 1.0f / tanf(toRadians(fov * 0.5f));

	m00 = q / aspect;
	m11 = q;
	m22 = -(far + near) / (far - near);
	m33 = 0.0f;

	m23 = -1.0f;
	m32 = -2.0f * far * near / (far - near);

	return *this;
}

Mat4& Mat4::rotateX(float degrees)
{
	float r = toRadians(degrees);
	float c = cosf(r);
	float s = sinf(r);

	float n10 = m10 * c + m20 * s;
	float n11 = m11 * c + m21 * s;
	float n12 = m12 * c + m22 * s;
	float n13 = m13 * c + m23 * s;

	m20 = m20 * c - m10 * s;
	m21 = m21 * c - m11 * s;
	m22 = m22 * c - m12 * s;
	m23 = m23 * c - m13 * s;

	m10 = n10;
	m11 = n11;
	m12 = n12;
	m13 = n13;

	return *this;
}

Mat4& Mat4::rotateY(float degrees)
{
	float r = toRadians(degrees);
	float c = cosf(r);
	float s = sinf(r);

	float n00 = m00 * c - m20 * s;
	float n01 = m01 * c - m21 * s;
	float n02 = m02 * c - m22 * s;
	float n03 = m03 * c - m23 * s;

	m20 = m00 * s + m20 * c;
	m21 = m01 * s + m21 * c;
	m22 = m02 * s + m22 * c;
	m23 = m03 * s + m23 * c;

	m00 = n00;
	m01 = n01;
	m02 = n02;
	m03 = n03;

	return *this;
}

Mat4& Mat4::rotateZ(float degrees)
{
	float r = toRadians(degrees);
	float c = cosf(r);
	float s = sinf(r);

	float n00 = m00 * c + m10 * s;
	float n01 = m01 * c + m11 * s;
	float n02 = m02 * c + m12 * s;
	float n03 = m03 * c + m13 * s;

	m10 = m10 * c - m00 * s;
	m11 = m11 * c - m01 * s;
	m12 = m12 * c - m02 * s;
	m13 = m13 * c - m03 * s;

	m00 = n00;
	m01 = n01;
	m02 = n02;
	m03 = n03;

	return *this;
}

Mat4& Mat4::add(const Mat4& right)
{
	for (unsigned int i = 0; i < MAT4_NUM_ELEMENTS; i++)
		(*this).elements[i] += right.elements[i];
	return *this;
}

Mat4& Mat4::sub(const Mat4& right)
{
	for (unsigned int i = 0; i < MAT4_NUM_ELEMENTS; i++)
		(*this).elements[i] -= right.elements[i];
	return *this;
}

Mat4& Mat4::mul(const Mat4& right)
{
	float n00 = m00 * right.m00 + m10 * right.m01 + m20 * right.m02 + m30 * right.m03;
	float n01 = m01 * right.m00 + m11 * right.m01 + m21 * right.m02 + m31 * right.m03;
	float n02 = m02 * right.m00 + m12 * right.m01 + m22 * right.m02 + m32 * right.m03;
	float n03 = m03 * right.m00 + m13 * right.m01 + m23 * right.m02 + m33 * right.m03;

	float n10 = m00 * right.m10 + m10 * right.m11 + m20 * right.m12 + m30 * right.m13;
	float n11 = m01 * right.m10 + m11 * right.m11 + m21 * right.m12 + m31 * right.m13;
	float n12 = m02 * right.m10 + m12 * right.m11 + m22 * right.m12 + m32 * right.m13;
	float n13 = m03 * right.m10 + m13 * right.m11 + m23 * right.m12 + m33 * right.m13;

	float n20 = m00 * right.m20 + m10 * right.m21 + m20 * right.m22 + m30 * right.m23;
	float n21 = m01 * right.m20 + m11 * right.m21 + m21 * right.m22 + m31 * right.m23;
	float n22 = m02 * right.m20 + m12 * right.m21 + m22 * right.m22 + m32 * right.m23;
	float n23 = m03 * right.m20 + m13 * right.m21 + m23 * right.m22 + m33 * right.m23;

	m30 = m00 * right.m30 + m10 * right.m31 + m20 * right.m32 + m30 * right.m33;
	m31 = m01 * right.m30 + m11 * right.m31 + m21 * right.m32 + m31 * right.m33;
	m32 = m02 * right.m30 + m12 * right.m31 + m22 * right.m32 + m32 * right.m33;
	m33 = m03 * right.m30 + m13 * right.m31 + m23 * right.m32 + m33 * right.m33;

	m00 = n00;
	m01 = n01;
	m02 = m02;
	m03 = n03;

	m10 = n10;
	m11 = n11;
	m12 = m12;
	m13 = n13;

	m20 = n20;
	m21 = n21;
	m22 = m22;
	m23 = n23;

	return *this;
}

Mat4& Mat4::operator=(const Mat4& right) 
{
	std::memcpy(elements, right.elements, sizeof(elements));
	return *this;
}

Mat4& Mat4::operator+=(const Mat4& right)
{
	return this->add(right);
}

Mat4& Mat4::operator-=(const Mat4& right)
{
	return this->sub(right);
}

Mat4& Mat4::operator*=(const Mat4& right)
{
	return this->mul(right);
}

Vec4& Mat4::operator[](unsigned int rowIndex)
{
	if (rowIndex < 0 || rowIndex >= MAT4_NUM_ROWS)
		throw - 1;
	return rows[rowIndex];
}

Mat4 operator+(Mat4 left, const Mat4& right)
{
	return left.add(right);
}

Mat4 operator-(Mat4 left, const Mat4& right)
{
	return left.sub(right);
}

Mat4 operator*(Mat4 left, const Mat4& right)
{
	return left.mul(right);
}

std::ostream& operator<<(std::ostream& os, const Mat4& mat) 
{
	for (unsigned int r = 0; r < MAT4_NUM_ROWS; r++) {
		if (r != 0)
			os << ", \n";
		os << mat.rows[r];
	}

	return os << '\n';
}
