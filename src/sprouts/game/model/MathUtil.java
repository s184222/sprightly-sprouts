package sprouts.game.model;

public class MathUtil {
	
	public static float distance(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return (float)Math.sqrt(dx*dx+dy*dy);
	}
	
		
	public static int sign (float value) {
		if (value < 0) return -1;
		else if (value > 0) return 1;
		return 0;
	}
	
	public static int wrap(int value, int max) {
		return Math.floorMod(value, max);
	}
}
