package com.sprouts.composition.view;

import com.sprouts.composition.Composition;
import com.sprouts.composition.material.IMaterial;
import com.sprouts.composition.material.IMaterialState;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class MaterialViewHandler extends ViewHandler<IMaterial> {

	private ResourceType<? extends IMaterial> resourceType;
	
	private IMaterial material;
	private IMaterialState materialState;
	
	@Override
	public void onUnbind(Composition composition) {
		super.onUnbind(composition);

		material = null;
		materialState = null;
	}
	
	@Override
	public ResourceType<? extends IMaterial> getResourceType() {
		return resourceType;
	}

	@Override
	public void setResourceType(ResourceType<? extends IMaterial> type) {
		checkBound();
		
		resourceType = type;
		
		setMaterial((type == null) ? null : composition.getResource(type));
	}
	
	@Override
	public void onResourceChanged() {
		checkBound();
		
		if (resourceType != null)
			setMaterial(composition.getResource(resourceType));
	}
	
	private void setMaterial(IMaterial material) {
		this.material = material;
		this.materialState = (material == null) ? null : material.createState();
	
		if (!composition.isDynamic() && isDynamicRequired()) {
			composition.requestDynamic();
		} else {
			composition.requestDraw(false);
		}
	}
	
	@Override
	public boolean onDynamicUpdate(int deltaMillis) {
		if (materialState != null) {
			materialState.dynamicUpdate(deltaMillis);
			return materialState.isDynamic();
		}
		
		return false;
	}
	
	public void applyMaterial(ITessellator2D tessellator) {
		checkBound();
		
		if (material != null)
			material.apply(materialState, tessellator);
	}

	@Override
	public boolean isDynamicRequired() {
		return (materialState != null && materialState.isDynamic());
	}

	@Override
	public IMaterial getResource() {
		return material;
	}
}
