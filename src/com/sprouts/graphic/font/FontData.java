package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.texture.Texture;

public class FontData {
	
	public static final int FIRST_PRINTABLE_CHARACTER = 0x20; /* SPACE */
	public static final int LAST_PRINTABLE_CHARACTER = 0x7E; /* ~ */
	public static final int NUM_PRINTABLE_CHARACTERS = LAST_PRINTABLE_CHARACTER - FIRST_PRINTABLE_CHARACTER + 1;
	
	private final ByteBuffer ttfData;
	
	public FontData(ByteBuffer ttfData) {
		this.ttfData = ttfData;
	}
	
	public Font createFont(float fontSize) {
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.malloc(NUM_PRINTABLE_CHARACTERS);
		
		Texture textureAtlas = createAtlas(fontSize, cdata);
		
		float ascent = getMaxAscent(cdata);
		float descent = getMaxDescent(cdata);
		
		return new Font(fontSize, ascent, descent, textureAtlas, cdata);
	}
	
	private Texture createAtlas(float fontSize, STBTTPackedchar.Buffer cdata) {
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			int bitmapW = (int) (6 * fontSize);
			int bitmapH = (int) (5 * fontSize);
			
			// Generate a 4-channel bitmap with ARGB
			ByteBuffer bitmap = MemoryUtil.memAlloc(4 * bitmapW * bitmapH);
			
			stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 1, 1);
			stbtt_PackFontRange(pc, ttfData, 0, fontSize, FIRST_PRINTABLE_CHARACTER, cdata);

			resolveBitmap(bitmap, bitmapW, bitmapH);
			
			Texture textureAtlas = new Texture(bitmap, bitmapW, bitmapH, 4);
			
			MemoryUtil.memFree(bitmap);
			
			return textureAtlas;
		}
	}
	
	private void resolveBitmap(ByteBuffer bitmap, int width, int height) {
		// Alpha index is 4 times lower, since bitmap is only single
		// channel. We have to go from high to low indices to ensure
		// that we do not write pixels before we read them.
		int ai = 1 * width * height;
		int pi = 4 * width * height;
		
		while (ai != 0) {
			byte alpha = bitmap.get(--ai);
			
			bitmap.put(--pi, alpha);
			bitmap.put(--pi, (byte)0xFF);
			bitmap.put(--pi, (byte)0xFF);
			bitmap.put(--pi, (byte)0xFF);
		}
	}
	
	private float getMaxAscent(STBTTPackedchar.Buffer cdata) {
		float ascent = 0;
		for(int i = 0; i < NUM_PRINTABLE_CHARACTERS; i++) {
			STBTTPackedchar c = cdata.get(i);
			
			if (c.yoff() < ascent) {
				ascent = c.yoff();
			}
		}
		return ascent;
	}

	private float getMaxDescent(STBTTPackedchar.Buffer cdata) {
		float descent = 0;
		for(int i = 0; i < NUM_PRINTABLE_CHARACTERS; i++) {
			STBTTPackedchar c = cdata.get(i);
			
			if (c.yoff2() > descent) {
				descent = c.yoff2();
			}
		}
		return descent;
	}
	
	/*
	public int getFontHeight() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer bufAscent = stack.ints(0);
			IntBuffer bufDescent = stack.ints(0);
			IntBuffer bufLineGap = stack.ints(0);
			
			stbtt_GetFontVMetrics(info, bufAscent, bufDescent, bufLineGap);
			
			int ascent = bufAscent.get(0);
		    int descent = bufDescent.get(0);
		    int lineGap = bufLineGap.get(0);
		    
		    return ascent - descent + lineGap;
		}
		
	}
	*/
}
