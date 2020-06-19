package com.sprouts.composition.drawable;

import com.sprouts.composition.material.BiMaterialState;
import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.composition.material.ITextureMaterial;
import com.sprouts.composition.material.TextureMaterial;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.ITextureRegion;

/**
 * @author Christian
 */
public class TextureDrawable implements IDrawable {

	private final IColorMaterial tint;
	private final ITextureMaterial texture;

	public TextureDrawable(VertexColor tint, ITextureRegion textureRegion) {
		this(new ColorGradientMaterial(tint), textureRegion);
	}
	
	public TextureDrawable(IColorMaterial tint, ITextureRegion textureRegion) {
		this(tint, new TextureMaterial(textureRegion));
	}
	
	public TextureDrawable(IColorMaterial tint, ITextureMaterial texture) {
		if (tint == null)
			throw new IllegalArgumentException("tint is null!");
		if (texture == null)
			throw new IllegalArgumentException("texture is null!");
		
		this.tint = tint;
		this.texture = texture;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMaterialState createMaterialState() {
		IMaterialState tintState = tint.createState();
		IMaterialState textureState = texture.createState();
		return new BiMaterialState(tintState, textureState);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
		tessellator.clearMaterial();

		BiMaterialState bms = (BiMaterialState)materialState;
	
		tint.apply(bms.getFirst(), tessellator);
		texture.apply(bms.getSecond(), tessellator);
	
		tessellator.drawQuad(x, y, x + width, y + height);
	}
	
	public IColorMaterial getTint() {
		return tint;
	}
	
	public ITextureMaterial getTexture() {
		return texture;
	}
}
