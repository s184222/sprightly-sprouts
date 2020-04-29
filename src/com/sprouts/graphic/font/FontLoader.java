package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import com.sprouts.util.FileUtil;

public class FontLoader {
	
	public static Font loadFont(String path) throws IOException {
		int ascent;
		int descent;
		int lineGap;
		
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
	    
	
		try (MemoryStack stack = stackPush()) {
			IntBuffer bufAscent = stack.ints(0);
			IntBuffer bufDescent = stack.ints(0);
			IntBuffer bufLineGap = stack.ints(0);
			
			stbtt_GetFontVMetrics(info, bufAscent, bufDescent, bufLineGap);
			
	     
			ascent = bufAscent.get(0);
		    descent = bufDescent.get(0);
		    lineGap = bufLineGap.get(0);
		}
		
		Font font = new Font(ttf, ascent, descent, lineGap);
		
		return null;
	}
}