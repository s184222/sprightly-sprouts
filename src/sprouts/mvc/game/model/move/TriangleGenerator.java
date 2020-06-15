package sprouts.mvc.game.model.move;

import java.util.ArrayList;
import java.util.List;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import sprouts.mvc.game.model.Line;
import sprouts.mvc.game.model.Position;
import sprouts.mvc.game.model.Sprout;
import sprouts.mvc.game.model.Vertex;

public class TriangleGenerator {
	
	public List<Triangle> getTriangles(Position position) {

		try {
			ConstrainedMesh positionMesh = new ConstrainedMesh();
			
			List<Line> lines = position.getLines();
			for (Line line : lines) {
				for (int i = 0; i < line.size() - 1; i++ ) {
					Vertex from = line.get(i);
					Vertex to = line.get(i+1);
					
					DEdge edge = new DEdge(from.x, from.y, 0, to.x, to.y, 0);
					positionMesh.addConstraintEdge(edge);
				}
			}
			
			List<Sprout> sprouts = position.getSprouts();
			for (Sprout sprout : sprouts) {
				if (sprout.neighbours.size() > 0) continue;
				Vertex vertex = sprout.position;
				
				DPoint point = new DPoint(vertex.x, vertex.y, 0);
				positionMesh.addPoint(point);
			}
			
			List<Vertex> corners = position.getOuterCorners();
			for (Vertex corner : corners) {
				DPoint point = new DPoint(corner.x, corner.y, 0);
				positionMesh.addPoint(point);
			}
			
			positionMesh.processDelaunay();
			List<DTriangle> dtriangles = positionMesh.getTriangleList();
			
			List<Triangle> triangles = new ArrayList<>();
			for (DTriangle dtriangle : dtriangles) {
				List<DPoint> points = dtriangle.getPoints();
				
				Vertex p1 = asVertex(points.get(0));
				Vertex p2 = asVertex(points.get(1));
				Vertex p3 = asVertex(points.get(2));
				
				Triangle triangle = isCounterClockwise(dtriangle) ? new Triangle(p1, p3, p2) : new Triangle(p1, p2, p3);
				triangles.add(triangle);
			}
			
			return triangles;

		} catch (DelaunayError e) {
			throw new IllegalStateException("triangulation is broken!");
		}
	}
	
	private Vertex asVertex(DPoint point) {
		return new Vertex((float) point.getX(), (float) point.getY());
	}
	
  private boolean isCounterClockwise(DTriangle dtriangle) {
  	List<DPoint> points = dtriangle.getPoints();
  	DPoint a = points.get(0);
  	DPoint b = points.get(1);
  	DPoint c = points.get(2);
  	
    double a11 = a.getX() - c.getX();
    double a21 = b.getX() - c.getX();

    double a12 = a.getY() - c.getY();
    double a22 = b.getY() - c.getY();

    double det = a11 * a22 - a12 * a21;

    return det > 0.0d;
  }
}
