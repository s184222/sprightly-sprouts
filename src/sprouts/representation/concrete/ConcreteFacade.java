package sprouts.representation.concrete;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sprouts.test.Test;

public class ConcreteFacade {
	
	private Position position;

	public void makeMove(Move move) {
		position.makeMove(move);
	}
	
	public void createFreshGame(int initialNumberOfSpots) {
		position = new Position(initialNumberOfSpots);
	}

	public String getPosition() {
		return position.toString();
	}
	
	// ================
	// testing purposes
	// ================
	
	public void  buildGame(String raw) {
		int endTokenAt = raw.length() - 1;
		if (raw.charAt(endTokenAt) != '!') {
			throw new IllegalStateException("illegal expression");
		}
		
		String noEndToken = raw.substring(0, endTokenAt);
		
		position = new Position();
		
		String[] regions = noEndToken.split("}");
		for (String regionString : regions) {
			Region region = new Region();
			
			String[] boundaries = regionString.split("\\.");
			
			for (String boundaryString : boundaries) {
				Boundary boundary = new Boundary();
				region.addBoundary(boundary);
				
				for (int i = 0; i < boundaryString.length(); i++) {
					char vertex = boundaryString.charAt(i);
					
					int id = VertexUtil.getIndex(vertex);
					boundary.addVertex(id);
				}
			}
			
			position.addRegion(region);
		}
		
		// bulletproof
		Test.equals(getPosition(), raw);
	}
	
	public void makeMove(String raw) {
		Move move = interpretMove(raw);
		position.makeMove(move);
	}
	
	private Move interpretMove(String raw) {
		String regex = "(\\d+),(\\d+)(\\[\\d+(?:,\\d+)*\\])?";

		List<String> matches = match(regex, raw);

		int fromVertexId = Integer.parseInt(matches.get(1));
		int toVertexId = Integer.parseInt(matches.get(2));
		
		Move move = new Move();
		move.fromId = fromVertexId;
		move.toId = toVertexId;

		String maybeContaining = matches.get(3);
		if (maybeContaining != null) {
			String peeled = peel(maybeContaining);
			String[] rawVertexIds = peeled.split(",");
			for (String rawId : rawVertexIds) {
				int vertexId = Integer.parseInt(rawId);
				move.containingIds.add(vertexId);
			}
		}

		return move;
	}
	
	private String peel(String string) {
		return string.substring(1, string.length() - 1);
	}
	
	private List<String> match(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		boolean match = matcher.matches();

		if (!match) {
			throw new IllegalStateException("no match: " + matcher.group());
		}
		
		System.out.printf("====================\n");
		List<String> matches = new LinkedList<>();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			String stringMatch = matcher.group(i);
			matches.add(stringMatch);
			
			System.out.printf("stringMatcher: '%s'\n", stringMatch);
		}
		
		return matches;
	}
}
