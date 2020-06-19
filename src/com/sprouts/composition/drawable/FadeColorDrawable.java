package com.sprouts.composition.drawable;

import com.sprouts.composition.material.AnimationType;
import com.sprouts.composition.material.EmptyMaterialState;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class FadeColorDrawable implements IDrawable {

	private final VertexColor color;
	
	private final AnimationType fadeInType;
	private final int fadeInDuration;
	
	private final AnimationType fadeOutType;
	private final int fadeOutDuration;

	public FadeColorDrawable(VertexColor color, int fadeMillis) {
		this(color, AnimationType.LINEAR_IN, fadeMillis, AnimationType.LINEAR_OUT, fadeMillis);
	}

	public FadeColorDrawable(VertexColor color, int fadeInMillis, int fadeOutMillis) {
		this(color, AnimationType.LINEAR_IN, fadeInMillis, AnimationType.LINEAR_OUT, fadeOutMillis);
	}
	
	public FadeColorDrawable(VertexColor color, AnimationType fadeInType, int fadeMillis) {
		this(color, fadeInType, fadeMillis, fadeInType.getOpposite(), fadeMillis);
	}

	public FadeColorDrawable(VertexColor color, AnimationType fadeInType, int fadeInMillis, int fadeOutMillis) {
		this(color, fadeInType, fadeInMillis, fadeInType.getOpposite(), fadeOutMillis);
	}
	
	public FadeColorDrawable(VertexColor color, AnimationType fadeInType, int fadeInMillis, AnimationType fadeOutType, int fadeOutMillis) {
		if (color == null)
			throw new IllegalArgumentException("color is null!");
		if (fadeInMillis < 0 || fadeOutMillis < 0)
			throw new IllegalArgumentException("Durations must be non-negative!");
		if (fadeInType.isFadeOut())
			throw new IllegalArgumentException("fadeInType is a fade out type!");
		if (!fadeOutType.isFadeOut())
			throw new IllegalArgumentException("fadeOutType is a fade in type!");
		
		this.color = color;
		
		this.fadeInType = fadeInType;
		this.fadeInDuration = fadeInMillis;

		this.fadeOutType = fadeOutType;
		this.fadeOutDuration = fadeOutMillis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransitionDrawable getRevealDrawable(IDrawable prevDrawable, IMaterialState prevState) {
		if (fadeInDuration == 0)
			return null;
		return new FadeTransitionDrawble(prevDrawable, prevState, color, fadeInType, fadeInDuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TransitionDrawable getConcealDrawable(IDrawable nextDrawable, IMaterialState nextState) {
		if (fadeOutDuration == 0)
			return null;
		return new FadeTransitionDrawble(nextDrawable, nextState, color, fadeOutType, fadeOutDuration);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMaterialState createMaterialState() {
		return EmptyMaterialState.INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height) {
		tessellator.clearMaterial();

		tessellator.setColor(color);
		tessellator.drawQuad(x, y, x + width, y + height);
	}
	
	public VertexColor getColor() {
		return color;
	}
}
