#version 300 es

in vec3 fs_color;

layout (location = 0) out vec4 fragColor;

void main(void) {
	fragColor = vec4(fs_color, 1.0);
}
