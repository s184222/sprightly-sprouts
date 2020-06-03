package com.sprouts.math;

public final class LinMath {
	
	public static final float EPSILON = 0.01f;
	
	private LinMath() {
	}
	
	public static int clamp(int v, int mn, int mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}

	public static float clamp(float v, float mn, float mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}

	public static long clamp(long v, long mn, long mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}
	
	public static double clamp(double v, double mn, double mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}
}
