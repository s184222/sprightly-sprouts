#pragma once

#define MATH_PI 3.14159265359f
#define MATH_EPSILON 1E-5f

inline float toRadians(float degrees) 
{
	return MATH_PI * degrees / 180.0f;
}

inline float toDegrees(float radians) 
{
	return 180.0f * radians / MATH_PI;
}
