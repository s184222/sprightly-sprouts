package com.sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

import com.sprouts.math.Vec2d;

/**
 * 
 * Builds a position
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class PositionBuilder {
	
	private List<Vertex> pendingSprouts;
	
	public PositionBuilder() {
		pendingSprouts = new ArrayList<>();
	}

	public PositionBuilder createSproutsCircle(int initialNumberOfSprouts, double centerX, double centerY, double radius) {
		Vec2d rotater = new Vec2d();

		double deltaDegrees = 360 / (double) initialNumberOfSprouts;
		
		rotater.set(1,0);
		for (int i = 0; i < initialNumberOfSprouts; i++) {
			rotater.normalize().setAngle(deltaDegrees * i);
			rotater.mul(radius);
			
			double x = centerX + rotater.x;
			double y = centerY + rotater.y;
			
			Vertex vertex = new Vertex(x, y);
			pendingSprouts.add(vertex);
		}
		
		return this;
	}
	
	public Position build() {
		Position position = new Position();
		
		for (Vertex vertex : pendingSprouts) {
			position.addVertex(vertex);
			Sprout sprout = position.createSprout(vertex);
			position.getOuterRegion().innerSprouts.add(sprout);
		}
		
		pendingSprouts.clear();
		
		return position;
	}
}
