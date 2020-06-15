package sprouts.game.model.move;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sprouts.game.model.Edge;
import sprouts.game.model.Position;
import sprouts.game.model.Region;
import sprouts.game.model.Sprout;
import sprouts.game.model.Util;

public class MovePreprocessor {
	
	public Move process(RawMove rawMove, Position position) throws MoveException {
		Move move = new Move();
		
		int fromId = rawMove.fromId;
		int toId = rawMove.toId;
		
		boolean toAscending = rawMove.toAscending;
		boolean fromAscending = rawMove.fromAscending;
	
		Sprout from = position.getSprout(fromId);
		Sprout to = position.getSprout(toId);
		
		List<Integer> innerIds = rawMove.inner;
		boolean inverted = rawMove.inverted;
		
		if (from == null) throw new MoveException("Sprout %d does not exist.\n", fromId);
		if (to == null) throw new MoveException("Sprout %d does not exist.\n", toId);
		
		int fromCount = from.getNeighbourCount();
		int toCount = to.getNeighbourCount();
		
		if (from.equals(to)) {
			if (fromCount >= 2) throw new MoveException("Sprout %d has too many neighbours: %d, but expected < 2.\n", fromId, fromCount);
		} else {
			if (fromCount == 3) throw new MoveException("Sprout %d sprout has too many neighbours: %d, but expected < 3.\n", fromId, fromCount);
			if (toCount == 3) throw new MoveException("Sprout %d sprout has too many neighbours: %d, but expected < 3.\n", toId, toCount);
		}
		
		List<Sprout> inner = new ArrayList<>();
		for (int id : innerIds) {
			Sprout sprout = position.getSprout(id);
			if (sprout == null) throw new MoveException("Sprout %d does not exist.\n", id);
			inner.add(sprout);
		}
		
		for (Sprout sprout : inner) {
			innerIds.remove(Integer.valueOf(sprout.id));
			
			Set<Integer> neighbourIds = new TreeSet<>();
			for (Edge edge : sprout.neighbours) {
				List<Integer> sproutIds = edge.getBoundarySproutIds();
				neighbourIds.addAll(sproutIds);
			}
			
			for (int boundaryId : neighbourIds) {
				if (innerIds.contains(boundaryId)) {
					throw new MoveException("The boundary containing sprout %d is specified more than once.\n", boundaryId);
				}
			}
			
			innerIds.add(sprout.id);
		}
			
		Region region = getRegion(from, fromAscending, to, toAscending, inner, position.getRegions());
		
		Edge fromEdge = getEdge(from, fromAscending, region);
		Edge toEdge = getEdge(to, toAscending, region);
		
		if (inverted) inner.clear();
		
		move.from = from;
		move.to = to;
		move.fromEdge = fromEdge;
		move.toEdge = toEdge;
		move.inners = inner;
		move.region = region;
		
		return move;
	}
	
	private Edge getEdge(Sprout origin, boolean ascending, Region region) {
		List<Edge> neighbours = origin.neighbours;
		
		if (neighbours.size() == 0) {
			// sprout is not in a boundary
			return null;
		} else if (neighbours.size() == 1) {
			// there is only 1 edge, this can happen, if an edge in a boundary is not "between" 2 other edges.
			Edge c1 = neighbours.get(0);
			Util.require(c1.isAscending() == ascending);
			return c1;
		} else {
			Util.require(neighbours.size() == 2);

			Edge c1 = neighbours.get(0);
			Edge c2 = neighbours.get(1);
			
			if (c1.isAscending() && c2.isAscending()) {
				// special case, where it is a boundary with only 2 sprouts, e.g. 2-4-2...
				// in this case both candidates are ascending.
				
				for (Edge boundary : region.innerBoundaries) {
					if (boundary.isSameBoundary(c1)) return c1;
					else if (boundary.isSameBoundary(c2)) return c2;
				}
				
				if (!region.isOuterRegion()) {
					if (c1.isOuterBoundary()) return c1;
					else if (c2.isOuterBoundary()) return c2;
				}
			
				throw new IllegalStateException("broken! an edge should have been found!");
				
			} else {
				Util.require(c1.isAscending() != c2.isAscending());
				
				if (ascending) {
					return c1.isAscending() ? c1 : c2;
				} else {
					return c1.isAscending() ? c2 : c1;
				}
			}
		}
	}
	
	private List<Region> getRegions(Sprout sprout, List<Region> regions2) {
		List<Region> regions = new ArrayList<>();
		
		if (sprout.neighbours.size() == 0) {
			for (Region region : regions2) {
				if (region.isInnerSprout(sprout)) {
					regions.add(region);
				}
			}
			
		} else {
			for (Edge neighbour : sprout.neighbours) {
				if (!regions.contains(neighbour.region)) regions.add(neighbour.region);
			}
		}
		
		return regions;
	}
	
	private List<Region> getRegions(Sprout sprout, boolean ascending, List<Region> regions2) {
		List<Region> regions = new ArrayList<>();

		if (sprout.neighbours.size() == 0) {
			for (Region region : regions2) {
				if (region.isInnerSprout(sprout)) {
					regions.add(region);
				}
			}
			
		} else {
			for (Edge neighbour : sprout.neighbours) {
				if (neighbour.isAscending() == ascending) {
					if (!regions.contains(neighbour.region)) {
						regions.add(neighbour.region);
					}
				}
			}
		}
		
		return regions;
	}
	
	private Region getRegion(Sprout from, boolean fromAscending, Sprout to, boolean toAscending, List<Sprout> inners, List<Region> regions) throws MoveException {
		List<Region> fromRegions = getRegions(from, fromAscending, regions);
		List<Region> toRegions = getRegions(to, toAscending, regions);
		
		List<Region> candidates = new ArrayList<>();
		candidates.addAll(fromRegions);
		candidates.retainAll(toRegions);
		
		for (Sprout sprout : inners) {
			List<Region> innerCandidates = getRegions(sprout, regions);
			candidates.retainAll(innerCandidates);
		}
		
		if (candidates.size() == 1) {	
			return candidates.get(0);
		} else if (candidates.size() > 1) {
			// we end here, if it is a 1-boundary move, where the (outer) boundary only contains "from" and "to".
			Util.require(inners.size() == 0);

			for (Region candidate : candidates) {
				
				List<Sprout> innerSprouts = candidate.innerSprouts;
				List<Edge> innerBoundaries = candidate.innerBoundaries;
				
				if (innerSprouts.size() != 0) continue;
				
				if (innerBoundaries.size() == 0) {
					return candidate;
				} else if (innerBoundaries.size() == 1) {
					Edge itself = innerBoundaries.get(0);
					
					List<Sprout> boundarySprouts = itself.getBoundarySprouts();
					if (boundarySprouts.contains(from) && boundarySprouts.contains(to)) return candidate;
				}
			}
		}
		
		throw new MoveException("A common region does not exist.\n");
	}
}
