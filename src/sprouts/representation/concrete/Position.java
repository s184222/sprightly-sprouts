package sprouts.representation.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Position {

	/*
	 * GraphicalRepresentation:
	 * 
	 * ConcreteRepresentation:
	 * 
	 * (Paper/Abstact)Representation: position: string
	 * 
	 * ====
	 * 
	 * AI: 
	 * moves = concrete.computeAllMoves() 
	 * for Move m in moves: 
	 *   abstract = concrete.toAbstractRepresentation()
	 * 	 winning = abstract.isWinning(abstract) 
	 *   if winning then concrete.doMove(m)
	 */

	private List<Region> regions;
	private Map<Integer, Integer> spotIdToLives;
	
	public Position() {
		this(0);
	}

	public Position(int initialNumberOfSpots) {
		regions = new ArrayList<>();
		spotIdToLives = new HashMap<>();

		if (initialNumberOfSpots > 0) {
			Region initialRegion = new Region();
			regions.add(initialRegion);
	
			for (int i = 0; i < initialNumberOfSpots; i++) {
				int vertex = createVertex();
	
				Boundary boundary = new Boundary();
				boundary.addVertex(vertex);
	
				initialRegion.addBoundary(boundary);
			}
		}
	}
	
	public void addRegion(Region region) {
		regions.add(region);
		
		for (Boundary boundary : region.getBoundaries()) {
			for (int vertexId : boundary.getVertices()) {
				Integer live = spotIdToLives.remove(vertexId);
				int updatedLive = (live == null) ? 3 : live - 1;
				spotIdToLives.put(vertexId, updatedLive);
				
				// @hack
				if (vertexId >= currentVertexId) currentVertexId += 1;
			}
		}
	}

	// ==============
	// @move, we need vertexId in graphics representation aswell, so the mapping of vertex ids:
	// graphic representation <-> concrete representation 
	// is easier.
	int currentVertexId = 0;

	private int createVertex() {
		int id = currentVertexId;
		spotIdToLives.put(id, 3);
		currentVertexId += 1;
		return id;
	}
	// ================

	/*
	 * start,end[{boundary of spots}={containing spots}]
	 * start,end[containing spots] this may be enough?
	 * 
	 */
	public void makeMove(Move move) {
		int i = move.fromId;
		int j = move.toId;

		Region region = getJointRegion(i, j);

		Boundary x = region.getBoundary(i);
		Boundary y = region.getBoundary(j);

		if (!x.equals(y)) {
			//System.out.printf("2-boundary\n");
			// === 2-boundary move ===
			Boundary merged = new Boundary();

			int z = createVertex();

			Boundary x1_xi = x.grabTo(i);

			merged.addVertices(x1_xi.getVertices());
			merged.addVertex(z);

			if (y.size() > 1) {
				Boundary yj_yn = y.grabFrom(j);
				merged.addVertices(yj_yn.getVertices());
			}

			Boundary y1_yj = y.grabTo(j);
			merged.addVertices(y1_yj.getVertices());
			merged.addVertex(z);

			if (x.size() > 1) {
				Boundary xi_xm = x.grabFrom(i);
				merged.addVertices(xi_xm.getVertices());
			}

			region.removeBoundary(x);
			region.removeBoundary(y);
			region.addBoundary(merged);

		} else {

			//System.out.printf("1-boundary\n");
			// === 1-boundary move ===
			List<Boundary> otherBoundaries = new LinkedList<>();
			otherBoundaries.addAll(region.getBoundaries());
			otherBoundaries.remove(x);

			// @test: put the right one the right place
			List<Boundary> B1 = getBoundariesContainingVertices(move.containingIds, otherBoundaries);
			List<Boundary> B2 = getBoundaryAMinusBoundaryB(otherBoundaries, B1);

			//System.out.printf("B1: %d\n", B1.size());
			//System.out.printf("B2: %d\n", B2.size());

			int z = createVertex();

			// === region 1 ===

			// It seems like the paper has made a mistake for region 1.
			// It should be:
			// 		xj_xn x1_xi z
			// and NOT
			// 		x1_xi z xj_xn
			// @testing

			Boundary b1 = new Boundary();

			if (x.size() > 1) {
				Boundary xj_xn = x.grabFrom(j);
				b1.addVertices(xj_xn.getVertices());
			}

			Boundary x1_xi = x.grabTo(i);
			b1.addVertices(x1_xi.getVertices());

			b1.addVertex(z);

			Region r1 = new Region();
			r1.addBoundary(b1);
			r1.addBoundaries(B1);

			regions.add(r1);

			// === region 2 ===
			Boundary b2 = new Boundary();

			Boundary xi_xj = x.grabRange(i, j);
			b2.addVertices(xi_xj.getVertices());

			b2.addVertex(z);

			Region r2 = new Region();
			r2.addBoundary(b2);
			r2.addBoundaries(B2);

			regions.add(r2);

			// remove outdated region
			regions.remove(region);
		}
	}

	private List<Boundary> getBoundariesContainingVertices(List<Integer> vertices, List<Boundary> boundaries) {
		List<Boundary> containing = new LinkedList<>();

		next: for (Boundary boundary : boundaries) {
			for (int vertex : vertices) {
				if (boundary.containsVertex(vertex)) {
					containing.add(boundary);
					break next;
				}
			}
		}

		return containing;
	}

	private List<Boundary> getBoundaryAMinusBoundaryB(List<Boundary> boundaryA, List<Boundary> boundaryB) {
		List<Boundary> minus = new LinkedList<>();
		minus.addAll(boundaryA);

		for (Boundary a : boundaryA) {
			if (boundaryB.contains(a)) minus.remove(a);
		}

		return minus;
	}

	private Region getJointRegion(int vertexA, int vertexB) {
		List<Region> regionsA = getRegions(vertexA);
		List<Region> regionsB = getRegions(vertexB);

		regionsA.retainAll(regionsB);

		if (regionsA.size() != 1) {
			throw new IllegalStateException("could not find joint region!");
		}

		return regionsA.get(0);
	}

	private List<Region> getRegions(int vertex) {
		List<Region> found = new ArrayList<>();
		for (Region region : regions) {
			for (Boundary boundary : region.getBoundaries()) {
				if (boundary.containsVertex(vertex)) {
					found.add(region);
					break;
				}
			}
		}
		return found;
	}

	@Override
	public String toString() {
		String string = "";
		for (Region region : regions) {
			string += region;
		}
		string += "!";
		return string;
	}
}
