package com.sprouts.graphic.tessellator2d;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.clip.ClipPlane;
import com.sprouts.graphic.clip.ClipRect;
import com.sprouts.graphic.clip.ClipShape;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.ITextureRegion;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.math.LinMath;
import com.sprouts.math.Mat3;
import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;
import com.sprouts.util.UnsafeUtil;

import sun.misc.Unsafe;

/**
 * @author Christian
 */
public abstract class AbstractTessellator2D implements ITessellator2D, AutoCloseable {

	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
	
	private static final byte WHITE_TEXTURE_INDEX = Byte.MIN_VALUE;
	private static final int MAX_TEXTURE_COUNT = 32;
	private static final int BATCH_VERTEX_COUNT = 3 * 200; /* 200 triangles */
	
	private static final int MAX_CLIPPED_CACHE_SIZE = 32;
	private static final float Z_CLIP_OFFSET = 0.0f;
	
	private static final float PROJ_NEAR = -100.0f;
	private static final float PROJ_FAR  =  100.0f;
	
	protected final Tessellator2DShader shader;
		
	protected final VertexArray vertexArray;
	protected final VertexBuffer vertexBuffer;
	
	protected ColorGradient2D colorGradient;
	
	protected List<Texture> activeTextures;
	protected ITextureRegion textureRegion;
	protected int textureIndex;
	
	protected final Mat3 transform;
	
	protected ClipShape clipShape;
	protected ClipTriangle[] triangleCache;
	
	public AbstractTessellator2D(Tessellator2DShader shader) {
		if (shader == null)
			throw new IllegalArgumentException("shader is null!");
		
		this.shader = shader;
		
		vertexArray = new VertexArray();
		vertexBuffer = new VertexBuffer(shader.getVertexByteSize(), BATCH_VERTEX_COUNT);
		shader.initBuffers(vertexArray, vertexBuffer);
		
		activeTextures = new ArrayList<Texture>();

		transform = new Mat3();

		clearMaterial();
		clearTransform();
		clearClipShape();
		
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
		glDisable(GL_DEPTH_TEST);
		
		vertexArray.bind();
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glEnable(GL_DEPTH_TEST);
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
		if (textureRegion != null) {
			float u0 = textureRegion.getU0();
			float v0 = textureRegion.getV0();
			float u1 = textureRegion.getU1();
			float v1 = textureRegion.getV1();
			
			drawQuadImpl(x0, y0, u0, v0, x1, y1, u1, v1);
		} else {
			drawQuadImpl(x0, y0, 0.0f, 0.0f, x1, y1, 0.0f, 0.0f);
		}
	}
	
	@Override
	public void drawQuadRegion(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1) {
		if (textureRegion != null) {
			ITextureRegion region = textureRegion.getRegion(u0, v0, u1, v1);
			
			u0 = region.getU0();
			v0 = region.getV0();
			u1 = region.getU1();
			v1 = region.getV1();
			
			drawQuadImpl(x0, y0, u0, v0, x1, y1, u1, v1);
		} else {
			drawQuadImpl(x0, y0, 0.0f, 0.0f, x1, y1, 0.0f, 0.0f);
		}
	}
	
	private void drawQuadImpl(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1) {
		Vec2 c0 = transform.mul(new Vec2(x0, y0)); // Top-left
		Vec2 c1 = transform.mul(new Vec2(x0, y1)); // Bottom-left
		Vec2 c2 = transform.mul(new Vec2(x1, y1)); // Bottom-right
		Vec2 c3 = transform.mul(new Vec2(x1, y0)); // Top-right
		
		drawTriangleNoTransform(c0.x, c0.y, u0, v0, c1.x, c1.y, u0, v1, c3.x, c3.y, u1, v0);
		drawTriangleNoTransform(c2.x, c2.y, u1, v1, c3.x, c3.y, u1, v0, c1.x, c1.y, u0, v1);
	}
	
	@Override
	public void drawTriangle(float x0, float y0, float x1, float y1, float x2, float y2) {
		if (textureRegion != null) {
			float u0 = textureRegion.getU0();
			float v0 = textureRegion.getV0();
			float u1 = textureRegion.getU1();
			float v1 = textureRegion.getV1();
			
			// Default texture region is top left part
			// of the texture region.
			drawTriangleImpl(x0, y0, u0, v0, x1, y1, u1, v0, x2, y2, u0, v1);
		} else {
			drawTriangleImpl(x0, y0, 0.0f, 0.0f, x1, y1, 0.0f, 0.0f, x2, y2, 0.0f, 0.0f);
		}
	}
	
	@Override
	public void drawTriangleRegion(float x0, float y0, float u0, float v0, 
	                               float x1, float y1, float u1, float v1,
	                               float x2, float y2, float u2, float v2) {
		
		if (textureRegion != null) {
			float du = textureRegion.getU1() - textureRegion.getU0();
			float dv = textureRegion.getV1() - textureRegion.getV0();
			
			float nu0 = textureRegion.getU0() + du * u0;
			float nv0 = textureRegion.getV0() + dv * v0;
			
			float nu1 = textureRegion.getU0() + du * u1;
			float nv1 = textureRegion.getV0() + dv * v1;

			float nu2 = textureRegion.getU0() + du * u2;
			float nv2 = textureRegion.getV0() + dv * v2;
			
			drawTriangleImpl(x0, y0, nu0, nv0, x1, y1, nu1, nv1, x2, y2, nu2, nv2);
		} else {
			drawTriangleImpl(x0, y0, 0.0f, 0.0f, x1, y1, 0.0f, 0.0f, x2, y2, 0.0f, 0.0f);
		}
	}
	
	private void drawTriangleImpl(float x0, float y0, float u0, float v0, 
	                              float x1, float y1, float u1, float v1, 
	                              float x2, float y2, float u2, float v2) {

		Vec2 p0 = transform.mul(new Vec2(x0, y0));
		Vec2 p1 = transform.mul(new Vec2(x1, y1));
		Vec2 p2 = transform.mul(new Vec2(x2, y2));
	
		drawTriangleNoTransform(p0.x, p0.y, u0, v0, p1.x, p1.y, u1, v1, p2.x, p2.y, u2, v2);
	}

	@Override
	public void drawLine(float x0, float y0, float x1, float y1, float lineWidth) {
		Vec2 p0 = transform.mul(new Vec2(x0, y0));
		Vec2 p1 = transform.mul(new Vec2(x1, y1));
		
		float dx = p1.x - p0.x;
		float dy = p1.y - p0.y;
		
		float distSqr = dx * dx + dy * dy;
		if (distSqr >= LinMath.EPSILON * LinMath.EPSILON) {
			float m = 0.5f * lineWidth / ((float)Math.sqrt(distSqr));
			
			dx *= m;
			dy *= m;
			
			// Vertical line: Top-left
			float lx0 = p0.x - dy;
			float ly0 = p0.y + dx;

			// Vertical line: Bottom-left
			float lx1 = p1.x - dy;
			float ly1 = p1.y + dx;

			// Vertical line: Bottom-right
			float lx2 = p1.x + dy;
			float ly2 = p1.y - dx;

			// Vertical line: Top-right
			float lx3 = p0.x + dy;
			float ly3 = p0.y - dx;
			
			if (textureRegion != null) {
				float u0 = textureRegion.getU0();
				float v0 = textureRegion.getV0();
				float u1 = textureRegion.getU1();
				float v1 = textureRegion.getV1();
				
				drawTriangleNoTransform(lx0, ly0, u0, v0, lx1, ly1, u0, v1, lx3, ly3, u1, v0);
				drawTriangleNoTransform(lx2, ly2, u1, v1, lx3, ly3, u1, v0, lx1, ly1, u0, v1);
			} else {
				drawTriangleNoTransform(lx0, ly0, 0.0f, 0.0f, lx1, ly1, 0.0f, 0.0f, lx3, ly3, 0.0f, 0.0f);
				drawTriangleNoTransform(lx2, ly2, 0.0f, 0.0f, lx3, ly3, 0.0f, 0.0f, lx1, ly1, 0.0f, 0.0f);
			}
		}
	}
	
	private void drawTriangleNoTransform(float x0, float y0, float u0, float v0, 
	                                     float x1, float y1, float u1, float v1, 
	                                     float x2, float y2, float u2, float v2) {

		if (clipShape != null && clipShape.getPlaneCount() > 0) {
			// Note: we need an extra triangle for storing
			//       the initial triangle when drawing.
			ensureCacheSize(clipShape.getPlaneCount() + 1);
			
			triangleCache[0].set(x0, y0, u0, v0, x1, y1, u1, v1, x2, y2, u2, v2);
			clipAndTessellate(clipShape.getPlanes(), 0);
		} else {
			// In this case we can tessellate the triangle immediately
			// and skip the expensive clipping routine.
			tessellateTriangle(x0, y0, u0, v0, x1, y1, u1, v1, x2, y2, u2, v2);
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
			triangleCache[i] = new ClipTriangle();
	}
	
	private void clipAndTessellate(ClipPlane[] planes, int planeIndex) {
		ClipTriangle t = triangleCache[planeIndex];
		
		if (planeIndex >= planes.length) {
			tessellateTriangle(t.v0.x, t.v0.y, t.v0.u, t.v0.v,
			                   t.v1.x, t.v1.y, t.v1.u, t.v1.v,
			                   t.v2.x, t.v2.y, t.v2.u, t.v2.v);
		} else {
			ClipPlane plane = planes[planeIndex];
			if (plane.contains(t.v0.x, t.v0.y, Z_CLIP_OFFSET)) {
				if (plane.contains(t.v1.x, t.v1.y, Z_CLIP_OFFSET)) {
					if (plane.contains(t.v2.x, t.v2.y, Z_CLIP_OFFSET)) {
						triangleCache[planeIndex + 1].set(t);
						clipAndTessellate(planes, planeIndex + 1);
					} else {
						clip2Inside(planes, planeIndex, t.v0, t.v1, t.v2);
					}
				} else {
					if (plane.contains(t.v2.x, t.v2.y, Z_CLIP_OFFSET)) {
						clip2Inside(planes, planeIndex, t.v2, t.v0, t.v1);
					} else {
						clip2Outside(planes, planeIndex, t.v0, t.v1, t.v2);
					}
				}
			} else {
				if (plane.contains(t.v1.x, t.v1.y, Z_CLIP_OFFSET)) {
					if (plane.contains(t.v2.x, t.v2.y, Z_CLIP_OFFSET)) {
						clip2Inside(planes, planeIndex, t.v1, t.v2, t.v0);
					} else {
						clip2Outside(planes, planeIndex, t.v1, t.v2, t.v0);
					}
				} else {
					if (plane.contains(t.v2.x, t.v2.y, Z_CLIP_OFFSET)) {
						clip2Outside(planes, planeIndex, t.v2, t.v0, t.v1);
					} else {
						// Triangle is completely outside of the clip
						// shape. Discard it completely.
					}
				}
			}
		}
	}
	
	private void clip2Inside(ClipPlane[] planes, int planeIndex, ClipVertex in0, ClipVertex in1, ClipVertex out) {
		ClipPlane plane = planes[planeIndex];
		ClipTriangle t = triangleCache[planeIndex + 1];
		
		t.v0.set(in0);
		interpolateClipVertex(plane, out, in1, t.v1);
		interpolateClipVertex(plane, out, in0, t.v2);

		clipAndTessellate(planes, planeIndex + 1);
		
		t.v2.set(t.v1);
		t.v0.set(in0);
		t.v1.set(in1);

		clipAndTessellate(planes, planeIndex + 1);
	}
	
	private void clip2Outside(ClipPlane[] planes, int planeIndex, ClipVertex in, ClipVertex out0, ClipVertex out1) {
		ClipPlane plane = planes[planeIndex];
		ClipTriangle t = triangleCache[planeIndex + 1];
		
		t.v0.set(in);
		interpolateClipVertex(plane, out0, in, t.v1);
		interpolateClipVertex(plane, out1, in, t.v2);

		clipAndTessellate(planes, planeIndex + 1);
	}

	private void interpolateClipVertex(ClipPlane plane, ClipVertex out, ClipVertex in, ClipVertex result) {
		float t = plane.intersect(out.x, out.y, Z_CLIP_OFFSET, in.x, in.y, Z_CLIP_OFFSET);
		
		result.x = out.x + (in.x - out.x) * t;
		result.y = out.y + (in.y - out.y) * t;
		result.u = out.u + (in.u - out.u) * t;
		result.v = out.v + (in.v - out.v) * t;
	}
	
	protected void tessellateTriangle(float x0, float y0, float u0, float v0, 
	                                  float x1, float y1, float u1, float v1, 
	                                  float x2, float y2, float u2, float v2) {
		
		tessellateVertex(x0, y0, u0, v0);
		tessellateVertex(x1, y1, u1, v1);
		tessellateVertex(x2, y2, u2, v2);
	}
	
	protected void tessellateVertex(float x, float y, float u, float v) {
		VertexAttribBuilder builder = getBuilder();
		
		builder.ensureCapacity(builder.getVertexSize());

		ByteBuffer buffer = builder.getWritableBuffer();
		if (UNSAFE != null) {
			// This is mostly an optimization, so the DirectByteBuffer
			// from the java.nio package does not have to check bounds
			// every time we want to put new data into the buffer. Note
			// that we have the VertexAttribBuilder#ensureCapacity call
			// above to ensure that we do not write to illegal memory.
			long address = MemoryUtil.memAddress(buffer);

			UNSAFE.putFloat(address + 0, x);
			UNSAFE.putFloat(address + 4, y);
			
			VertexColor color = colorGradient.getColor(x, y, transform);
			UNSAFE.putByte(address + 8, (byte)color.getRed());
			UNSAFE.putByte(address + 9, (byte)color.getGreen());
			UNSAFE.putByte(address + 10, (byte)color.getBlue());
			UNSAFE.putByte(address + 11, (byte)color.getAlpha());
			
			UNSAFE.putFloat(address + 12, u);
			UNSAFE.putFloat(address + 16, v);

			UNSAFE.putByte(address + 20, (byte)textureIndex);
			
			buffer.position(buffer.position() + 21);
		} else {
			builder.put(x, y);
			
			VertexColor color = colorGradient.getColor(x, y, transform);
			builder.put((byte)color.getRed());
			builder.put((byte)color.getGreen());
			builder.put((byte)color.getBlue());
			builder.put((byte)color.getAlpha());
			
			builder.put(u, v);
			
			builder.put((byte)textureIndex);
		}
	}
	
	protected abstract VertexAttribBuilder getBuilder();
	
	@Override
	public void clearMaterial() {
		colorGradient = new ConstantColorGradient2D(VertexColor.BLACK);
		
		textureRegion = null;
		textureIndex = WHITE_TEXTURE_INDEX;
	}
	
	@Override
	public ColorGradient2D getColorGradient() {
		return colorGradient;
	}
	
	@Override
	public void setColorGradient(ColorGradient2D colorGradient) {
		if (colorGradient == null)
			throw new IllegalArgumentException("colorGradient is null!");

		this.colorGradient = colorGradient;
	}
	
	@Override
	public ITextureRegion getTextureRegion() {
		return textureRegion;
	}

	@Override
	public void setTextureRegion(ITextureRegion textureRegion) {
		this.textureRegion = textureRegion;
		
		if (textureRegion != null) {
			textureIndex = getOrAddTextureIndex(textureRegion.getTexture());
		} else {
			textureIndex = WHITE_TEXTURE_INDEX;
		}
	}
	
	private int getOrAddTextureIndex(Texture texture) {
		int nextTextureIndex = activeTextures.size();
		
		for (int i = 0; i < nextTextureIndex; i++) {
			Texture activeTexture = activeTextures.get(i);
			
			if (activeTexture == texture)
				return i;
		}
		
		if (nextTextureIndex >= MAX_TEXTURE_COUNT)
			throw new IllegalStateException("Too many textures. Maximum is " + MAX_TEXTURE_COUNT);
		
		activeTextures.add(texture);
	
		return nextTextureIndex;
	}

	@Override
	public void clearTransform() {
		transform.toIdentity();
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
	public void translate(float xt, float yt) {
		transform.translate(xt, yt);
	}
	
	@Override
	public void scale(float xs, float ys) {
		transform.scale(xs, ys);
	}

	@Override
	public void rotateZ(float radians) {
		transform.rotateZ(radians);
	}
	
	@Override
	public void clearClipShape() {
		clipShape = null;
		
		triangleCache = new ClipTriangle[0];
	}

	@Override
	public ClipShape getClipShape() {
		return clipShape;
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
	public void dispose() {
		vertexArray.dispose();
		vertexBuffer.dispose();
	}

	@Override
	public void close() {
		dispose();
	}
	
	private static class ClipTriangle {

		public ClipVertex v0;
		public ClipVertex v1;
		public ClipVertex v2;
		
		public ClipTriangle() {
			v0 = new ClipVertex();
			v1 = new ClipVertex();
			v2 = new ClipVertex();
		}
		
		public void set(ClipTriangle t) {
			this.v0.set(t.v0);
			this.v1.set(t.v1);
			this.v2.set(t.v2);
		}

		public void set(float x0, float y0, float u0, float v0,
		                float x1, float y1, float u1, float v1,
		                float x2, float y2, float u2, float v2) {

			this.v0.set(x0, y0, u0, v0);
			this.v1.set(x1, y1, u1, v1);
			this.v2.set(x2, y2, u2, v2);
		}
	}
	
	private static class ClipVertex {
		
		public float x;
		public float y;
		public float u;
		public float v;

		public ClipVertex() {
			x = y = u = v = 0.0f;
		}
		
		public void set(ClipVertex other) {
			x = other.x;
			y = other.y;
			u = other.u;
			v = other.v;
		}
		
		public void set(float x, float y, float u, float v) {
			this.x = x;
			this.y = y;
			this.u = u;
			this.v = v;
		}
	}
}
