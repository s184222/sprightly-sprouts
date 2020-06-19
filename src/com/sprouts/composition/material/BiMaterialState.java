package com.sprouts.composition.material;

/**
 * @author Christian
 */
public class BiMaterialState implements IMaterialState {

	private final IMaterialState first;
	private final IMaterialState second;
	
	public BiMaterialState(IMaterialState first, IMaterialState second) {
		if (first == null || second == null)
			throw new IllegalArgumentException("States can not be null!");
		
		this.first = first;
		this.second = second;
	}

	@Override
	public void dynamicUpdate(int deltaMillis) {
		first.dynamicUpdate(deltaMillis);
		second.dynamicUpdate(deltaMillis);
	}

	@Override
	public boolean isDynamic() {
		return first.isDynamic() || second.isDynamic();
	}

	public IMaterialState getFirst() {
		return first;
	}

	public IMaterialState getSecond() {
		return second;
	}
}
