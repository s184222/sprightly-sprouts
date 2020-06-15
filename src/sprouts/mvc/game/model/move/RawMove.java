package sprouts.mvc.game.model.move;

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
	
	public List<Integer> inner;
	public boolean inverted;
	
	public RawMove() {
		inner = new ArrayList<>();
		fromId = -1;
		toId = -1;
	}
	
	/*
	 * 2>,3>,[1,4,2]!
	 */
	public RawMove(String rawMove) throws MoveNotationException {

		List<String> matches = matchNotation(rawMove);

		String rawFromId = matches.get(1);
		String rawFromAscending = matches.get(2);
		String rawToId = matches.get(3);
		String rawToAscending = matches.get(4);
		String rawInner = matches.get(5);
		String rawInverted = matches.get(6);

		this.fromId = Integer.parseInt(rawFromId);
		this.fromAscending = isAscending(rawFromAscending);
		this.toId = Integer.parseInt(rawToId);
		this.toAscending = isAscending(rawToAscending);
		this.inner = parseInner(rawInner);
		this.inverted = parseInverted(rawInverted);

	}
	
	private List<String> matchNotation(String rawMove) throws MoveNotationException {
		String regex = "(\\d+)(>|<),(\\d+)(>|<)(?:,(\\[.*\\])(\\!?))?";

		try {
			List<String> matches = match(regex, rawMove);
			return matches;
		} catch (IllegalStateException e) {
			throw new MoveNotationException("error parsing notation.");
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
	
	@Override
	public String toString() {
		String whiteSpaces =  String.format("%d%s,%d%s,%s%s", fromId, fromAscending ? "<" : ">", toId, toAscending ? "<" : ">", inner, inverted ? "!" : "");
		return whiteSpaces.replace(" ", "");
	}
}
