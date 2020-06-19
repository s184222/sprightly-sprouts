package com.sprouts.composition.layout;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;

/**
 * @author Christian
 */
public interface ILayoutManager {
	
	public void install(ParentComposition parent);

	public void uninstall(ParentComposition parent);

	public void onCompositionAdded(Composition child, LayoutSpecification spec);

	public void onCompositionRemoved(Composition child);
	
	public CompositionSize getMinimumLayoutSize(ParentComposition parent);

	public void layoutChildren(ParentComposition parent);

}
