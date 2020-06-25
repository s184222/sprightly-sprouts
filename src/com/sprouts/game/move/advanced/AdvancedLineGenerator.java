package com.sprouts.game.move.advanced;

import com.sprouts.game.model.Position;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;
import com.sprouts.game.move.pathfinder.PathFinder;
import com.sprouts.game.move.pipe.LineGenerator;
import com.sprouts.game.move.pipe.LinePathResult;
import com.sprouts.game.move.triangles.TriangleGenerator;

/**
 * The advanced line generator has two different line generators,
 * depending on the move is a 1 boundary move or 2 boundary move.
 * 
 * @author Rasmus Møller Larsen, s184190
 * 
 */
public class AdvancedLineGenerator implements LineGenerator {
	
	private LineGenerator twoBoundaryGenerator;
	private LineGenerator oneBoundaryGenerator;
	
	public AdvancedLineGenerator(PathFinder pathfinder, TriangleGenerator triangleGenerator) {
		twoBoundaryGenerator = new TwoBoundaryLineGenerator(pathfinder, triangleGenerator);
		oneBoundaryGenerator = new OneBoundaryLineGenerator(pathfinder, triangleGenerator);
	}

	@Override
	public LinePathResult generate(Move move, Position position) throws MoveException {
		LinePathResult result = null;
		
		if (move.region.isInSameBoundary(move.from, move.to)) {
			result = oneBoundaryGenerator.generate(move, position);
		} else {
			result = twoBoundaryGenerator.generate(move, position);
		}
		
		return result;
	}

}
