package com.sprouts.graphic.tessellator2d;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator2d.clip.ClipPlane;
import com.sprouts.graphic.tessellator2d.clip.ClipRect;
import com.sprouts.graphic.tessellator2d.clip.ClipShape;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.math.Mat3;
import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;

public abstract class AbstractTessellator2D implements ITessellator2D, AutoCloseable {

	private static final int WHITE_TEXTURE_INDEX = Byte.MIN_VALUE;
	private static final int MAX_TEXTURE_COUNT = 32;
	private static final int BATCH_VERTEX_COUNT = 3 * 200; /* 200 triangles */
	
	private static final int MAX_CLIPPED_CACHE_SIZE = 32;
	
	private static final float PROJ_NEAR = -100.0f;
	private static final float PROJ_FAR  =  100.0f;
	
	protected final Tessellator2DShader shader;
		
	protected final VertexArray vertexArray;
	protected final VertexBuffer vertexBuffer;
	
	protected ColorGradient2D colorGradient;
	protected int currentTextureIndex;

	protected List<Texture> activeTextures;

	protected final Mat3 transform;
	protected float zOffset;
	
	protected ClipShape clipShape;
	protected ClippedTriangle[] triangleCache;
	
	public AbstractTessellator2D(Tessellator2DShader shader) {
		this.shader = shader;
		
		vertexArray = new VertexArray();
		vertexBuffer = new VertexBuffer(shader.getVertexByteSize(), BATCH_VERTEX_COUNT);
		shader.initBuffers(vertexArray, vertexBuffer);
		
		colorGradient = new ConstantColorGradient2D(VertexColor.BLACK);
		currentTextureIndex = WHITE_TEXTURE_INDEX;
	
		activeTextures = new ArrayList<Texture>();

		transform = new Mat3();
		zOffset = 0.0f;

		clipShape = null;
		triangleCache = new ClippedTriangle[0];
		
		initShaderTextureSamplers();
	}

	private void initShaderTextureSamplers() {
		int[] textureSamplers = new int[MAX_TEXTURE_COUNT];
		for (int i = 0; i < MAX_TEXTURE_COUNT; i++)
			textureSamplers[i] = i;
		
		shader.enable();
		shader.setTextureSamplers(textureSamplers);
	}
	
	protected void drawBuffer(int vertexCount) {
		shader.enable();
		bindActiveTextures();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		vertexArray.bind();
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glDisable(GL11.GL_BLEND);
	}
	
	protected void bindActiveTextures() {
		for (int i = 0; i < activeTextures.size(); i++) {
			Texture activeTexture = activeTextures.get(i);

			glActiveTexture(GL_TEXTURE0 + i);
			activeTexture.bind();
		}
	}
	
	@Override
	public void setViewport(float x0, float y0, float x1, float y1) {
		shader.enable();
		shader.setProjMat(new Mat4().toOrthographic(x0, x1, y0, y1, PROJ_NEAR, PROJ_FAR));
	}

	@Override
	public void drawQuad(float x0, float y0, float x1, float y1) {
		drawTexturedQuadImpl(x0, y0, 0.0f, 0.0f, x1, y1, 0.0f, 0.0f, WHITE_TEXTURE_INDEX);
	}

	@Override
	public void drawTexturedQuad(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1) {
		drawTexturedQuadImpl(x0, y0, u0, v0, x1, y1, u1, v1, currentTextureIndex);
	}
	
	private void drawTexturedQuadImpl(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1, int textureIndex) {
		Vec2 c0 = transform.mul(new Vec2(x0, y0)); // Top-left
		Vec2 c1 = transform.mul(new Vec2(x0, y1)); // Bottom-left
		Vec2 c2 = transform.mul(new Vec2(x1, y1)); // Bottom-right
		Vec2 c3 = transform.mul(new Vec2(x1, y0)); // Top-right
		
		drawTriangleNoTransform(c0.x, c0.y, u0, v0, c1.x, c1.y, u0, v1, c3.x, c3.y, u1, v0, (byte)textureIndex);
		drawTriangleNoTransform(c2.x, c2.y, u1, v1, c3.x, c3.y, u1, v0, c1.x, c1.y, u0, v1, (byte)textureIndex);
	}
	
	private void drawTriangleNoTransform(float x0, float y0, float u0, float v0, 
	                                     float x1, float y1, float u1, float v1, 
	                                     float x2, float y2, float u2, float v2,
	                                     byte textureIndex) {

		if (clipShape != null && clipShape.getPlaneCount() > 0) {
			// Note: we need an extra triangle for storing
			//       the initial triangle when drawing.
			ensureCacheSize(clipShape.getPlaneCount() + 1);
			
			triangleCache[0].set(x0, y0, u0, v0, x1, y1, u1, v1, x2, y2, u2, v2);
			clipAndTessellate(clipShape.getPlanes(), 0, textureIndex);
		} else {
			// In this case we can tessellate the triangle immediately
			// and skip the expensive clipping routine.
			tessellateTriangle(x0, y0, u0, v0, x1, y1, u1, v1, x2, y2, u2, v2, textureIndex);
		}
	}
	
	private void ensureCacheSize(int cacheSize) {
		int currentSize = triangleCache.length;
		
		// In case the current cache size exceeds the maximum it should
		// be the exact cache size that we are looking for.
		if (currentSize >  MAX_CLIPPED_CACHE_SIZE && currentSize == cacheSize)
			return;
		if (currentSize <= MAX_CLIPPED_CACHE_SIZE && currentSize >= cacheSize)
			return;
		
		triangleCache = Arrays.copyOf(triangleCache, cacheSize);
		for (int i = currentSize; i < cacheSize; i++)
			triangleCache[i] = new ClippedTriangle();
	}
	
	private void clipAndTessellate(ClipPlane[] planes, int planeIndex, byte textureIndex) {
		ClippedTriangle t = triangleCache[planeIndex];
		
		if (planeIndex >= planes.length) {
			tessellateTriangle(t.x0, t.y0, t.u0, t.v0,
			                   t.x1, t.y1, t.u1, t.v1,
			                   t.x2, t.y2, t.u2, t.v2,
			                   textureIndex);
		} else {
			ClipPlane plane = planes[planeIndex];
			if (plane.contains(t.x0, t.y0, zOffset)) {
				if (plane.contains(t.x1, t.y1, zOffset)) {
					if (plane.contains(t.x2, t.y2, zOffset)) {
						triangleCache[planeIndex + 1].set(t);
						clipAndTessellate(planes, planeIndex + 1, textureIndex);
					} else {
						// TODO: make this work, I dunno how yet....
					}
				} else if (plane.contains(t.x2, t.y2, zOffset)) {
					
				} else {
					
				}
			} else if (plane.contains(t.x1, t.y1, zOffset)) {
				if (plane.contains(t.x2, t.y2, zOffset)) {
					
				} else {
					
				}
			} else if (plane.contains(t.x2, t.y2, zOffset)) {
				
			}
		}
	}
	
	protected void tessellateTriangle(float x0, float y0, float u0, float v0, 
	                                  float x1, float y1, float u1, float v1, 
	                                  float x2, float y2, float u2, float v2,
	                                  byte textureIndex) {
		
		tessellateVertex(x0, y0, u0, v0, textureIndex);
		tessellateVertex(x1, y1, u1, v1, textureIndex);
		tessellateVertex(x2, y2, u2, v2, textureIndex);
	}
	
	protected void tessellateVertex(float x, float y, float u, float v, byte textureIndex) {
		VertexAttribBuilder builder = getBuilder();
		
		builder.put(x, y, zOffset);
		
		VertexColor color = colorGradient.getColor(x, y, transform);
		builder.put((byte)color.getRed());
		builder.put((byte)color.getGreen());
		builder.put((byte)color.getBlue());
		builder.put((byte)color.getAlpha());
		
		builder.put(u, v);
		
		builder.put(textureIndex);
	}
	
	protected abstract VertexAttribBuilder getBuilder();
	
	protected void clearTextures() {
		activeTextures.clear();
		currentTextureIndex = WHITE_TEXTURE_INDEX;
	}

	protected void clearColor() {
		setColor(VertexColor.BLACK);
	}
	
	@Override
	public ColorGradient2D getColorGradient() {
		return colorGradient;
	}
	
	@Override
	public void setColorGradient(ColorGradient2D colorGradient) {
		if (colorGradient == null)
			throw new NullPointerException("colorGradient is null!");

		this.colorGradient = colorGradient;
	}

	@Override
	public void setTexture(Texture texture) {
		int nextTextureIndex = activeTextures.size();
		
		for (int i = 0; i < nextTextureIndex; i++) {
			Texture activeTexture = activeTextures.get(i);
			if (activeTexture == texture) {
				currentTextureIndex = i;
				return;
			}
		}
		
		if (nextTextureIndex >= MAX_TEXTURE_COUNT)
			throw new IllegalStateException("Too many textures. Maximum is " + MAX_TEXTURE_COUNT);
		
		activeTextures.add(texture);
		currentTextureIndex = nextTextureIndex;
	}

	@Override
	public void clearTransform() {
		transform.toIdentity();
		zOffset = 0.0f;
	}

	@Override
	public void translate(float xt, float yt, float zt) {
		transform.translate(xt, yt);
		zOffset += zt;
	}

	@Override
	public void rotateZ(float radians) {
		transform.rotateZ(radians);
	}

	@Override
	public Mat3 getTransform() {
		return transform.copy();
	}

	@Override
	public void setTransform(Mat3 transform) {
		this.transform.set(transform);
	}

	@Override
	public float getZOffset() {
		return zOffset;
	}

	@Override
	public void setZOffset(float zOffset) {
		this.zOffset = zOffset;
	}
	
	@Override
	public void setClipRect(float x0, float y0, float x1, float y1) {
		setClipShape(new ClipRect(x0, y0, x1, y1));
	}
	
	@Override
	public void setClipShape(ClipShape shape) {
		this.clipShape = shape;
	}

	@Override
	public ClipShape getClipShape() {
		return clipShape;
	}
	
	public void dispose() {
		vertexArray.dispose();
		vertexBuffer.dispose();
	}

	@Override
	public void close() {
		dispose();
	}
	
	private class ClippedTriangle {

		public float x0;
		public float y0;
		public float u0;
		public float v0;

		public float x1;
		public float y1;
		public float u1;
		public float v1;

		public float x2;
		public float y2;
		public float u2;
		public float v2;
		
		public ClippedTriangle() {
			this(0.0f, 0.0f, 0.0f, 0.0f,
			     0.0f, 0.0f, 0.0f, 0.0f,
			     0.0f, 0.0f, 0.0f, 0.0f);
		}
		
		public ClippedTriangle(float x0, float y0, float u0, float v0,
		                       float x1, float y1, float u1, float v1,
		                       float x2, float y2, float u2, float v2) {
		
			set(x0, y0, u0, v0,
			    x1, y1, u1, v1,
			    x2, y2, u2, v2);
		}
		
		public void set(ClippedTriangle t) {
			set(t.x0, t.y0, t.u0, t.v0,
			    t.x1, t.y1, t.u1, t.v1,
			    t.x2, t.y2, t.u2, t.v2);
		}

		public void set(float x0, float y0, float u0, float v0,
		                float x1, float y1, float u1, float v1,
		                float x2, float y2, float u2, float v2) {

			this.x0 = x0;
			this.y0 = y0;
			this.u0 = u0;
			this.v0 = v0;
			
			this.x1 = x1;
			this.y1 = y1;
			this.u1 = u1;
			this.v1 = v1;
			
			this.x2 = x2;
			this.y2 = y2;
			this.u2 = u2;
			this.v2 = v2;
		}
	}
}
