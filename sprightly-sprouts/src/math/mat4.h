#pragma once

#include "vec4.h"

#include <ostream>

#define MAT4_NUM_ROWS 4
#define MAT4_NUM_COLS 4
#define MAT4_NUM_ELEMENTS (MAT4_NUM_ROWS * MAT4_NUM_COLS)

class Mat4
{

public:

	union {
		/* All matrix elements in row major order */
		float elements[MAT4_NUM_ELEMENTS];

		Vec4 rows[MAT4_NUM_ROWS];

		struct {
			/* Element m[c][r], where c is column and r is row. */
			float m00, m10, m20, m30,
			      m01, m11, m21, m31,
			      m02, m12, m22, m32,
			      m03, m13, m23, m33;
		};
	};

public:

	Mat4() : Mat4(1.0f) { }
	explicit Mat4(float d);
	Mat4(const Mat4& other);

	Mat4& toIdentity() { return toIdentity(1.0f); };
	Mat4& toIdentity(float d);

	Mat4& toOrthographic(float left, float right, float top, float bottom, float near, float far);
	Mat4& toPerspective(float fov, float aspect, float near, float far);

	Mat4& rotateX(float degrees);
	Mat4& rotateY(float degrees);
	Mat4& rotateZ(float degrees);

	Mat4& add(const Mat4& right);
	Mat4& sub(const Mat4& right);

	Mat4& mul(const Mat4& right);
	
	Mat4& operator =(const Mat4& right);

	Mat4& operator +=(const Mat4& right);
	Mat4& operator -=(const Mat4& right);
	Mat4& operator *=(const Mat4& right);
	
	Vec4& operator[](unsigned int rowIndex);

	friend Mat4 operator +(Mat4 left, const Mat4& right);
	friend Mat4 operator -(Mat4 left, const Mat4& right);
	friend Mat4 operator *(Mat4 left, const Mat4& right);

	friend std::ostream& operator<<(std::ostream& os, const Mat4& mat);

};
