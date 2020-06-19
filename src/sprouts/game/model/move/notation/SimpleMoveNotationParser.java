package sprouts.game.model.move.notation;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;

public class SimpleMoveNotationParser implements MoveNotationParser {
	
	public RawMove parse(String rawMove) throws MoveNotationException {
		List<String> matches = matchNotation(rawMove);
		
		String rawFromId = matches.get(1);
		String rawToId = matches.get(2);
		
		RawMove move = new RawMove();
		move.fromId = Integer.parseInt(rawFromId);
		move.toId = Integer.parseInt(rawToId);
		move.any = true;
		
		return move;
	}
	
	private List<String> matchNotation(String rawMove) throws MoveNotationException {
		String regex = "(\\d+),(\\d+)";

		try {
			List<String> matches = match(regex, rawMove);
			return matches;
		} catch (IllegalStateException e) {
			throw new MoveNotationException("error parsing.");
		}
	}
	
	private List<String> match(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		boolean match = matcher.matches();
		if (!match) {
			throw new IllegalStateException("no match!");
		}
		
		List<String> matches = new LinkedList<>();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			String stringMatch = matcher.group(i);
			matches.add(stringMatch);
		}
		
		return matches;
	}
	
	
	
}
