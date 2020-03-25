package sprouts.representation.concrete;

import java.util.LinkedList;
import java.util.List;

public class Move {
	
	public int fromId, toId;
	
	// only used when a 1 boundary move is made.
	public List<Integer> containingIds = new LinkedList<>();
	public List<Integer> idsOfContainingBoundary = new LinkedList<>();	
	
}
