package sprouts.game.model;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class LineSegment {
	
	public Vertex from, to;
	
	public LineSegment(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
	}
	
	public void set(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
	}
	
	public Vertex getMiddle() {
		
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		
		double vx = from.x + dx / 2d;
		double vy = from.y + dy / 2d;
		
		return new Vertex(vx, vy);
	}

	public Vertex getVertexAt(double ratio) {
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		
		double vx = from.x + dx * ratio;
		double vy = from.y + dy * ratio;
		
		return new Vertex(vx, vy);
	}
	
	public void reverse() {
		Vertex tmp = from;
		from = to;
		to = tmp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineSegment other = (LineSegment) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}
}