package com.sprouts.math;

import java.util.List;

import sprouts.game.model.Vertex;

public final class LinMath {
	
	public static final double EPSILON = 0.01f;
	
	private LinMath() {
	}
	
	public static int clamp(int v, int mn, int mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}

	public static double clamp(double v, double mn, double mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}

	public static long clamp(long v, long mn, long mx) {
		return v < mn ? mn : (v > mx ? mx : v);
	}
	
	public static float clamp(float v, float mn, float mx) {
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
	
	public static boolean intersect(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
		return intersect(x0, y0, x1, y1, x2, y2, x3, y3, null);
	}
	
	/*
	public static boolean intersect (double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4,
			Vec2d intersection) {
			double d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
			if (d == 0) return false;

			double yd = y1 - y3;
			double xd = x1 - x3;
			double ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
			if (ua < 0 || ua > 1) return false;

			double ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
			if (ub < 0 || ub > 1) return false;

			if (intersection != null) intersection.set(x1 + (x2 - x1) * ua, y1 + (y2 - y1) * ua);
			return true;
		}
	*/
	
	public static boolean intersect(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Vec2 intersection) {
		double a1 = y1 - y0;
		double b1 = x0 - x1;
		double c1 = a1 * x1 + b1 * y1;
		double a2 = y3 - y2;
		double b2 = x2 - x3;
		double c2 = a2 * x2 + b2 * y2;
		double divisor = a1 * b2 - a2 * b1;

		if (Math.abs(divisor) <= EPSILON * EPSILON) {
			return false; // if divide == 0 it means the lines are parallel
		}

		double x = (b2 * c1 - b1 * c2) / divisor;
		double y = (a1 * c2 - a2 * c1) / divisor;
		double ratiox1 = (x - x0) / (x1 - x0);
		double ratioy1 = (y - y0) / (y1 - y0);
		double ratiox2 = (x - x2) / (x3 - x2);
		double ratioy2 = (y - y2) / (y3 - y2);
		
//		System.out.println(ratiox1 + " " + ratioy1 + " " + ratiox2 + " " + ratioy2 + " " + x + " " + y + " " + x1 + " " + y1 + " " + EPSILON);
		if (((ratiox1 >= 0 && ratiox1 <= 1) || (ratioy1 >= 0 && ratioy1 <= 1)) && 
				((ratiox2 >= 0 && ratiox2 <= 1) || (ratioy2 >= 0 && ratioy2 <= 1))){
			if (intersection != null) intersection.set((float)x, (float)y);
			
			return true;
			
		}
		
		return false;
	}
	
	//this intersect is only used for the function contains 
	public static Vec2 intersectContains(Vec2 p0, Vec2 p1, Vec2 p2, Vec2 p3) {
		double a1 = p1.y - p0.y;
		double b1 = p0.x - p1.x;
		double c1 = a1 * p1.x + b1 * p1.y;
		double a2 = p3.y - p2.y;
		double b2 =p2.x - p3.x;
		double c2 =a2 * p2.x + b2 * p2.y;
		double divisor = a1 * b2 - a2 * b1;
		
		if (Math.abs(divisor) < EPSILON*EPSILON) {
			return null; // if divide == 0 it means the lines are parallel
		}
		
		double x = (b2*c1-b1*c2)/divisor;
		double y = (a1*c2-a2*c1)/divisor;
		
		//if x and y is the same as the point we want to check, return the vec2, so that we can catch it in the function Contains
		if (x==p2.x && y==p2.y) return new Vec2((float)x, (float)y); 
		
		double ratiox1 = (x - p0.x) / (p1.x - p0.x); //if p1x == p0x then ratiox1 will be NaN
		double ratioy1 = (y - p0.y) / (p1.y - p0.y); //if p1y == p0y then ratioy1 will be NaN
		double ratiox2 = (x - p2.x) / (p3.x - p2.x); //if p3x == p2x then ratiox2 will be NaN
		double ratioy2 = (y - p2.y) / (p3.y - p2.y); //if p3y == p2y then ratioy2 will be NaN
		if (((ratiox1 > 0 && ratiox1 <=1) || (ratioy1 > 0 && ratioy1 <= 1)) && 
				((ratiox2 > 0 && ratiox2 <=1) || (ratioy2 > 0 && ratioy2 <= 1))){
			
			return new Vec2((float)x, (float)y);
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
		Vec2 vNew = new Vec2(v.x + 10, v.y + 10);
		
		int increment = 0;
		for (int i = 0; i < poly.size() - 1; i++) {
			Vec2 p = intersectContains(poly.get(i), poly.get(i + 1), v, vNew);
			
			if (p != null) {
				if (p.x==v.x && p.y==v.y) return false;
				increment++;
			}
		}
		
		return (increment % 2 != 0);
	}

	public static boolean contains(Vec2 a, Vec2 b, Vec2 c, Vec2 p) {
		double bx = b.x - a.x;
		double by = b.y - a.y;
		double cx = c.x - a.x;
		double cy = c.y - a.y;
		double divisor = -((bx * cy) - (by * cx));

		if (Math.abs(divisor) < EPSILON * EPSILON)
			return false;

		double px = p.x - a.x;
		double py = p.y - a.y;
		double w1 = ((cx * py) - (cy * px)) / divisor;
		double w2 = -((bx * py) - (by * px)) / divisor;

		return (w1 >= 0.0f && w2 >= 0.0f && (w1 + w2) <= 1.0f);
	}
	
	
//	public static void main(String[] args) {
//		
//		List<Vec2> list = new ArrayList<Vec2>();
//		list.add(new Vec2(0,0));
//		list.add(new Vec2(3,2));
//		list.add(new Vec2(4,2));
//		list.add(new Vec2(2,6));
//		list.add(new Vec2(5,4));
//		list.add(new Vec2(5,1));
//		System.out.println(LinMath.contains(list,new Vec2(4,2)));
//		System.out.println(intersect(new Vec2(0,0),new Vec2(3,3),new Vec2(2,3),new Vec2(4,3),false,true));
//	}
	
	/*
	public static boolean isPointInPolygon(Vertex point, List<Vertex> vertices) {
		List<Vec2> outer = new ArrayList<>();
		for (Vertex vertex : vertices) outer.add(new Vec2(vertex.x, vertex.y));
		Vec2 point2 = new Vec2(point.x, point.y);
		return LinMath.contains(outer, point2);
	}
	*/
	
	public static boolean isPointInPolygon (Vertex point, List<Vertex> polygon) {
		Vertex last = polygon.get(polygon.size()-1);
		double x = point.x, y = point.y;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.size(); i++) {
			Vertex vertex = polygon.get(i);
			if ((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) {
				if (vertex.x + (y - vertex.y) / (last.y - vertex.y) * (last.x - vertex.x) < x) oddNodes = !oddNodes;
			}
			last = vertex;
		}
		return oddNodes;
	}
	
	public static boolean isPointInPolygon (Vertex point, Vertex[] polygon) {
		Vertex last = polygon[polygon.length-1];
		double x = point.x, y = point.y;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.length; i++) {
			Vertex vertex = polygon[i];
			if ((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) {
				if (vertex.x + (y - vertex.y) / (last.y - vertex.y) * (last.x - vertex.x) < x) oddNodes = !oddNodes;
			}
			last = vertex;
		}
		return oddNodes;
	}
}
