package sprouts.mvc.game.model.move;

public class MoveException extends Exception {
	
	public MoveException(String format, Object ... args) {
		super(String.format(format, args));
	}
}
