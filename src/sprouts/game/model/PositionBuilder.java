package sprouts.game.model;

import com.sprouts.math.Vec2;

/**
 * 
 * Builds a position
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class PositionBuilder {
	
	private Position position;
	
	public PositionBuilder() {
		position = new Position();
	}

	public PositionBuilder createSproutsCircle(int initialNumberOfSprouts, float centerX, float centerY, float radius) {
		Vec2 rotater = new Vec2();

		float deltaDegrees = 360 / (float) initialNumberOfSprouts;
		
		rotater.set(1,0);
		for (int i = 0; i < initialNumberOfSprouts; i++) {
			rotater.normalize().setAngle(deltaDegrees * i);
			rotater.mul(radius);
			
			float x = centerX + rotater.x;
			float y = centerY + rotater.y;
			
			createFreshSprout(x, y);
		}
		
		return this;
	}
	
	public PositionBuilder createFreshSprout(float x, float y) {
		Vertex vertex = new Vertex(x, y);
		position.addVertex(vertex);
		Sprout sprout = position.createSprout(vertex);
		Region outer = position.getOuterRegion();
		outer.innerSprouts.add(sprout);
		return this;
	}
	
	public Position build() {
		Position output = position;
		position = new Position();
		return output;
	}
}
