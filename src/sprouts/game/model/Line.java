package sprouts.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.sprouts.math.LinMath;

import sprouts.game.util.MathUtil;
import sprouts.game.util.Assert;

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
	
	public Line[] splitMiddle() {
		Assert.that(size() >= 3);
		
		float closest = Float.MAX_VALUE;
		int centerIndex = -1;
		
		float totalDistance = getDistance();
		float centerDistance = totalDistance / 2f;
		float accumulatedDistance = 0;
		
		for (int i = 1; i < size() - 1; i++) {
			Vertex v1 = get(i - 1);
			Vertex v2 = get(i);
			
			float distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			accumulatedDistance += distance;
			
			float dt = Math.abs(accumulatedDistance - centerDistance);
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

	public void removeLast() {
		remove(size() - 1);
	}

	public void removeFirst() {
		remove(0);
	}
	
	public float getDistance() {
		float totalDistance = 0;
		
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);
			
			float distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			totalDistance += distance;
		}
		
		return totalDistance;
	}
	
	public int addMiddlePoint() {
		float totalDistance = getDistance();
		float centerDistance = totalDistance / 2f;
		float accumulatedDistance = 0;
		
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);
			
			float distance = MathUtil.distance(v2.x, v2.y, v1.x, v1.y);
			accumulatedDistance += distance;
			
			if (accumulatedDistance >= centerDistance) {
				float scale = 1f - (accumulatedDistance - centerDistance) / distance;
				
				float vx = (v2.x - v1.x) * scale + v1.x;
				float vy = (v2.y - v1.y) * scale + v1.y;
				
				Vertex vertex = new Vertex(vx, vy);
				
				if (!contains(vertex)) {
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
	
	public boolean intersects(float x1, float y1, float x2, float y2) {
		for (int i = 0; i < size() - 1; i++) {
			Vertex v1 = get(i);
			Vertex v2 = get(i + 1);

			boolean intersection = LinMath.intersect(x1, y1, x2, y2, v1.x, v1.y, v2.x, v2.y);
			if (intersection)	return true;
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
