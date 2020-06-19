package sprouts.game.move.pipe;

import sprouts.game.move.MoveNotationException;
import sprouts.game.move.IdMove;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public interface MoveNotationParser {
	/**
	 * 
	 * Extracts the string representation {@link Move} into a {@link IdMove} object.
	 * <br>
	 * A {@link MoveNotationParser} may choose to only fill a subset of the fields in {@link IdMove}, 
	 * if it is adequate for the corresponding {@link MovePathGenerator} to generate a move.
	 * 
	 * @param move - a string in the given move notation.
	 * @return RawMove - extraction of the notation
	 * @throws MoveNotationException if the format of the {@code move} is illegal.
	 * 
	 * @see MovePathGenerator
	 */
	public IdMove parse(String move) throws MoveNotationException;
}
