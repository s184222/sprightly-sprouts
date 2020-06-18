#version 300 es

precision mediump float;

layout (location = 0) out vec4 color;

in vec2 fs_TexCoord;
in vec3 fs_NormalCoord;


uniform sampler2D u_TextureSampler;

void main(void) {
	vec4 texColor = vec4(1.0);
	
	texColor = texture(u_TextureSampler, fs_TexCoord);
		
	color = vec4(1.0, 0.0, 0.0, 1.0);
}
