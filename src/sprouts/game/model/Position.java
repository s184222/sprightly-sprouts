package sprouts.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sprouts.math.LinMath;

import sprouts.game.Trig;
import sprouts.game.UidGenerator;
import sprouts.game.move.IdMove;
import sprouts.game.util.Assert;

public class Position {
	
	private List<Sprout> sprouts;
	private List<Edge> edges;
	private List<Line> lines;
	private List<Region> regions;
	private List<Vertex> vertices;
	
	private Region outerRegion;	// is also contained in regions.
	private BufferRectangle outerRectangle;

	private UidGenerator sproutIdGenerator;
	
	public Position() {
		sproutIdGenerator = new UidGenerator();
		
		sprouts = new ArrayList<>();
		edges = new ArrayList<>();
		lines = new ArrayList<>();
		regions = new ArrayList<>();
		vertices = new ArrayList<>();
		
		Region region = new Region();
		regions.add(region);
		outerRegion = region;

		outerRectangle = new BufferRectangle();
	}
	
	public IdMove update(Line line) {
		Line lineWithMiddle = new Line();
		lineWithMiddle.addAll(line);
		lineWithMiddle.addMiddlePoint();

		Vertex fromPosition = lineWithMiddle.getFirst();
		Sprout from = getSprout(fromPosition);

		Vertex toPosition = lineWithMiddle.getLast();
		Sprout to = getSprout(toPosition);

		Region region = getLineRegion(lineWithMiddle);
		
		if (region.isInSameBoundary(from, to)) {
			IdMove move = getOneBoundaryMove(from, to, region, lineWithMiddle);
			return move;
		} else {
			IdMove move = getTwoBoundaryMove(from, to, region, lineWithMiddle);
			return move;
		}
	}
	
	private IdMove getTwoBoundaryMove(Sprout from, Sprout to, Region region, Line line) {
		IdMove move = new IdMove();
		move.fromId = from.id;
		move.toId = to.id;
		
		boolean isFromOuterBoundary = region.isInOuterBoundary(from);
		boolean isToOuterBoundary = region.isInOuterBoundary(to);
		
		boolean isFromSoloSprout = region.isInnerSprout(from);
		boolean isToSoloSprout = region.isInnerSprout(to);
		
		Assert.that(!isFromOuterBoundary || !isToOuterBoundary, "broken! both are in the outer boundary. A 1 boundary move should had be executed instead.");
		
		solidifyVertices(line);
		
		Line[] splitted = line.splitMiddle();
		Line line1 = splitted[0];
		Line line2 = splitted[1];
		
		lines.add(line1);
		lines.add(line2);
		
		Vertex createdPosition = line1.getLast();
		Sprout createdSprout = createSprout(createdPosition);
		
		// the boundary of "to" and "from" is freed, which means the region does not contain the boundaries of "from" and "to"
		// afterwards a new boundary is added, which is the boundary created after "merging" the boundaries of "from"
		// and "to".
		region.freeBoundary(to);
		region.freeBoundary(from);
		
		Edge e1_1 = createEdge(from, line1, region);
		Edge e1_2 = createEdge(createdSprout, line1.reversedCopy(), region);
		Edge e2_1 = createEdge(createdSprout, line2, region);
		Edge e2_2 = createEdge(to, line2.reversedCopy(), region);
		
		twin(e1_1, e1_2);
		twin(e2_1, e2_2);
		
		createdSprout.neighbours.add(e1_2);
		createdSprout.neighbours.add(e2_1);
		
		from.neighbours.add(e1_1);
		to.neighbours.add(e2_2);
		
		// add the new boundary to the region, where e1_1 is the representative.
		// This is outdated after the swap, but we dont care which is which.
		if (isFromOuterBoundary || isToOuterBoundary) {
			region.outerBoundary = e1_1;
		} else {
			region.innerBoundaries.add(e1_1);
		}
		
		Vertex first = line.getSemiFirst();
		Vertex last = line.getSemiLast();

		Edge e1_1_prev = isFromSoloSprout ? e1_2 : Trig.getFirstEdgeCounterClockwise(from, first).twin;
		Edge e1_2_after = isFromSoloSprout ? e1_1 : Trig.getFirstEdgeClockwise(from, first);
		Edge e2_1_after = isToSoloSprout ? e2_2 : Trig.getFirstEdgeClockwise(to, last);
		Edge e2_2_prev = isToSoloSprout ? e2_1 : Trig.getFirstEdgeCounterClockwise(to, last).twin;

		// we check if e1_2_after (which was the old next of the from edge)
		// is ascending or not.
		move.fromAscending = e1_2_after.isAscending();	
		move.toAscending = e2_1_after.isAscending();

		connect(e1_1, e2_1);
		connect(e2_2, e1_2);
		
		connect(e1_1_prev, e1_1);
		connect(e1_2, e1_2_after);
		connect(e2_1, e2_1_after);
		connect(e2_2_prev, e2_2);
		
		e1_1.setAsBoundaryRepresentative();
		e1_1.setBoundaryRegion(region);
		
		return move;
	}
	
	private IdMove getOneBoundaryMove(Sprout from, Sprout to, Region region, Line line) {
		IdMove move = new IdMove();
		move.fromId = from.id;
		move.toId = to.id;
			
		boolean isSoloSprout = !from.isInBoundary();
		boolean isInOuterBoundary = region.isInOuterBoundary(from);

		boolean swapped = false;
		if (!isInOuterBoundary) {
			
			// the algorithm assumes the line going from "from - line - to - from" is clockwise when it is an inner boundary
			// so swap from and to, if they are not clockwise.
			// this does not matter if it is an outer boundary, because 1 extra region is created.
	
			if (!isLineClockwise(from, to, line)) {
				swapped = true;
				
				// swap from and to
				Sprout tmp = from;
				from = to;
				to = tmp;
				
				line.reverse();
			}
		}
		
		solidifyVertices(line);
		
		Line[] splitted = line.splitMiddle();
		Line line1 = splitted[0];
		Line line2 = splitted[1];
		
		lines.add(line1);
		lines.add(line2);
		
		Vertex createdPosition = line2.getFirst();
		Sprout createdSprout = createSprout(createdPosition);
		
		// free old boundary.
		region.freeBoundary(from);
		
		Region newRegion = new Region();
		addRegion(newRegion);
	
		Edge e1_1 = createEdge(from, line1, region);
		Edge e1_2 = createEdge(createdSprout, line1.reversedCopy(), newRegion);
		Edge e2_1 = createEdge(createdSprout, line2, region);
		Edge e2_2 = createEdge(to, line2.reversedCopy(), newRegion);
		
		twin(e1_1, e1_2);
		twin(e2_2, e2_1);
		
		to.neighbours.add(e2_2);
		from.neighbours.add(e1_1);
		
		createdSprout.neighbours.add(e1_2);
		createdSprout.neighbours.add(e2_1);
		
		if (isSoloSprout) {
			region.innerBoundaries.add(e1_1);
			newRegion.outerBoundary = e2_2;
			
			connect(e1_1, e2_1);
			connect(e2_1, e1_1);
			connect(e2_2, e1_2);
			connect(e1_2, e2_2);
			
			move.fromAscending = true;
			move.toAscending = true;
	
		} else {
			
			if (isInOuterBoundary) {
				region.outerBoundary = e1_1;
				newRegion.outerBoundary = e2_2;
			} else {
				region.innerBoundaries.add(e1_1);
				newRegion.outerBoundary = e2_2;
			}
			
			Vertex first = line.getSemiFirst();
			Vertex last = line.getSemiLast();
			
			Edge e1_2_after = Trig.getFirstEdgeClockwise(from, first);
			Edge e1_1_prev = Trig.getFirstEdgeCounterClockwise(from, first).twin;
			Edge e2_2_prev = Trig.getFirstEdgeCounterClockwise(to, last).twin;
			Edge e2_1_after = Trig.getFirstEdgeClockwise(to, last);
			
			if (swapped) {
				move.fromAscending = e2_1_after.isAscending();
				move.toAscending = e1_2_after.isAscending();
			} else {
				move.fromAscending = e1_2_after.isAscending();
				move.toAscending = e2_1_after.isAscending();
			}
			
			connect(e1_1, e2_1);
			connect(e2_2, e1_2);
			
			connect(e1_1_prev, e1_1);
			connect(e2_1, e2_1_after);
			connect(e1_2, e1_2_after);
			connect(e2_2_prev, e2_2);
		}
		
		e1_1.setAsBoundaryRepresentative();
		e2_2.setAsBoundaryRepresentative();
		
		e1_1.setBoundaryRegion(region);
		e2_2.setBoundaryRegion(newRegion);
		
		List<Vertex> newRegionOuterBoundary = newRegion.outerBoundary.getBoundaryLine();
		List<Sprout> newRegionOuterSprouts = newRegion.outerBoundary.getBoundarySprouts();
		
		List<Edge> transferredBoundaries = new ArrayList<>();
		for (Edge inner : region.innerBoundaries) {
			if (newRegionOuterSprouts.contains(inner.origin))	continue;
			
			if (LinMath.isPointInPolygon(inner.origin.position, newRegionOuterBoundary)) {
				inner.setBoundaryRegion(newRegion);
				transferredBoundaries.add(inner);
			}
		}

		ArrayList<Sprout> transferredSprouts = new ArrayList<>();
		for (Sprout innerSprout : region.innerSprouts) {
			if (LinMath.isPointInPolygon(innerSprout.position, newRegionOuterBoundary)) {
				transferredSprouts.add(innerSprout);
			}
		}
		
		region.innerSprouts.removeAll(transferredSprouts);
		region.innerBoundaries.removeAll(transferredBoundaries);
		
		newRegion.innerSprouts.addAll(transferredSprouts);
		newRegion.innerBoundaries.addAll(transferredBoundaries);
		
		newRegion.outerBoundary.setBoundaryRegion(newRegion);
		
		// add the representatives sprouts.
		addInnerToMove(move, newRegion);
		
		return move;
	}

	private void twin(Edge e0, Edge e1) {
		e0.twin = e1;
		e1.twin = e0;
	}

	private void connect(Edge from, Edge to) {
		from.next = to;
		to.prev = from;
	}

	private void solidifyVertices(Line line) {
		for (int i = 1; i < line.size() - 1; i++) {
			Vertex vertex = line.get(i);
			addVertex(vertex);
		}
	}

	private void addInnerToMove(IdMove move, Region region) {
		for (Edge innerBoundary : region.innerBoundaries) {
			
			// the canonization uses the the lowest id.
			List<Integer> ids = innerBoundary.getBoundarySproutIds();
			Collections.sort(ids);
			
			int lowest = ids.get(0);
			move.inner.add(lowest);
		}
		
		for (Sprout innerSprout : region.innerSprouts) {
			move.inner.add(innerSprout.id);
		}
		
		Collections.sort(move.inner);
	}
	
	private boolean isClockwise(List<Vertex> vertices) {
		if (vertices.size() <= 2) {
			throw new IllegalStateException("could not determine the orientation!");
		}
		
		double accumulated = 0;
		for (int i = 0; i < vertices.size() - 1; i++) {
			Vertex v1 = vertices.get(i);
			Vertex v2 = vertices.get(i+1);
			
			// no need to divide by 2, because all areas under the curve do it, so it just corresponds to
			// the trueAccumulated = accumulated / 2
			double areaUnderTriangle = (v2.x - v1.x) * (v2.y + v1.y);
			accumulated += areaUnderTriangle;
		}
		
		// the last one.
		Vertex v1 = vertices.get(vertices.size() - 1);
		Vertex v2 = vertices.get(0);
		
		double areaUnderTriangle = (v2.x - v1.x) * (v2.y + v1.y);
		accumulated += areaUnderTriangle;
		
		return accumulated > 0;
	}
	
	// assumes from and to is in the same boundary
	private boolean isLineClockwise(Sprout from, Sprout to, Line line) {
		ArrayList<Vertex> vertices = new ArrayList<>();
		vertices.addAll(line);
		
		if (!from.equals(to)) {
			Vertex last = line.getSemiLast();
			Edge afterTo = Trig.getFirstEdgeClockwise(to, last);
	
			// check if it is 1 sprout "boundary", if so just use the line to determine the orientation.
			// else follow the boundary till we get back to from.
			if (afterTo != null) {
				// follow the path till we get back to from.
				Edge current = afterTo;
				while (!current.origin.equals(from)) {
					vertices.addAll(current.line);
					current = current.next;
				}
			}
		}
		
		return isClockwise(vertices);
	}
	
	private Region getLineRegion(Line line) {
		Vertex point = line.getSemiFirst();
		List<Region> regions = getRegions();
		
		for (int i = 0; i < regions.size(); i++) {
			Region region = regions.get(i);
			
			if (region.isInsideRegion(point)) return region;
		}

		throw new IllegalStateException("no region found");
	}
	
	public boolean isLineSegmentOnLine(Vertex v1, Vertex v2) {
		for (Line line : lines) {
			if (line.isLineSegment(v1, v2)) return true;
			if (line.isLineSegment(v2, v1)) return true;
		}
		return false;
	}

	
	public Sprout getSprout(Vertex vertex) {
		for (Sprout sprout : sprouts) {
			if (sprout.position.equals(vertex)) return sprout;
		}
		return null;
	}
	
	public Sprout getSprout(int id) {
		for (Sprout sprout : sprouts) {
			if (sprout.id == id) return sprout;
		}
		return null;
	}
	
	public boolean isGameOver() {
		for (Region region : regions) {
			if (region.isAlive()) return false;
		}
		return true;
	}
	
	public Edge createEdge(Sprout origin, Line line, Region region) {
		Edge edge = new Edge();
		edge.origin = origin;
		edge.line = line;
		edge.region = region;
		
		edges.add(edge);

		return edge;
	}
	
	public Sprout createSprout(Vertex vertex) {
		int id = sproutIdGenerator.generate();

		Sprout sprout = new Sprout(id);
		sprout.position = vertex;
		
		sprouts.add(sprout);
		
		return sprout;
	}

	public void addVertex(Vertex vertex) {
		outerRectangle.update(vertex.x, vertex.y);
		vertices.add(vertex);
	}
	
	public List<Vertex> getVertices() {
		return vertices;
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public void addRegion(Region region) {
		regions.add(region);
	}
	
	public List<Region> getRegions() {
		return regions;
	}
	
	public void addSprout(Sprout sprout) {
		sprouts.add(sprout);
	}
	
	public List<Sprout> getSprouts() {
		return sprouts;
	}

	public void addLine(Line line) {
		lines.add(line);
	}
	
	public List<Line> getLines() {
		return lines;
	}
	
	public Region getOuterRegion() {
		return outerRegion;
	}
	
	public List<Vertex> getOuterCorners() {
		return outerRectangle.getCorners();
	}
	
}
