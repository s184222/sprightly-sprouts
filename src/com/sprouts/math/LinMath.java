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
	
	public static boolean intersect(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
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
		
		if (((ratiox1 >= 0 && ratiox1 <= 1) || (ratioy1 >= 0 && ratioy1 <= 1)) && 
				((ratiox2 >= 0 && ratiox2 <= 1) || (ratioy2 >= 0 && ratioy2 <= 1))){			
			return true;
			
		}
		
		return false;
	}
	

	public static boolean contains(Vertex a, Vertex b, Vertex c, Vertex p) {
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

		return (w1 > 0.0f && w2 >= 0.0f && (w1 + w2) <= 1.0f);
	}
	
	
	public static boolean isPointInPolygon (Vertex point, List<Vertex> polygon) {
		Vertex start = polygon.get(0);
		int increment = 0;
		for (int i = 2; i < polygon.size(); i++) {
			if (contains(start,polygon.get(i-1),polygon.get(i),point)) {

				increment  ++;
			} 
		}
		return (increment % 2 != 0);
	}
	
	public static boolean isPointInPolygon (Vertex point, Vertex[] polygon) {
		Vertex start = polygon[0];
		int increment = 0;
		for (int i = 2; i < polygon.length; i++) {
			if (contains(start,polygon[i-1],polygon[i],point)) {

				increment  ++;
			} 
		}
		return (increment % 2 != 0);
	}}
