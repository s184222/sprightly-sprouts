package sprouts.game.move.advanced;

import sprouts.game.model.Position;
import sprouts.game.move.Move;
import sprouts.game.move.MoveException;
import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.LineGenerator;
import sprouts.game.move.pipe.LinePathResult;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * The advanced line generator has two different line generators,
 * depending on the move is a 1 boundary move or 2 boundary move.
 * 
 * @author Rasmus M�ller Larsen, s184190
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