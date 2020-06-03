package sprouts.mvc.game.model.representation.concrete;

import java.util.ArrayList;
import java.util.List;

public class Region {
	
	private List<Boundary> boundaries;

	public Region() {
		boundaries = new ArrayList<>();
	}

	public void add(Boundary boundary) {
		boundaries.add(boundary);
	}

	public void remove(Boundary boundary) {
		boundaries.remove(boundary);
	}
	
	public void add(List<Boundary> other) {
		boundaries.addAll(other);
	}

	public List<Boundary> getBoundaries() {
		return boundaries;
	}

	public Boundary getBoundary(int vertexId) {
		for (Boundary boundary : boundaries) {
			if (boundary.contains(vertexId)) return boundary;
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
