package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.composition.resource.ResourceType;

/**
 * @author Christian
 */
public abstract class ViewHandler<T> {

	protected Composition composition;
	
	/*
	 * Checks if the handler is bound to a composition and if it is not then an
	 * IllegalStateException is thrown.
	 * 
	 * @throws IllegalStateException
	 */
	protected void checkBound() {
		if (composition == null)
			throw new IllegalStateException("handler is not bound to a composition!");
	}

	/*
	 * Checks if the handler is already bound to a composition and if it is then an
	 * IllegalStateException is thrown.
	 * 
	 * @throws IllegalStateException
	 */
	protected void checkNotBound() {
		if (composition != null)
			throw new IllegalStateException("handler is already bound to a composition!");
	}
	
	/**
	 * Should be called when the {@link CompositionView} is bound to a {@link Composition}.
	 * 
	 * @param composition - the composition which the view is bound to.
	 * 
	 * @see #onUnbind(Composition)
	 * 
	 * @throws IllegalStateException if the handler is already bound to a composition.
	 */
	public void onBind(Composition composition) {
		checkNotBound();
		
		this.composition = composition;
	}

	/**
	 * Should be called when the {@link CompositionView} is unbound from a {@link Composition}.
	 * 
	 * @param composition - the composition which the view is unbound from
	 * 
	 * @see #onBind(Composition)
	 * 
	 * @throws IllegalStateException if the handler is not currently bound to a composition.
	 */
	public void onUnbind(Composition composition) {
		checkBound();
		
		this.composition = null;
	}

	public abstract ResourceType<? extends T> getResourceType();
	
	public abstract void setResourceType(ResourceType<? extends T> type);

	public abstract void onResourceChanged();
	
	/**
	 * Should be called when the bound {@link Composition} receives a dynamic update.
	 * 
	 * @param deltaMillis - The difference, in milliseconds, since last frame.
	 */
	public abstract boolean onDynamicUpdate(int deltaMillis);
	
	/**
	 * @return True, if the view handler requires dynamic drawing, false otherwise.
	 */
	public abstract boolean isDynamicRequired();

	/**
	 * @return The current resource that is displayed by this handler.
	 */
	public abstract T getResource();

}
