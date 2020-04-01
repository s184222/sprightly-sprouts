#version 300 es

in vec4 fs_color;

layout (location = 0) out vec4 fragColor;

void main(void) {
	fragColor = fs_color;
}
