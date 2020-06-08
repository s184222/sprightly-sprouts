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
	
	private final ByteBuffer ttf;
	
	public FontData(ByteBuffer ttf) {
		this.ttf = ttf;
	}
	
	public Font createFont(float fontSize) {
		
		STBTTPackedchar.Buffer cdata = STBTTPackedchar.malloc(96);
        
		try (STBTTPackContext pc = STBTTPackContext.malloc()) {
			int bitmapW = (int) (6*fontSize);
			int bitmapH = (int) (5*fontSize);
			
			
			ByteBuffer bitmap = MemoryUtil.memAlloc(bitmapW*bitmapH);
			
			stbtt_PackBegin(pc, bitmap, bitmapW, bitmapH, 0, 1, NULL);
			
			stbtt_PackSetOversampling(pc, 1, 1);
			
			stbtt_PackFontRange(pc, ttf, 0, fontSize, 32, cdata);
			
			Texture textureAtlas = new Texture(bitmap, bitmapW, bitmapH, 1);
			
			MemoryUtil.memFree(bitmap);
			
			return new Font(fontSize, textureAtlas, cdata);	
		}
	
		
		
	}
	

}
