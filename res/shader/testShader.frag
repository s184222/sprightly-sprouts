#version 300 es

in vec4 fs_color;
in vec2 fs_texCoord;

layout (location = 0) out vec4 fragColor;

uniform sampler2D tex;

void main(void) {
	vec4 texColor = texture(tex, fs_texCoord);

	fragColor = texColor * fs_color;	
}
