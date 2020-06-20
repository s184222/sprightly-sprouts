package sprouts.game.move;

/**
 * Exception is throws if a {@link Move} is invalid in a given position.
 * 
 * @see Move
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
@SuppressWarnings("serial")
public class MoveException extends Exception {
	
	public MoveException(String format, Object ... args) {
		super(String.format(format, args));
	}
}
