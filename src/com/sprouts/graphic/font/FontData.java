package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_PackBegin;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;
import static org.lwjgl.stb.STBTruetype.stbtt_PackSetOversampling;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.texture.Texture;

public class FontData {
	
	public static final int FIRST_PRINTABLE_CHARACTER = 0x20; /* SPACE */
	public static final int LAST_PRINTABLE_CHARACTER = 0x7E; /* ~ */
	public static final int NUM_PRINTABLE_CHARACTERS = LAST_PRINTABLE_CHARACTER - FIRST_PRINTABLE_CHARACTER + 1;
	
	private final ByteBuffer ttfData;
    private final STBTTFontinfo info;
	
	public FontData(ByteBuffer ttfData, STBTTFontinfo info) {
		this.ttfData = ttfData;
		this.info = info;
	}
	
	public Font createFont(float fontSize) {
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.malloc(NUM_PRINTABLE_CHARACTERS);
			
			Texture textureAtlas = createAtlas(fontSize, cdata);
			
			return new Font(fontSize, textureAtlas, cdata, info);
	}
	
	private Texture createAtlas(float fontSize, STBTTPackedchar.Buffer cdata) {
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			int bitmapW = (int) (6 * fontSize);
			int bitmapH = (int) (5 * fontSize);
			
			ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapW * bitmapH);
			
			stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 1, 1);
			stbtt_PackFontRange(pc, ttfData, 0, fontSize, FIRST_PRINTABLE_CHARACTER, cdata);
			
			Texture textureAtlas = new Texture(bitmap, bitmapW, bitmapH, 1);
			
			MemoryUtil.memFree(bitmap);
			
			return textureAtlas;
		}
		
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
