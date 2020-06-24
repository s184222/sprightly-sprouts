package sprouts.game.util;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

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
