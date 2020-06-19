package sprouts.game.model.move;

import sprouts.game.model.Position;
import sprouts.game.model.move.generators.MovePathGenerator;
import sprouts.game.model.move.generators.MovePathResult;
import sprouts.game.model.move.generators.one.AdvancedMoveGenerator;
import sprouts.game.model.move.generators.two.SimpleMoveGenerator;
import sprouts.game.model.move.pathfinder.AStarPathFinder;
import sprouts.game.model.move.pathfinder.PathFinder;

public class RawMovePathGenerator {
	
	private MovePreprocessor preprocessor;
	
	private PathFinder pathfinder;
	private TriangleGenerator triangleGenerator;
	
	private MovePathGenerator simpleGenerator;
	private MovePathGenerator advancedGenerator;
	
	public RawMovePathGenerator() {
		pathfinder = new AStarPathFinder();
		triangleGenerator = new TriangleGenerator();
		preprocessor = new MovePreprocessor();
		
		simpleGenerator = new SimpleMoveGenerator(pathfinder, triangleGenerator);
		advancedGenerator = new AdvancedMoveGenerator(pathfinder, triangleGenerator);
	}
	
	public MovePathResult generate(RawMove rawMove, Position position) throws MoveException {
		MovePathResult result = null;

		Move move = preprocessor.process(rawMove, position);

		boolean simple = rawMove.any;
		if (simple) {
			result = simpleGenerator.generate(move, position);
		} else {
			if (move.region.isInSameBoundary(move.from, move.to)) {
				result = advancedGenerator.generate(move, position);
			} else {
				result = simpleGenerator.generate(move, position);
			}
		}
		
		return result;
	}
}
