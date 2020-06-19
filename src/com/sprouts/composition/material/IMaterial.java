package com.sprouts.composition.material;

import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public interface IMaterial {

	default public IMaterialState createState() {
		return EmptyMaterialState.INSTANCE;
	}

	public void apply(IMaterialState state, ITessellator2D tessellator);
	
}
