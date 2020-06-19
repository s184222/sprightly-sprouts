package com.sprouts.composition;

/**
 * @author Christian
 */
public final class CompositionSize {

	public static final CompositionSize ZERO = new CompositionSize(0, 0);
	public static final CompositionSize ONE = new CompositionSize(1, 1);
	public static final CompositionSize MAX_VALUE = new CompositionSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
	
	private final int width;
	private final int height;
	
	public CompositionSize(int width, int height) {
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Size must be non-negative!");
		
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CompositionSize))
			return false;
	
		return ((CompositionSize)other).width == width && 
		       ((CompositionSize)other).height == height;
	}
}
