package sprouts.game.move;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A move representation container, where the sprouts are specified by their ids.
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class IdMove {
	
	public int fromId;
	public boolean fromAscending;
	
	// optional to fill out.
	
	public int toId;
	public boolean toAscending;
	
	public List<Integer> inner;
	public boolean inverted;
	
	public IdMove() {
		inner = new ArrayList<>();
		fromId = -1;
		toId = -1;
	}

	@Override
	public String toString() {
		String whiteSpaces =  String.format("%d%s,%d%s,%s%s", fromId, fromAscending ? "<" : ">", toId, toAscending ? "<" : ">", inner, inverted ? "!" : "");
		return whiteSpaces.replace(" ", "");
	}
}
