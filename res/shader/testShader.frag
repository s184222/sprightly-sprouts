#version 330 core

layout (location = 0) out vec4 frag_color;

in DATA {
	vec3 color;
	vec3 frag_pos;
} fs_in;

void main(void) {
	if (length(fs_in.frag_pos) > sqrt(2.0)) {
		frag_color = vec4(0.0, 0.0, 0.0, 1.0);
	} else {
		frag_color = vec4(fs_in.color, 1.0);
	}
}