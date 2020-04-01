package com.sprouts.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

public class TextureLoader {
	private static ByteBuffer image;

	public static Texture loadTexture(String path) {
		int w;
		int h;
		int comp;
		
		try (MemoryStack stack = stackPush()) {
	        IntBuffer wB    = stack.mallocInt(1);
	        IntBuffer hB    = stack.mallocInt(1);
	        IntBuffer compB = stack.mallocInt(1);
	
	        // Decode the image
	        image = stbi_load(path, wB, hB, compB, 0);
	        if (image == null) {
	            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
	        }
	        
	        w = wB.get(0);
	        h = hB.get(0);
	        comp = compB.get(0);
		}
    
	    int texID = glGenTextures();
	
	    glBindTexture(GL_TEXTURE_2D, texID);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	
	    int format;
	    
	    if (comp == 3) {
	        if ((w & 3) != 0) {
	            glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
	        }
	        format = GL_RGB;
	    } else {
	        format = GL_RGBA;
	    }
	
	    glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);
	

	    stbi_image_free(image);

    	return new Texture(texID);
	
	}
}