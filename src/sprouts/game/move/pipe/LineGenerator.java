package sprouts.game.move.pipe;

import sprouts.game.model.Position;
import sprouts.game.move.MoveException;
import sprouts.game.move.Move;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public interface LineGenerator {
	
	/**
	 * Generates a line which satisfies the {@code move}
	 * 
	 * @param move - request
	 * @param position - the current position of the game
	 * @return the line and some optimal custom data
	 * @throws MoveException if no line satisfies the move
	 */
	public LinePathResult generate(Move move, Position position) throws MoveException;
}
