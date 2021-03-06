package com.sprouts.ai;

import java.util.List;

import com.sprouts.game.UidGenerator;
import com.sprouts.game.move.MoveNotationException;
import com.sprouts.game.move.MovePipeLineException;
import com.sprouts.game.move.advanced.AdvancedMoveNotationParser;
import com.sprouts.game.move.pipe.MoveNotationParser;

/**
 * 
 * Abstract representation of the Sprouts based on the paper by,<br>
 * "Computer analysis of sprouts with nimbers"<br>
 * http://library.msri.org/books/Book63/files/131105-LeMoine.pdf
 * 
 * @author Rasmus
 *
 */
public class AIFacade {
	
	private UidGenerator vertexIdGenerator;
	private Position position;
	

	public AIFacade() {
		vertexIdGenerator = new UidGenerator();
	}
	
	public boolean makeMoves(List<String> moves) {
		for (String move : moves) {
			try {
				makeMove(move);
			} catch (MovePipeLineException e) {
				System.out.printf("not possible to execute the move: %s. Early termining the sequence.\n", move);
				return false;
			}
		}
		
		return true;
	}
	
	public void makeMove(String move) {
		try {
			MoveNotationParser parser = new AdvancedMoveNotationParser();
			position.makeMove(parser.parse(move));
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
	}
}
