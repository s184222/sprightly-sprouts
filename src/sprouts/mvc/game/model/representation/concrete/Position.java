package sprouts.mvc.game.model.representation.concrete;

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

	// @incomplete: when we use/implement the ai, we may not need this one
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

			// 2 boundary moves
			List<Boundary> boundaries = region.getBoundaries();
			for (int i = 1; i < boundaries.size(); i++) {
				Boundary boundary1 = boundaries.get(i - 1);
				Boundary boundary2 = boundaries.get(i);
				
				List<Integer> vertices1 = boundary1.getVertices();
				List<Integer> vertices2 = boundary2.getVertices();
				
				for (int j = 0; j < vertices1.size(); j++) {
					for (int k = j; k < vertices2.size(); k++) {
						Move move = new Move();
						move.fromId = j;
						move.toId = k;
						moves.add(move);
					}
				}
			}
			
			/*
			 * get 1 boundary
			 * select two vertices
			 * bs = getAllOtherBoundaries()
			 * ps = powerset(bs)
			 * 
			 * outerBoundary = make2ndHalfOfMove()
			 * outer = outerBoundary.getVerticesExcludingNewest
			 * for powerset in ps:
			 * 	move.from = i
			 * 	move.to = j
			 * 	move.outer = outer
			 * 	move.inner = powerset.getVertices() 
			 */
			
			// split into 2 sets of boundaries.

			/*
			// 1 boundary moves
			for (int i = 0; i < boundaries.size(); i++) {
				Boundary boundary = boundaries.get(i);
				
				// getOtherBoundaries
				List<Boundary> otherBoundaries = new LinkedList<>();
				otherBoundaries.addAll(region.getBoundaries());
				otherBoundaries.remove(boundary);
				
				// powerset
				List<PowerSet> powersets = getPowerSet(otherBoundaries);
				
				Boundary outerBoundary = make2ndHalfOfMove();
				List<Integer> 
				
				List<Integer> vertices = boundary.getVertices();
				
				for (int j = 0; j < vertices.size(); j++) {
					for (int k = j; k < vertices.size(); k++) {
						Move move = new Move();
						move.fromId = j;
						move.toId = k;
						move.outerIds = outer;
						move.innerIds = inner;
						moves.add(move);
					}
				}
			}
			*/
			
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
	
	private void connect(int vertex1, int vertex2) {
		Integer v1Live = spotIdToLives.remove(vertex1);
		Integer v2Live = spotIdToLives.remove(vertex2);

		spotIdToLives.put(vertex1, v1Live-1);
		spotIdToLives.put(vertex2, v2Live-1);
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
			int fromIndex = x.getIndexOfFirstMatch(from);
			int toIndex = y.getIndexOfFirstMatch(to);
			
			//System.out.printf("2-boundary\n");
			// === 2-boundary move ===
			Boundary merged = new Boundary();

			// @todo: update numberOfLives hashmap
			int z = createVertex();

			List<Integer> x1_xi = x.grabTo(fromIndex);

			merged.add(x1_xi);
			merged.add(z);
			connect(z, x1_xi.get(x1_xi.size() - 1));

			if (y.size() > 1) {
				List<Integer> yj_yn = y.grabFrom(toIndex);
				merged.add(yj_yn);
			}

			List<Integer> y1_yj = y.grabTo(toIndex);
			merged.add(y1_yj);
			merged.add(z);
			connect(z, y1_yj.get(y1_yj.size() - 1));

			if (x.size() > 1) {
				List<Integer> xi_xm = x.grabFrom(fromIndex);
				merged.add(xi_xm);
				connect(z, xi_xm.get(xi_xm.size() - 1));
			}

			region.remove(x);
			region.remove(y);
			region.add(merged);

		} else {
			//System.out.printf("1-boundary\n");
			// === 1-boundary move ===
			int z = createVertex();
			
			// @fix: should from <= to? The paper may have a mistake.
			// grabRange should be wrong either way
			// swap @hack
			int fromIndex = x.getIndexOfFirstMatch(from);
			int toIndex = y.getIndexOfFirstMatch(to);

			if (toIndex < fromIndex) {
				int tmp = fromIndex;
				fromIndex = toIndex;
				toIndex = tmp;
			}

			// region 1

			// @fix!
			// the paper is actually correct!
			// It seems like the paper has made a mistake for region 1.
			// It should be:
			// 		xj_xn x1_xi z
			// and NOT
			// 		x1_xi z xj_xn
			// @testing

			Boundary boundary1 = new Boundary();

			if (x.size() > 1) {
				List<Integer> xj_xn = x.grabFrom(toIndex);
				boundary1.add(xj_xn);
			}

			List<Integer> x1_xi = x.grabTo(fromIndex);
			boundary1.add(x1_xi);

			Region region1 = new Region();
			region1.add(boundary1);

			
			// region 2
			Boundary boundary2 = new Boundary();
			List<Integer> xi_xj = x.grabRange(fromIndex, toIndex);
			boundary2.add(xi_xj);

			Region region2 = new Region();
			region2.add(boundary2);

			
			List<Boundary> otherBoundaries = new LinkedList<>();
			otherBoundaries.addAll(region.getBoundaries());
			otherBoundaries.remove(x);

			// @test: put the right one the right place
			List<Boundary> B1 = getBoundariesContainingVertices(move.innerIds1, otherBoundaries);
			List<Boundary> B2 = getFirstBoundariesMinusSecondBoundaries(otherBoundaries, B1);
			
			// check which one is the outer boundary
			// the outer boundary has to contain all of either b1 or b2
			// but no more or less vertices.
			if (boundary1.containsSameVertices(move.outerIds2)) {
				region1.add(B1);
				region2.add(B2);
			} else {
				Assert.assertTrue(boundary2.containsSameVertices(move.outerIds2));
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

		for (int id : move.innerIds1) {
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
