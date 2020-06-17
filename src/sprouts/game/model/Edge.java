package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Edge {
	
	public Sprout origin;

	public Region region;

	public Edge next;
	public Edge prev;
	
	public Edge representative;
	public Edge twin;
	
	public int id;	// @debug: only for debug purposes.
	
	public Line line;
	
	public Edge() {
		id = DebugIdGenerators.getEdgeId();
		System.out.printf("edge id: %d\n", id);
	}
	
	/*
	 * edge specific methods
	 */
	
	public boolean isAscending() {
		// doesn't matter what we set it to. 
		// It is a single sprout, so we will not use this info anyway.
		if (next == null && prev == null) return true;
		return prev.origin.id <= next.origin.id;
	}
	
	public boolean isSameBoundary(Edge other) {
		return representative.equals(other.representative);
	}
	
	public boolean isRepresentative() {
		return equals(representative);
	}
	
	public boolean isOuterBoundary() {
		return representative.equals(region.outerBoundary);
	}
	
	/*
	 * boundary specific methods
	 */
	
	public List<Sprout> getBoundarySprouts() {
		List<Sprout> sprouts = new ArrayList<>();
		traverse(edge -> sprouts.add(edge.origin));
		return sprouts;
	}
	
	public List<Integer> getBoundarySproutIds() {
		List<Integer> sproutIds = new ArrayList<>();
		traverse(edge -> sproutIds.add(edge.origin.id));
		return sproutIds;
	}
	
	public void setAsBoundaryRepresentative() {
		traverse(current -> current.representative = this);
	}
	
	public void setBoundaryRegion(Region region) {
		traverse(current -> current.region = region);
	}
	
	public List<Edge> getBoundaryEdges() {
		List<Edge> edges = new ArrayList<>();
		traverse(edge -> edges.add(edge));
		return edges;
	}
	
	public Line getBoundaryLine() {
		Line line = new Line(); 
		traverse(edge -> line.append(edge.line));
		line.removeLast();
		return line;
	}
	
	public int getBoundaryLives() {
		IntRef lives = new IntRef();
		lives.value = 0;
		traverse(edge -> lives.value += edge.origin.getLives());
		return lives.value;
	}
	
	public boolean isInsideBoundary(Vertex point) {
		ArrayList<Vertex> boundaryLine = getBoundaryLine();
		if (GraphicalFacade.isPointInPolygon(point, boundaryLine)) return true;
		return false;
	}
	
	private List<Vertex> traverse(Consumer<Edge> consumer) {
		ArrayList<Vertex> boundary = new ArrayList<>();
		
		Edge current = this;
		while (true) {
			consumer.accept(current);
			if (current.next.equals(this)) break;
			current = current.next;
		}
		
		return boundary;
	}

	public void verboseBoundaryTraverse() {
		traverse(current -> System.out.printf("%d ", current.id));
		System.out.printf("\n");
	}
}