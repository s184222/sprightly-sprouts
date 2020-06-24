package sprouts.game.model;

import java.util.ArrayList;
import java.util.Collections;

import com.sprouts.math.LinMath;

import sprouts.game.util.Assert;
import sprouts.game.util.MathUtil;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
@SuppressWarnings("serial")
public class Line extends ArrayList<Vertex> {
	
	public Vertex getFirst() {
		return get(0);
	}
	
	public Vertex getSemiFirst() {
		return get(1);
	}
	
	public Vertex getLast() {
		return get(size() - 1);
	}
	
	public Vertex getSemiLast() {
		return get(size() - 2);
	}
	
	public Line[] split(int at) {
		Line line1 = new Line();
		line1.addAll(subList(0, at + 1));
		
		Line line2 = new Line();
		line2.addAll(subList(at, size()));
		
		Line[] lines = {line1, line2};

		return lines;
	}
	
	/**
	 * 
	 * Splits the line at the vertex closest to the middle of the line
	 * 
	 * 
	 * @return the line splitted at the middle into two smaller lines
	 *
	 */
	public Line[] splitMiddle() {
		Assert.that(size() >= 3);
		
		double closest = Double.MAX_VALUE;
		int centerIndex = -1;
		
		double totalDistance = getDistance();
		double centerDistance = totalDistance / 2d;
		double accumulatedDistance = 0;
		
		for (int i = 1; i < size() - 1; i++) {
			Vertex v1 = get(i - 1);
			Vertex v2 = get(i);
			
			double distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			accumulatedDistance += distance;
			
			double dt = Math.abs(accumulatedDistance - centerDistance);
			if (dt <= closest) {
				closest = dt;
				centerIndex = i;
			}
		}
		
		return split(centerIndex);
	}
	
	public boolean isLineSegment(Vertex from, Vertex to) {
		for (int i = 0; i < size() - 1; i++) {
			Vertex l1 = get(i);
			Vertex l2 = get(i + 1);
			if (from.equals(l1) && to.equals(l2)) return true;
		}
		return false;
	}
	
	public LineSegment getFirstSegment() {
		return new LineSegment(getFirst(), getSemiFirst());
	}
	
	public LineSegment getLastSegment() {
		return new LineSegment(getLast(), getSemiLast());
	}
	
	public LineSegment get1stQuarterSegment() {
		int center = (size() - 1) / 4;
		return new LineSegment(get(center), get(center+1));
	}
	
	public LineSegment getMiddleSegment() {
		int center = (size() - 1) / 2;
		return new LineSegment(get(center), get(center+1));
	}

	public Vertex removeLast() {
		return remove(size() - 1);
	}

	public Vertex removeFirst() {
		return remove(0);
	}
	
	public double getDistance() {
		double totalDistance = 0;
		
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);
			
			double distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			totalDistance += distance;
		}
		
		return totalDistance;
	}
	
	public int addMiddlePoint() {
		double totalDistance = getDistance();
		double centerDistance = totalDistance / 2d;
		double accumulatedDistance = 0;
		
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);
			
			double distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			accumulatedDistance += distance;
			
			if (accumulatedDistance >= centerDistance) {
				double scale = 1d - (accumulatedDistance - centerDistance) / distance;
				
				if (scale >= 0.2 && scale <= 0.8) {
					double vx = (v2.x - v1.x) * scale + v1.x;
					double vy = (v2.y - v1.y) * scale + v1.y;
					
					Vertex vertex = new Vertex(vx, vy);
					
					add(i + 1, vertex);

					return i + 1;
				}

				return i;
			}
		}
		
		throw new IllegalStateException("could not add a middle point, because the line only has 1 point!");
	}
	
	public void reverse() {
		Collections.reverse(this);
	}
	
	public Line reversedCopy() {
		Line reversed = copy();
		reversed.reverse();
		return reversed;
	}
	
	public Line copy() {
		Line line = new Line();
		line.addAll(this);
		return line;
	}
	
	public void append(Line line) {
		if (size() > 0) {
			Assert.that(getLast().equals(line.getFirst()));
			removeLast();
		}
		
		addAll(line);
	}
	
	public boolean intersects(double x1, double y1, double x2, double y2) {
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);

			boolean intersection = LinMath.intersect(x1, y1, x2, y2, v1.x, v1.y, v2.x, v2.y);
			if (intersection) return true;
		}
		
		return false;
	}
	
	public boolean intersects(Line other) {
		for (int i = 0; i < other.size() - 1; i++) {
			Vertex v1 = other.get(i);
			Vertex v2 = other.get(i + 1);
			
			if (intersects(v1.x, v1.y, v2.x, v2.y)) return true;
		}
		
		return false;
	}
}
