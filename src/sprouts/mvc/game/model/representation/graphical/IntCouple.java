package sprouts.mvc.game.model.representation.graphical;

// 2-tuple
public class IntCouple {

	public int a, b;
	
	public IntCouple() {
	}

	public IntCouple(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	public void set(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", a, b);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + b;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntCouple other = (IntCouple) obj;
		if (a != other.a)
			return false;
		if (b != other.b)
			return false;
		return true;
	}
}
