package com.sprouts.game;

import java.util.ArrayList;
import java.util.List;

import com.sprouts.game.model.Edge;
import com.sprouts.game.model.Sprout;
import com.sprouts.game.model.Vertex;
import com.sprouts.game.move.triangles.Triangle;
import com.sprouts.math.Vec2d;

/**
 * A collection of trigonometric functions concerned with the rotation.
 * 
 * Utility functions which return the first vertex, triangle or edge 
 * encountered during a clockwise or counterclockwise rotation given a start
 * line (origin to reference vertex).
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Trig {
	
	/**
	 * The algorithm behaves as if a linesegment given by (origin, reference) rotates around itself
	 * either clockwise or counterclockwise and returns the index of the first candidates which it 
	 * encounters.
	 * 
	 * @param origin
	 * @param reference
	 * @param candidateTos - vertices to consider
	 * @param clockwise if true else counterclockwise
	 * @return returns the index of the first candidate first encountered 
	 * 		   during a clockwise/counterclockwise rotation around origin with (origin, reference)
	 * 		   as the initial direction of the scanline.
	 */
	public static int getFirstOnRotation(Vertex origin, Vertex reference, List<Vertex> candidateTos, boolean clockwise) {
		Vec2d vec = new Vec2d();
		vec.set(reference.x, reference.y)
		 	 .sub(origin.x, origin.y);

		double initialAngle = vec.angle();
		double smallestAngle = 360;

		int smallestAngleIndex = -1;
		
		for (int i = 0; i < candidateTos.size(); i++) {
			Vertex to2 = candidateTos.get(i);
		
			vec.set(to2.x, to2.y)
				 .sub(origin.x, origin.y);
		
			double currentAngle = vec.angle();
			
			double deltaAngle = clockwise ? initialAngle - currentAngle : currentAngle - initialAngle;
			if (deltaAngle <= 0) deltaAngle += 360;
			
			if (deltaAngle <= smallestAngle) {
				smallestAngle = deltaAngle;
				smallestAngleIndex = i;
			}
		}
		
		return smallestAngleIndex;
	}
	
	public static Edge getFirstEdgeClockwise(Sprout origin, Vertex destination) {
		return getFirstEdge(origin.position, destination, origin.neighbours, true);
	}
	
	public static Edge getFirstEdgeCounterClockwise(Sprout origin, Vertex destination) {
		return getFirstEdge(origin.position, destination, origin.neighbours, false);
	}
	
	/**
	 * 
	 * @param origin
	 * @param reference
	 * @param tos - edges to consider
	 * @param clockwise if true else counterclockwise
	 * @return the first edge encountered during either a clockwise or counterclockwise sweep.
	 */
	public static Edge getFirstEdge(Vertex origin, Vertex reference, List<Edge> tos, boolean clockwise) {
		List<Vertex> candidates = new ArrayList<>();
		
		for (Edge neighbour : tos) {
			Vertex to = neighbour.line.getSemiFirst();
			candidates.add(to);
		}
		
		int smallestAngleIndex = Trig.getFirstOnRotation(origin, reference, candidates, clockwise);
		return tos.get(smallestAngleIndex);
	}
	
	/**
	 * 
	 * @param origin
	 * @param reference
	 * @param triangles - triangles to consider
	 * @param clockwise if true else counterclockwise
	 * @return the first triangle encountered during either a clockwise or counterclockwise sweep.
	 */
	public static Triangle getFirstTriangle(Vertex origin, Vertex reference, List<Triangle> triangles, boolean clockwise) {
		List<Vertex> candidates = new ArrayList<>();
		
		for (Triangle triangle : triangles) {
			Vertex center = triangle.getCenter();
			candidates.add(center);
		}
		
		int smallestAngleIndex = Trig.getFirstOnRotation(origin, reference, candidates, clockwise);
		return triangles.get(smallestAngleIndex);
	}
}
