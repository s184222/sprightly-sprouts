package sprouts.game.ai;

import sprouts.game.model.UidGenerator;
import sprouts.game.model.Util;
import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;

public class AbstractFacade {
	
	private UidGenerator vertexIdGenerator;
	private Position position;

	public AbstractFacade() {
		vertexIdGenerator = new UidGenerator();
	}
	
	public void makeMove(String move) {
		try {
			RawMove rawMove = new RawMove(move);
			position.makeMove(rawMove);
		} catch (MoveNotationException e) {
			System.out.printf("%s\n", e.getMessage());
		}
	}
	
	public void createFreshPosition(int initialNumberOfSprouts) {
		position = new Position(initialNumberOfSprouts, vertexIdGenerator);
	}

	public String getPositionString() {
		return position.toString();
	}
	
	public Position getPosition() {
		return position;
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
				region.add(boundary);
				
				String[] ids = boundaryString.split(",");
				
				for (String rawId : ids) {
					int id = Integer.parseInt(rawId);
					
					vertexIdGenerator.update(id);
					
					boundary.add(id);
				}
			}
			
			position.add(region);
		}
		
		// @TODO: remove sanity check
		Util.require(getPositionString().equals(raw));
	}
}
