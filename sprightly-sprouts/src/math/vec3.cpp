#include "vec3.h"
#include "math_constants.h"

#include <math.h>

Vec3::Vec3(float c) :
	Vec3(c, c, c)
{ }

Vec3::Vec3(float x, float y, float z) :
	x(x), y(y), z(z)
{ }

Vec3::Vec3(const Vec3& other)
{
	set(other);
}

Vec3& Vec3::set(const Vec3& other)
{
	x = other.x;
	y = other.y;
	z = other.z;

	return *this;
}

Vec3& Vec3::set(float c)
{
	x = y = z = c;

	return *this;
}

Vec3& Vec3::add(const Vec3& other)
{
	x += other.x;
	y += other.y;
	z += other.z;

	return *this;
}

Vec3& Vec3::sub(const Vec3& other)
{
	x -= other.x;
	y -= other.y;
	z -= other.z;

	return *this;
}

Vec3& Vec3::mul(const Vec3& other)
{
	x *= other.x;
	y *= other.y;
	z *= other.z;

	return *this;
}

Vec3& Vec3::div(const Vec3& other)
{
	x /= other.x;
	y /= other.y;
	z /= other.z;

	return *this;
}

Vec3& Vec3::add(float c)
{
	x += c;
	y += c;
	z += c;

	return *this;
}

Vec3& Vec3::sub(float c)
{
	x -= c;
	y -= c;
	z -= c;

	return *this;
}

Vec3& Vec3::mul(float c)
{
	x *= c;
	y *= c;
	z *= c;

	return *this;
}

Vec3& Vec3::div(float c)
{
	x /= c;
	y /= c;
	z /= c;

	return *this;
}

float Vec3::dot(const Vec3& other) const
{
	return x * other.x +
	       y * other.y +
	       z * other.z;
}

float Vec3::lengthSqr() const
{
	return dot(*this);
}

float Vec3::length() const
{
	return sqrtf(lengthSqr());
}

Vec3& Vec3::normalize()
{
	float len = length();
	return (len < MATH_EPSILON) ? set(0.0f) : div(len);
}

Vec3 Vec3::cross(const Vec3& right) const
{
	return Vec3(y * right.z - z * right.y, 
	            z * right.x - x * right.z, 
	            x * right.y - y * right.x);
}

Vec3& Vec3::operator+=(const Vec3& right)
{
	return add(right);
}

Vec3& Vec3::operator-=(const Vec3& right)
{
	return sub(right);
}

Vec3& Vec3::operator*=(const Vec3& right)
{
	return mul(right);
}

Vec3& Vec3::operator/=(const Vec3& right)
{
	return div(right);
}

Vec3& Vec3::operator+=(float c)
{
	return add(c);
}

Vec3& Vec3::operator-=(float c)
{
	return sub(c);
}

Vec3& Vec3::operator*=(float c)
{
	return mul(c);
}

Vec3& Vec3::operator/=(float c)
{
	return div(c);
}

float& Vec3::operator[](unsigned int elementIndex)
{
	if (elementIndex < 0 || elementIndex >= VEC3_NUM_ELEMENTS)
		throw - 1;
	return elements[elementIndex];
}

Vec3 operator+(Vec3 left, const Vec3& right)
{
	return left.add(right);
}

Vec3 operator-(Vec3 left, const Vec3& right)
{
	return left.sub(right);
}

Vec3 operator*(Vec3 left, const Vec3& right)
{
	return left.mul(right);
}

Vec3 operator/(Vec3 left, const Vec3& right)
{
	return left.div(right);
}

std::ostream& operator<<(std::ostream& os, const Vec3& vec)
{
	return os << vec.x << ", " << vec.y << ", " << vec.z;
}
