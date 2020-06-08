package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import com.sprouts.graphic.texture.Texture;

public class Font {
	private int ascent;
	private int descent;
	private int lineGap;
	STBTTFontinfo info;
	ByteBuffer bitmap;
	ByteBuffer tempBitmap;
	ByteBuffer ttf;
	STBTTPackedchar.Buffer cdata;
	
	public Font(int ascent, int descent, int lineGap, STBTTFontinfo info, ByteBuffer ttf) {
		
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
		this.info = info;
		this.ttf = ttf;
	}

	
	public Texture createFontAltlas(float fontSize) {
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			int bitmapW = (int) (12*fontSize);
			int bitmapH = (int) (8*fontSize);
			
			cdata = STBTTPackedchar.malloc(96);
			
			bitmap = BufferUtils.createByteBuffer(bitmapW*bitmapH);
			
			stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);

	        stbtt_PackSetOversampling(pc, 1, 1);

	        stbtt_PackFontRange(pc, ttf, 0, fontSize, 32, cdata);
			
			Texture texture = new Texture(bitmap, bitmapW, bitmapH, 1);
			
			return texture;		
		}
		
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
