package sprouts.ai;

import java.util.LinkedList;

/**
 * 
 * @author Rasmus Møller Larsen
 *
 */

@SuppressWarnings("serial")
public class Region extends LinkedList<Boundary> {
	
	public Boundary getBoundary(int sprout) {
		for (Boundary boundary : this) {
			if (boundary.contains(sprout)) return boundary;
		}
		
		throw new IllegalStateException("could not find its boundary");
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (Boundary boundary : this) {
			builder.append(boundary.toString());
		}
		
		builder.append("}");
		
		return builder.toString();
	}

	public Boundary getOuterBoundary() {
		for (Boundary boundary : this) {
			if (boundary.outerBoundary) return boundary;
		}
		return null;
	}
}
