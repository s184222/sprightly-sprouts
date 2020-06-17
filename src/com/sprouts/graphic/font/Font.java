package com.sprouts.graphic.font;

import static org.lwjgl.stb.STBTruetype.stbtt_GetPackedQuad;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import com.sprouts.IResource;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.Texture;

public class Font implements IResource {

	private final float fontSize;
	
	private final float ascent;
	private final float descent;
	private final float lineGap;
	
	private final Texture textureAtlas;
	private final STBTTPackedchar.Buffer cdata;

	public Font(float fontSize, float ascent, float descent, float lineGap, Texture textureAtlas, STBTTPackedchar.Buffer cdata) {
		this.fontSize = fontSize;
	
		this.ascent = ascent;
		this.descent = descent;
		this.lineGap = lineGap;
		
		this.textureAtlas = textureAtlas;
		this.cdata = cdata;
	}

	public void drawString(ITessellator2D tessellator, String text, float x, float y) {
		tessellator.setTextureRegion(textureAtlas);

		int atlasWidth = textureAtlas.getWidth();
		int atlasHeight = textureAtlas.getHeight();

		int charIndex;
		
		try (MemoryStack memStack = MemoryStack.stackPush(); STBTTAlignedQuad quad = STBTTAlignedQuad.malloc()) {
			FloatBuffer xptr = memStack.floats(x);
			FloatBuffer yptr = memStack.floats(y);

			for (int i = 0; i < text.length(); i++) {
					
				if ((charIndex = getCharIndex(text.charAt(i))) != -1) {
					
					stbtt_GetPackedQuad(cdata, atlasWidth, atlasHeight, charIndex, xptr, yptr, quad, true);
					
					tessellator.drawQuadRegion(quad.x0(), quad.y0(), quad.s0(), quad.t0(),
							quad.x1(), quad.y1(), quad.s1(), quad.t1());
				}

			}

		}
	}
	
	public String trimText(String text, float width, String ellipses) {
		if(text.isEmpty() || getStringWidth(text) <= width) {
			return text;
		}
		
		String output = ellipses;
		
		String nextOutput;
		
		for (int i = 0; i < text.length(); i++) {
			nextOutput = text.substring(0, i) + ellipses;
			if (getStringWidth(nextOutput) > width) {
				return output;
			}
			output = nextOutput;
		}
		return text;
	}
	
	public List<String> getWrappedString(ITessellator2D tessellator, float x, float y, float width, String text) {
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
		
		return strings;



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
		float width = getStringWidth(text);
		
		return new TextBounds(0, -ascent, width, getFontHeight());
	}

	public float getStringWidth(String text) {
		float width = 0;
		
		int charIndex;
		
		for (int i = 0; i < text.length(); i++) {

			if ((charIndex = getCharIndex(text.charAt(i))) != -1) {
				
				STBTTPackedchar c = cdata.get(charIndex);
				
				width += c.xadvance();
				
			}
		}
		
		return width;
	}

	public float getCharWidth(char c) {
		
		int charIndex;
		
		if ((charIndex = getCharIndex(c)) != -1) {
			
			STBTTPackedchar pChar = cdata.get(charIndex);
			
			return pChar.xadvance();
		}
		
		return 0;
	}
	
	private int getCharIndex(char c) {
		if (c >= FontData.FIRST_PRINTABLE_CHARACTER && c <= FontData.LAST_PRINTABLE_CHARACTER) {
			return c - FontData.FIRST_PRINTABLE_CHARACTER;
		}
		return -1;
	}
	
	public float getFontSize() {
		return fontSize;
	}
	
	public float getFontHeight() {
		return getFontSize();
	}
	
	public float getLineHeight() {
		return (ascent + descent + lineGap);
	}
	
	public float getAscent() {
		return ascent;
	}

	public float getDescent() {
		return descent;
	}
	
	public float getLineGap() {
		return lineGap;
	}

	public Texture getTextureAtlas() {
		return textureAtlas;
	}
	
	@Override
	public void dispose() {
		textureAtlas.dispose();
		
		cdata.close();
	}
}
