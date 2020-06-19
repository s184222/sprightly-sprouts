package com.sprouts.composition.material;

/**
 * @author Christian
 */
public class AnimatedMaterialState implements IMaterialState {

	private final AnimationType type;
	private final int durationMillis;
	
	private int progressMillis;
	private float progress;
	
	public AnimatedMaterialState(int durationMillis) {
		this(AnimationType.LINEAR_IN, durationMillis);
	}
	
	public AnimatedMaterialState(AnimationType type, int durationMillis) {
		if (durationMillis < 0)
			throw new IllegalArgumentException("durationMillis must be non-negative!");
		
		this.type = type;
		this.durationMillis = durationMillis;
		
		progressMillis = 0;
		progress = type.convertLinearProgress((durationMillis == 0) ? 1.0f : 0.0f);
	}
	
	@Override
	public void dynamicUpdate(int deltaMillis) {
		progressMillis += deltaMillis;
		
		if (progressMillis >= durationMillis) {
			progressMillis = durationMillis;
			progress = type.convertLinearProgress(1.0f);
		} else {
			float p = (float)progressMillis / durationMillis;
			progress = type.convertLinearProgress(p);
		}
	}

	@Override
	public boolean isDynamic() {
		return (progressMillis < durationMillis);
	}
	
	public AnimationType getType() {
		return type;
	}
	
	public int getDurationMillis() {
		return durationMillis;
	}
	
	public int getProgressMillis() {
		return progressMillis;
	}
	
	/**
	 * @return The current progress of the animation as a floating-point value between
	 *         0.0 and 1.0 inclusive, depending on what type of animation is specified.
	 */
	public float getProgress() {
		return progress;
	}
}
