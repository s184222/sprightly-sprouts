package com.sprouts.composition.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.resource.IResourceManager;
import com.sprouts.composition.resource.IResourcePack;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public abstract class CompositionView implements ICompositionView {

	private final ArrayList<ViewHandler<?>> viewHandlers;
	private final Set<ResourceType<?>> viewResources;

	public CompositionView() {
		viewHandlers = new ArrayList<ViewHandler<?>>(2);
		viewResources = new HashSet<>();
	}
	
	protected void registerViewHandler(ViewHandler<?> handler) {
		viewHandlers.add(handler);
	}
	
	@Override
	public final void bindView(Composition comp) {
		viewHandlers.trimToSize();
		
		for (ViewHandler<?> handler : viewHandlers)
			handler.onBind(comp);

		installResources(comp);
		
		onBindView(comp);
	}

	protected abstract void onBindView(Composition comp);
	
	@Override
	public final void unbindView(Composition comp) {
		onUnbindView(comp);

		uninstallResources(comp);
		
		for (ViewHandler<?> handler : viewHandlers)
			handler.onUnbind(comp);
	}

	protected abstract void onUnbindView(Composition comp);

	protected void installResources(Composition comp) {
		IResourceManager resourceManager = comp.getResourceManager();
		IResourcePack pack = resourceManager.getResourcePack(comp);
		
		for (ResourceType<?> type : comp.getResourceTypes()) {
			if (comp.getResource(type) == null) {
				setViewResource(comp, type, pack);
				viewResources.add(type);
			}
		}
	}
	
	/* Helper method for installResources to help type checking. */
	private <T> void setViewResource(Composition comp, ResourceType<T> type, IResourcePack pack) {
		comp.setResource(type, pack.getResource(type));
	}
	
	protected void uninstallResources(Composition comp) {
		for (ResourceType<?> type : viewResources)
			comp.setResource(type, null);
		
		viewResources.clear();
	}

	@Override
	public void layoutChanged(Composition comp) {
	}
	
	@Override
	public void resourceChanged(Composition comp, ResourceType<?> type) {
		viewResources.remove(type);
		
		for (ViewHandler<?> handler : viewHandlers) {
			if (handler.getResourceType() == type)
				handler.onResourceChanged();
		}
	}
	
	@Override
	public final boolean dynamicUpdate(Composition comp, int deltaMillis) {
		boolean dynamicRequired = false;
		for (ViewHandler<?> handler : viewHandlers) {
			if (handler.isDynamicRequired() && handler.onDynamicUpdate(deltaMillis))
				dynamicRequired = true;
		}
		
		if (!dynamicRequired) {
			comp.requestDraw(false);
			return false;
		}
		
		return true;
	}
	
	@Override
	public final void draw(Composition comp, ITessellator2D tessellator) {
		drawView(comp, tessellator);
	}
	
	protected abstract void drawView(Composition comp, ITessellator2D tessellator);
	
	protected void drawBackground(Composition comp, ITessellator2D tessellator, DrawableViewHandler background) {
		background.draw(tessellator, comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
	}
	
	protected void drawBorder(Composition comp, ITessellator2D tessellator, BorderViewHandler border) {
		Margin borderMargin = comp.getBorder().getMargin();

		int x = comp.getX() - borderMargin.left;
		int y = comp.getY() - borderMargin.top;
		int width = comp.getWidth() + borderMargin.left + borderMargin.right;
		int height = comp.getHeight() + borderMargin.top + borderMargin.bottom;
	
		border.draw(tessellator, x, y, width, height);
	}

	@Override
	public CompositionSize getMinimumSize(Composition comp) {
		Margin padding = comp.getPadding();
		int mnw = padding.getHorizontalMargin() + 1;
		int mnh = padding.getVerticalMargin() + 1;
		return new CompositionSize(mnw, mnh);
	}

	@Override
	public CompositionSize getMaximumSize(Composition comp) {
		return CompositionSize.MAX_VALUE;
	}
}
