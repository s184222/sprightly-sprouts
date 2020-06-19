package com.sprouts.composition.material;

/**
 * @author Christian
 */
public final class EmptyMaterialState implements IMaterialState {

	public static final EmptyMaterialState INSTANCE = new EmptyMaterialState();
	
	private EmptyMaterialState() {
	}

	@Override
	public void dynamicUpdate(int deltaMillis) {
	}

	@Override
	public boolean isDynamic() {
		return false;
	}
}
