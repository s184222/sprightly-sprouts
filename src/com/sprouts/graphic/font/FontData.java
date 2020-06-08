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
        
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			int bitmapW = (int) (6 * fontSize);
			int bitmapH = (int) (5 * fontSize);
			
			ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapW * bitmapH);
			
			stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);
			stbtt_PackSetOversampling(pc, 1, 1);
			stbtt_PackFontRange(pc, ttfData, 0, fontSize, FIRST_PRINTABLE_CHARACTER, cdata);
			
			Texture textureAtlas = new Texture(bitmap, bitmapW, bitmapH, 1);
			
			MemoryUtil.memFree(bitmap);
			
			return new Font(fontSize, textureAtlas, cdata);
		}
	}
}
