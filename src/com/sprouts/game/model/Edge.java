package com.sprouts.game.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.sprouts.math.LinMath;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

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
		Set<Sprout> sprouts = new HashSet<>();
		traverse(edge -> sprouts.add(edge.origin));
		return new ArrayList<>(sprouts);
	}
	
	public List<Integer> getBoundarySproutIds() {
		Set<Integer> sproutIds = new HashSet<>();
		traverse(edge -> sproutIds.add(edge.origin.id));
		return new ArrayList<>(sproutIds);
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
	
	private class IntRef {
		public int value;
	}
	
	public int getBoundaryLives() {
		List<Sprout> sprouts = getBoundarySprouts();
		IntRef lives = new IntRef();
		lives.value = 0;
		for (Sprout sprout : sprouts) {
			lives.value += sprout.getLives();
		}
		return lives.value;
	}
	
	public boolean isInsideBoundary(Vertex point) {
		ArrayList<Vertex> boundaryLine = getBoundaryLine();
		if (LinMath.isPointInPolygon(point, boundaryLine)) return true;
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