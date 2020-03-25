#version 330 core

in vec3 position;
in vec3 color;

out DATA {
	vec3 color;
} vs_out;

uniform mat4 proj_mat;
uniform mat4 view_mat;
uniform mat4 modl_mat;

void main(void) {
	vs_out.color = color;
	
	gl_Position = proj_mat * view_mat * modl_mat * vec4(position, 1.0);
}