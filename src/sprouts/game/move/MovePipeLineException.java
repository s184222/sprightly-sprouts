package sprouts.game.move;

/**
 * Exception is throws if no pipelines can execute the move.
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class MovePipeLineException extends IllegalStateException {
	
	public MovePipeLineException(String format, Object ... args) {
		super(String.format(format, args));
	}
}

