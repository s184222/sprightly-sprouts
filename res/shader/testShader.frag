#version 330

layout (location = 0) out vec4 frag_color;

in DATA {
	vec3 color;
} fs_in;

void main(void) {
	frag_color = vec4(fs_in.color, 1.0);
}