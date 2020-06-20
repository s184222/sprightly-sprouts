package sprouts.game.model;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Vertex {

	public double x, y;

	public Vertex(double x, double y) {
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
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
}