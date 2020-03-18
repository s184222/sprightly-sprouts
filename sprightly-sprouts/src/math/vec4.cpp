#include "vec4.h"
#include "math_constants.h"

#include <math.h>

Vec4::Vec4(float c) :
	Vec4(c, c, c, c)
{ }

Vec4::Vec4(float x, float y, float z, float w) :
	x(x), y(y), z(z), w(w)
{ }

Vec4::Vec4(const Vec4& other) 
{
	set(other);
}

Vec4& Vec4::set(const Vec4& other) 
{
	x = other.x;
	y = other.y;
	z = other.z;
	w = other.w;

	return *this;
}

Vec4& Vec4::set(float c) 
{
	x = y = z = w = c;

	return *this;
}

Vec4& Vec4::add(const Vec4& other)
{
	x += other.x;
	y += other.y;
	z += other.z;
	w += other.w;

	return *this;
}

Vec4& Vec4::sub(const Vec4& other)
{
	x -= other.x;
	y -= other.y;
	z -= other.z;
	w -= other.w;

	return *this;
}

Vec4& Vec4::mul(const Vec4& other) 
{
	x *= other.x;
	y *= other.y;
	z *= other.z;

	w *= other.w;

	return *this;
}

Vec4& Vec4::div(const Vec4& other) 
{
	x /= other.x;
	y /= other.y;
	z /= other.z;
	w /= other.w;

	return *this;
}

Vec4& Vec4::add(float c)
{
	x += c;
	y += c;
	z += c;
	w += c;

	return *this;
}

Vec4& Vec4::sub(float c)
{
	x -= c;
	y -= c;
	z -= c;
	w -= c;

	return *this;
}

Vec4& Vec4::mul(float c)
{
	x *= c;
	y *= c;
	z *= c;
	w *= c;

	return *this;
}

Vec4& Vec4::div(float c)
{
	x /= c;
	y /= c;
	z /= c;
	w /= c;

	return *this;
}

float Vec4::dot(const Vec4& other) const
{
	return x * other.x + 
	       y * other.y + 
	       z * other.z + 
	       w * other.w;
}

float Vec4::lengthSqr() const
{
	return dot(*this);
}

float Vec4::length() const
{
	return sqrtf(lengthSqr());
}

Vec4& Vec4::normalize() 
{
	float len = length();
	return (len < MATH_EPSILON) ? set(0.0f) : div(len);
}

Vec4& Vec4::operator+=(const Vec4& right) 
{
	return add(right);
}

Vec4& Vec4::operator-=(const Vec4& right) 
{
	return sub(right);
}

Vec4& Vec4::operator*=(const Vec4& right) 
{
	return mul(right);
}

Vec4& Vec4::operator/=(const Vec4& right) 
{
	return div(right);
}

Vec4& Vec4::operator+=(float c) 
{
	return add(c);
}

Vec4& Vec4::operator-=(float c) 
{
	return sub(c);
}

Vec4& Vec4::operator*=(float c) 
{
	return mul(c);
}

Vec4& Vec4::operator/=(float c) 
{
	return div(c);
}

float& Vec4::operator[](unsigned int elementIndex) 
{
	if (elementIndex < 0 || elementIndex >= VEC4_NUM_ELEMENTS)
		throw -1;
	return elements[elementIndex];
}

Vec4 operator+(Vec4 left, const Vec4& right) 
{
	return left.add(right);
}

Vec4 operator-(Vec4 left, const Vec4& right) 
{
	return left.sub(right);
}

Vec4 operator*(Vec4 left, const Vec4& right) 
{
	return left.mul(right);
}

Vec4 operator/(Vec4 left, const Vec4& right) 
{
	return left.div(right);
}

std::ostream& operator<<(std::ostream& os, const Vec4& vec) 
{
	return os << vec.x << ", " << vec.y << ", " << vec.z << ", " << vec.w;
}
