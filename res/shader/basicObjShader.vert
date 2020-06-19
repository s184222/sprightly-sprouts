#version 300 es

in vec3 a_Position;
in vec2 a_TexCoord;
in vec3 a_NormalCoord;

out vec2 fs_TexCoord;
out vec3 fs_NormalCoord;

uniform mat4 u_ProjMat;
uniform mat4 u_ViewMat;
uniform mat4 u_ModlMat;

void main(void) {
	fs_TexCoord = a_TexCoord;
	fs_NormalCoord = a_NormalCoord;
	
	gl_Position = u_ProjMat * u_ViewMat * u_ModlMat * vec4(a_Position, 1.0);
}