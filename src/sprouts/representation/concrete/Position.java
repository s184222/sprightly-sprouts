package sprouts.representation.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
				boundary.add(vertex);
	
				initialRegion.add(boundary);
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

	
	public void add(Region region) {
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
		int from = move.fromId;
		int to = move.toId;

		Region region = getJointRegion(move);

		Boundary x = region.getBoundary(from);
		Boundary y = region.getBoundary(to);

		//System.out.printf("x: %s\n", x);
		//System.out.printf("y: %s\n", y);
		
		if (!x.equals(y)) {
			//System.out.printf("2-boundary\n");
			// === 2-boundary move ===
			Boundary merged = new Boundary();

			// @todo: update numberOfLives hashmap
			int z = createVertex();

			List<Integer> x1_xi = x.grabTo(from);

			merged.add(x1_xi);
			merged.add(z);

			if (y.size() > 1) {
				List<Integer> yj_yn = y.grabFrom(to);
				merged.add(yj_yn);
			}

			List<Integer> y1_yj = y.grabTo(to);
			merged.add(y1_yj);
			merged.add(z);

			if (x.size() > 1) {
				List<Integer> xi_xm = x.grabFrom(from);
				merged.add(xi_xm);
			}

			region.remove(x);
			region.remove(y);
			region.add(merged);

		} else {
			//System.out.printf("1-boundary\n");
			// === 1-boundary move ===
			int z = createVertex();

			// region 1

			// It seems like the paper has made a mistake for region 1.
			// It should be:
			// 		xj_xn x1_xi z
			// and NOT
			// 		x1_xi z xj_xn
			// @testing

			Boundary boundary1 = new Boundary();

			if (x.size() > 1) {
				List<Integer> xj_xn = x.grabFrom(to);
				boundary1.add(xj_xn);
			}

			List<Integer> x1_xi = x.grabTo(from);
			boundary1.add(x1_xi);

			Region region1 = new Region();
			region1.add(boundary1);

			
			// region 2
			Boundary boundary2 = new Boundary();

			// @fix: should from <= to? The paper may have a mistake.
			// grabRange should be wrong either way
			List<Integer> xi_xj = x.grabRange(from, to);
			boundary2.add(xi_xj);

			Region region2 = new Region();
			region2.add(boundary2);

			
			List<Boundary> otherBoundaries = new LinkedList<>();
			otherBoundaries.addAll(region.getBoundaries());
			otherBoundaries.remove(x);

			// @test: put the right one the right place
			List<Boundary> B1 = getBoundariesContainingVertices(move.innerIds, otherBoundaries);
			List<Boundary> B2 = getFirstBoundariesMinusSecondBoundaries(otherBoundaries, B1);
			
			// check which one is the outer boundary
			// the outer boundary has to contain all of either b1 or b2
			// but no more or less vertices.
			if (boundary1.containsSameVertices(move.outerIds)) {
				region1.add(B1);
				region2.add(B2);
			} else {
				Assert.assertTrue(boundary2.containsSameVertices(move.outerIds));
				region1.add(B2);
				region2.add(B1);
			}
			
			// add z after doing the B1/B2 placement, because we expect that that
			// the outer boundary ignores the newly generated vertex when checking.
			boundary1.add(z);
			boundary2.add(z);
			
			regions.add(region1);
			regions.add(region2);

			// remove outdated region
			regions.remove(region);
		}
	}

	private List<Boundary> getBoundariesContainingVertices(List<Integer> vertices, List<Boundary> boundaries) {
		List<Boundary> containing = new LinkedList<>();

		for (Boundary boundary : boundaries) {
			for (int vertex : vertices) {
				if (boundary.contains(vertex)) {
					containing.add(boundary);
					break;
				}
			}
		}

		return containing;
	}

	private List<Boundary> getFirstBoundariesMinusSecondBoundaries(List<Boundary> first, List<Boundary> second) {
		List<Boundary> minus = new LinkedList<>();
		minus.addAll(first);
		minus.removeAll(second);
		return minus;
	}

	private void removeRegionsNotContainingVertex(List<Region> regions, int vertexId) {
		for (Iterator<Region> it = regions.iterator(); it.hasNext();) {
			Region region = it.next();

			boolean insideRegion = false;
			for (Boundary boundary : region.getBoundaries()) {
				if (boundary.contains(vertexId)) {
					insideRegion = true;
					break;
				}
			}
			
			if (!insideRegion) it.remove();
		}
	}
	
	private Region getJointRegion(Move move) {
		List<Region> joint = getRegions(move.fromId);
		removeRegionsNotContainingVertex(joint, move.toId);

		for (int id : move.innerIds) {
			removeRegionsNotContainingVertex(joint, id);
		}
		
		if (joint.size() != 1) {
			String error = String.format("found %d joint regions.", joint.size());
			throw new IllegalStateException(error);
		}

		return joint.get(0);
	}

	private List<Region> getRegions(int vertex) {
		List<Region> found = new ArrayList<>();
		for (Region region : regions) {
			for (Boundary boundary : region.getBoundaries()) {
				if (boundary.contains(vertex)) {
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
