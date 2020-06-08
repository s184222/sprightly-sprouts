package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

import java.nio.FloatBuffer;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.Texture;

public class Font {
	
//	private int ascent;
//	private int descent;
//	private int lineGap;
	
	private final float fontSize;
	private final Texture textureAtlas;
	private final STBTTPackedchar.Buffer cdata;
	
	public Font(float fontSize, Texture textureAtlas, STBTTPackedchar.Buffer cdata) {
		this.fontSize = fontSize;
		this.textureAtlas = textureAtlas;
		this.cdata = cdata;
	}
	
	public void drawString (ITessellator2D tessellator, float x, float y, String text) {
		tessellator.setTexture(textureAtlas);
		
		int atlasWidth = textureAtlas.getWidth();
		int atlasHeight = textureAtlas.getHeight();
		
		try (MemoryStack memStack = MemoryStack.stackPush(); STBTTAlignedQuad quad = STBTTAlignedQuad.malloc()) {
			FloatBuffer xptr = memStack.floats(x);
			FloatBuffer yptr = memStack.floats(y);
			
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c >= FontData.FIRST_PRINTABLE_CHARACTER && c <= FontData.LAST_PRINTABLE_CHARACTER) {
					int charIndex = c - FontData.FIRST_PRINTABLE_CHARACTER;
					
					stbtt_GetPackedQuad(cdata, atlasWidth, atlasHeight, charIndex, xptr, yptr, quad, true);
		            
					tessellator.drawTexturedQuad(quad.x0(), quad.y0(), quad.s0(), quad.t0(), 
					                             quad.x1(), quad.y1(), quad.s1(), quad.t1());
				}
			}
		}
	}

	public float getFontSize() {
		return fontSize;
	}
	
	public Texture getTextureAtlas() {
		return textureAtlas;
	}

	public void dispose() {
		textureAtlas.dispose();
		cdata.close();
	}

	/*
    public float getStringWidth(String text, boolean isKerningEnabled) {
        int width = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer pCodePoint       = stack.mallocInt(1);
            IntBuffer pAdvancedWidth   = stack.mallocInt(1);
            IntBuffer pLeftSideBearing = stack.mallocInt(1);

            int i = 0;
            int to = text.length();
            while (i < to) {
                i += getCP(text, to, i, pCodePoint);
                int cp = pCodePoint.get(0);

                stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
                width += pAdvancedWidth.get(0);

                if (isKerningEnabled && i < to) {
                    getCP(text, to, i, pCodePoint);
                    width += stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0));
                }
            }
        }

        return width * stbtt_ScaleForPixelHeight(info, fontHeight);
    }
	
    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }
    */
}
