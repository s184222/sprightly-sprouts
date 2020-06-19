package com.sprouts.composition.layout;

import com.sprouts.math.LinMath;

/**
 * @author Christian
 */
public class LayoutSpecification implements Cloneable {

	public static final float ALIGN_CENTER = 0.5f;

	public static final float ALIGN_LEFT   = 0.0f;
	public static final float ALIGN_RIGHT  = 1.0f;
	public static final float ALIGN_TOP    = 0.0f;
	public static final float ALIGN_BOTTOM = 1.0f;
	
	private CompositionFill horizontalFill;
	private CompositionFill verticalFill;
	
	private float horizontalAlignment;
	private float verticalAlignment;

	public LayoutSpecification() {
		horizontalFill = CompositionFill.FILL_REMAINING;
		verticalFill = CompositionFill.FILL_REMAINING;
		
		horizontalAlignment = ALIGN_LEFT;
		verticalAlignment = ALIGN_TOP;
	}

	public CompositionFill getHorizontalFill() {
		return horizontalFill;
	}

	public void setHorizontalFill(CompositionFill horizontalFill) {
		if (horizontalFill == null)
			throw new IllegalArgumentException("horizontalFill is null!");
		
		this.horizontalFill = horizontalFill;
	}

	public CompositionFill getVerticalFill() {
		return verticalFill;
	}

	public void setVerticalFill(CompositionFill verticalFill) {
		if (verticalFill == null)
			throw new IllegalArgumentException("verticalFill is null!");
		
		this.verticalFill = verticalFill;
	}
	
	public float getHorizontalAlignment() {
		return horizontalAlignment;
	}
	
	public void setHorizontalAlignment(float alignment) {
		if (Float.isNaN(alignment))
			throw new IllegalArgumentException("alignment is not a number!");

		horizontalAlignment = LinMath.clamp(alignment, ALIGN_LEFT, ALIGN_RIGHT);
	}

	public float getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(float alignment) {
		if (Float.isNaN(alignment))
			throw new IllegalArgumentException("alignment is not a number!");
		
		verticalAlignment = LinMath.clamp(alignment, ALIGN_TOP, ALIGN_BOTTOM);
	}
	
	public LayoutSpecification getCopy() {
		LayoutSpecification copy = new LayoutSpecification();
		copy.set(this);
		return copy;
	}

	public void set(LayoutSpecification other) {
		horizontalFill = other.horizontalFill;
		verticalFill = other.verticalFill;
		
		horizontalAlignment = other.horizontalAlignment;
		verticalAlignment = other.verticalAlignment;
	}
}
