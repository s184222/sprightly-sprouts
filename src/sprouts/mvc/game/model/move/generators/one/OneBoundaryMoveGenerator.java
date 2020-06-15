package sprouts.mvc.game.model.move.generators.one;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.sprouts.math.LinMath;

import sprouts.mvc.game.model.Edge;
import sprouts.mvc.game.model.GraphicalFacade;
import sprouts.mvc.game.model.Line;
import sprouts.mvc.game.model.LineSegment;
import sprouts.mvc.game.model.MathUtil;
import sprouts.mvc.game.model.Position;
import sprouts.mvc.game.model.Region;
import sprouts.mvc.game.model.Sprout;
import sprouts.mvc.game.model.Trig;
import sprouts.mvc.game.model.Util;
import sprouts.mvc.game.model.Vertex;
import sprouts.mvc.game.model.move.Move;
import sprouts.mvc.game.model.move.MoveException;
import sprouts.mvc.game.model.move.MovePathGenerator;
import sprouts.mvc.game.model.move.MovePathResult;
import sprouts.mvc.game.model.move.PathFinder;
import sprouts.mvc.game.model.move.Triangle;
import sprouts.mvc.game.model.move.TriangleGenerator;

public class OneBoundaryMoveGenerator implements MovePathGenerator {
	
	private PathFinder pathfinder;
	private TriangleGenerator triangleGenerator;
	
	private String name;
	
	public OneBoundaryMoveGenerator(PathFinder pathfinder, TriangleGenerator triangleGenerator) {
		this.pathfinder = pathfinder;
		this.triangleGenerator = triangleGenerator;
		
		name = "oneBoundary";
	}

	@Override
	public MovePathResult generate(Move move, Position position) throws MoveException {
		MovePathResult result = new MovePathResult();
		result.generatorType = name;
		
		Sprout from = move.from;
		Sprout to = move.to;
		
		List<Sprout> inner = move.inners;
		
		Edge fromEdge = move.fromEdge;
		Edge toEdge = move.toEdge;

		Region region = move.region;
		
		if (!region.isInSameBoundary(from, to)) {
			throw new IllegalStateException("this move generator can only be used if from and to are in the same boundary, which they are not.");
		}
		
		List<Triangle> triangles = triangleGenerator.getTriangles(position);
		
		Map<Triangle, List<Triangle>> graph = getTriangleGraph(triangles, region, position);
		List<Triangle> slithering = slither(graph, from, to, fromEdge, toEdge);
		
		List<Triangle> wrapping = wrapAroundContaining(slithering, inner, graph, region, from.position, to.position);
		Util.require(canCreatePath(slithering, from.position, to.position));
		
		condense(slithering, region);
		Util.require(canCreatePath(slithering, from.position, to.position));
		
		List<Vertex> path = createPathFromTriangles(slithering, from.position, to.position);

		Line line = new Line();
		line.addAll(path);
		
		OneBoundaryMoveGeneratorData data = new OneBoundaryMoveGeneratorData();
		data.triangles = triangles;
		data.oneBoundaryGraph = graph;
		data.slither = slithering;
		data.wrapper = wrapping;
		
		result.line = line;
		result.customData = data;
		
		return result;
	}
	
	private Map<Triangle, List<Triangle>> getTriangleGraph(List<Triangle> triangles, Region region, Position position) {
		Map<Triangle, List<Triangle>> graph = new HashMap<>();

		for (int i = 0; i < triangles.size(); i++) {
			Triangle triangleA = triangles.get(i);
			
			if (!region.isInsideRegion(triangleA.getCenter())) continue;
			
			for (int j = i + 1; j < triangles.size(); j++) {
				Triangle triangleB = triangles.get(j);
				
				if (!areTrianglesAdjacent(triangleA, triangleB, position)) continue;
				
				List<Triangle> adjacencyA = graph.get(triangleA);
				if (adjacencyA == null) {
					adjacencyA = new ArrayList<>();
					graph.put(triangleA, adjacencyA);
				}
				adjacencyA.add(triangleB);
				
				List<Triangle> adjacencyB = graph.get(triangleB);
				if (adjacencyB == null) {
					adjacencyB = new ArrayList<>();
					graph.put(triangleB, adjacencyB);
				}
				adjacencyB.add(triangleA);
			}
		}
		
		return graph;
	}

	private boolean areTrianglesAdjacent(Triangle triangleA, Triangle triangleB, Position position) {
		LineSegment segment = getSharedLineSegment(triangleA, triangleB);
		if (segment == null) return false;
		if (position.isLineSegmentOnLine(segment.from, segment.to)) return false;
		return true;
	}

	private List<Triangle> wrapAroundContaining(List<Triangle> slithering, List<Sprout> inner, Map<Triangle, List<Triangle>> graph, Region region, Vertex from, Vertex to) {

		List<Triangle> lastWrapping = new ArrayList<>();
		
		BiFunction<Triangle, Triangle, Float> costFunction = new BiFunction<Triangle, Triangle, Float>() {

			@Override
			public Float apply(Triangle from, Triangle to) {
				if (slithering.contains(from) && slithering.contains(to)) return 0f;
				
				Vertex fromCenter = from.getCenter();
				Vertex toCenter = to.getCenter();
				
				return MathUtil.distance(fromCenter.x, fromCenter.y, toCenter.x, toCenter.y);
			}
		};
		
		for (Sprout sprout : inner) {
			Triangle source = slithering.get(0);
			
			Triangle goal = getWrapTriangle(sprout, slithering, graph);
			List<Triangle> path = pathfinder.find(source, goal, graph, costFunction, costFunction);

			Triangle slitherEntry = reduceInitialSearchPath(slithering, path);
			
			List<Triangle> pathReversed = new ArrayList<>();
			pathReversed.addAll(path);
			Collections.reverse(pathReversed);
			
			List<Triangle> wrapping = wrap(sprout, goal, region, graph);

			path.addAll(wrapping);
			path.addAll(pathReversed);
			path.add(slitherEntry);

			lastWrapping.clear();
			lastWrapping.addAll(wrapping);

			int at = getSlitherEntryIndex(slithering, slitherEntry, from, to, path);
			slithering.addAll(at + 1, path);
		}

		return lastWrapping;
	}

	private Triangle reduceInitialSearchPath(List<Triangle> slithering, List<Triangle> path) {
		Triangle slitherEntry = null;
		for (Iterator<Triangle> it = path.iterator(); it.hasNext();) {
			Triangle triangle = it.next();
			
			if (!slithering.contains(triangle)) break;
			slitherEntry = triangle;
			
			it.remove();
		}
		
		Util.require(slitherEntry != null);
		
		return slitherEntry;
	}

	private int getSlitherEntryIndex(List<Triangle> slithering, Triangle slitherEntry, Vertex from, Vertex to, List<Triangle> path) {

		List<Integer> candidateIndices = new ArrayList<>();
		for (int i = 0; i < slithering.size(); i++) {
			Triangle slitherTriangle = slithering.get(i);
			if (slitherTriangle.equals(slitherEntry)) {
				candidateIndices.add(i);
			}
		}
		
		Util.require(candidateIndices.size() > 0);
		
		int at = -1;
		for (int index : candidateIndices) {
			List<Triangle> slitherCopy = new ArrayList<>();

			slitherCopy.addAll(slithering);
			slitherCopy.addAll(index + 1, path);
			
			if (canCreatePath(slitherCopy, from, to)) {
				at = index;
				break;
			}
		}
		
		Util.require(at != -1);
		
		return at;
	}
	
	private Triangle getWrapTriangle(Sprout sprout, List<Triangle> slithering, Map<Triangle, List<Triangle>> graph) {
		List<Triangle> candidates = getTrianglesTouching(sprout.position, graph);
		return candidates.get(0);
	}

	private List<Triangle> getTrianglesTouching(Vertex vertex, Map<Triangle, List<Triangle>> graph) {
		return getTrianglesTouching(vertex, graph.keySet());
	}
	
	private List<Triangle> getTrianglesTouching(Vertex vertex, Collection<Triangle> triangles) {
		List<Triangle> candidates = new ArrayList<>();
		for (Triangle triangle : triangles) {
			if (triangle.isCorner(vertex)) candidates.add(triangle);
		}
		
		return candidates;
	}
	
	private List<Triangle> moveAround(Triangle start, Triangle end, Line line, Map<Triangle, List<Triangle>> graph) {
		List<Triangle> path = new ArrayList<>();
		path.add(start);
		
		int at = advanceBy(0, start, line);
		while (true) {
			Triangle current = path.get(path.size() - 1);
			
			Vertex l0 = line.get(at);

			List<Triangle> neighbours = graph.get(current);
			List<Triangle> candidates = getTrianglesTouching(l0, neighbours);
			
			Triangle next = Trig.getFirstTriangle(l0, current.getCenter(), candidates, true);

			at += advanceBy(at, next, line);
			
			path.add(next);
			
			if (at >= line.size() - 1 && next.equals(end)) break;
		}
		
		return path;
	}

	
	private List<Triangle> wrap(Sprout sprout, Triangle start, Region region, Map<Triangle, List<Triangle>> graph) {
		
		Line mergedLine;
		if (sprout.neighbours.size() > 0) {
			Vertex reference = start.getCenter();
			Edge edge = Trig.getFirstEdgeClockwise(sprout, reference);

			mergedLine = edge.getBoundaryLine();
			mergedLine.add(mergedLine.getFirst());
		} else {
			mergedLine = new Line();
			mergedLine.add(sprout.position);
		}

		List<Triangle> wrapper = moveAround(start, start, mergedLine, graph);
		
		// remove the entries
		wrapper.remove(0);
		wrapper.remove(wrapper.size() - 1);
		
		return wrapper;
	}

	private Triangle getStartTriangle(Sprout sprout, Vertex reference, Map<Triangle, List<Triangle>> graph) {
		return getTriangleEntry(sprout, reference, graph, true);
	}
	
	private Triangle getEndTriangle(Sprout sprout, Vertex reference, Map<Triangle, List<Triangle>> graph) {
		return getTriangleEntry(sprout, reference, graph, false);
	}
	
	private Triangle getTriangleEntry(Sprout sprout, Vertex reference, Map<Triangle, List<Triangle>> graph, boolean clockwise) {
		List<Triangle> candidates = getTrianglesTouching(sprout.position, graph);

		Util.require(candidates.size() > 0);
		
		// a single sprout it does not matter which triangle we use.
		if (reference == null) return candidates.get(0);
		return Trig.getFirstTriangle(sprout.position, reference, candidates, clockwise);
	}

	
	private List<Triangle> slither(Map<Triangle, List<Triangle>> graph, Sprout from, Sprout to, Edge fromEdge, Edge toEdge) {
		
		Vertex fromReference = null;
		if (fromEdge != null) fromReference = fromEdge.prev.line.getSemiLast();
		Triangle start = getStartTriangle(from, fromReference, graph);
		
		Util.require(start != null);
		
		Vertex toReference = start.getCenter();
		if (toEdge != null) toReference = toEdge.line.getSemiFirst();
		Triangle end = getEndTriangle(to, toReference, graph);

		Util.require(end != null);
		
		Line mergedLine;
		if (fromEdge != null && toEdge != null) {
			mergedLine = mergeBoundaryLines(fromEdge, toEdge);
		} else {
			mergedLine = new Line();
		}
		
		if (mergedLine.size() == 0) mergedLine.add(from.position);

		List<Triangle> slither = moveAround(start, end, mergedLine, graph);

		return slither;
	}
	
	private int advanceBy(int at, Triangle next, List<Vertex> line) {
		int sharedCorners = 1;
		
		Vertex l0 = line.get(at);
		Util.require(next.isCorner(l0));
		
		int p3i = next.getCornerIndex(l0);
		Vertex[] corners = next.getCorners();
		Vertex p2 = corners[MathUtil.wrap(p3i - 1, corners.length)];
		Vertex p1 = corners[MathUtil.wrap(p3i - 2, corners.length)];
		
		if (at + 1 < line.size()) {
			Vertex l1 = line.get(at + 1);
		
			if (l1.equals(p2)) {
				sharedCorners += 1;
				
				if (at + 2 < line.size()) {
					Vertex l2 = line.get(at + 2);
					if (l2.equals(p1)) sharedCorners += 1;
				}
			}
		}
		
		int advancement = sharedCorners - 1;
		
		return advancement;
	}

	private Line mergeBoundaryLines(Edge fromEdge, Edge toEdge) {
		Line concatenatedLine = new Line();

		Edge currentEdge = fromEdge;
		while (!currentEdge.equals(toEdge)) {
			concatenatedLine.append(currentEdge.line);
			currentEdge = currentEdge.next;
		}
		
		return concatenatedLine;
	}
	
	public boolean canCreatePath(List<Triangle> triangles, Vertex from, Vertex to) {
		List<Vertex> path = createPathFromTriangles(triangles, from, to);
		return path != null;
	}
	
	public List<Vertex> createPathFromTriangles(List<Triangle> triangles, Vertex from, Vertex to) {
		List<Vertex> path = new ArrayList<>();
		
		Map<LineSegment, Integer> counts = new HashMap<>();
		List<LineSegment> pathSegments = new ArrayList<>();
	
		// 1. get linesegment between 2 adjacent triangles,
		// 2. count the number of times the line segment occurs
		// 3. store the path indices which will go through the triangle in triangleIndices.
		//		path[index] -- path[index+1]
		for (int i = 0; i < triangles.size() - 1; i++) {
			Triangle current = triangles.get(i);
			Triangle next = triangles.get(i+1);
			
			LineSegment segment = getSharedLineSegment(current, next);
			Util.require(segment != null);
			
			Integer count = counts.remove(segment);
			if (count != null) {
				count += 1;
				counts.put(segment, count);
				pathSegments.add(segment);
			} else {
				LineSegment reversedSegment = new LineSegment(segment.to, segment.from);
				Integer reversedCount = counts.remove(reversedSegment);
				if (reversedCount != null) {
					reversedCount += 1;
					counts.put(reversedSegment, reversedCount);
					pathSegments.add(reversedSegment);
				} else {
					counts.put(segment, 1);
					pathSegments.add(segment);
				}
			}
		}
		
		// create the line segment points
		
		Map<LineSegment, Integer> ats = new HashMap<>();
		Map<LineSegment, List<Vertex>> segmentToPoints = new HashMap<>();
	
		Map<Vertex, LineSegment> pointToLineSegment = new HashMap<>();
		
		for (LineSegment segment : pathSegments) {
			int total = counts.get(segment);
			
			Integer at = ats.remove(segment);
			if (at == null) at = 0;
			at += 1;
			ats.put(segment, at);
			
			float ratio = at * 1f / (total + 1f);
			
			Vertex vertex = segment.getVertexAt(ratio);
			
			List<Vertex> points = segmentToPoints.get(segment);
			if (points == null) {
				points = new ArrayList<>();
				segmentToPoints.put(segment, points);
			}
			points.add(vertex);
			
			pointToLineSegment.put(vertex, segment);
		}
	
		// create random path
		
		path.add(from);
	
		for (int i = 0; i < pathSegments.size(); i++) {
			LineSegment segment = pathSegments.get(i);
			List<Vertex> points = segmentToPoints.get(segment);
			
			Util.require(points.size() > 0);
			
			Vertex point = points.remove(0);
			path.add(point);
		}
	
		path.add(to);
		
		// fix path entanglements
		
		for (int i = 0; i < path.size() - 1; i++) {
			for (int j = i+1; j < path.size() - 1; j++) {
				Vertex p0 = path.get(i);
				Vertex p1 = path.get(i + 1);
				
				Vertex q0 = path.get(j);
				Vertex q1 = path.get(j + 1);
				
				// @TODO: fix problem where the lines are parallel and intersect.
				if (p0.x == q0.x && p0.y == q0.y) continue;
				if (p0.x == q1.x && p0.y == q1.y) continue;
				if (p1.x == q0.x && p1.y == q0.y) continue;
				if (p1.x == q1.x && p1.y == q1.y) continue;
				
				if (LinMath.intersect(p0.x, p0.y, p1.x, p1.y, q0.x, q0.y, q1.x, q1.y)) {
					LineSegment p1s = pointToLineSegment.get(p1);
					LineSegment q0s = pointToLineSegment.get(q0);
					LineSegment q1s = pointToLineSegment.get(q1);
					
					if (p1s != null && q0s != null && p1s.equals(q0s)) {
						Collections.swap(path, i+1, j);
					}	else if (p1s != null && q1s != null && p1s.equals(q1s)){
						Collections.swap(path, i+1, j+1);
					} else {
						return null;
						//System.out.printf("%s %s %s %s\n", p0, p1, q0, q1);
						//Util.require(false, "broken");
						//return path;
					}
				}
			}
		}
		
		// @TODO: remove
		if (intersectsWithItself(path)) Util.require(false);
		
		return path;
	}

	// @TODO: remove sanity check.
	private boolean intersectsWithItself(List<Vertex> path) {
		for (int i = 0; i < path.size() - 1; i++) {
			for (int j = i+1; j < path.size() - 1; j++) {
				Vertex p0 = path.get(i);
				Vertex p1 = path.get(i + 1);
				
				Vertex q0 = path.get(j);
				Vertex q1 = path.get(j + 1);
				
				if (p0.x == q0.x && p0.y == q0.y) continue;
				if (p0.x == q1.x && p0.y == q1.y) continue;
				if (p1.x == q0.x && p1.y == q0.y) continue;
				if (p1.x == q1.x && p1.y == q1.y) continue;
				
				if (LinMath.intersect(p0.x, p0.y, p1.x, p1.y, q0.x, q0.y, q1.x, q1.y)) {
					System.out.printf("collides %d %d\n", i, j);
					return true;
				}
			}
		}
			
		return false;
	}
	
	private LineSegment getSharedLineSegment(Triangle triangleA, Triangle triangleB) {
		Vertex[] corners = triangleA.getCorners();
		
		for (int i = 0; i < corners.length; i++) {
			Vertex prev = corners[MathUtil.wrap(i-1, corners.length)];
			Vertex current = corners[i];
			
			if (triangleB.isCorner(current) && triangleB.isCorner(prev)) {
				return new LineSegment(prev, current);
			}
		}
		
		return null;
		//throw new IllegalStateException("no common side");
	}
	
	private void condense(List<Triangle> slither, Region region) {

		boolean removing = true;
		while (removing) {
			removing = false;
			
			Map<Triangle, List<Integer>> triangleToIndices = new HashMap<>();
			
			for (int i = 0; i < slither.size(); i++) {
				Triangle triangle = slither.get(i);
				
				List<Integer> indices = triangleToIndices.get(triangle);
				if (indices == null) {
					indices = new ArrayList<>();
					triangleToIndices.put(triangle, indices);
				}

				indices.add(i);
			}
			
			for (int i = 0; i < slither.size(); i++) {
				Triangle triangle = slither.get(i);
				
				List<Integer> indices = triangleToIndices.get(triangle);
				indices.remove(0);
				
				if (indices.size() == 0) continue;
				
				int next = indices.get(0);
				List<Triangle> condensable = slither.subList(i, next + 1);

				Vertex end = triangle.getCenter();
				if (!canCreatePath(condensable, end, end)) continue;
				
				List<Vertex> path = createPathFromTriangles(condensable, end, end);
				path.remove(0);	// start and end vertex are the same, isPointInPolygon expects that they are not the same. 
												// So remove one of them.

				boolean shouldRemove = true;
				
				List<Sprout> innerSprouts = region.getInnerSprouts();
				for (Sprout single : innerSprouts) {
					if (GraphicalFacade.isPointInPolygon(single.position, path)) {
						shouldRemove = false;
						break;
					}
				}
				
				if (shouldRemove) {
					List<Edge> innerBoundaries = region.getInnerBoundaries();
					for (Edge inner : innerBoundaries) {
						List<Sprout> sprouts = inner.getBoundarySprouts();
						
						for (Sprout sprout : sprouts) {
							if (GraphicalFacade.isPointInPolygon(sprout.position, path)) {
								shouldRemove = false;
								break;
							}
						}
					}
				}
				
				if (shouldRemove) {
					removing = true;
					slither.subList(i, next).clear();
					break;
				}
			}
		}
	}
}