package com.sprouts.composition;

import java.util.List;

import com.sprouts.composition.layout.AbstractLayoutManager;
import com.sprouts.composition.layout.ILayoutManager;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.composition.view.ICompositionView;
import com.sprouts.graphic.tessellator2d.ILayeredTessellator2D;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
final class RootComposition extends ParentComposition {

	RootComposition() {
		super(new RootLayoutManager(), false);
	
		setView(new RootCompositionView());
	}

	void setContent(Composition content) {
		setContent(content, null);
	}
	
	void setContent(Composition content, LayoutSpecification spec) {
		Composition prevContent = getContent();
		if (prevContent != null)
			super.remove(prevContent);
		
		if (content != null)
			super.add(content, spec);
	}
	
	Composition getContent() {
		return children.isEmpty() ? null : children.get(0);
	}
	
	@Override
	public void onAdded(Composition parent) {
		throw new IllegalStateException("Root composition can not have a parent!");
	}

	@Override
	public void onRemoved(Composition parent) {
		throw new IllegalStateException("Root composition can not have a parent!");
	}
	
	@Override
	public void add(Composition composition) {
		throw new IllegalStateException("Root composition can not have children!");
	}

	@Override
	public void add(Composition composition, LayoutSpecification spec) {
		throw new IllegalStateException("Root composition can not have children!");
	}
	
	@Override
	public void remove(Composition composition) {
		throw new IllegalStateException("Can not remove children from root composition!");
	}
	
	@Override
	public void setLayoutManager(ILayoutManager layoutManager) {
		if (this.layoutManager != null)
			throw new IllegalStateException("Layout manager can only be set once!");
		
		super.setLayoutManager(layoutManager);
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (this.view != null)
			throw new IllegalStateException("View can only be set once!");
	
		super.setView(view);
	}
	
	@Override
	public void draw(ILayeredTessellator2D tessellator) {
		if (isDrawFlagSet(RESTRUCTURE_DRAW_FLAG)) {
			tessellator.rebuildLayer(cachedLayerId, true);
			drawAll(tessellator);
			tessellator.finishRebuilding();
		} else {
			drawChildren(tessellator);
		}
		
		clearDrawFlags(drawFlags);
	}
	
	private static final class RootCompositionView implements ICompositionView {

		@Override
		public void bindView(Composition comp) {
		}

		@Override
		public void unbindView(Composition comp) {
		}

		@Override
		public void layoutChanged(Composition comp) {
		}

		@Override
		public void resourceChanged(Composition comp, ResourceType<?> resource) {
		}

		@Override
		public boolean dynamicUpdate(Composition comp, int deltaMillis) {
			return false;
		}

		@Override
		public void draw(Composition comp, ITessellator2D tessellator) {
		}

		@Override
		public CompositionSize getMinimumSize(Composition comp) {
			return CompositionSize.ZERO;
		}

		@Override
		public CompositionSize getMaximumSize(Composition comp) {
			return CompositionSize.MAX_VALUE;
		}
	}
	
	private static final class RootLayoutManager extends AbstractLayoutManager {

		@Override
		public CompositionSize getMinimumLayoutSize(ParentComposition parent) {
			return null;
		}

		@Override
		public void layoutChildren(ParentComposition parent) {
			List<Composition> children = parent.getChildren();
			
			if (!children.isEmpty()) {
				Composition content = children.get(0);
				layoutComposition(content, parent.x, parent.y, parent.width, parent.height);
			}
		}
	}
}
