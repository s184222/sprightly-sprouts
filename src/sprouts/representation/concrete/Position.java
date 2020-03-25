package sprouts.representation.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sprouts.test.Assert;

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
	
	private UidGenerator vertexIdGenerator;

	private int initialNumberOfLives;

	private List<Region> regions;
	private Map<Integer, Integer> spotIdToLives;
	
	public Position(UidGenerator vertexIdGenerator) {
		this(0, vertexIdGenerator);
	}

	public Position(int initialNumberOfSpots, UidGenerator vertexIdGenerator) {
		this.vertexIdGenerator = vertexIdGenerator;
		
		vertexIdGenerator.reset();
		
		regions = new ArrayList<>();
		spotIdToLives = new HashMap<>();
		
		initialNumberOfLives = 3;

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

	// @incomplete
	// @move?
	public List<Move> getAllMoves() {
		List<Move> moves = new LinkedList<>();
		
		for (Region region : regions) {

			// 1 boundary moves
			List<Boundary> boundaries = region.getBoundaries();
			
			for (int i = 0; i < boundaries.size(); i++) {
				Boundary boundary = boundaries.get(i);
				List<Integer> vertices = boundary.getVertices();
				
				for (int j = i; j < vertices.size(); j++) {
					Move move = new Move();
					move.fromId = j;
					move.toId = i;
					
					moves.add(move);
				}
			}

			// 2 boundary moves
			
		}
		
		return moves;
	}

	
	public void addRegion(Region region) {
		regions.add(region);
		
		for (Boundary boundary : region.getBoundaries()) {
			for (int vertexId : boundary.getVertices()) {
				Integer live = spotIdToLives.remove(vertexId);
				int updatedLive = (live == null) ? initialNumberOfLives : live - 1;
				spotIdToLives.put(vertexId, updatedLive);
			}
		}
	}

	private int createVertex() {
		int id = vertexIdGenerator.generate();
		spotIdToLives.put(id, initialNumberOfLives);
		return id;
	}

	/*
	 * start,end[inner boundary]{outer boundary}
	 */
	
	// we need vertexId in graphics representation aswell, so the mapping of vertex ids:
	// graphic representation <-> concrete representation 
	// is easier. We could result a moveResult (success, id) so the graphics get it.

	public void makeMove(Move move) {
		int i = move.fromId;
		int j = move.toId;

		Region region = getJointRegion(move);

		Boundary x = region.getBoundary(i);
		Boundary y = region.getBoundary(j);

		//System.out.printf("x: %s\n", x);
		//System.out.printf("y: %s\n", y);
		
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

			Region r1 = new Region();
			r1.addBoundary(b1);

			// === region 2 ===
			Boundary b2 = new Boundary();

			Boundary xi_xj = x.grabRange(i, j);
			b2.addVertices(xi_xj.getVertices());

			Region r2 = new Region();
			r2.addBoundary(b2);

			// === make B1 and B2 ===
			List<Boundary> otherBoundaries = new LinkedList<>();
			otherBoundaries.addAll(region.getBoundaries());
			otherBoundaries.remove(x);

			// @test: put the right one the right place
			List<Boundary> B1 = getBoundariesContainingVertices(move.innerIds, otherBoundaries);
			List<Boundary> B2 = getFirstBoundaryMinusSecondBoundary(otherBoundaries, B1);
			
			// check which one is the outer boundary
			// the outer boundary has to contain all of either b1 or b2
			// but no more or less vertices.
			if (b1.containsExactlyVertices(move.outerIds)) {
				r1.addBoundaries(B1);
				r2.addBoundaries(B2);
			} else {
				Assert.assertTrue(b2.containsExactlyVertices(move.outerIds));
				r1.addBoundaries(B2);
				r2.addBoundaries(B1);
			}
			
			b1.addVertex(z);
			b2.addVertex(z);
			
			regions.add(r1);
			regions.add(r2);

			// remove outdated region
			regions.remove(region);
		}
	}

	private List<Boundary> getBoundariesContainingVertices(List<Integer> vertices, List<Boundary> boundaries) {
		List<Boundary> containing = new LinkedList<>();

		for (Boundary boundary : boundaries) {
			for (int vertex : vertices) {
				if (boundary.containsVertex(vertex)) {
					containing.add(boundary);
				}
			}
		}

		return containing;
	}

	private List<Boundary> getFirstBoundaryMinusSecondBoundary(List<Boundary> first, List<Boundary> second) {
		List<Boundary> minus = new LinkedList<>();
		minus.addAll(first);

		for (Boundary a : first) {
			if (second.contains(a)) minus.remove(a);
		}

		return minus;
	}

	// @speed
	private Region getJointRegion(Move move) {
		List<Region> regionsA = getRegions(move.fromId);
		List<Region> regionsB = getRegions(move.toId);

		regionsA.retainAll(regionsB);

		for (int id : move.innerIds) {
			List<Region> regionsC = getRegions(id);
			regionsA.retainAll(regionsC);
		}
		
		if (regionsA.size() != 1) {
			String error = String.format("found %d joint regions.", regionsA.size());
			throw new IllegalStateException(error);
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
