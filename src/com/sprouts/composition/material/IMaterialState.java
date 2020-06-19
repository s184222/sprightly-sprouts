package com.sprouts.composition.material;

/**
 * @author Christian
 */
public interface IMaterialState {

	public void dynamicUpdate(int deltaMillis);

	public boolean isDynamic();
	
}
