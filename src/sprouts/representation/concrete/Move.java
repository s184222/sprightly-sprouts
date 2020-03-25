package sprouts.representation.concrete;

import java.util.LinkedList;
import java.util.List;

public class Move {
	
	public int fromId, toId;
	
	// only used when a 1 boundary move is made.
	// when a 1 boundary move is made, then some other boudaries
	// within that same region, will be split.
	public List<Integer> innerIds = new LinkedList<>();
	public List<Integer> outerIds = new LinkedList<>();	
	
}
