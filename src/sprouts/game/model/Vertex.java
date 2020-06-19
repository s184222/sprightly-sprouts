package sprouts.game.model;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Vertex {

	public float x, y;

	public Vertex() {
	}

	public Vertex(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
	
	public Vertex copy() {
		return new Vertex(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
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
		Vertex other = (Vertex) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
}