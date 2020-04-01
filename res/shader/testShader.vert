#version 300 es

in vec3 position;
in vec4 color;
in vec2 texCoord;

out vec4 fs_color;
out vec2 fs_texCoord;

uniform mat4 proj_mat;
uniform mat4 view_mat;
uniform mat4 modl_mat;

void main(void) {
	fs_color = color;
	fs_texCoord = texCoord;
	
	gl_Position = proj_mat * view_mat * modl_mat * vec4(position, 1.0);
}
