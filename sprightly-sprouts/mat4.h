#pragma once

#define MAT4_NUM_ELEMENTS (4 * 4)

class Mat4
{

public:
	union {
		/* All matrix elements in row major order */
		float arr[MAT4_NUM_ELEMENTS];

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
	Mat4(float d);
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

};
