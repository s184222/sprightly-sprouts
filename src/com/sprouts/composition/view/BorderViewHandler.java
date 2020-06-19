package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.composition.border.IBorder;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

public class BorderViewHandler extends ViewHandler<IBorder> {

	private ResourceType<? extends IBorder> resourceType;
	
	private IBorder border;
	private IMaterialState borderState;
	
	@Override
	public void onUnbind(Composition composition) {
		super.onUnbind(composition);

		border = null;
		borderState = null;
	}
	
	@Override
	public ResourceType<? extends IBorder> getResourceType() {
		return resourceType;
	}

	@Override
	public void setResourceType(ResourceType<? extends IBorder> type) {
		checkBound();
		
		resourceType = type;
		
		setBorder((type == null) ? null : composition.getResource(type));
	}
	
	@Override
	public void onResourceChanged() {
		checkBound();
		
		if (resourceType != null)
			setBorder(composition.getResource(resourceType));
	}
	
	private void setBorder(IBorder border) {
		this.border = border;
		this.borderState = (border == null) ? null : border.createMaterialState();
	
		if (!composition.isDynamic() && isDynamicRequired()) {
			composition.requestDynamic();
		} else {
			composition.requestDraw(false);
		}
	}
	
	@Override
	public boolean onDynamicUpdate(int deltaMillis) {
		if (borderState != null) {
			borderState.dynamicUpdate(deltaMillis);
			return borderState.isDynamic();
		}
		
		return false;
	}
	
	public void draw(ITessellator2D tessellator, int x, int y, int width, int height) {
		checkBound();
		
		border.draw(borderState, tessellator, x, y, width, height);
	}

	@Override
	public boolean isDynamicRequired() {
		return (borderState != null && borderState.isDynamic());
	}

	@Override
	public IBorder getResource() {
		return border;
	}
}
