package com.sprouts.game.move.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.sprouts.game.Trig;
import com.sprouts.game.model.Edge;
import com.sprouts.game.model.Line;
import com.sprouts.game.model.LineSegment;
import com.sprouts.game.model.Position;
import com.sprouts.game.model.Sprout;
import com.sprouts.game.model.Vertex;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;
import com.sprouts.game.move.pathfinder.PathFinder;
import com.sprouts.game.move.pipe.LineGenerator;
import com.sprouts.game.move.pipe.LinePathResult;
import com.sprouts.game.move.triangles.Triangle;
import com.sprouts.game.move.triangles.TriangleGenerator;
import com.sprouts.game.util.MathUtil;

/**
 * 
 * Generates a line which satisfies the two boundary move.
 * 
 * @author Rasmus M�ller Larsen, s184190
 * 
 */
public class TwoBoundaryLineGenerator implements LineGenerator {
	
	private BiFunction<Vertex, Vertex, Double> costFunction;

	private PathFinder pathFinder;
	private TriangleGenerator triangleGenerator;
	
	private String name;
	
	public TwoBoundaryLineGenerator(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		this.pathFinder = pathFinder;
		this.triangleGenerator = triangleGenerator;
		
		costFunction = (from, to) -> MathUtil.distance(from.x, from.y, to.x, to.y);
		
		name = "twoBoundary";
	}
	
	@Override
	public LinePathResult generate(Move move, Position position) throws MoveException {
		LinePathResult result = new LinePathResult();
		result.generatorType = name;
		
		Sprout from = move.from;
		Sprout to = move.to;

		if (move.inners.size() != 0) throw new MoveException("Sprout %d and %d are in different boundaries. Supplying inner sprouts does not make sense.", from.id, to.id);
		
		Edge fromEdge = move.fromEdge;
		Edge toEdge = move.toEdge;

		List<Triangle> triangles = triangleGenerator.getTriangles(position);
		Map<Vertex, List<Vertex>> graph = getVertexGraph(from.position, to.position, fromEdge, toEdge, triangles, position);
		
		List<Vertex> path = pathFinder.find(from.position, to.position, graph, costFunction, costFunction);
		if (path.size() == 0) throw new MoveException("could not generate the path");
		
		Line line = new Line();
		line.addAll(path);
		
		TwoBoundaryLineGeneratorData data = new TwoBoundaryLineGeneratorData();
		data.twoBoundaryGraph = graph;
		data.triangles = triangles;

		result.line = line;
		result.customData = data;
		
		return result;
	}
	
	/**
	 * The only sprouts which the graph contains is the source sprout and target sprout.
	 * 
	 * @param source - position of the "from" sprout
	 * @param target - position of the "to" sprout
	 * @param triangles - the position triangulated
	 * @param position - the current position
	 * @return a mapping between a vertex and its neighbours
	 * 
	 */
	private Map<Vertex, List<Vertex>> getVertexGraph(Vertex source, Vertex target, Edge sourceEdge, Edge targetEdge, List<Triangle> triangles, Position position) {
		Map<Vertex, List<Vertex>> graph = new HashMap<>();
		
		for (Triangle triangle : triangles) {
			List<Vertex> adjacent = new ArrayList<>();
	
			Vertex[] corners = triangle.getCorners();
			for (int i = 0; i < corners.length; i++) {
				Vertex v1 = corners[i];
				Vertex v2 = corners[MathUtil.wrap(i+1, corners.length)];
				
				LineSegment segment = new LineSegment(v1, v2);
				
				// if a sprout is in a boundary, then it may have more sides
				// so check that the we are on the correct side by comparing with sourceEdge and targetEdge.
				if (isCorrectSide(segment, source, sourceEdge) || isCorrectSide(segment, target, targetEdge)) {
					adjacent.add(segment.from);
				}
				
				if (!position.isLineSegmentOnLine(segment.from, segment.to)) {
					// due to double precision, 2 linesegment which are reversed may yield different middle vertices, so linesegments are canonized:
					// the same orientation of the linesegment is always selected.
					if (shouldReverse(segment)) segment.reverse();
					Vertex vertex = segment.getMiddle();
					adjacent.add(vertex);
				}
			}
			
			List<Vertex> neighbours = new ArrayList<>();
			neighbours.addAll(adjacent);
			
			for (Vertex vertex : adjacent) {
				neighbours.remove(vertex);
				
				List<Vertex> adjacencyList = graph.remove(vertex);
				if (adjacencyList == null) adjacencyList = new ArrayList<>();
				
				for (Vertex neighbour : neighbours) {
					if (!adjacencyList.contains(neighbour)) {
						adjacencyList.add(neighbour);
					}
				}
				
				graph.put(vertex, adjacencyList);
				
				neighbours.add(vertex);
			}
		}
					
		return graph;
	}
	
	/*
	 *  checks if a linesegment is canonized. 
	 */
	private boolean shouldReverse(LineSegment segment) {
		if (segment.to.x < segment.from.x) {
			return true;
		} else if (segment.to.x == segment.from.x) {
			if (segment.to.y < segment.from.y) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isCorrectSide(LineSegment segment, Vertex origin, Edge edge) {
		return segment.from.equals(origin) && isCorrectSide(edge, segment.to);
	}
	
	private boolean isCorrectSide(Edge edge, Vertex reference) {
		if (edge == null) return true;
		if (edge.isOuterBoundary()) return true;
		Edge nextEdge = Trig.getFirstEdgeClockwise(edge.origin, reference);
		return nextEdge.equals(edge);
	}
}
