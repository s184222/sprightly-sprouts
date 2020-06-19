package sprouts.game.model.move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawMove {
	
	public int fromId;
	public boolean fromAscending;
	
	public int toId;
	public boolean toAscending;
	
	public boolean any;
	
	public List<Integer> inner;
	public boolean inverted;
	
	public RawMove() {
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
