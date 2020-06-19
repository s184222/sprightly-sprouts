package com.sprouts.composition.layout;

import java.util.HashMap;
import java.util.Map;

import com.sprouts.composition.Composition;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;

/**
 * @author Christian
 */
public abstract class AbstractLayoutManager implements ILayoutManager {

	protected ParentComposition parent;
	
	protected final Map<Composition, LayoutSpecification> specs;
	
	public AbstractLayoutManager() {
		specs = new HashMap<Composition, LayoutSpecification>();
	}
	
	@Override
	public void install(ParentComposition parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent is null!");
		if (this.parent != null)
			throw new IllegalArgumentException("Layout can only be installed on one Composition");
		
		this.parent = parent;
	}

	@Override
	public void uninstall(ParentComposition parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent is null!");
		if (this.parent != parent)
			throw new IllegalArgumentException("Layout is not installed on given parent");
		
		this.parent = null;
		
		specs.clear();
	}
	
	@Override
	public void onCompositionAdded(Composition child, LayoutSpecification spec) {
		if (spec != null) {
			// Make sure to copy the spec, as it could change before
			// #layoutComposition(Composition, int, int, int, int).
			specs.put(child, spec.getCopy());
		}
	}

	@Override
	public void onCompositionRemoved(Composition child) {
		specs.remove(child);
	}
	
	protected void layoutComposition(Composition child, int x, int y, int width, int height) {
		LayoutSpecification spec = specs.get(child);
		
		Margin bm = child.getBorder().getMargin();
		
		if (spec != null) {
			int cw = width;
			if (spec.getHorizontalFill() == CompositionFill.FILL_MINIMUM)
				cw = Math.min(child.getMinimumSize().getWidth() + bm.left + bm.right, width);

			int ch = height;
			if (spec.getVerticalFill() == CompositionFill.FILL_MINIMUM)
				ch = Math.min(child.getMinimumSize().getHeight() + bm.top + bm.bottom, height);

			x += (int)((width  - cw) * spec.getHorizontalAlignment());
			y += (int)((height - ch) * spec.getVerticalAlignment());
			
			width  = cw;
			height = ch;
		}

		width  = Math.max(0, width  - bm.left - bm.right);
		height = Math.max(0, height - bm.top  - bm.bottom);
		
		child.setBounds(x + bm.left, y + bm.right, width, height);
	}
}
