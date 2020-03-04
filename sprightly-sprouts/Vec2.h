#pragma once
class Vec2
{
private:
	
	float x, y;

public:

	Vec2();
	Vec2(float x, float y);

	Vec2 copy();

	Vec2& add(Vec2& v);
	Vec2& sub(Vec2& v);

	Vec2& norm();
	float len();
};


