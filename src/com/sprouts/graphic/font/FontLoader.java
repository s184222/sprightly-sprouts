package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBTTFontinfo;

import com.sprouts.util.FileUtil;

public class FontLoader {
	
	public static FontData loadFont(String path) throws IOException {
		
	    ByteBuffer ttf;

	    STBTTFontinfo info;
	    
	    InputStream is = FontLoader.class.getResourceAsStream(path);
	    if (is == null)
			throw new IOException("Unable to find image: " + path);
	    
	    try {
			ttf = FileUtil.readAllBytes(is);
		} catch (IOException e) {
			throw new IOException("Failed to load image.", e);
		}
	    
        info = STBTTFontinfo.create();
        
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }
	
		return new FontData(ttf, info);
	}
}