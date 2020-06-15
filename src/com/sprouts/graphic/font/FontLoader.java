package com.sprouts.graphic.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.sprouts.util.FileUtil;

public class FontLoader {
	
	public static FontData loadFont(String path) throws IOException {
	    InputStream is = FontLoader.class.getResourceAsStream(path);
	    if (is == null)
			throw new IOException("Unable to find font: " + path);
	    
	    ByteBuffer ttf;
	    
	    try {
			ttf = FileUtil.readAllBytes(is);
		} catch (IOException e) {
			throw new IOException("Failed to load font.", e);
		}
	
		return new FontData(ttf);
	}
}