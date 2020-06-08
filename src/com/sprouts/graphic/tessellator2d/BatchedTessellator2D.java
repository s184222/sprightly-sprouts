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
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.buffer.VertexArray;
import com.sprouts.graphic.buffer.VertexBuffer;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator2d.color.ColorGradient2D;
import com.sprouts.graphic.tessellator2d.color.ConstantColorGradient2D;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.math.Mat3;
import com.sprouts.math.Mat4;
import com.sprouts.math.Vec2;

public class BatchedTessellator2D implements ITessellator2D {
	
	private static final int WHITE_TEXTURE_INDEX = -1;
	private static final int MAX_TEXTURE_COUNT = 32;
	private static final int BATCH_VERTEX_COUNT = 3 * 200; /* 200 triangles */
	
	private static final float PROJ_NEAR = -100.0f;
	private static final float PROJ_FAR  =  100.0f;
	
	private final Tessellator2DShader shader;
	
	private final VertexAttribBuilder builder;
	
	private final VertexArray vertexArray;
	private final VertexBuffer vertexBuffer;
	
	private final Mat3 transform;
	private float zOffset;
	
	private ColorGradient2D colorGradient;
	private int currentTextureIndex;
	
	private boolean batching;
	private List<Texture> activeTextures;
	
	public BatchedTessellator2D(Tessellator2DShader shader) {
		this.shader = shader;
		
		int vertexSize = shader.getVertexByteSize();
		builder = new VertexAttribBuilder(vertexSize);
		
		vertexArray = new VertexArray();
		vertexBuffer = new VertexBuffer(vertexSize, BATCH_VERTEX_COUNT);
		shader.initBuffers(vertexArray, vertexBuffer);
		
		transform = new Mat3();
		zOffset = 0.0f;
		
		colorGradient = new ConstantColorGradient2D(VertexColor.BLACK);
		currentTextureIndex = WHITE_TEXTURE_INDEX;
	
		batching = false;
		activeTextures = new ArrayList<Texture>();
		
		initShaderTextureSamplers();
	}

	private void initShaderTextureSamplers() {
		int[] textureSamplers = new int[MAX_TEXTURE_COUNT];
		for (int i = 0; i < MAX_TEXTURE_COUNT; i++)
			textureSamplers[i] = i;
		
		shader.enable();
		shader.setTextureSamplers(textureSamplers);
	}
	
	@Override
	public void setViewport(float x0, float y0, float x1, float y1) {
		shader.enable();
		shader.setProjMat(new Mat4().toOrthographic(x0, x1, y0, y1, PROJ_NEAR, PROJ_FAR));
	}

	private void checkBatching() {
		if (!batching)
			throw new IllegalStateException("Tessellator is not batching!");
	}
	
	public void beginBatch() {
		if (batching)
			throw new IllegalStateException("Tessellator is already batching!");
		
		batching = true;
	}

	public void endBatch() {
		checkBatching();
	
		batching = false;
		
		drawBatch();
		
		clearTransform();
		setColor(VertexColor.BLACK);
		
		activeTextures.clear();
		currentTextureIndex = WHITE_TEXTURE_INDEX;
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
		checkBatching();

		Vec2 c0 = transform.mul(new Vec2(x0, y0)); // Top-left
		Vec2 c1 = transform.mul(new Vec2(x0, y1)); // Bottom-left
		Vec2 c2 = transform.mul(new Vec2(x1, y1)); // Bottom-right
		Vec2 c3 = transform.mul(new Vec2(x1, y0)); // Top-right
		
		tessellateTriangle(c0.x, c0.y, u0, v0, c1.x, c1.y, u0, v1, c3.x, c3.y, u1, v0, (byte)textureIndex);
		tessellateTriangle(c2.x, c2.y, u1, v1, c3.x, c3.y, u1, v0, c1.x, c1.y, u0, v1, (byte)textureIndex);
	}
	
	private void tessellateTriangle(float x0, float y0, float u0, float v0, 
	                                float x1, float y1, float u1, float v1, 
	                                float x2, float y2, float u2, float v2,
	                                byte textureIndex) {
		
		tessellateVertex(x0, y0, u0, v0, textureIndex);
		tessellateVertex(x1, y1, u1, v1, textureIndex);
		tessellateVertex(x2, y2, u2, v2, textureIndex);
	}
	
	private void tessellateVertex(float x, float y, float u, float v, byte textureIndex) {
		builder.put(x, y, zOffset);
		
		VertexColor color = colorGradient.getColor(x, y, transform);
		builder.put((byte)color.getRed());
		builder.put((byte)color.getGreen());
		builder.put((byte)color.getBlue());
		builder.put((byte)color.getAlpha());
		
		builder.put(u, v);
		
		builder.put(textureIndex);
	}

	private void drawBatch() {
		int vertexCount = builder.getPosition() / shader.getVertexByteSize();
		if (vertexCount <= 0) {
			// We have no vertices to draw
			return;
		}
		
		shader.enable();
		
		for (int i = 0; i < activeTextures.size(); i++) {
			Texture texture = activeTextures.get(i);
			int textureUnit = GL_TEXTURE0 + i;
			
			glActiveTexture(textureUnit);
			texture.bind();
		}
		
		builder.writeBuffer(vertexBuffer);
		builder.clear();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		vertexArray.bind();
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);
		
		glDisable(GL11.GL_BLEND);
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

	public void dispose() {
		builder.close();
		
		vertexArray.dispose();
		vertexBuffer.dispose();
	}
}
