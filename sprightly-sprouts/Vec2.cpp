#include <math.h>
#include "Vec2.h"


Vec2::Vec2()
{
	this->x = 0;
	this->y = 0;
}

Vec2::Vec2(float x, float y)
{
	this->x = x;
	this->y = y;
}

Vec2 Vec2::copy()
{
	return Vec2(x, y);
}

Vec2& Vec2::add(Vec2& v) 
{
	this->x += v.x;
	this->y += v.y;

	return *this;
}

Vec2& Vec2::sub(Vec2& v)
{
	this->x -= v.x;
	this->y -= v.y;

	return *this;
}

Vec2& Vec2::norm()
{
	float length = len();
	this->x /= length;
	this->y /= length;

	return *this;
}

float Vec2::len()
{
	return (float) sqrt(x * x + y * y);
}

