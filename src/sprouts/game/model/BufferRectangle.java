package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

public class BufferRectangle {
	
	private Vertex lowerLeft, lowerRight, upperLeft, upperRight;
	private List<Vertex> corners;
	private float bufferX, bufferY;
	
	public BufferRectangle() {
		bufferX = 40;
		bufferY = 25;
		
		lowerLeft = new Vertex(Float.MAX_VALUE, Float.MAX_VALUE);
		lowerRight = new Vertex(Float.MIN_VALUE, Float.MAX_VALUE);
		upperLeft = new Vertex(Float.MAX_VALUE, Float.MIN_VALUE);
		upperRight = new Vertex(Float.MIN_VALUE, Float.MIN_VALUE);
		
		corners = new ArrayList<>();
		corners.add(lowerLeft);
		corners.add(lowerRight);
		corners.add(upperLeft);
		corners.add(upperRight);
	}
	
	public void update(float x, float y) {
		float minX = lowerLeft.x + bufferX;
		float minY = lowerLeft.y + bufferY;
		
		float maxX = upperRight.x - bufferX;
		float maxY = upperRight.y - bufferY;
		
		if (x < minX) lowerLeft.x = upperLeft.x = x - bufferX;
		if (y < minY) lowerLeft.y = lowerRight.y = y - bufferY;
		if (x > maxX) lowerRight.x = upperRight.x = x + bufferX;
		if (y > maxY) upperLeft.y = upperRight.y = y + bufferY;
	}
	
	public List<Vertex> getCorners() {
		return corners;
	}
}
