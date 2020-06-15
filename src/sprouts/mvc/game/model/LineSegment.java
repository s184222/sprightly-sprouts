package sprouts.mvc.game.model;

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
		return getVertexAt(0.5f);
	}

	public Vertex getVertexAt(float ratio) {
		Vertex vertex = new Vertex();
		
		float dx = to.x - from.x;
		float dy = to.y - from.y;
		
		vertex.x = from.x + dx * ratio;
		vertex.y = from.y + dy * ratio;
		
		return vertex;
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