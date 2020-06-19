package sprouts.game.move.triangles;

import java.util.Arrays;

import sprouts.game.model.Vertex;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Triangle {
	
	private Vertex[] corners;
	private Vertex center;
	
	public Triangle(Vertex p1, Vertex p2, Vertex p3) {
		center = new Vertex();
		corners = new Vertex[] {p1, p2, p3};
		center.x = (p1.x + p2.x + p3.x) / 3f;
		center.y = (p1.y + p2.y + p3.y) / 3f;
	}
	
	public boolean isCorner(Vertex point) {
		return corners[0].equals(point) || corners[1].equals(point) || corners[2].equals(point);
	}
	
	public boolean areAllCorners(Vertex a, Vertex b, Vertex c) {
		if (a.equals(b) || b.equals(c) || a.equals(c)) return false;
		return isCorner(a) && isCorner(b) && isCorner(c);
	}
	
	public int getCornerIndex(Vertex ref) {
		for (int i = 0; i < corners.length; i++) {
			Vertex corner = corners[i];
			
			if (corner.equals(ref)) return i;
		}
		return -1;
	}
	
	public Vertex getCenter() {
		Vertex p1 = corners[0];
		Vertex p2 = corners[1];
		Vertex p3 = corners[2];
		
		center.x = (p1.x + p2.x + p3.x) / 3f;
		center.y = (p1.y + p2.y + p3.y) / 3f;
		
		return center;
	}
	
	public Vertex[] getCorners() {
		return corners;
	}
	
	public Vertex getP1() {
		return corners[0];
	}
	
	public Vertex getP2() {
		return corners[1];
	}
	
	public Vertex getP3() {
		return corners[2];
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("[");
		for (int i = 0; i < corners.length - 1; i++) {
			builder.append(corners[i] + "," );
		}
		
		builder.append(corners[corners.length - 1]);
		builder.append("]");
		
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((center == null) ? 0 : center.hashCode());
		result = prime * result + Arrays.hashCode(corners);
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
		Triangle other = (Triangle) obj;
		if (center == null) {
			if (other.center != null)
				return false;
		} else if (!center.equals(other.center))
			return false;
		if (!Arrays.equals(corners, other.corners))
			return false;
		return true;
	}
}
