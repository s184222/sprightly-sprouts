package com.sprouts.texture;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static java.lang.Math.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TextureLoader {
	private ByteBuffer image;

	private int w;
	private int h;
	private int comp;

	public Texture loadTexture(String path) {
		try (MemoryStack stack = stackPush()) {
	        IntBuffer w    = stack.mallocInt(1);
	        IntBuffer h    = stack.mallocInt(1);
	        IntBuffer comp = stack.mallocInt(1);
	
	        // Decode the image
	        image = stbi_load(path, w, h, comp, 0);
	        if (image == null) {
	            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
	        }
	        
	        this.w = w.get(0);
	        this.h = h.get(0);
	        this.comp = comp.get(0);
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
        premultiplyAlpha();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        format = GL_RGBA;
    }

    glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);

    ByteBuffer input_pixels = image;
    int        input_w      = w;
    int        input_h      = h;
    int        mipmapLevel  = 0;
    while (1 < input_w || 1 < input_h) {
        int output_w = Math.max(1, input_w >> 1);
        int output_h = Math.max(1, input_h >> 1);

        ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
        stbir_resize_uint8_generic(
            input_pixels, input_w, input_h, input_w * comp,
            output_pixels, output_w, output_h, output_w * comp,
            comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
            STBIR_EDGE_CLAMP,
            STBIR_FILTER_MITCHELL,
            STBIR_COLORSPACE_SRGB
        );

        if (mipmapLevel == 0) {
            stbi_image_free(image);
        } else {
            memFree(input_pixels);
        }

        glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

        input_pixels = output_pixels;
        input_w = output_w;
        input_h = output_h;
    }
    if (mipmapLevel == 0) {
        stbi_image_free(image);
    } else {
        memFree(input_pixels);
    }

    return new Texture(texID);
	
}
    private void premultiplyAlpha() {
        int stride = w * 4;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = y * stride + x * 4;

                float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
                image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
                image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
            }
        }
    }
}