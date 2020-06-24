package sprouts.game.util;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class MathUtil {
	
	public static double distance(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return (double)Math.sqrt(dx*dx+dy*dy);
	}
	
		
	public static int sign (double value) {
		if (value < 0) return -1;
		else if (value > 0) return 1;
		return 0;
	}
	
	public static int wrap(int value, int max) {
		return Math.floorMod(value, max);
	}
}
