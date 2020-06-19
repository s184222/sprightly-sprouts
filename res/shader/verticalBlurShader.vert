#version 300 es

in vec2 a_Position;

out vec2 fs_BlurTexCoords[11];

uniform float u_TargetSize;

void main(void) {
	vec2 texCoord = a_Position * 0.5 + 0.5;
	float texelSize = 1.0 / u_TargetSize;
	
	fs_BlurTexCoords[ 0] = texCoord + vec2(0.0, -5.0 * texelSize);
	fs_BlurTexCoords[ 1] = texCoord + vec2(0.0, -4.0 * texelSize);
	fs_BlurTexCoords[ 2] = texCoord + vec2(0.0, -3.0 * texelSize);
	fs_BlurTexCoords[ 3] = texCoord + vec2(0.0, -2.0 * texelSize);
	fs_BlurTexCoords[ 4] = texCoord + vec2(0.0, -1.0 * texelSize);
	fs_BlurTexCoords[ 5] = texCoord + vec2(0.0,  0.0            );
	fs_BlurTexCoords[ 6] = texCoord + vec2(0.0,  1.0 * texelSize);
	fs_BlurTexCoords[ 7] = texCoord + vec2(0.0,  2.0 * texelSize);
	fs_BlurTexCoords[ 8] = texCoord + vec2(0.0,  3.0 * texelSize);
	fs_BlurTexCoords[ 9] = texCoord + vec2(0.0,  4.0 * texelSize);
	fs_BlurTexCoords[10] = texCoord + vec2(0.0,  5.0 * texelSize);
	
	gl_Position = vec4(a_Position, 0.0, 1.0);
}
