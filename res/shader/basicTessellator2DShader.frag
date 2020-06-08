#version 300 es

precision mediump float;

layout (location = 0) out vec4 color;

in vec4 fs_Color;
in vec2 fs_TexCoord;
flat in int fs_TexIndex;

uniform sampler2D u_TextureSamplers[32];

void main(void) {
	vec4 texColor = fs_Color;
	
	switch (fs_TexIndex)
	{
		case  0: texColor *= texture(u_TextureSamplers[ 0], fs_TexCoord); break;
		case  1: texColor *= texture(u_TextureSamplers[ 1], fs_TexCoord); break;
		case  2: texColor *= texture(u_TextureSamplers[ 2], fs_TexCoord); break;
		case  3: texColor *= texture(u_TextureSamplers[ 3], fs_TexCoord); break;
		case  4: texColor *= texture(u_TextureSamplers[ 4], fs_TexCoord); break;
		case  5: texColor *= texture(u_TextureSamplers[ 5], fs_TexCoord); break;
		case  6: texColor *= texture(u_TextureSamplers[ 6], fs_TexCoord); break;
		case  7: texColor *= texture(u_TextureSamplers[ 7], fs_TexCoord); break;
		case  8: texColor *= texture(u_TextureSamplers[ 8], fs_TexCoord); break;
		case  9: texColor *= texture(u_TextureSamplers[ 9], fs_TexCoord); break;
		case 10: texColor *= texture(u_TextureSamplers[10], fs_TexCoord); break;
		case 11: texColor *= texture(u_TextureSamplers[11], fs_TexCoord); break;
		case 12: texColor *= texture(u_TextureSamplers[12], fs_TexCoord); break;
		case 13: texColor *= texture(u_TextureSamplers[13], fs_TexCoord); break;
		case 14: texColor *= texture(u_TextureSamplers[14], fs_TexCoord); break;
		case 15: texColor *= texture(u_TextureSamplers[15], fs_TexCoord); break;
		case 16: texColor *= texture(u_TextureSamplers[16], fs_TexCoord); break;
		case 17: texColor *= texture(u_TextureSamplers[17], fs_TexCoord); break;
		case 18: texColor *= texture(u_TextureSamplers[18], fs_TexCoord); break;
		case 19: texColor *= texture(u_TextureSamplers[19], fs_TexCoord); break;
		case 20: texColor *= texture(u_TextureSamplers[20], fs_TexCoord); break;
		case 21: texColor *= texture(u_TextureSamplers[21], fs_TexCoord); break;
		case 22: texColor *= texture(u_TextureSamplers[22], fs_TexCoord); break;
		case 23: texColor *= texture(u_TextureSamplers[23], fs_TexCoord); break;
		case 24: texColor *= texture(u_TextureSamplers[24], fs_TexCoord); break;
		case 25: texColor *= texture(u_TextureSamplers[25], fs_TexCoord); break;
		case 26: texColor *= texture(u_TextureSamplers[26], fs_TexCoord); break;
		case 27: texColor *= texture(u_TextureSamplers[27], fs_TexCoord); break;
		case 28: texColor *= texture(u_TextureSamplers[28], fs_TexCoord); break;
		case 29: texColor *= texture(u_TextureSamplers[29], fs_TexCoord); break;
		case 30: texColor *= texture(u_TextureSamplers[30], fs_TexCoord); break;
		case 31: texColor *= texture(u_TextureSamplers[31], fs_TexCoord); break;
	}

	color = texColor;
}
