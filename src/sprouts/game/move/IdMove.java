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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (fromAscending ? 1231 : 1237);
		result = prime * result + fromId;
		result = prime * result + ((inner == null) ? 0 : inner.hashCode());
		result = prime * result + (inverted ? 1231 : 1237);
		result = prime * result + (toAscending ? 1231 : 1237);
		result = prime * result + toId;
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
		IdMove other = (IdMove) obj;
		if (fromAscending != other.fromAscending)
			return false;
		if (fromId != other.fromId)
			return false;
		if (inner == null) {
			if (other.inner != null)
				return false;
		} else if (!inner.equals(other.inner))
			return false;
		if (inverted != other.inverted)
			return false;
		if (toAscending != other.toAscending)
			return false;
		if (toId != other.toId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		String whiteSpaces =  String.format("%d%s,%d%s,%s%s", fromId, fromAscending ? "<" : ">", toId, toAscending ? "<" : ">", inner, inverted ? "!" : "");
		return whiteSpaces.replace(" ", "");
	}
}
