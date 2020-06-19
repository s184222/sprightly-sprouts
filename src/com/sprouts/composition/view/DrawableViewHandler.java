package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.composition.drawable.EmptyDrawable;
import com.sprouts.composition.drawable.IDrawable;
import com.sprouts.composition.drawable.TransitionDrawable;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * A basic handler for {@link IDrawable} objects. The main purpose of this handler is
 * to make it easier for {@link CompositionView} implementations to support all the
 * features that are required when displaying drawable objects. This includes handling
 * of reveal & conceal transitions, dynamically drawn drawables and more.
 * 
 * @author Christian
 */
public class DrawableViewHandler extends ViewHandler<IDrawable> {

	private ResourceType<? extends IDrawable> resourceType;
	
	private IDrawable drawable;
	private IMaterialState drawableState;
	
	private TransitionDrawable transition;
	private IMaterialState transitionState;
	
	private boolean concealingTransition;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUnbind(Composition composition) {
		super.onUnbind(composition);

		drawable = null;
		drawableState = null;
		
		transition = null;
		transitionState = null;
	}
	
	@Override
	public ResourceType<? extends IDrawable> getResourceType() {
		return resourceType;
	}

	@Override
	public void setResourceType(ResourceType<? extends IDrawable> type) {
		checkBound();
		
		if (type != resourceType) {
			resourceType = type;
			
			setDrawable((type == null) ? null : composition.getResource(type));
		}
	}
	
	@Override
	public void onResourceChanged() {
		checkBound();
		
		if (resourceType != null)
			setDrawable(composition.getResource(resourceType));
	}
	
	/**
	 * Sets the drawable that is to be displayed by this handler. If the previous drawable
	 * had a conceal transition, this transition will be shown before the drawable is displayed.
	 * Likewise, if the new drawable has a reveal transition, this will also be shown before the
	 * drawable itself.
	 * <br><br>
	 * Any ongoing transitions will be discarded and overridden by the conceal & reveal transitions
	 * described above.
	 * 
	 * @param drawable - the drawable which should be displayed.
	 * 
	 * @see #setViewport(int, int, int, int)
	 */
	private void setDrawable(IDrawable drawable) {
		IMaterialState drawableState = createState(drawable);
		
		TransitionDrawable nextTransition = null;
		boolean concealingTransition = false;
		
		if (this.drawable != null) {
			IDrawable nextDrawable = transition;
			IMaterialState nextDrawableState = transitionState;
			
			if (nextDrawable == null && drawable != null) {
				nextDrawable = drawable;
				nextDrawableState = drawableState;
			} else {
				nextDrawable = EmptyDrawable.INSTANCE;
				nextDrawableState = EmptyDrawable.INSTANCE.createMaterialState();
			}
			
			nextTransition = this.drawable.getConcealDrawable(nextDrawable, nextDrawableState);
			concealingTransition = true;
		}

		// Current drawable has no conceal transition. Make sure
		// to check any reveal transitions from the next drawable.
		if (drawable != null && nextTransition == null) {
			IDrawable prevDrawable = this.drawable;
			IMaterialState prevDrawableState = this.drawableState;
			
			if (prevDrawable == null) {
				prevDrawable = EmptyDrawable.INSTANCE;
				prevDrawableState = EmptyDrawable.INSTANCE.createMaterialState();
			}
			
			nextTransition = drawable.getRevealDrawable(prevDrawable, prevDrawableState);
			concealingTransition = false;
		}

		setTransition(nextTransition, concealingTransition);
		
		this.drawable = drawable;
		this.drawableState = drawableState;
		
		if (!composition.isDynamic() && isDynamicRequired()) {
			composition.requestDynamic();
		} else {
			composition.requestDraw(false);
		}
	}

	private void setTransition(TransitionDrawable transition, boolean concealingTransition) {
		this.transition = transition;
		transitionState = createState(transition);
		
		this.concealingTransition = (transition != null && concealingTransition);
	}
	
	private IMaterialState createState(IDrawable drawable) {
		return (drawable == null) ? null : drawable.createMaterialState();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onDynamicUpdate(int deltaMillis) {
		if (transitionState != null) {
			transitionState.dynamicUpdate(deltaMillis);
			
			if (!transitionState.isDynamic()) {
				if (concealingTransition && drawable != null) {
					// We should not update the new transition. This
					// ensures that it is shown for at least one frame.
					setTransition(drawable.getRevealDrawable(transition, transitionState), false);
				} else {
					setTransition(null, false);
				}
			}
		} else if (drawableState != null) {
			drawableState.dynamicUpdate(deltaMillis);
		}
		
		return isDynamicRequired();
	}

	/**
	 * Should be called when the bound {@link Composition} is drawn.
	 * 
	 * @param tessellator - The 2D tessellator that should be drawn to.
	 */
	public void draw(ITessellator2D tessellator, int x, int y, int width, int height) {
		checkBound();
		
		if (transition != null) {
			transition.draw(transitionState, tessellator, x, y, width, height);
		} else if (drawable != null) {
			drawable.draw(drawableState, tessellator, x, y, width, height);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamicRequired() {
		if (transitionState != null && transitionState.isDynamic())
			return true;
		return (drawableState != null && drawableState.isDynamic());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IDrawable getResource() {
		return drawable;
	}
}
