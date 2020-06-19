package sprouts.game.util;

public class Assert {
	
	public static void that(boolean condition, String message) {
		if (!condition) {
			String error = String.format("error... %s\n", message);
			throw new IllegalStateException(error);
		}
	}
	
	public static void that(boolean condition) {
		that(condition, "");
	}
}
