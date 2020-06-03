package sprouts.mvc.game.model.representation.graphical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.sprouts.math.Vec2;

import sprouts.mvc.game.model.delaunay.DelaunayTriangulator;
import sprouts.mvc.game.model.delaunay.NotEnoughPointsException;
import sprouts.mvc.game.model.delaunay.Triangle2D;
import sprouts.mvc.game.model.delaunay.Vector2D;

public class GraphicalFacade {
	
	public ArrayList<Sprout> sprouts;
	public ArrayList<Edge> edges;
	public ArrayList<Line> lines;
	public ArrayList<Line> nonReversedLines;	// @hack
	public ArrayList<Region> regions;
	public ArrayList<Vertex> vertices;
	public Region outerRegion;	// is also contained in regions.
	
	public Sprout from;
	public float minimumDistance;
	public ArrayList<Vertex> currentLine;
	
	private int sproutId;
	private int edgeId;
	private int vertexId;
	
	boolean drawingLine;
	
	public int sproutRadius;
	
	public GraphicalFacade() {
		currentLine = new ArrayList<>();
		
		minimumDistance = 25;
		
		sproutId = 1;
		edgeId = 1;
		vertexId = 1;
		
		sprouts = new ArrayList<>();
		edges = new ArrayList<>();
		lines = new ArrayList<>();
		nonReversedLines = new ArrayList<>();
		regions = new ArrayList<>();
		vertices = new ArrayList<>();

		Region region = new Region();
		regions.add(region);
		outerRegion = region;

		createSproutsCircle(5);

		/*
		createFreshSprout(140, 360, outerRegion);
		createFreshSprout(340, 360, outerRegion);
		createFreshSprout(140, 160, outerRegion);
		createFreshSprout(440, 180, outerRegion);
		createFreshSprout(100, 60, outerRegion);
		createFreshSprout(540, 60, outerRegion);
		createFreshSprout(540, 400, outerRegion);
		createFreshSprout(100, 400, outerRegion);
		*/
		
		
		// @todo.
		// "outer boundary" hardcoded for now.
		// we will probably create this on the fly when do getTriangles()
		// so we don't limit the world size?
		
		drawingLine = false;
		sproutRadius = 8;
	}
	
	private void createSproutsCircle(int initialNumberOfSprouts) {
		Vec2 rotater = new Vec2();

		float centerX = 320;
		float centerY = 240;
		float radius = 140;
		
		float deltaDegrees = 360 / (float) initialNumberOfSprouts;
		
		rotater.set(1,0);
		for (int i = 0; i < initialNumberOfSprouts; i++) {
			rotater.normalize().setAngle(deltaDegrees * i);
			rotater.mul(radius);
			
			float x = centerX + rotater.x;
			float y = centerY + rotater.y;
			
			createFreshSprout(x, y, outerRegion);
		}
	}

	private Edge createEdge(Sprout origin, Line line, Region region) {
		Edge edge = new Edge();
		edge.id = edgeId;
		edge.origin = origin;
		edge.line = line;
		edge.region = region;
		edgeId += 1;
		edges.add(edge);

		return edge;
	}

	private Line[] splitLine(Line line) {
		int centerIndex = (line.size() - 1) / 2;
		
		Line line1 = new Line();
		line1.addAll(line.subList(0, centerIndex + 1));
		
		Line line2 = new Line();
		line2.addAll(line.subList(centerIndex, line.size()));
		
		Line[] lines = {line1, line2};

		return lines;
	}
	
	public Move getMove(Sprout from, Sprout to, Region region, ArrayList<Vertex> currentLine) {
		if (isInSameBoundary(from, to, region)) {
			Move move = getOneBoundaryMove(from, to, region, currentLine);
			//System.out.printf("1) %s\n", move);
			return move;
		} else {
			Move move = getTwoBoundaryMove(from, to, region, currentLine);
			//System.out.printf("2) %s\n", move);
			return move;
		}

	}
	
	public Move getTwoBoundaryMove(Sprout from, Sprout to, Region region, ArrayList<Vertex> currentLine) {

		Line line = new Line();
		line.addAll(currentLine);
		
		addMiddlePoint(line);
		
		boolean isFromOuterBoundary = isInOuterBoundary(from, region);
		boolean isToOuterBoundary = isInOuterBoundary(to, region);
		
		if (isToOuterBoundary) {
			// swap
			Sprout tmp = from;
			from = to;
			to = tmp;
			
			Collections.reverse(line);
		}
		
		solidifyVertices(line);
		
		Line[] splitted = splitLine(line);
		Line line1 = splitted[0];
		Line line2 = splitted[1];
		
		lines.add(line1);
		lines.add(line2);
		
		nonReversedLines.add(line1);
		nonReversedLines.add(line2);
		
		Move move = new Move();
		move.fromId = from.id;
		move.toId = to.id;
		
		Vertex createdPosition = line2.get(0);
		Sprout createdSprout = createMiddleSprout(createdPosition);
		
		Edge e1_1 = createEdge(from, line1, region);
		Edge e1_2 = createEdge(createdSprout, reversed(line1), region);
		Edge e2_1 = createEdge(createdSprout, line2, region);
		Edge e2_2 = createEdge(to, reversed(line2), region);
		
		twin(e1_1, e1_2);
		twin(e2_1, e2_2);
		
		boolean isFromSingleSpot = region.innerSprouts.contains(from);
		boolean isToSingleSpot = region.innerSprouts.contains(to);
		
		if (isFromOuterBoundary && isToOuterBoundary) {
			throw new IllegalStateException("broken! both are in outer boundary. A 1 boundary move should be executed.");
		}
		
		// remove old boundary representives.
		removeBoundary(to, region);
		removeBoundary(from, region);
		
		// add the new representitive to the region.
		// @note: outdated after the swap, but we dont care which is which.
		if (isFromOuterBoundary || isToOuterBoundary) {
			region.outer = e1_1;
		} else {
			region.innerBoundaries.add(e1_1);
		}
		
		Vertex first = getSemiFirst(line);
		Vertex last = getSemiLast(line);

		Edge e1_1_prev = isFromSingleSpot ? e1_2 : getFirstEdgeCounterClockwise(from, first).twin;
		Edge e1_2_after = isFromSingleSpot ? e1_1 : getFirstEdgeClockwise(from, first);
		Edge e2_1_after = isToSingleSpot ? e2_2 : getFirstEdgeClockwise(to, last);
		Edge e2_2_prev = isToSingleSpot ? e2_1 : getFirstEdgeCounterClockwise(to, last).twin;

		// we check if e1_2_after (which was the old next of the from edge)
		// is ascending or not. We will use this info, when we have to figure which
		// edge to pick when the computer auto-generates the path given a "Move".
		// An inner boundary can have to edges with a sprout as origin. Thus we
		// have to know which was should connect.
		move.fromAscending = isAscending(e1_2_after);	
		move.toAscending = isAscending(e2_1_after);

		connect(e1_1, e2_1);
		connect(e2_2, e1_2);
		
		connect(e1_1_prev, e1_1);
		connect(e1_2, e1_2_after);
		connect(e2_1, e2_1_after);
		connect(e2_2_prev, e2_2);
		
		createdSprout.neighbours.add(e1_2);
		createdSprout.neighbours.add(e2_1);
		
		from.neighbours.add(e1_1);
		to.neighbours.add(e2_2);
		
		updateBoundaryRepresentative(e1_1);
		
		return move;
	}
	
	private boolean isAscending(Edge edge) {
		if (edge.next == null || edge.prev == null) {
			Util.require(edge.next == null && edge.prev == null);
			// doesn't matter what we set it to. 
			// It is a single spot, so we will not use this info anyway.
			return true;	
		}

		return edge.prev.origin.id <= edge.next.origin.id;
	}
	
	private Edge getFirstEdgeClockwise(Sprout sprout, Vertex destination) {
		return getFirstEdge(sprout, destination, true);
	}
	
	private Edge getFirstEdgeCounterClockwise(Sprout sprout, Vertex destination) {
		return getFirstEdge(sprout, destination, false);
	}
	
	private boolean isInOuterBoundary(Sprout sprout, Region region) {
		ArrayList<Edge> regionNeighbours = filterNeighbours(sprout, region);
		if (regionNeighbours.isEmpty()) return false;
		Edge neighbour = regionNeighbours.get(0);
		return neighbour.representative.equals(region.outer);
	}

	private void twin(Edge e0, Edge e1) {
		e0.twin = e1;
		e1.twin = e0;
	}

	private void connect(Edge from, Edge to) {
		from.next = to;
		to.prev = from;
	}

	// update all the other edges in the boundary so they have same representive.
	private void updateBoundaryRepresentative(Edge representative) {
		traverse(representative, current -> current.representative = representative);
	}
	
	private void removeBoundary(Sprout sprout, Region region) {
		if (region.innerSprouts.contains(sprout)) {
			region.innerSprouts.remove(sprout);
		} else {
			Edge boundary = getBoundary(sprout, region);
			if (region.outer != null && region.outer.equals(boundary.representative)) {
				region.outer = null;
			} else {
				Util.require(region.innerBoundaries.contains(boundary.representative));
				region.innerBoundaries.remove(boundary.representative);
			}
		}
	}

	private Edge getBoundary(Sprout sprout, Region region) {
		if (region.outer != null) {
			Edge current = region.outer;
			while (current != null) {
				if (current.origin.equals(sprout)) return current;
				if (current.next == region.outer) break;
				current = current.next;
			}
		}
		
		for (Edge edge : region.innerBoundaries) {
			Edge current = edge;
			while (current != null) {
				if (current.origin.equals(sprout)) return current;
				if (current.next == edge) break;
				current = current.next;
			}
		}
		
		throw new IllegalStateException("could not find a boundary for: " + sprout.id);
	}
	
	private Move getOneBoundaryMove(Sprout from, Sprout to, Region region, ArrayList<Vertex> currentLine) {
		Line line = new Line();
		line.addAll(currentLine);
		
		addMiddlePoint(line);
		
		// this only matters, if the one boundary move is an inner boundary/sprout.
		// we assume that from -> line -> to -> ... -> from is clockwise
		// in the following code, because when we create the new region
		// the inner bound of the old region has to be clockwise, but the outer boundary
		// of the new region has to be counterclockwise.
		if (!isLineClockwise(from, to, line)) {
			// swap from and to
			Sprout tmp = from;
			from = to;
			to = tmp;

			Collections.reverse(line);
		}
		
		solidifyVertices(line);
		
		Line[] splitted = splitLine(line);
		Line line1 = splitted[0];
		Line line2 = splitted[1];
		
		lines.add(line1);
		lines.add(line2);
		
		nonReversedLines.add(line1);
		nonReversedLines.add(line2);
		
		Move move = new Move();
		move.fromId = from.id;
		move.toId = to.id;
		
		Vertex createdPosition = line2.get(0);
		Sprout createdSprout = createMiddleSprout(createdPosition);
		
		// this is a 1 boundary, so if from is empty then from==to (and only 1 spot)
		boolean isSingleSpot = from.neighbours.isEmpty();
		boolean isInOuterBoundary = isInOuterBoundary(from, region);
		
		// remove old boundary representive.
		removeBoundary(from, region);
		
		Region newRegion = new Region();
		regions.add(newRegion);

		Edge e2_2 = createEdge(to, reversed(line2), newRegion);
		Edge e2_1 = createEdge(createdSprout, line2, region);
		Edge e1_1 = createEdge(from, line1, region);
		Edge e1_2 = createEdge(createdSprout, reversed(line1), newRegion);
		
		twin(e1_1, e1_2);
		twin(e2_2, e2_1);
		
		if (isSingleSpot) {
			newRegion.outer = e2_2;
			region.innerBoundaries.add(e1_1);
			
			connect(e1_1, e2_1);
			connect(e2_1, e1_1);
			connect(e2_2, e1_2);
			connect(e1_2, e2_2);

		} else {
			if (isInOuterBoundary) {
				region.outer = e1_1;
				newRegion.outer = e2_2;
			} else {
				region.innerBoundaries.add(e1_1);
				newRegion.outer = e2_2;
			}
			
			Vertex first = getSemiFirst(line);
			Vertex last = getSemiLast(line);
			
			to.neighbours.add(e2_2);
			Edge e1_2_after = getFirstEdgeClockwise(from, first);
			to.neighbours.remove(e2_2);
			
			from.neighbours.add(e1_1);
			Edge e2_2_prev = getFirstEdgeCounterClockwise(to, last).twin;
			from.neighbours.remove(e1_1);
			
			Edge e1_1_prev = getFirstEdgeCounterClockwise(from, first).twin;
			Edge e2_1_after = getFirstEdgeClockwise(to, last);
			
			move.fromAscending = isAscending(e1_2_after);	
			move.toAscending = isAscending(e2_1_after);

			connect(e1_1, e2_1);
			connect(e2_2, e1_2);
			
			connect(e1_1_prev, e1_1);
			connect(e2_1, e2_1_after);
			connect(e1_2, e1_2_after);
			connect(e2_2_prev, e2_2);
		}
		
		// just the usual refreshing, the same as two boundary move + some region updating.
		createdSprout.neighbours.add(e1_2);
		createdSprout.neighbours.add(e2_1);
		
		to.neighbours.add(e2_2);
		from.neighbours.add(e1_1);
		
		updateBoundaryRepresentative(e1_1);
		updateBoundaryRepresentative(e2_2);
		
		updateInnerRegionEdgesAndSprouts(newRegion, region);
		updateOuterRegionsEdges(newRegion);
		
		// add the representatives sprouts.
		if (newRegion.innerSprouts.size() > 0 || newRegion.innerBoundaries.size() > 0) {
			move.invertedBoundaries = false;
			addInnerBoundariesToMove(move, newRegion);
		} else {
			move.invertedBoundaries = true;
			addInnerBoundariesToMove(move, region);
		}
		
		return move;
	}

	private void solidifyVertices(Line line) {
		for (int i = 0; i < line.size(); i++) {
			Vertex vertex = line.get(i);
			if (vertex.id != 0) continue;
			setVertexId(vertex);
			vertices.add(vertex);
		}
	}

	private void setVertexId(Vertex vertex) {
		vertex.id = vertexId;
		vertexId += 1;
	}

	private void addInnerBoundariesToMove(Move move, Region region) {
		for (Edge innerBoundary : region.innerBoundaries) {
			move.inner.add(innerBoundary.origin.id);
		}
		
		for (Sprout innerSprout : region.innerSprouts) {
			move.inner.add(innerSprout.id);
		}
	}
	
	private void updateOuterRegionsEdges(Region region) {
		traverse(region.outer, current -> current.region = region);
	}
	
	private void updateInnerRegionEdgesAndSprouts(Region updatingRegion, Region oldRegion) {
		ArrayList<Vertex> updatingRegionOuterBoundary = getBoundaryVertices(updatingRegion.outer);
		ArrayList<Sprout> updatingRegionOuterSprout = getBoundarySprouts(updatingRegion.outer);
		
		ArrayList<Edge> newInnerBoundaries = new ArrayList<>();
		for (Edge inner : oldRegion.innerBoundaries) {
			if (updatingRegionOuterSprout.contains(inner.origin)) continue;
			
			if (isPointInPolygon(inner.origin.position, updatingRegionOuterBoundary)) {
				inner.region = updatingRegion;
				newInnerBoundaries.add(inner);
			}
		}
		
		ArrayList<Sprout> newInnerSprout = new ArrayList<>();
		for (Sprout innerSprout : oldRegion.innerSprouts) {
			if (isPointInPolygon(innerSprout.position, updatingRegionOuterBoundary)) {
				newInnerSprout.add(innerSprout);
			}
		}
		
		oldRegion.innerSprouts.removeAll(newInnerSprout);
		oldRegion.innerBoundaries.removeAll(newInnerBoundaries);
		
		updatingRegion.innerSprouts.addAll(newInnerSprout);
		updatingRegion.innerBoundaries.addAll(newInnerBoundaries);
	}
	
	// @incomplete: do our own isPointInPolygon() implementation
	private boolean isPointInPolygon(Vertex point, ArrayList<Vertex> vertices) {
		ArrayList<Vec2> outer = new ArrayList<>();
		for (Vertex vertex : vertices) outer.add(new Vec2(vertex.x, vertex.y));
		Vec2 point2 = new Vec2(point.x, point.y);
		return isPointInPolygon(outer, point2);
	}
	
	public static boolean isPointInPolygon (ArrayList<Vec2> polygon, Vec2 point) {
		Vec2 last = polygon.get(polygon.size() - 1);
		float x = point.x, y = point.y;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.size(); i++) {
			Vec2 vertex = polygon.get(i);
			if ((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) {
				if (vertex.x + (y - vertex.y) / (last.y - vertex.y) * (last.x - vertex.x) < x) oddNodes = !oddNodes;
			}
			last = vertex;
		}
		return oddNodes;
	}
	
	// @todo: reconsider if we should have an extra copy when reversing a line
	// it simplifies some parts of the code, but make other things more difficult.
	private Line reversed(Line line) {
		Line reversed = new Line();
		reversed.addAll(line);

		Collections.reverse(reversed);
		
		lines.add(reversed);

		return reversed;
	}

	private ArrayList<Edge> filterNeighbours(Sprout sprout, Region region) {
		ArrayList<Edge> filtered = new ArrayList<>();
		for (Edge neighbour : sprout.neighbours) {
			if (neighbour.region.equals(region)) filtered.add(neighbour);
		}
		
		return filtered;
	}

	private boolean isInSameBoundary(Sprout spot1, Sprout spot2, Region commonRegion) {
		// check for inner spot
		if (spot1.equals(spot2)) return true;
		
		// gets the neighbours within commonRegion
		ArrayList<Edge> neighbours1InRegion = filterNeighbours(spot1, commonRegion);
		ArrayList<Edge> neighbours2InRegion = filterNeighbours(spot2, commonRegion);
		
		if (neighbours1InRegion.isEmpty()) return false;
		if (neighbours2InRegion.isEmpty()) return false;
		
		// if a sprout has neighbours in the region, then
		// all the neighbours have the same representitive.
		// So just take the first one.
		Edge edge1 = neighbours1InRegion.get(0);
		Edge edge2 = neighbours2InRegion.get(0);
		
		return edge1.representative.equals(edge2.representative);
	}
	
	private Edge getFirstEdge(Sprout sprout, Vertex destination, boolean clockwise) {
		Vertex origin = sprout.position;
		
		Vec2 vec = new Vec2();
		vec.set(destination.x, destination.y)
		 	 .sub(origin.x, origin.y);

		float angle1 = vec.angle();
		float shortestAngle = 360;

		Edge result = null;
		
		for (Edge neighbour : sprout.neighbours) {
			LineSegment segment = getFirstSegment(neighbour);
			vec.set(segment.to.x, segment.to.y)
				 .sub(origin.x, origin.y);
		
			float angle2 = vec.angle();
			
			float deltaAngle = clockwise ? angle1 - angle2 : angle2 - angle1;
			if (deltaAngle < 0) deltaAngle += 360;
			
			if (deltaAngle <= shortestAngle) {
				shortestAngle = deltaAngle;
				result = neighbour;
			}
		}
		
		return result;
		
	}
	
	private Vertex createVertex(float x, float y) {
		Vertex vertex = new Vertex();
		vertex.x = x;
		vertex.y = y;
		setVertexId(vertex);
		vertices.add(vertex);
		return vertex;
	}

	private Sprout createFreshSprout(float x, float y, Region region) {
		Sprout sprout = new Sprout();
		sprout.position = createVertex(x, y);
		sprout.id = sproutId;
		sproutId += 1;

		sprouts.add(sprout);
		region.innerSprouts.add(sprout);

		return sprout;
	}
	
	private Sprout createMiddleSprout (Vertex vertex) {
		Sprout sprout = new Sprout();
		sprout.position = vertex;
		sprout.id = sproutId;
		sproutId += 1;

		sprouts.add(sprout);
		
		return sprout;
	}
	
	public LineSegment getFirstSegment(Edge edge) {
		LineSegment segment = new LineSegment();
		segment.from = edge.line.get(0);
		segment.to = edge.line.get(1);
		
		return segment;
	}
	
	public LineSegment getLastSegment(Edge edge) {
		LineSegment segment = new LineSegment();
		segment.from = edge.line.get(edge.line.size() - 1);
		segment.to = edge.line.get(edge.line.size() - 2);
		
		return segment;
	}
	
	public LineSegment getQuarterSegment(Edge edge) {
		int center = (edge.line.size() - 1) / 4;

		LineSegment segment = new LineSegment();
		segment.from = edge.line.get(center);
		segment.to = edge.line.get(center + 1);

		return segment;
	}
	
	public LineSegment getMiddleSegment(Edge edge) {
		int center = (edge.line.size() - 1) / 2;

		LineSegment segment = new LineSegment();
		segment.from = edge.line.get(center);
		segment.to = edge.line.get(center + 1);
		
		return segment;
	}
	
	// @todo: move into handler.
	public void touchDown(float worldX, float worldY) {
		Vec2 vec = new Vec2(worldX, worldY);

		for (Sprout sprout : sprouts) {
			if (vec.dist(sprout.position.x, sprout.position.y) <= sproutRadius) {
				drawingLine = true;
				currentLine.add(sprout.position);
				from = sprout;

				break;
			}
		}
	}
	
	public void touchDragged(float worldX, float worldY) {
		if (!drawingLine) return;

		Vertex front = currentLine.get(currentLine.size() - 1);
		
		Vec2 vec = new Vec2();
		vec.set(worldX, worldY);
		
		float dst = vec.dist(front.x, front.y);
		if (dst >= minimumDistance) {
			if (currentLine.size() == 1) {

				ArrayList<Line> nonNeighbours = new ArrayList<>();
				nonNeighbours.addAll(lines);
				for (Edge edge : from.neighbours) {
					nonNeighbours.remove(edge.line);
					nonNeighbours.remove(edge.twin.line);
				}
				
				for (Line line : nonNeighbours) {
					for (int i = 0; i < line.size() - 1; i++) {
						Vertex v1 = line.get(i);
						Vertex v2 = line.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
						if (intersects) return;
					}
				}
				
				for (Edge edge : from.neighbours) {
					Line lineFirst = new Line();
					lineFirst.addAll(edge.line);

					LineSegment neighbourFirst = getFirstSegment(edge);
					lineFirst.remove(neighbourFirst.from);
					lineFirst.remove(neighbourFirst.to);
					
					for (int i = 0; i < lineFirst.size() - 1; i++) {
						Vertex v1 = lineFirst.get(i);
						Vertex v2 = lineFirst.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
						if (intersects) return;
					}
					
					
					Line lineSecond = new Line();
					lineSecond.addAll(edge.twin.line);
					
					LineSegment neighbourSecond = getLastSegment(edge.twin);
					lineSecond.remove(neighbourSecond.from);
					lineSecond.remove(neighbourFirst.to);
					
					for (int i = 0; i < lineSecond.size() - 1; i++) {
						Vertex v1 = lineSecond.get(i);
						Vertex v2 = lineSecond.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
						if (intersects) return;
					}
				}
				
			} else {
				
				for (Line line : lines) {
					for (int i = 0; i < line.size() - 1; i++) {
						Vertex v1 = line.get(i);
						Vertex v2 = line.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
						if (intersects) return;
					}
				}
				
				for (int i = 0; i < currentLine.size() - 2; i++) {
					Vertex v1 = currentLine.get(i);
					Vertex v2 = currentLine.get(i + 1);

					boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
					if (intersects) return;
				}
				
				Vertex v1 = currentLine.get(currentLine.size() - 2);
				Vertex v2 = currentLine.get(currentLine.size() - 1);

				boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y);
				if (intersects) return;
			}
			
			Vertex vertex = new Vertex();
			vertex.x = worldX;
			vertex.y = worldY;

			currentLine.add(vertex);
		}
	}
	
	public void touchUp(float worldX, float worldY) {
		if (!drawingLine) return;
		drawingLine = false;
		
		Vec2 vec = new Vec2(worldX, worldY);
		
		Vertex front = currentLine.get(currentLine.size() - 1);
		
		List<Sprout> endCandidates = new ArrayList<>();
		for (Sprout sprout : sprouts) {
			if (sprout == from && currentLine.size() == 1) continue;
			if (vec.dist(sprout.position.x, sprout.position.y) <= sproutRadius) {
				endCandidates.add(sprout);
			}
		}
		
		if (endCandidates.size() > 0) {
			
			outer: for (Sprout to : endCandidates) {
				// @speed @RegionLine
				// we could just use the lines in the region
				// and not check all.
				
				ArrayList<Line> nonNeighbours = new ArrayList<>();
				nonNeighbours.addAll(lines);
				for (Edge edge : to.neighbours) {
					nonNeighbours.remove(edge.line);
					nonNeighbours.remove(edge.twin.line);
				}
				
				for (Line line : nonNeighbours) {
					for (int i = 0; i < line.size() - 1; i++) {
						Vertex v1 = line.get(i);
						Vertex v2 = line.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(to.position.x, to.position.y, front.x, front.y, v1.x, v1.y, v2.x, v2.y);
						if (intersects) continue outer;
					}
				}
				
				for (Edge edge : to.neighbours) {
					Line line = new Line();
					line.addAll(edge.line);
	
					// ignore the first line segment.
					for (int i = 1; i < line.size() - 1; i++) {
						Vertex v1 = line.get(i);
						Vertex v2 = line.get(i + 1);
						boolean intersects = GeometryUtil.intersectsSegments(v1.x, v1.y, v2.x, v2.y, to.position.x, to.position.y, front.x, front.y);
						if (intersects) continue outer;
					}
				}
				
				for (int i = 0; i < currentLine.size() - 2; i++) {
					Vertex v1 = currentLine.get(i);
					Vertex v2 = currentLine.get(i + 1);
	
					boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y, false, false);
					if (intersects) continue outer;
				}
				
				Vertex v1 = currentLine.get(currentLine.size() - 2);
				Vertex v2 = currentLine.get(currentLine.size() - 1);
	
				boolean intersects = GeometryUtil.intersectsSegments(front.x, front.y, worldX, worldY, v1.x, v1.y, v2.x, v2.y);
				if (intersects) continue outer;
				
				if (front.x == to.position.x && front.y == to.position.y) {
					currentLine.remove(currentLine.size() - 1);
				}
				
				currentLine.add(to.position);
				
				// add middle point now, so when we do "getCurrentLineRegion()", we have a line points which
				// is not "from" nor "to". 
				Region region = getCurrentLineRegion();
				Move move = getMove(from, to, region, currentLine);
				
				break;
			}
		}
		
		currentLine.clear();
	}

	private void addMiddlePoint(ArrayList<Vertex> line) {
		Vec2 vec = new Vec2();
		
		float totalDistance = 0;
		for (int i = 0; i < line.size() - 1; i++) {
			Vertex v1 = line.get(i);
			Vertex v2 = line.get(i + 1);
			
			float distance = vec.set(v2.x, v2.y).sub(v1.x, v1.y).length();
			totalDistance += distance;
		}
		
		float centerTotalDistance = totalDistance / 2f;
		float accumulatedDistance = 0;
		for (int i = 0; i < line.size() - 1; i++) {
			Vertex v1 = line.get(i);
			Vertex v2 = line.get(i + 1);
			
			float distance = vec.set(v2.x, v2.y).sub(v1.x, v1.y).length();
			accumulatedDistance += distance;
			
			if (accumulatedDistance >= centerTotalDistance) {
				float scale = 1 - (accumulatedDistance - centerTotalDistance) / distance;
				
				float vx = (v2.x - v1.x) * scale + v1.x;
				float vy = (v2.y - v1.y) * scale + v1.y;
				Vertex vertex = createVertex(vx, vy);
				
				line.add(i + 1, vertex);
				
				break;
			}
		}
	}
	
	public ArrayList<Sprout> getBoundarySprouts(Edge start) {
		ArrayList<Sprout> sprouts = new ArrayList<>();
		traverse(start, edge -> sprouts.add(edge.origin));
		return sprouts;
	}
	
	public ArrayList<Edge> getBoundaryEdges(Edge start) {
		ArrayList<Edge> edges = new ArrayList<>();
		traverse(start, edge -> edges.add(edge));
		return edges;
	}
	
	public ArrayList<Vertex> getBoundaryVertices(Edge start) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		traverse(start, edge -> vertices.addAll(edge.line));
		return vertices;
	}
	
	public ArrayList<Vertex> traverse(Edge start, Consumer<Edge> consumer) {
		ArrayList<Vertex> boundary = new ArrayList<>();
		
		Edge current = start;
		while (current != null) {
			consumer.accept(current);
			if (current.next == start) break;
			current = current.next;
		}
		
		return boundary;
	}

	public void verboseTraverse(Edge start) {
		traverse(start, current -> System.out.printf("%d ", current.id));
		System.out.printf("\n");
	}
	
	public Region getCurrentLineRegion() {
		Vertex point = getSemiFirst(currentLine);
		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			if (region.outer == null) continue;	// outer region.
			
			ArrayList<Vertex> outer = getBoundaryVertices(region.outer);
			
			if (isPointInPolygon(point, outer)) {
				return region;
			}
		}

		return outerRegion;
	}

	// @incomplete: we may use something like this when we go from move -> graphical representation
	public Region getCommonRegion(Sprout sprout1, Sprout sprout2) {
		ArrayList<Region> commonRegions = new ArrayList<>();

		for (Region region : regions) {
			if (region.innerSprouts.contains(sprout1) && region.innerSprouts.contains(sprout2)) return region;
		}
		
		for (Edge edge1 : sprout1.neighbours) {
			for (Edge edge2 : sprout2.neighbours) {
				if (edge1.region == edge2.region) {
					commonRegions.add(edge1.region);
				}
			}
		}
		
		if (commonRegions.size() != 1) {
			throw new IllegalStateException("number of common region(s): " + commonRegions.size());
		}
		
		return commonRegions.get(0);
	}
	
	public boolean isOuterBoundary(Edge edge) {
		return edge.representative.equals(edge.region.outer);
	}
	
	// assumes from and to is in the same boundary
	public boolean isLineClockwise(Sprout from, Sprout to, Line line) {
		Vertex last = getSemiLast(line);
		Edge afterTo = getFirstEdgeClockwise(to, last);
		
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.addAll(line);
		
		// check if it is 1 spot "boundary", if so just use the line to determine the orientation.
		// else follow the boundary till we get back to from.
		if (afterTo != null) {
			// follow the path till we get back to from.
			Edge current = afterTo;
			while (!current.origin.equals(from)) {
				vertices.addAll(current.line);
				current = current.next;
			}
		}
		
		return isClockwise(vertices);
	}
	
	public Vertex getSemiFirst(ArrayList<Vertex> line) {
		return line.get(1);
	}
	
	public Vertex getSemiLast(ArrayList<Vertex> line) {
		return line.get(line.size() - 2);
	}
	
	public boolean isClockwise(List<Vertex> vertices) {
		if (vertices.size() <= 2) {
			throw new IllegalStateException("could not determine the orientation!");
		}
		
		float accumulated = 0;
		for (int i = 0; i < vertices.size() - 1; i++) {
			Vertex v1 = vertices.get(i);
			Vertex v2 = vertices.get(i+1);
			
			// no need to divide by 2, because all areas under the curve do it, so it just corresponds to
			// the trueAccumulated = accumulated / 2
			float areaUnderTriangle = (v2.x - v1.x) * (v2.y + v1.y);
			accumulated += areaUnderTriangle;
		}
		
		// the last one.
		Vertex v1 = vertices.get(vertices.size() - 1);
		Vertex v2 = vertices.get(0);
		
		float areaUnderTriangle = (v2.x - v1.x) * (v2.y + v1.y);
		accumulated += areaUnderTriangle;
		
		return accumulated > 0;
	}
	
	public List<Triangle> getTriangles() {
		return getTriangles(vertices, nonReversedLines);
	}
	
	public List<Triangle> getTriangles(List<Vertex> points, List<Line> lines) {
		List<Vector2D> polyPoints = new ArrayList<>();
		for (Vertex vertex : points) {
			polyPoints.add(new Vector2D(vertex.x, vertex.y));
		}

		DelaunayTriangulator triangulator = new DelaunayTriangulator(polyPoints);
		try {
			triangulator.triangulate();
		} catch (NotEnoughPointsException e) {
			e.printStackTrace();
		}
		
		List<Triangle2D> output = triangulator.getTriangles();
		List<Triangle> triangles = new ArrayList<>();
		
		/*
		 * @todo: 
		 * change the delaynay triangulator, so we don't need to do the getVertex lookup.
		 * the simplest way is to use our own Vec2 and then Vertex extends Vec2.
		 * Thus we can get the vertices (+ids) at O(1).
		 */
		for (Triangle2D t2d : output) {
			Vertex p1 = getVertex((float)t2d.a.x, (float)t2d.a.y);
			Vertex p2 = getVertex((float)t2d.b.x, (float)t2d.b.y);
			Vertex p3 = getVertex((float)t2d.c.x, (float)t2d.c.y);
			
			Triangle triangle = new Triangle();
			if (t2d.isOrientedCCW()) {
				triangle.p1 = p1;
				triangle.p2 = p3;
				triangle.p3 = p2;
			} else {
				triangle.p1 = p1;
				triangle.p2 = p2;
				triangle.p3 = p3;
			}
			
			triangles.add(triangle);
		}
		
		/*
		 * should we use fromVertexToVertex?
		 * 
		 * The space complexity is O(n)
		 * The running time is 		 O(k*n)
		 * 
		 * without fromVertexToVertex:
		 * space complexity: "O(1)"
		 * time complexity:  O(m*n)
		 * 
		 * n := number of lines getting overlapped
		 * k := number of neighbours (range ~5)
		 * m := number of vertices (>1000)
		 */
		
		Map<Integer, List<Integer>> fromVertexToVertex = new HashMap<>();

		for (Vertex vertex : vertices) {
			fromVertexToVertex.put(vertex.id, new LinkedList<>());
		}

		Map<IntCouple, Integer> matrix = new HashMap<>();
		
		for (int i = 0; i < triangles.size(); i++) {
			Triangle triangle = triangles.get(i);
			Vertex p1 = triangle.p1;
			Vertex p2 = triangle.p2;
			Vertex p3 = triangle.p3;
			
			setTriangleSide(p1.id, p2.id, i, matrix, fromVertexToVertex);
			setTriangleSide(p2.id, p3.id, i, matrix, fromVertexToVertex);
			setTriangleSide(p3.id, p1.id, i, matrix, fromVertexToVertex);
		}
		
		IntCouple couple = new IntCouple();
		
		for (int i = 0; i < lines.size(); i++) {
			Line line = lines.get(i);
			
			for (int j = 0; j < line.size() - 1; j++) {
				Vertex first = line.get(j);
				Vertex second = line.get(j + 1);
				
				if (first.x == second.x && second.y == first.y) {
					throw new IllegalStateException("broken");
				}
				
				/*
				 * if the line is part of one of the triangles,
				 * then it can't intersect a triangle, so just skip it.
				 */
				couple.set(first.id, second.id);
				if (matrix.containsKey(couple)) continue;

				couple.set(second.id, first.id);
				if (matrix.containsKey(couple)) continue;
				
				Line clockwiseSide = new Line();
				clockwiseSide.add(first);
				
				Line counterClockwiseSide = new Line();
				counterClockwiseSide.add(first);
				
				LineSegment lineSegment = new LineSegment();
				lineSegment.from = first;
				lineSegment.to = second;
				
				LineSegment edge = null;
				int triangleIndex = -1;
				
				List<Integer> tos = fromVertexToVertex.get(first.id);
				
				for (int to : tos) {
					couple.set(first.id, to);
					Integer triangleIndexRef = matrix.get(couple);
					if (triangleIndexRef == null) continue;
					triangleIndex = triangleIndexRef;
					edge = getNextEdge(triangleIndex, null, lineSegment, triangles);
					
					if (edge != null) break;
				}
				
				/*
				for (int k = 0; k < points.size(); k++) {
					Vertex other = points.get(k);
					couple.set(first.id, other.id);
					Integer triangleIndexRef = matrix.get(couple);
					if (triangleIndexRef == null) continue;
					triangleIndex = triangleIndexRef;
					edge = getNextEdge(triangleIndex, null, lineSegment, triangles);
					
					if (edge != null) break;
				}
				*/
				
				if (edge == null)	{
					throw new IllegalStateException("we should have found an edge overlapping the line segment, but didn't.");
				}
				
				Triangle oldTriangle = triangles.get(triangleIndex);
				oldTriangle.intersectingLine = true;
				updateFromVertexToVertex(fromVertexToVertex, oldTriangle);
				
				while (edge != null) {
					if (!edge.from.equals(clockwiseSide.get(clockwiseSide.size() - 1))) {
						clockwiseSide.add(edge.from);
						List<Integer> tos2 = fromVertexToVertex.get(edge.from.id);
						removeFromVertexToVertex(edge.to.id, tos2);
					}
					
					if (!edge.to.equals(counterClockwiseSide.get(counterClockwiseSide.size() - 1))) {
						counterClockwiseSide.add(edge.to);
						List<Integer> tos2 = fromVertexToVertex.get(edge.to.id);
						removeFromVertexToVertex(edge.from.id, tos2);
					}
					
					// we reverse the couple, so we get the triangle after, 
					// instead of the same one (which we just had).
					couple.set(edge.to.id, edge.from.id);
					Integer triangleIndexRef = matrix.get(couple);
					if (triangleIndexRef == null) break;
					int triangleIndex2 = triangleIndexRef;
					edge = getNextEdge(triangleIndex2, edge, lineSegment, triangles);
					
					Triangle oldTriangle2 = triangles.get(triangleIndex2);
					oldTriangle2.intersectingLine = true;
					updateFromVertexToVertex(fromVertexToVertex, oldTriangle2);
				}

				clockwiseSide.add(lineSegment.to);
				counterClockwiseSide.add(lineSegment.to);
				
				Util.require(isClockwise(clockwiseSide));
				Collections.reverse(counterClockwiseSide);
				addTriangles(triangles, matrix, clockwiseSide, fromVertexToVertex);
				addTriangles(triangles, matrix, counterClockwiseSide, fromVertexToVertex);

				/*
				if (isClockwise(clockwiseSide)) {
					Collections.reverse(counterClockwiseSide);
					addTriangles(triangles, matrix, clockwiseSide, fromVertexToVertex);
					addTriangles(triangles, matrix, counterClockwiseSide, fromVertexToVertex);
				} else {
					Collections.reverse(clockwiseSide);
					System.out.printf("yeah\n");
					addTriangles(triangles, matrix, clockwiseSide, fromVertexToVertex);
					addTriangles(triangles, matrix, counterClockwiseSide, fromVertexToVertex);
				}
				*/
			}
		}

		for (Iterator<Triangle> it = triangles.iterator(); it.hasNext();) {
			Triangle triangle = (Triangle) it.next();
			if(triangle.intersectingLine) it.remove();
		}
		
		return triangles;
	}
	
	private void removeFromVertexToVertex(int removeId, List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			int id = list.get(i);
			if (id == removeId) {
				list.remove(i);
				break;
			}
		}
	}
	
	private void updateFromVertexToVertex(Map<Integer, List<Integer>> fromVertexToVertex, Triangle oldTriangle) {
		List<Integer> p1List = fromVertexToVertex.get(oldTriangle.p1.id);
		removeFromVertexToVertex(oldTriangle.p2.id, p1List);
		
		List<Integer> p2List = fromVertexToVertex.get(oldTriangle.p2.id);
		removeFromVertexToVertex(oldTriangle.p3.id, p2List);
		
		List<Integer> p3List = fromVertexToVertex.get(oldTriangle.p3.id);
		removeFromVertexToVertex(oldTriangle.p1.id, p3List);
	}

	private void addTriangles(List<Triangle> triangles, Map<IntCouple, Integer> matrix, List<Vertex> triangleEdges, Map<Integer, List<Integer>> fromVertexToVertex) {
		while (triangleEdges.size() > 2) {
			int i = triangleEdges.size() - 1;
			while(--i > 0) {
				Vertex p1 = triangleEdges.get(i - 1);
				Vertex p2 = triangleEdges.get(i);
				Vertex p3 = triangleEdges.get(i + 1);
				
				List<Vertex> triangleList = new ArrayList<>();
				triangleList.add(p1);
				triangleList.add(p2);
				triangleList.add(p3);
				
				if (!isClockwise(triangleList)) continue;
				
				int newTriangleId = triangles.size();
				setTriangleSide(p1.id, p2.id, newTriangleId, matrix, fromVertexToVertex);
				setTriangleSide(p2.id, p3.id, newTriangleId, matrix, fromVertexToVertex);
				setTriangleSide(p3.id, p1.id, newTriangleId, matrix, fromVertexToVertex);
				
				Triangle triangle = new Triangle();
				triangle.p1 = p1;
				triangle.p2 = p2;
				triangle.p3 = p3;

				triangles.add(triangle);

				triangleEdges.remove(i);
			}
		}
	}
	
	private void setTriangleSide(int fromId, int toId, int triangleId, Map<IntCouple, Integer> matrix, Map<Integer, List<Integer>> fromVertexToVertex) {
		IntCouple couple = new IntCouple(fromId, toId);
		matrix.remove(couple);
		matrix.put(couple, triangleId);
		List<Integer> tos = fromVertexToVertex.get(fromId);
		tos.add(toId);
	}

	private LineSegment getNextEdge(int triangleIndex, LineSegment currentEdge, LineSegment segment, List<Triangle> triangles) {
		List<LineSegment> intersecting = new LinkedList<>();
		
		Triangle triangle = triangles.get(triangleIndex);
		
		if (triangle.intersectingLine) {
			return null;
		}
		
		Vertex p1 = triangle.p1;
		Vertex p2 = triangle.p2;
		Vertex p3 = triangle.p3;

		if (GeometryUtil.intersectsSegmentsAllowTouch(p1.x, p1.y, p2.x, p2.y, segment.from.x, segment.from.y, segment.to.x, segment.to.y)) {
			LineSegment seg = new LineSegment();
			seg.from = p1;
			seg.to = p2;
			intersecting.add(seg);
		}
		
		if (GeometryUtil.intersectsSegmentsAllowTouch(p2.x, p2.y, p3.x, p3.y, segment.from.x, segment.from.y, segment.to.x, segment.to.y)) {
			LineSegment seg = new LineSegment();
			seg.from = p2;
			seg.to = p3;
			intersecting.add(seg);
		}

		if (GeometryUtil.intersectsSegmentsAllowTouch(p3.x, p3.y, p1.x, p1.y, segment.from.x, segment.from.y, segment.to.x, segment.to.y)) {
			LineSegment seg = new LineSegment();
			seg.from = p3;
			seg.to = p1;
			intersecting.add(seg);
		}
		
		if (currentEdge == null) {
			if (intersecting.size() >= 1) {
				return intersecting.get(0);
			} else {
				return null;
			}
		}
		
		if (currentEdge.from == intersecting.get(0).to) {
			if (intersecting.size() >= 2)	return intersecting.get(1);
			else return null;
		} else {
			if (intersecting.size() >= 1) return intersecting.get(0);
			return null;
		}
	}

	// @hack
	// super slow.
	// if we have our own delaynay triangulator implementation, then we will get the indices
	// of the triangles instead. Thus this can be removed!
	private Vertex getVertex(float x, float y) {
		for (Vertex vertex : vertices) {
			if(vertex.x == x && vertex.y == y) return vertex;
		}

		throw new IllegalStateException("broken?");
	}
}
