package com.sprouts.graphic.font;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;

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
		
		STBTTAlignedQuad quad  = STBTTAlignedQuad.malloc();
		
		FloatBuffer xb = memAllocFloat(1);
	    FloatBuffer yb = memAllocFloat(1);
	    
        xb.put(0, x);
        yb.put(0, y);
		
		Map<Integer, Integer> chardataIndices = new HashMap<>();

        for(int i = 0 ; i < cdata.remaining() ; i++) {
            chardataIndices.put(i + 32, i);
        }
		
		for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(
                    cdata,
                    (int) (6*fontSize), (int) (5*fontSize),
                    chardataIndices.get((int)text.charAt(i)),
                    xb, yb,
                    quad,
                    true);
            tessellator.drawTexturedQuad(quad.x0(), quad.y0(), quad.s0(), quad.t0(), quad.x1(), quad.y1(), quad.s1(), quad.t1());
		}
		
	}

	public Texture getTextureAtlas() {
		return textureAtlas;
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
