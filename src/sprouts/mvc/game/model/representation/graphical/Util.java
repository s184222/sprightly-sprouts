package sprouts.mvc.game.model.representation.graphical;

public class Util {
	
	public static void require(boolean condition, String message) {
		if (!condition) {
			String error = String.format("error... %s\n", message);
			throw new IllegalStateException(error);
		}
	}
	
	public static void require(boolean condition) {
		require(condition, "");
	}
}
