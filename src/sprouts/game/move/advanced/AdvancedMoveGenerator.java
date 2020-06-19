package sprouts.game.move.advanced;

import sprouts.game.model.Position;
import sprouts.game.move.Move;
import sprouts.game.move.MoveException;
import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MovePathGenerator;
import sprouts.game.move.pipe.MovePathResult;
import sprouts.game.move.triangles.TriangleGenerator;

/**
 * The advanced move generator has two different move generators,
 * depending on the move is a 1 boundary move or 2 boundary move.
 * 
 * @author Rasmus Møller Larsen, s184190
 * 
 */
public class AdvancedMoveGenerator implements MovePathGenerator {
	
	private MovePathGenerator oneBoundaryGenerator;
	private MovePathGenerator twoBoundaryGenerator;
	
	public AdvancedMoveGenerator(PathFinder pathfinder, TriangleGenerator triangleGenerator) {
		oneBoundaryGenerator = new TwoBoundaryMoveGenerator(pathfinder, triangleGenerator);
		twoBoundaryGenerator = new OneBoundaryMoveGenerator(pathfinder, triangleGenerator);
	}

	@Override
	public MovePathResult generate(Move move, Position position) throws MoveException {
		MovePathResult result = null;
		
		if (move.region.isInSameBoundary(move.from, move.to)) {
			result = twoBoundaryGenerator.generate(move, position);
		} else {
			result = oneBoundaryGenerator.generate(move, position);
		}
		
		return result;
	}

}
