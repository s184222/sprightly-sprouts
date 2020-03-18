#pragma once

#include <ostream>

#define VEC4_NUM_ELEMENTS 4

class Vec4 
{

public:

	union {
		float elements[VEC4_NUM_ELEMENTS];

		struct {
			float x, y, z, w;
		};
	};

public:

	Vec4() : Vec4(0.0f) { }
	explicit Vec4(float c);
	Vec4(float x, float y, float z, float w);
	Vec4(const Vec4& other);

	Vec4& set(const Vec4& other);
	Vec4& set(float c);

	Vec4& add(const Vec4& other);
	Vec4& sub(const Vec4& other);
	Vec4& mul(const Vec4& other);
	Vec4& div(const Vec4& other);
	
	Vec4& add(float c);
	Vec4& sub(float c);
	Vec4& mul(float c);
	Vec4& div(float c);

	float dot(const Vec4& other) const;
	
	float lengthSqr() const;
	float length() const;

	Vec4& normalize();

	Vec4& operator+=(const Vec4& right);
	Vec4& operator-=(const Vec4& right);
	Vec4& operator*=(const Vec4& right);
	Vec4& operator/=(const Vec4& right);
	
	Vec4& operator+=(float c);
	Vec4& operator-=(float c);
	Vec4& operator*=(float c);
	Vec4& operator/=(float c);

	float& operator[](unsigned int elementIndex);

	friend Vec4 operator+(Vec4 left, const Vec4& right);
	friend Vec4 operator-(Vec4 left, const Vec4& right);
	friend Vec4 operator*(Vec4 left, const Vec4& right);
	friend Vec4 operator/(Vec4 left, const Vec4& right);

	friend std::ostream& operator<<(std::ostream& os, const Vec4& vec);

};