package sprouts.mvc.game.model.move.two;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import sprouts.mvc.game.model.Edge;
import sprouts.mvc.game.model.Line;
import sprouts.mvc.game.model.LineSegment;
import sprouts.mvc.game.model.MathUtil;
import sprouts.mvc.game.model.Position;
import sprouts.mvc.game.model.Sprout;
import sprouts.mvc.game.model.Trig;
import sprouts.mvc.game.model.Vertex;
import sprouts.mvc.game.model.move.Move;
import sprouts.mvc.game.model.move.MoveException;
import sprouts.mvc.game.model.move.MovePathGenerator;
import sprouts.mvc.game.model.move.MovePathResult;
import sprouts.mvc.game.model.move.PathFinder;
import sprouts.mvc.game.model.move.Triangle;
import sprouts.mvc.game.model.move.TriangleGenerator;

public class TwoBoundaryMoveGenerator implements MovePathGenerator {
	
	private BiFunction<Vertex, Vertex, Float> costFunction;

	private PathFinder pathFinder;
	private TriangleGenerator triangleGenerator;
	
	private String name;
	
	public TwoBoundaryMoveGenerator(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
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
		List<Vertex> path = pathFinder.find(from.position, to.position, graph, costFunction, costFunction);
		
		Line line = new Line();
		line.addAll(path);
		
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
				adjacencyList.addAll(neighbours);
				graph.put(vertex, adjacencyList);
				
				neighbours.add(vertex);
			}
		}
					
		return graph;
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
