package com.sprouts.composition.border;

public class Margin {

	public final int left;
	public final int right;
	public final int top;
	public final int bottom;

	public Margin(int margin) {
		this(margin, margin, margin, margin);
	}

	public Margin(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public int getHorizontalMargin() {
		return left + right;
	}

	public int getVerticalMargin() {
		return top + bottom;
	}
}
