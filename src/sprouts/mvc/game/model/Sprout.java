package sprouts.mvc.game.model;

import java.util.ArrayList;
import java.util.List;

public class Sprout {
	
	public int id;
	public Vertex position;
	public List<Edge> neighbours;
	
	private int totalLives;
	
	public Sprout() {
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
}