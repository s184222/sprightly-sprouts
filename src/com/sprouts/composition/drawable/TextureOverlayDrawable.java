package com.sprouts.composition.drawable;

import com.sprouts.composition.CompositionContext;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.ITextureRegion;

/**
 * @author Christian
 */
public class TextureOverlayDrawable implements IDrawable {

	private final ITextureRegion textureRegion;
	private final IDrawable overlayDrawable;

	public TextureOverlayDrawable(ITextureRegion textureRegion, IDrawable overlayDrawable) {
		if (textureRegion == null)
			throw new IllegalArgumentException("textureRegion is null!");
		if (overlayDrawable == null)
			throw new IllegalArgumentException("overlayDrawable is null!");
		
		this.textureRegion = textureRegion;
		this.overlayDrawable = overlayDrawable;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMaterialState createMaterialState() {
		return overlayDrawable.createMaterialState();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
		tessellator.clearMaterial();

		int w = CompositionContext.getDisplayWidth();
		int h = CompositionContext.getDisplayHeight();
		
		float u0 = (float)x / w;
		float v0 = (float)y / h;
		float u1 = (float)(x + width) / w;
		float v1 = (float)(y + height) / h;
		
		tessellator.setColor(VertexColor.WHITE);
		tessellator.setTextureRegion(textureRegion.getRegion(u0, v0, u1, v1));
		tessellator.drawQuad(x, y, x + width, y + height);
	
		overlayDrawable.draw(materialState, tessellator, x, y, width, height);
	}
	
	public ITextureRegion getTexture() {
		return textureRegion;
	}
	
	public IDrawable getOverlayDrawable() {
		return overlayDrawable;
	}
}
