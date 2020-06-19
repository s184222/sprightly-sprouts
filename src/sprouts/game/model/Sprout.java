package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * All sprouts have an unique ID. A sprout may be connected to other sprouts,
 * if sprouts are connected then their exists an edge which goes from the first sprout
 * to the second sprout and another edge which goes from the second sprout to the first sprout.
 * The edges which are leaving a sprout are contained in {@code neighbours}
 * A sprout is in a boundary if it has at least 1 neighbour. A sprout may be in multiple boundaries.
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Sprout {
	
	public final int id;
	
	public Vertex position;
	public List<Edge> neighbours;
	
	private int totalLives;
	
	public Sprout(int id) {
		this.id = id;
		
		totalLives = 3;
		
		neighbours = new ArrayList<>(totalLives);
	}
	
	public int getNeighbourCount() {
		return neighbours.size();
	}

	/**
	 * A sprout can have up to 3 neighbours.
	 * Lives are defined as how many more neighbours it can have.
	 * 
	 * @return the lives left of that sprout
	 */
	public int getLives() {
		return totalLives - getNeighbourCount();
	}
	
	/**
	 * If a sprout has no edges leaving it, then it is by definition not in a boundary.
	 * The sprout is 
	 * 
	 * @return true if the sprout is in a boundary, false otherwise
	 */
	public boolean isInBoundary() {
		return getNeighbourCount() != 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((neighbours == null) ? 0 : neighbours.hashCode());
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + totalLives;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sprout other = (Sprout) obj;
		if (id != other.id)
			return false;
		if (neighbours == null) {
			if (other.neighbours != null)
				return false;
		} else if (!neighbours.equals(other.neighbours))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (totalLives != other.totalLives)
			return false;
		return true;
	}
}