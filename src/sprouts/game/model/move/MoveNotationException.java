package sprouts.game.model.move;

public class MoveNotationException extends Exception {
	
	public MoveNotationException(String format, Object ... args) {
		super(String.format(format, args));
	}
}
