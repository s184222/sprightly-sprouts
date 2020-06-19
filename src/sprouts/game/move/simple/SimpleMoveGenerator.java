package sprouts.game.move.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.Position;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.move.MoveException;
import sprouts.game.move.Move;
import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MovePathGenerator;
import sprouts.game.move.pipe.MovePathResult;
import sprouts.game.move.triangles.Triangle;
import sprouts.game.move.triangles.TriangleGenerator;
import sprouts.game.util.MathUtil;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class SimpleMoveGenerator implements MovePathGenerator {
	
	private BiFunction<Vertex, Vertex, Float> costFunction;

	private PathFinder pathFinder;
	private TriangleGenerator triangleGenerator;
	
	private String name;
	
	public SimpleMoveGenerator(PathFinder pathFinder, TriangleGenerator triangleGenerator) {
		this.pathFinder = pathFinder;
		this.triangleGenerator = triangleGenerator;
		
		costFunction = (from, to) -> MathUtil.distance(from.x, from.y, to.x, to.y);
		
		name = "simple";
	}
	
	@Override
	public MovePathResult generate(Move move, Position position) throws MoveException {
		MovePathResult result = new MovePathResult();
		result.generatorType = name;
		
		Sprout from = move.from;
		Sprout to = move.to;

		List<Triangle> triangles = triangleGenerator.getTriangles(position);
		Map<Vertex, List<Vertex>> graph = getGraph(from.position, to.position, triangles, position);
		
		List<Vertex> endings = graph.get(to.position);
		
		// if "from" sprout and "two" sprout are the same, then a search algorithm will not create a line with a loop.
		// Instead it will return a path containing only 1 point; the position of the from/to sprout itself.
		// To ensure the line has a loop when from==to, we do a search to a neighbour vertex instead.
		// Furthermore we remove that neighbour vertex, such that the path to the neighbour contains minimum
		// 1 additional vertex which is neither the neighbour nor the start/end sprout.
		// Thus ensuring a loop is created after adding sprout as the final point on the line.
		// The line contains the following points:
		// 		sprout - some other neighbour - neighbour which we are searching after - sprout
		boolean same = from.equals(to);
		Vertex end = same ? endings.remove(0) : to.position;
		
		List<Vertex> path = pathFinder.find(from.position, end, graph, costFunction, costFunction);
		
		Line line = new Line();
		line.addAll(path);
		
		if (same) line.add(move.to.position);
		
		SimpleMoveGeneratorData data = new SimpleMoveGeneratorData();
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
	private Map<Vertex, List<Vertex>> getGraph(Vertex source, Vertex target, List<Triangle> triangles, Position position) {
		Map<Vertex, List<Vertex>> graph = new HashMap<>();
		
		for (Triangle triangle : triangles) {
			List<Vertex> adjacent = new ArrayList<>();
	
			Vertex[] corners = triangle.getCorners();
			for (int i = 0; i < corners.length; i++) {
				Vertex v0 = corners[i];
				Vertex v1 = corners[MathUtil.wrap(i+1, corners.length)];
				
				LineSegment segment = new LineSegment(v0, v1);
				
				if (segment.from.equals(source) || segment.from.equals(target)) {
					adjacent.add(segment.from);
				}
				
				if (!position.isLineSegmentOnLine(segment.from, segment.to)) {
					// due to floating precision, 2 linesegment which are reversed may yield different middle vertices, so linesegments are canonized
					// so select the same orientation of the linesegment.
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
	 * checks if a linesegment is canonized.
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
}
