package sprouts.game.model.move.generators.two;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import sprouts.game.model.Edge;
import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.MathUtil;
import sprouts.game.model.Position;
import sprouts.game.model.Sprout;
import sprouts.game.model.Trig;
import sprouts.game.model.Vertex;
import sprouts.game.model.move.Move;
import sprouts.game.model.move.MoveException;
import sprouts.game.model.move.Triangle;
import sprouts.game.model.move.TriangleGenerator;
import sprouts.game.model.move.generators.MovePathGenerator;
import sprouts.game.model.move.generators.MovePathResult;
import sprouts.game.model.move.pathfinder.PathFinder;

public class SimpleMoveGenerator implements MovePathGenerator {
	
	private BiFunction<Vertex, Vertex, Float> costFunction;

	private PathFinder pathFinder;
	private TriangleGenerator triangleGenerator;
	
	private String name;
	
	public SimpleMoveGenerator(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		this.pathFinder = pathFinder;
		this.triangleGenerator = triangleGenerator;
		
		costFunction = (from, to) -> MathUtil.distance(from.x, from.y, to.x, to.y);
		
		name = "twoBoundary";
	}
	
	@Override
	public MovePathResult generate(Move move, Position position) throws MoveException {
		MovePathResult result = new MovePathResult();
		result.generatorType = name;
		
		Sprout from = move.from;
		Sprout to = move.to;

		if (move.inners.size() != 0) throw new MoveException("Sprout %d and %d are in different boundaries. Supplying inner sprouts does not make sense.", from.id, to.id);
		
		Edge fromEdge = move.fromEdge;
		Edge toEdge = move.toEdge;

		List<Triangle> triangles = triangleGenerator.getTriangles(position);
		Map<Vertex, List<Vertex>> graph = getVertexGraph(from.position, to.position, fromEdge, toEdge, triangles, position);
		
		List<Vertex> endings = graph.get(to.position);
		
		boolean same = from.equals(to);
		Vertex end = same ? endings.remove(0) : to.position;
		
		List<Vertex> path = pathFinder.find(from.position, end, graph, costFunction, costFunction);
		
		System.out.printf("%d\n", path.size());
		
		Line line = new Line();
		line.addAll(path);
		
		if (same) line.add(move.to.position);
		
		TwoBoundaryMoveGeneratorData data = new TwoBoundaryMoveGeneratorData();
		data.twoBoundaryGraph = graph;
		data.triangles = triangles;

		result.line = line;
		result.customData = data;
		
		return result;
	}
	
	private Map<Vertex, List<Vertex>> getVertexGraph(Vertex source, Vertex target, Edge sourceEdge, Edge targetEdge, List<Triangle> triangles, Position position) {
		Map<Vertex, List<Vertex>> graph = new HashMap<>();
		
		for (Triangle triangle : triangles) {
			List<Vertex> adjacent = new ArrayList<>();
	
			Vertex[] corners = triangle.getCorners();
			for (int i = 0; i < corners.length; i++) {
				Vertex v1 = corners[i];
				Vertex v2 = corners[MathUtil.wrap(i+1, corners.length)];
				
				LineSegment segment = new LineSegment(v1, v2);
				
				if (isCorrectSide(segment, source, sourceEdge) || isCorrectSide(segment, target, targetEdge)) {
					adjacent.add(segment.from);
				}
				
				if (!position.isLineSegmentOnLine(segment.from, segment.to)) {
					// due to floating precision, 2 linesegment which are revsered may yield different middle vertices, so linesegments are canonized
					// so the same orientation of the linesegment is always selected.
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
