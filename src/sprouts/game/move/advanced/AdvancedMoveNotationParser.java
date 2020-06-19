package sprouts.game.move.advanced;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.move.IdMove;
import sprouts.game.move.MoveNotationException;
import sprouts.game.move.Regex;
import sprouts.game.move.pipe.MoveNotationParser;

/**
 * The advance notation gives the user the ability to decide the following: <br>
 * 1. which sprouts are connected <br>
 * 2. if the sprouts are in boundaries, which side of the boundary should the start and the end of line be. <br>
 * 3. if the sprouts are in the same boundary, which other sprouts should the line contain <br>
 * <br>
 * Advance move notation is given by the following format: 
 * 	"{fromId}{fromAscending},{toId},[{inner1}, {inner2}, ...]{inverted}"
 * 
 * <br>
 * @see Region
 * 
 * @author Rasmus Møller Larsen, s184190
 * 
 */

public class AdvancedMoveNotationParser implements MoveNotationParser {

	@Override
	public IdMove parse(String rawMove) throws MoveNotationException {
		IdMove move = new IdMove();
		List<String> matches = matchNotation(rawMove);

		String rawFromId = matches.get(1);
		String rawFromAscending = matches.get(2);
		String rawToId = matches.get(3);
		String rawToAscending = matches.get(4);
		String rawInner = matches.get(5);
		String rawInverted = matches.get(6);

		move.fromId = Integer.parseInt(rawFromId);
		move.fromAscending = isAscending(rawFromAscending);
		move.toId = Integer.parseInt(rawToId);
		move.toAscending = isAscending(rawToAscending);
		move.inner = parseInner(rawInner);
		move.inverted = parseInverted(rawInverted);
		
		return move;
	}
	
	private List<String> matchNotation(String rawMove) throws MoveNotationException {
		String regex = "(\\d+)(>|<),(\\d+)(>|<)(?:,(\\[.*\\])(\\!?))?";

		try {
			List<String> matches = Regex.match(regex, rawMove);
			return matches;
		} catch (IllegalStateException e) {
			throw new MoveNotationException("error parsing.");
		}
	}
	
	private boolean parseInverted(String rawInverted) throws MoveNotationException {
		if (rawInverted == null) return false;
		else if ("".equals(rawInverted)) return false;
		else if ("!".equals(rawInverted)) return true;
		throw new MoveNotationException("illegal inverted character");
	}

	private List<Integer> parseInner(String rawInner) throws MoveNotationException {
		List<Integer> inner = new ArrayList<>();
		
		if (rawInner != null) {
			String peeled = peel(rawInner);
			if (peeled.length() == 0) return inner;
			
			String[] sproutIds = peeled.split(",");
			
			for (String rawId : sproutIds) {

				try {
					int id = Integer.parseInt(rawId);
					inner.add(id);
					
				} catch (NumberFormatException e) {
					throw new MoveNotationException("\"" + rawId + "\"" + " is not a integer.\n");
				}
			}
		}
		
		return inner;
	}

	private boolean isAscending(String ascendingString) throws MoveNotationException {
		if (">".equals(ascendingString)) return false;
		if ("<".equals(ascendingString)) return true;
		throw new MoveNotationException("unexpected characters: " + ascendingString);
	}
	
	private String peel(String string) {
		return string.substring(1, string.length() - 1);
	}
}