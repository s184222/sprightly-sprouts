package com.sprouts.math;

import java.util.List;

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
	
	/*
	 * for test:
	 * public static void main(String[] args) {
	 *      Point p0 = new Point (100,100);
	 *      Point p1 = new Point (500,500);
	 *      Point p2 = new Point (600,50);
	 *      Point p3 = new Point (80,600);
	 *      Vec4 v1 = new Vec4 (p0.x, p0.y, p1.x, p1.y);
	 *      Vec4 v2 = new Vec4 (p2.x, p2.y, p3.x, p3.y);
	 *      System.out.println(LinMath.intersect(v1,v2));
	 *      System.out.println(LinMath.intersect(p0, p1, p2, p3));
	 * }
	 * 
	 * source: https://www.youtube.com/watch?v=4bIsntTiKfM
	 * source: https://www.youtube.com/watch?v=A86COO8KC58
	 */
	public static Vec2 intersect(Vec2 p0, Vec2 p1, Vec2 p2, Vec2 p3, boolean b, boolean c) {
		return intersect(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, b, c);
	}

	public static Vec2 intersect(float p0x, float p0y, float p1x, float p1y, float p2x, float p2y, float p3x, float p3y, boolean b, boolean c) {
		float a1 = p1y - p0y;
		float b1 = p0x - p1x;
		float c1 = a1 * p1x + b1 * p1y;
		float a2 = p3y - p2y;
		float b2 = p2x - p3x;
		float c2 = a2 * p2x + b2 * p2y;
		float divisor = a1 * b2 - a2 * b1;

		if (Math.abs(divisor) < EPSILON * EPSILON) {
			return null; // if divide == 0 it means the lines are parallel
		}

		float x = (b2 * c1 - b1 * c2) / divisor;
		float y = (a1 * c2 - a2 * c1) / divisor;
		float ratiox1 = (x - p0x) / (p1x - p0x);
		float ratioy1 = (y - p0y) / (p1y - p0y);
		float ratiox2 = (x - p2x) / (p3x - p2x);
		float ratioy2 = (y - p2y) / (p3y - p2y);

		if (((ratiox1 >= 0 && ratiox1 <= 1) || (ratioy1 >= 0 && ratioy1 <= 1)) && ((ratiox2 >= 0 && ratiox2 <= 1) || (ratioy2 >= 0 && ratioy2 <= 1))) {
			return new Vec2(x, y);
		}
		
		return null;
	}

	/* for test:
	 * public static void main(String[] args) {
	 *     Point p0 = new Point (2,3);
	 *     Point p1 = new Point (2,2);
	 *     Point p2 = new Point (3,3);
	 *     Point p3 = new Point (3,3);
	 *     System.out.println(LinMath.isInside(p0,p1,p2,p3));
	 * }
	 *
	 * a, b, c is the triangle p is the point.
	 * source: https://www.youtube.com/watch?v=HYAgJN3x4GA&t=164s
	 */
	public static boolean contains(List<Vec2> poly, Vec2 v) {
		Vec2 vNew = new Vec2(v.x + 10000, v.y);
		
		int increment = 0;
		for (int i = 0; i < poly.size() - 1; i++) {
			Vec2 p = intersect(poly.get(i), poly.get(i + 1), v, vNew, true, true);
			
			if (p != null)
				increment++;
		}
		
		return (increment % 2 != 0);
	}

	public static boolean contains(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
		float bx = b.x - a.x;
		float by = b.y - a.y;
		float cx = c.x - a.x;
		float cy = c.y - a.y;
		float divisor = -((bx * cy) - (by * cx));

		if (Math.abs(divisor) < EPSILON * EPSILON)
			return false;

		float px = p.x - a.x;
		float py = p.y - a.y;
		float w1 = ((cx * py) - (cy * px)) / divisor;
		float w2 = -((bx * py) - (by * px)) / divisor;

		return (w1 >= 0.0f && w2 >= 0.0f && (w1 + w2) <= 1.0f);
	}
}
