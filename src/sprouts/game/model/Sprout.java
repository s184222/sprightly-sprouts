package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

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

	public int getLives() {
		return totalLives - getNeighbourCount();
	}
	
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