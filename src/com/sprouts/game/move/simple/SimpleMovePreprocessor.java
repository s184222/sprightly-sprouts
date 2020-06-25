package com.sprouts.game.move.simple;

import java.util.ArrayList;
import java.util.List;

import com.sprouts.game.model.Edge;
import com.sprouts.game.model.Position;
import com.sprouts.game.model.Region;
import com.sprouts.game.model.Sprout;
import com.sprouts.game.move.IdMove;
import com.sprouts.game.move.Move;
import com.sprouts.game.move.MoveException;
import com.sprouts.game.move.pipe.MovePreprocessor;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class SimpleMovePreprocessor implements MovePreprocessor {
	
	public Move process(IdMove idMove, Position position) throws MoveException {
		Move move = new Move();
		
		int fromId = idMove.fromId;
		int toId = idMove.toId;
		
		Sprout from = position.getSprout(fromId);
		Sprout to = position.getSprout(toId);
		
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
		
		Region region = getRegion(from, to, position.getRegions());
		
		Edge fromEdge = getAnyEdge(from, region);
		Edge toEdge = getAnyEdge(to, region);
		
		move.from = from;
		move.to = to;
		move.fromEdge = fromEdge;
		move.toEdge = toEdge;
		move.region = region;
		
		return move;
	}
	
	/**
	 * 
	 * @param origin - a sprout which is origin
	 * @param region
	 * @return the first edge, which has the sprout {@code origin} as origin. 
	 * 		   If the sprout is not in a boundary then no edge has that sprout as origin,
	 * 		   so return null.
	 */
	private Edge getAnyEdge(Sprout origin, Region region) {
		List<Edge> candidates = new ArrayList<>();
		
		for (Edge neighbour : origin.neighbours) {
			if (region.equals(neighbour.region)) {
				candidates.add(neighbour);
			}
		}
		
		if (candidates.size() == 0) return null;
		return candidates.get(0);
	}
	
	/**
	 * @param sprout
	 * @param allRegions - region candidates
	 * @return all the regions of the sprout
	 */
	private List<Region> getRegions(Sprout sprout, List<Region> allRegions) {
		List<Region> regions = new ArrayList<>();
		
		if (sprout.neighbours.size() == 0) {
			for (Region region : allRegions) {
				if (region.isInnerSprout(sprout)) {
					regions.add(region);
					break;	// a sprout which is not in a boundary is only in 1 region.
				}
			}
			
		} else {
			for (Edge neighbour : sprout.neighbours) {
				if (!regions.contains(neighbour.region)) regions.add(neighbour.region);
			}
		}
		
		return regions;
	}
	
	/**
	 * 
	 * Returns the first region which both sprouts have in common.
	 * 
	 * @param from
	 * @param to
	 * @param regions - the candidate regions
	 * @return the first common region
	 * @throws MoveException if no common region exists
	 */
	private Region getRegion(Sprout from, Sprout to, List<Region> regions) throws MoveException {
		List<Region> fromRegions = getRegions(from, regions);
		List<Region> toRegions = getRegions(to, regions);
		
		List<Region> candidates = new ArrayList<>();
		candidates.addAll(fromRegions);
		candidates.retainAll(toRegions);
		
		if (candidates.size() != 0) return candidates.get(0);

		throw new MoveException("A common region does not exist.\n");
	}
}
