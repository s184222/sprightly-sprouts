package sprouts.representation.concrete;

import java.util.ArrayList;
import java.util.List;

public class Region {
	
	private List<Boundary> boundaries;

	public Region() {
		boundaries = new ArrayList<>();
	}

	public void addBoundary(Boundary boundary) {
		boundaries.add(boundary);
	}

	public void removeBoundary(Boundary boundary) {
		boundaries.remove(boundary);
	}
	
	public void addBoundaries(List<Boundary> other) {
		boundaries.addAll(other);
	}

	public List<Boundary> getBoundaries() {
		return boundaries;
	}

	public Boundary getBoundary(int vertexId) {
		for (Boundary boundary : boundaries) {
			if (boundary.containsVertex(vertexId)) return boundary;
		}
		
		throw new IllegalStateException("could not find its boundary");
	}

	@Override
	public String toString() {
		String string = "";
		for (Boundary boundary : boundaries) {
			string += boundary.toString();
		}
		
		string += "}";
		
		return string;
	}
}
