package sprouts.game;

import java.util.ArrayList;
import java.util.List;

import com.sprouts.math.Vec2;

import sprouts.game.model.Edge;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.move.triangles.Triangle;

public class Trig {
	
	public static int getFirstOnRotation(Vertex origin, Vertex reference, List<Vertex> candidateTos, boolean clockwise) {
		Vec2 vec = new Vec2();
		vec.set(reference.x, reference.y)
		 	 .sub(origin.x, origin.y);

		float initialAngle = vec.angle();
		float smallestAngle = 360;

		int smallestAngleIndex = -1;
		
		for (int i = 0; i < candidateTos.size(); i++) {
			Vertex to2 = candidateTos.get(i);
		
			vec.set(to2.x, to2.y)
				 .sub(origin.x, origin.y);
		
			float currentAngle = vec.angle();
			
			float deltaAngle = clockwise ? initialAngle - currentAngle : currentAngle - initialAngle;
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
	
	public static Edge getFirstEdge(Vertex origin, Vertex reference, List<Edge> tos, boolean clockwise) {
		List<Vertex> candidates = new ArrayList<>();
		
		for (Edge neighbour : tos) {
			Vertex to = neighbour.line.getSemiFirst();
			candidates.add(to);
		}
		
		int smallestAngleIndex = Trig.getFirstOnRotation(origin, reference, candidates, clockwise);
		return tos.get(smallestAngleIndex);
	}
	
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
