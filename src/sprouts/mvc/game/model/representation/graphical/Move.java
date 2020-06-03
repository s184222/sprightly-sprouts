package sprouts.mvc.game.model.representation.graphical;

import java.util.ArrayList;
import java.util.List;

public class Move {
	
	public int fromId;
	public boolean fromAscending;
	public int toId;
	public boolean toAscending;
	
	public List<Integer> inner;
	public boolean invertedBoundaries;
	//public int regionSprout;
	
	public Move() {
		inner = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return String.format("%d%s,%d%s", fromId, fromAscending ? "" : "!", toId, toAscending ? "" : "!");
	}
}
