package com.sprouts.composition.material;

import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.texture.ITextureRegion;

/**
 * @author Christian
 */
public class TextureMaterial implements ITextureMaterial {

	private final ITextureRegion textureRegion;
	
	public TextureMaterial(ITextureRegion textureRegion) {
		if (textureRegion == null)
			throw new IllegalArgumentException("textureRegion is null!");
		
		this.textureRegion = textureRegion;
	}

	@Override
	public void apply(IMaterialState state, ITessellator2D tessellator) {
		tessellator.setTextureRegion(textureRegion);
	}
	
	public ITextureRegion getTextureRegion() {
		return textureRegion;
	}
}
