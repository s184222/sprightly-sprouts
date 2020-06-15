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
}