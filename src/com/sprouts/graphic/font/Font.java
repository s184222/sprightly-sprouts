package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_GetFontVMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetCodepointBox;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphBox;
import static org.lwjgl.stb.STBTruetype.stbtt_GetGlyphHMetrics;
import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.Texture;

public class Font {

	private final float fontSize;
	private final Texture textureAtlas;
	private final STBTTPackedchar.Buffer cdata;
	private final STBTTFontinfo info;

	public Font(float fontSize, Texture textureAtlas, STBTTPackedchar.Buffer cdata, STBTTFontinfo info) {
		this.fontSize = fontSize;
		this.textureAtlas = textureAtlas;
		this.cdata = cdata;
		this.info = info;
	}

	public void drawString(ITessellator2D tessellator, float x, float y, String text) {
		tessellator.setTextureRegion(textureAtlas);

		int atlasWidth = textureAtlas.getWidth();
		int atlasHeight = textureAtlas.getHeight();

		try (MemoryStack memStack = MemoryStack.stackPush(); STBTTAlignedQuad quad = STBTTAlignedQuad.malloc()) {
			FloatBuffer xptr = memStack.floats(x);
			FloatBuffer yptr = memStack.floats(y);

			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (c >= FontData.FIRST_PRINTABLE_CHARACTER && c <= FontData.LAST_PRINTABLE_CHARACTER) {
					int charIndex = c - FontData.FIRST_PRINTABLE_CHARACTER;

					stbtt_GetPackedQuad(cdata, atlasWidth, atlasHeight, charIndex, xptr, yptr, quad, true);

					tessellator.drawQuadRegion(quad.x0(), quad.y0(), quad.s0(), quad.t0(),
					                           quad.x1(), quad.y1(), quad.s1(), quad.t1());
				}
			}

		}
	}
	
	public String trimText(String text, float width, String ellipses) {
		String output;
		for (int i = 0; i < text.length(); i++) {
			output = text.substring(0, i) + ellipses;
			if (getStringWidth(text.substring(0, i+1) + ellipses) > width) {
				return output;
			}
		}
		return "";
	}
	//TODO: Fix the error
	
	
	public void drawWrappedString(ITessellator2D tessellator, float x, float y, float width, String text) {
		tessellator.setTextureRegion(textureAtlas);

		String[] words = text.split(" ");
		
		ArrayList<String> strings = new ArrayList<>();
		
		String temp = words[0];
		
		for(int i = 1; i < words.length; i++) {
			
			if (getStringWidth(words[i]) > width) {
				strings.add(temp);
				ArrayList<String> splitWords = splitWord(words[i], width);
				for (int j = 0; j < splitWords.size()-1; j++) {
					strings.add(splitWords.get(j));
				}
				temp = splitWords.get(splitWords.size()-1);
				continue;
			}
			
			if (getStringWidth(temp + " " + words[i]) > width) {
				strings.add(temp);
				temp = words[i];
				continue;
			}
			
			temp += " " + words[i];
		}
		
		strings.add(temp);

		try (MemoryStack memStack = MemoryStack.stackPush(); STBTTAlignedQuad quad = STBTTAlignedQuad.malloc()) {
			IntBuffer bufAscent = memStack.ints(0);
			IntBuffer bufDescent = memStack.ints(0);
			IntBuffer bufLineGap = memStack.ints(0);

			stbtt_GetFontVMetrics(info, bufAscent, bufDescent, bufLineGap);

			int lineJump = (int) ((bufAscent.get(0) - bufDescent.get(0) + bufLineGap.get(0))
					* stbtt_ScaleForPixelHeight(info, fontSize));
			int lineNumber = 0;
			for(String s : strings) {
				drawString(tessellator, x, y + (lineJump * lineNumber), s);
				lineNumber++;
			}
		}

	}
	
	private ArrayList<String> splitWord(String word, float width) {
		
		String temp = "";
		ArrayList<String> wordSplit = new ArrayList<String>();
		
		for(int i = 0; i < word.length(); i++) {
			if (getStringWidth(temp + word.charAt(i) + "-") > width) {
				temp += "-";
				wordSplit.add(temp);
				temp = "";
			}
			temp += word.charAt(i);
		}
		wordSplit.add(temp);
		return wordSplit;
	}
	
	public TextBounds getTextBounds(String text) {
		float x = 0, ascent = 0, descent = 0;

		try (MemoryStack memStack = MemoryStack.stackPush()) {
			IntBuffer x0buf = memStack.ints(0);
			IntBuffer y0buf = memStack.ints(0);
			IntBuffer x1buf = memStack.ints(0);
			IntBuffer y1buf = memStack.ints(0);

			for (int i = 0; i < text.length(); i++) {
				stbtt_GetCodepointBox(info, text.charAt(i), x0buf, y0buf, x1buf, y1buf);

				if (i == 0) {
					x = -x0buf.get(0);
				}
				
				float y1 = y1buf.get(0);
				if (y1 > ascent) {
					ascent = y1;
				}
				
				float y0 = y0buf.get(0);
				if (y0 < descent) {
					descent = y0;
				}
			}

			float width = getStringWidth(text);
			float scale = stbtt_ScaleForPixelHeight(info, fontSize);

			return new TextBounds(x * scale, ascent * scale, width, (ascent - descent) * scale);
		}
	}

	public float getFontSize() {
		return fontSize;
	}

	public Texture getTextureAtlas() {
		return textureAtlas;
	}

	public void dispose() {
		textureAtlas.dispose();
		cdata.close();
	}

	public float getStringWidth(String text) {
		int width = 0;
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pAdvancedWidth = stack.mallocInt(1);
			IntBuffer pLeftSideBearing = stack.mallocInt(1);

			for (int i = 0; i < text.length(); i++) {
				int charIndex = stbtt_FindGlyphIndex(info, text.charAt(i));
				
				if (charIndex != 0) {
					if (i == text.length() - 1) {
						width += getCharWidth(charIndex);
					} else {
						stbtt_GetGlyphHMetrics(info, charIndex, pAdvancedWidth, pLeftSideBearing);
	
						width += pAdvancedWidth.get(0);
	
						if (i == 0) {
							width -= pLeftSideBearing.get(0);
						}
					}
				}
			}
			
			return width * stbtt_ScaleForPixelHeight(info, fontSize);
		}
	}

	private float getCharWidth(int charIndex) {
		float width = 0;

		try (MemoryStack memStack = MemoryStack.stackPush()) {
			IntBuffer x0buf = memStack.ints(0);
			IntBuffer x1buf = memStack.ints(0);

			stbtt_GetGlyphBox(info, charIndex, x0buf, null, x1buf, null);

			width = x1buf.get(0);
		}

		return width;
	}
}
