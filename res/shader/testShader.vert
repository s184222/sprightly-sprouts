#version 330 core

in vec3 position;

out DATA {
	vec3 color;
} vs_out;

void main(void) {
	vs_out.color = vec3(position.x + 0.5, 0.0, position.y + 0.5);
	gl_Position = vec4(position, 1.0);
}