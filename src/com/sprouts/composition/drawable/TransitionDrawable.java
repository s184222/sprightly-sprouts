package com.sprouts.composition.drawable;

import com.sprouts.composition.material.IMaterialState;

/**
 * @author Christian
 */
public abstract class TransitionDrawable implements IDrawable {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TransitionDrawable getRevealDrawable(IDrawable prevDrawable, IMaterialState prevState) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TransitionDrawable getConcealDrawable(IDrawable nextDrawable, IMaterialState nextState) {
		return null;
	}
}
