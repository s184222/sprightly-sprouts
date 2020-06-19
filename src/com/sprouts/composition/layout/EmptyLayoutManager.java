package com.sprouts.composition.layout;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;

/**
 * @author Christian
 */
public final class EmptyLayoutManager implements ILayoutManager {

	public static final EmptyLayoutManager INSTANCE = new EmptyLayoutManager();
	
	private EmptyLayoutManager() {
	}
	
	@Override
	public void install(ParentComposition parent) {
	}

	@Override
	public void uninstall(ParentComposition parent) {
	}
	
	@Override
	public void onCompositionAdded(Composition child, LayoutSpecification spec) {
	}

	@Override
	public void onCompositionRemoved(Composition child) {
	}
	
	@Override
	public void layoutChildren(ParentComposition parent) {
	}

	@Override
	public CompositionSize getMinimumLayoutSize(ParentComposition parent) {
		return null;
	}
}
