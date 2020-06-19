#version 300 es

precision mediump float;

layout (location = 0) out vec4 color;

in vec2 fs_BlurTexCoords[11];

uniform sampler2D u_TextureSampler;

void main(void) {
	color = vec4(0.0);
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 0]) * 0.0093;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 1]) * 0.028002;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 2]) * 0.065984;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 3]) * 0.121703;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 4]) * 0.175713;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 5]) * 0.198596;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 6]) * 0.175713;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 7]) * 0.121703;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 8]) * 0.065984;
	color += texture(u_TextureSampler, fs_BlurTexCoords[ 9]) * 0.028002;
	color += texture(u_TextureSampler, fs_BlurTexCoords[10]) * 0.0093;
}
