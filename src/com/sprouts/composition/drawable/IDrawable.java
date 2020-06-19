package com.sprouts.composition.drawable;

import com.sprouts.composition.material.IMaterialState;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public interface IDrawable {
	
	/**
	 * A transition drawable that will be shown before this drawable is made visible.
	 * Note that {@link #draw(IMaterialState, ITessellator2D, int, int, int, int)}
	 * will not be invoked until the given drawable has finished the transition.
	 * A transition is considered finished when the drawable is no longer dynamic.
	 * <br><br>
	 * If a conceal transition from {@code prevDrawable} is already running, this
	 * method will not be called until that transition has finished.
	 * 
	 * @param prevDrawable - the previously visible drawable or EmptyDrawable, if
	 *                       this is the first visible drawable.
	 * @param prevState - the state of the previously visible drawable or the state
	 *                    of the EmptyDrawable, if this is the first visible drawable.
	 * 
	 * @return A transition drawable that will be shown before revealing this
	 *         drawable, or null if there is no transition.
	 * 
	 * @see #getConcealDrawable(IDrawable)
	 */
	default public TransitionDrawable getRevealDrawable(IDrawable prevDrawable, IMaterialState prevState) {
		return null;
	}

	/**
	 * A transition drawable that will be shown after this drawable has been
	 * hidden. The {@code nextDrawable} will not be shown before the returned
	 * drawable has finished its transition. A transition is considered to
	 * be finished when it is no longer dynamic.
	 * 
	 * @param nextDrawable - the drawable that will be made visible once the
	 *                       transition has finished or EmptyDrawable, if there
	 *                       is no next drawable.
	 * @param nextState - the state of the drawable that will be made visible
	 *                    after the transition has finished, or the state of
	 *                    the EmptyDrawable if there is no next drawable.
	 * 
	 * @return A drawable transition that will be shown when concealing this
	 *         drawable, or null if there is no transition.
	 * 
	 * @see #getRevealDrawable(IDrawable)
	 */
	default public TransitionDrawable getConcealDrawable(IDrawable nextDrawable, IMaterialState nextState) {
		return null;
	}
	
	/**
	 * @return A new state that should be used when drawing this drawable.
	 */
	public IMaterialState createMaterialState();
	
	/**
	 * The method responsible for drawing this {@code IDrawable}. Any vertices
	 * that are drawn to the {@code tessellator} must be kept within the specified
	 * viewport {@code (x, y, width, height)}.
	 * 
	 * @param materialState - the material state that should be used when drawing.
	 * @param tessellator - the tessellator to draw this drawable to.
	 * @param x - the x-coordinate of the viewport.
	 * @param y - the y-coordinate of the viewport.
	 * @param width - the width of the viewport.
	 * @param height - the height of the viewport.
	 */
	public void draw(IMaterialState materialState, ITessellator2D tessellator, int x, int y, int width, int height);
	
}
