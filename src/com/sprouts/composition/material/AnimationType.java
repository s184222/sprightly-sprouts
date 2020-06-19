package com.sprouts.composition.material;

import java.util.function.Function;

/**
 * @author Christian
 */
public enum AnimationType {

	LINEAR_IN(0, 1, false, p -> p),
	LINEAR_OUT(1, 0, true, p -> 1.0f - p),
	
	QUAD_IN(2, 3, false, p -> p * p),
	QUAD_OUT(3, 2, true, p -> 1.0f - p * p),

	CUBIC_IN(4, 5, false, p -> p * p * p),
	CUBIC_OUT(5, 4, true, p -> 1.0f - p * p * p);

	private static final AnimationType[] TYPES;
	
	static {
		TYPES = new AnimationType[values().length];
		
		for (AnimationType type : values())
			TYPES[type.index] = type;
	}
	
	private final int index;
	private final int oppositeIndex;
	
	private final boolean fadeOut;
	
	private final Function<Float, Float> converter;
	
	private AnimationType(int index, int oppositeIndex, boolean fadeOut, Function<Float, Float> converter) {
		this.index = index;
		this.oppositeIndex = oppositeIndex;

		this.fadeOut = fadeOut;
		
		this.converter = converter;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getOppositeIndex() {
		return oppositeIndex;
	}
	
	public boolean isFadeOut() {
		return fadeOut;
	}
	
	public Function<Float, Float> getConverter() {
		return converter;
	}
	
	public AnimationType getOpposite() {
		return TYPES[oppositeIndex];
	}
	
	public float convertLinearProgress(float progress) {
		return converter.apply(progress);
	}
}
