package com.sprouts.graphic.font;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;

import com.sprouts.graphic.texture.InvalidFormatException;

public class Font {
	private final int fontId;
	private int ascent;
	private int descent;
	private int lineGap;
    STBTTBakedChar.Buffer charData = STBTTBakedChar.malloc(96);
	
	private final int DEFAULT_BITMAP_W = 512;
	private final int DEFAULT_BITMAP_H = 512;
	private final int DEFUALT_FONT_HEIGHT = 30;
	

	public Font() {
		fontId =  GL11.glGenTextures();
		
		bind();
		
		// Use nearest neighbor for scaling
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		// Clamp texture when wrapped
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		unbind();
	}
	
	public Font(ByteBuffer ttf, int ascent, int descent, int lineGap) {
		this();
		
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
			
		try {
			setFontData(ttf, DEFAULT_BITMAP_W, DEFAULT_BITMAP_H, DEFUALT_FONT_HEIGHT);	
		} catch (InvalidFormatException e) {
			dispose();
			throw e;
		}
		
		
	}

	
	public void setFontData(ByteBuffer ttf, int bitmap_w, int bitmap_h, int fontHeight) throws InvalidFormatException {

        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmap_w * bitmap_h);
        stbtt_BakeFontBitmap(ttf, fontHeight, bitmap, bitmap_w, bitmap_h, 32, charData);
		
		bind();
		glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, bitmap_w, bitmap_h, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
		unbind();
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontId);
	}

	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void dispose() {
		GL11.glDeleteTextures(fontId);
	}

	public int getAscent() {
		return ascent;
	}

	public int getDescent() {
		return descent;
	}

	public int getLineGap() {
		return lineGap;
	}
	
	public STBTTBakedChar.Buffer getCharData() {
		return charData;
	}
}
