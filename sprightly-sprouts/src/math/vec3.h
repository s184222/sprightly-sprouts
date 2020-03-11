#pragma once

#include <ostream>

#define VEC3_NUM_ELEMENTS 3

class Vec3
{

public:

	union {
		float elements[VEC3_NUM_ELEMENTS];

		struct {
			float x, y, z;
		};
	};

public:

	Vec3() : Vec3(0.0f) { }
	explicit Vec3(float c);
	Vec3(float x, float y, float z);
	Vec3(const Vec3& other);

	Vec3& set(const Vec3& other);
	Vec3& set(float c);

	Vec3& add(const Vec3& other);
	Vec3& sub(const Vec3& other);
	Vec3& mul(const Vec3& other);
	Vec3& div(const Vec3& other);

	Vec3& add(float c);
	Vec3& sub(float c);
	Vec3& mul(float c);
	Vec3& div(float c);

	float dot(const Vec3& other) const;

	float lengthSqr() const;
	float length() const;

	Vec3& normalize();

	Vec3 cross(const Vec3& other) const;

	Vec3& operator+=(const Vec3& right);
	Vec3& operator-=(const Vec3& right);
	Vec3& operator*=(const Vec3& right);
	Vec3& operator/=(const Vec3& right);

	Vec3& operator+=(float c);
	Vec3& operator-=(float c);
	Vec3& operator*=(float c);
	Vec3& operator/=(float c);

	float& operator[](unsigned int elementIndex);

	friend Vec3 operator+(Vec3 left, const Vec3& right);
	friend Vec3 operator-(Vec3 left, const Vec3& right);
	friend Vec3 operator*(Vec3 left, const Vec3& right);
	friend Vec3 operator/(Vec3 left, const Vec3& right);

	friend std::ostream& operator<<(std::ostream& os, const Vec3& vec);

};