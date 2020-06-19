package com.sprouts.composition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sprouts.composition.layout.GridLayoutManager;
import com.sprouts.composition.layout.ILayoutManager;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.graphic.tessellator2d.ILayeredTessellator2D;

/**
 * @author Christian
 */
public class ParentComposition extends Composition {

	protected ILayoutManager layoutManager;

	protected final List<Composition> children;
	
	private boolean minimumSizeSet;

	public ParentComposition() {
		this(new GridLayoutManager());
	}

	public ParentComposition(ILayoutManager layoutManager) {
		this(layoutManager, true);
	}
	
	protected ParentComposition(ILayoutManager layoutManager, boolean setInitialView) {
		super(setInitialView);
		
		this.layoutManager = null;

		children = new ArrayList<Composition>();
	
		minimumSizeSet = false;
		
		setLayoutManager(layoutManager);
	}

	public void add(Composition composition) {
		add(composition, null);
	}
	
	public void add(Composition composition, LayoutSpecification spec) {
		children.add(composition);

		composition.onAdded(this);
		composition.setVisible(isVisible());

		layoutManager.onCompositionAdded(composition, spec);

		requestLayout();
		requestDraw(true);
	}

	public void remove(Composition composition) {
		children.remove(composition);
		
		composition.setVisible(false);
		composition.onRemoved(this);
		
		layoutManager.onCompositionRemoved(composition);
		
		requestLayout();
		requestDraw(true);
	}
	
	public void removeAll() {
		while (!children.isEmpty())
			remove(children.get(children.size() - 1));
	}
	
	@Override
	protected void doLayout() {
		layoutManager.layoutChildren(this);

		super.doLayout();
	}
	
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		
		for (Composition child : children)
			child.layout();
	}
	
	@Override
	protected void drawChildren(ILayeredTessellator2D tessellator) {
		super.drawChildren(tessellator);
		
		for (Composition child : children)
			child.draw(tessellator);
	}

	@Override
	public void setVisible(boolean visible) {
		boolean oldVisible = isVisible();
		
		super.setVisible(visible);

		if (visible != oldVisible) {
			for (Composition child : children)
				child.setVisible(visible);
		}
	}
	
	public List<Composition> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public ILayoutManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(ILayoutManager layoutManager) {
		if (layoutManager == null)
			throw new IllegalArgumentException("layoutManager is null!");

		if (this.layoutManager != null) {
			// Note that layoutManager is null when called
			// from the constructor.
			this.layoutManager.uninstall(this);
		}
		
		this.layoutManager = layoutManager;
		layoutManager.install(this);
	
		requestLayout();
	}
	
	@Override
	public CompositionSize getMinimumSize() {
		if (!minimumSizeSet && minimumSize == null)
			minimumSize = layoutManager.getMinimumLayoutSize(this);
		
		return super.getMinimumSize();
	}
	
	@Override
	public void setMinimumSize(CompositionSize minimumSize) {
		super.setMinimumSize(minimumSize);
		
		minimumSizeSet = (minimumSize != null);
	}
	
	@Override
	public Composition getChildAt(int x, int y) {
		for (Composition child : children) {
			if (child.isInBounds(x, y))
				return child;
		}
		
		return super.getChildAt(x, y);
	}
}
