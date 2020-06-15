package old.sprouts.mvc.game.model.representation.concrete;

import java.util.LinkedList;
import java.util.List;

public class Move {
	
	public int fromId, toId;
	
	// only used when a 1 boundary move is made.
	// when a 1 boundary move is made, then some other boudaries
	// within that same region, will be split.
	public List<Integer> innerIds1 = new LinkedList<>();
	public List<Integer> outerIds1 = new LinkedList<>();	
	
	public List<Integer> innerIds2 = new LinkedList<>();
	public List<Integer> outerIds2 = new LinkedList<>();	
}
