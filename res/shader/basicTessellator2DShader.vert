#version 300 es

in vec3 a_Position;
in vec4 a_Color;
in vec2 a_TexCoord;
in int a_TexIndex;

out vec4 fs_Color;
out vec2 fs_TexCoord;
flat out int fs_TexIndex;

uniform mat4 u_ProjMat;

void main(void) {
	fs_Color = a_Color;
	fs_TexCoord = a_TexCoord;
	fs_TexIndex = a_TexIndex;
	
	gl_Position = u_ProjMat * vec4(a_Position, 1.0);
}
