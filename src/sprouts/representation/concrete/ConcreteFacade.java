package sprouts.representation.concrete;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sprouts.test.Test;

public class ConcreteFacade {
	
	private UidGenerator vertexIdGenerator;
	private Position position;

	public ConcreteFacade() {
		vertexIdGenerator = new UidGenerator();
	}
	
	public void makeMove(Move move) {
		position.makeMove(move);
	}
	
	public void createFreshPosition(int initialNumberOfSpots) {
		position = new Position(initialNumberOfSpots, vertexIdGenerator);
	}

	public String getPosition() {
		return position.toString();
	}
	
	// ================
	// testing purposes
	// ================
	
	public void  buildPosition(String raw) {
		int endTokenAt = raw.length() - 1;
		if (raw.charAt(endTokenAt) != '!') {
			throw new IllegalStateException("illegal expression");
		}
		
		String noEndToken = raw.substring(0, endTokenAt);
		
		position = new Position(vertexIdGenerator);
		
		String[] regions = noEndToken.split("}");
		for (String regionString : regions) {
			Region region = new Region();
			
			String[] boundaries = regionString.split("\\.");
			
			for (String boundaryString : boundaries) {
				Boundary boundary = new Boundary();
				region.addBoundary(boundary);
				
				for (int i = 0; i < boundaryString.length(); i++) {
					char vertexName = boundaryString.charAt(i);
					
					int id = VertexUtil.getId(vertexName);
					vertexIdGenerator.update(id);
					
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
		String regex = "(\\d+),(\\d+)(?:(\\[\\d+(?:,\\d+)*\\])(\\{\\d+(?:,\\d+)*\\}))?";

		List<String> matches = match(regex, raw);

		int fromVertexId = Integer.parseInt(matches.get(1));
		int toVertexId = Integer.parseInt(matches.get(2));
		
		Move move = new Move();
		move.fromId = fromVertexId;
		move.toId = toVertexId;

		String maybeContaining = matches.get(3);
		String maybeOuter = matches.get(4);
		if (maybeContaining != null && maybeOuter != null) {
			// containing
			String peeledContaining = peel(maybeContaining);
			String[] rawVertexIdsContaining = peeledContaining.split(",");
			for (String rawId : rawVertexIdsContaining) {
				int vertexId = Integer.parseInt(rawId);
				move.containingIds.add(vertexId);
			}
			
			// outer
			String peeledOuter = peel(maybeOuter);
			String[] rawVertexIdsOuter = peeledOuter.split(",");
			for (String rawId : rawVertexIdsOuter) {
				int vertexId = Integer.parseInt(rawId);
				move.idsOfContainingBoundary.add(vertexId);
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
			throw new IllegalStateException("no match!");
		}
		
		List<String> matches = new LinkedList<>();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			String stringMatch = matcher.group(i);
			matches.add(stringMatch);
			
			// System.out.printf("group: '%s'\n", stringMatch);
		}
		
		return matches;
	}
}
