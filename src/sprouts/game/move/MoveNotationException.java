package sprouts.game.move;

/**
 * Exception is throws if a move in a string format is invalid.
 * 
 * @author Rasmus M�ller Larsen, s184190
 *
 */
public class MoveNotationException extends Exception {
	
	public MoveNotationException(String format, Object ... args) {
		super(String.format(format, args));
	}
}
