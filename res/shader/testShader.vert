#version 300 es

in vec3 position;
in vec3 color;

out vec3 fs_color;

uniform mat4 proj_mat;
uniform mat4 view_mat;
uniform mat4 modl_mat;

void main(void) {
	fs_color = color;
	
	gl_Position = proj_mat * view_mat * modl_mat * vec4(position, 1.0);
}
