package sprouts.game.move.simple;

import java.util.List;

import sprouts.game.move.IdMove;
import sprouts.game.move.MoveNotationException;
import sprouts.game.move.Regex;
import sprouts.game.move.pipe.MoveNotationParser;


/**
 * 
 * Simple move notation is given by the following format: "{fromId},{toId}"
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class SimpleMoveNotationParser implements MoveNotationParser {
	
	public IdMove parse(String move) throws MoveNotationException {
		List<String> matches = matchNotation(move);
		
		String rawFromId = matches.get(1);
		String rawToId = matches.get(2);
		
		IdMove idMove = new IdMove();
		idMove.fromId = Integer.parseInt(rawFromId);
		idMove.toId = Integer.parseInt(rawToId);
		
		return idMove;
	}
	
	private List<String> matchNotation(String rawMove) throws MoveNotationException {
		String regex = "(\\d+),(\\d+)";

		try {
			List<String> matches = Regex.match(regex, rawMove);
			return matches;
		} catch (IllegalStateException e) {
			throw new MoveNotationException("error parsing.");
		}
	}
}
