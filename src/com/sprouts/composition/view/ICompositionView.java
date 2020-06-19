package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

public interface ICompositionView {

	public void bindView(Composition comp);
	
	public void unbindView(Composition comp);

	public void layoutChanged(Composition comp);
	
	public void resourceChanged(Composition comp, ResourceType<?> resource);
	
	public boolean dynamicUpdate(Composition comp, int deltaMillis);
	
	public void draw(Composition comp, ITessellator2D tessellator);

	public CompositionSize getMinimumSize(Composition comp);

	public CompositionSize getMaximumSize(Composition comp);
	
}
