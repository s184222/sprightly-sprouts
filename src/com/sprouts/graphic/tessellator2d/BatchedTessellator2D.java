package com.sprouts.graphic.tessellator2d;

import com.sprouts.graphic.tessellator.VertexAttribBuilder;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;

/**
 * @author Christian
 */
public class BatchedTessellator2D extends AbstractTessellator2D {
	
	protected final VertexAttribBuilder builder;
	
	private boolean batching;
	
	public BatchedTessellator2D(Tessellator2DShader shader) {
		super(shader);

		builder = new VertexAttribBuilder(shader.getVertexByteSize());
		
		batching = false;
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

		clearMaterial();
		clearAlphaMultiplier();
		clearTransform();
		clearClipShape();
		
		activeTextures.clear();
	}
	
	private void drawBatch() {
		int vertexCount = builder.getPosition() / shader.getVertexByteSize();
		if (vertexCount > 0) {
			builder.writeBuffer(vertexBuffer);
			builder.clear();
			
			drawBuffer(vertexCount);
		}
	}
	
	@Override
	public void drawQuad(float x0, float y0, float x1, float y1) {
		checkBatching();

		super.drawQuad(x0, y0, x1, y1);
	}
	
	@Override
	public void drawQuadRegion(float x0, float y0, float u0, float v0, float x1, float y1, float u1, float v1) {
		checkBatching();
		
		super.drawQuadRegion(x0, y0, u0, v0, x1, y1, u1, v1);
	}
	
	@Override
	protected VertexAttribBuilder getBuilder() {
		return builder;
	}
	
	@Override
	public void dispose() {
		builder.close();

		super.dispose();
	}
}
