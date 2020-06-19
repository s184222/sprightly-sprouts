#version 300 es

precision mediump float;

layout (location = 0) out vec4 color;

in vec2 fs_TexCoord;
in vec3 fs_NormalCoord;

uniform sampler2D u_TextureSampler;

void main(void) {
	color = texture(u_TextureSampler, vec2(fs_TexCoord.x, 1.0 - fs_TexCoord.y));
}
