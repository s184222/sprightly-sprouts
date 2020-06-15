package sprouts.mvc.game.model.move;

import sprouts.mvc.game.model.Position;
import sprouts.mvc.game.model.move.generators.one.OneBoundaryMoveGenerator;
import sprouts.mvc.game.model.move.two.TwoBoundaryMoveGenerator;

public class RawMovePathGenerator {
	
	private MovePreprocessor preprocessor;
	
	private PathFinder pathfinder;
	private TriangleGenerator triangleGenerator;
	
	private MovePathGenerator twoBoundaryGenerator;
	private MovePathGenerator oneBoundaryGenerator;
	
	public RawMovePathGenerator() {
		pathfinder = new AStarPathFinder();
		triangleGenerator = new TriangleGenerator();
		preprocessor = new MovePreprocessor();
		
		twoBoundaryGenerator = new TwoBoundaryMoveGenerator(pathfinder, triangleGenerator);
		oneBoundaryGenerator = new OneBoundaryMoveGenerator(pathfinder, triangleGenerator);
	}
	
	public MovePathResult generate(RawMove rawMove, Position position) throws MoveException {
		MovePathResult result = null;
		
		Move move = preprocessor.process(rawMove, position);
		
		if (move.region.isInSameBoundary(move.from, move.to)) {
			result = oneBoundaryGenerator.generate(move, position);
		} else {
			result = twoBoundaryGenerator.generate(move, position);
		}
		
		return result;
	}
}
