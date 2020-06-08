#version 300 es

in vec3 a_Position;
in vec4 a_Color;
in vec2 a_TexCoord;
in float a_TexIndex;

out vec4 fs_Color;
out vec2 fs_TexCoord;
out float fs_TexIndex;

uniform mat4 u_ProjMat;

void main(void) {
	fs_Color = a_Color;
	fs_TexCoord = a_TexCoord;
	
	// Add 0.5 to ensure we have no rounding errors
	fs_TexIndex = a_TexIndex + 0.5;
	
	gl_Position = u_ProjMat * vec4(a_Position, 1.0);
}
