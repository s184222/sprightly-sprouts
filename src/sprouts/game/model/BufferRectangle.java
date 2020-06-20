package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class BufferRectangle {
	
	private Vertex lowerLeft, lowerRight, upperLeft, upperRight;
	private List<Vertex> corners;

	private double bufferX, bufferY;
	
	public BufferRectangle() {
		bufferX = 55;
		bufferY = 55;
		
		lowerLeft = new Vertex(Double.MAX_VALUE, Double.MAX_VALUE);
		lowerRight = new Vertex(Double.MAX_VALUE, Double.MIN_VALUE);
		upperLeft = new Vertex(Double.MIN_VALUE, Double.MAX_VALUE);
		upperRight = new Vertex(Double.MIN_VALUE, Double.MIN_VALUE);
		
		corners = new ArrayList<>();
		
		corners.add(lowerLeft);
		corners.add(lowerRight);
		corners.add(upperLeft);
		corners.add(upperRight);
	}
	
	/**
	 * resizes the rectangle, if (x,y) is outside the rectangle after taking the buffing into account.
	 * 
	 * @param x
	 * @param y
	 */
	public void update(double x, double y) {
		double minX = lowerLeft.x + bufferX;
		double minY = lowerLeft.y + bufferY;
		
		double maxX = upperRight.x - bufferX;
		double maxY = upperRight.y - bufferY;
		
		if (x < minX) lowerLeft.x = upperLeft.x = x - bufferX;
		if (y < minY) lowerLeft.y = lowerRight.y = y - bufferY;
		if (x > maxX) lowerRight.x = upperRight.x = x + bufferX;
		if (y > maxY) upperLeft.y = upperRight.y = y + bufferY;
	}
	
	public List<Vertex> getCorners() {
		return corners;
	}
}
