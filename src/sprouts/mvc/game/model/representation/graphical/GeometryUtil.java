package sprouts.mvc.game.model.representation.graphical;

public class GeometryUtil {
	
	public static boolean intersectsSegments(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return intersectsSegments(x1, y1, x2, y2, x3, y3, x4, y4, true, false);
	}
	
	public static boolean intersectsSegmentsAllowTouch(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		return intersectsSegments(x1, y1, x2, y2, x3, y3, x4, y4, true, true);
	}
	
	public static boolean intersectsSegments(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, boolean allowFirst, boolean allowSecond) {
			float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
			if (d == 0) return false;
			
			// @bug, maybe related to big bug.
			// allowFirst=true
			// then it can stil oversect if is parallel and on top
			// (more than just 1 point intersecting).
			if (allowFirst) {
				if (x1 == x3 && y1 == y3) return false;
				if (x1 == x4 && y1 == y4) return false;
			} else {
				if (x1 == x3 && y1 == y3) return true;
				if (x1 == x4 && y1 == y4) return true;
			}
			
			if (allowSecond) {
				if (x2 == x3 && y2 == y3) return false;
				if (x2 == x4 && y2 == y4) return false;
			} else {
				if (x2 == x3 && y2 == y3) return true;
				if (x2 == x4 && y2 == y4) return true;
			}

			float yd = y1 - y3;
			float xd = x1 - x3;
			float ua = ((x4 - x3) * yd - (y4 - y3) * xd) / d;
			if (ua < 0 || ua > 1) return false;

			float ub = ((x2 - x1) * yd - (y2 - y1) * xd) / d;
			if (ub < 0 || ub > 1) return false;

			return true;
	}
}
