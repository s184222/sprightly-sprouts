package com.sprouts.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sprouts.game.move.IdMove;

/**
 * 
 * Generates all the possible moves given the AI position
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class AllMoveGenerator {
	
	public List<IdMove> getAllMoves(Position position) {
		List<Region> regions = position.getRegions();
		Map<Integer, Integer> sproutIdToLives = position.getSproutIdToLives();
		
		List<IdMove> moves = new LinkedList<>();
		
		for (Region region : regions) {

			// 2 boundary moves
			for (int b1 = 0; b1 < region.size(); b1++) {
				Boundary boundary1 = region.get(b1);
				
				for (int b2 = b1+1; b2 < region.size(); b2++) {
					Boundary boundary2 = region.get(b2);
				
					for (int s1 = 0; s1 < boundary1.size(); s1++) {
						int id1 = boundary1.get(s1);
						int sprout1Lives = sproutIdToLives.get(id1);
						if (sprout1Lives == 0) continue;
						
						for (int s2 = s1; s2 < boundary2.size(); s2++) {
							int id2 = boundary2.get(s2);
							int sprout2Lives = sproutIdToLives.get(id2);
							if (sprout2Lives == 0) continue;
							
							IdMove move = new IdMove();
							move.fromId = id1;
							move.fromAscending = boundary1.isAscending(s1);
							move.toId = id2;
							move.toAscending = boundary2.isAscending(s2);
							moves.add(move);
						}
					}
				}
			}
			
			// 1 boundary moves
			for (int b = 0; b < region.size(); b++) {
				Boundary boundary = region.get(b);
				
				List<Boundary> innerBoundaries = new ArrayList<>();
				innerBoundaries.addAll(region);
				innerBoundaries.remove(boundary);

				Boundary outerBoundary = region.getOuterBoundary();
				if (outerBoundary != null) innerBoundaries.remove(outerBoundary);
				
				List<Integer> inners = new ArrayList<>();
				for (Boundary innerBoundary : innerBoundaries) {
					int sproutId = innerBoundary.get(0);
					inners.add(sproutId);
				}
				
				Set<List<Integer>> innersPowerset = powerSet(inners);
				
				for (int s = 0; s < boundary.size(); s++) {
					int id1 = boundary.get(s);
					int sprout1Lives = sproutIdToLives.get(id1);
					if (sprout1Lives == 0) continue;
					
					for (int k = 0; k < boundary.size(); k++) {
						int id2 = boundary.get(k);
						int sprout2Lives = sproutIdToLives.get(id2);
						if (sprout2Lives == 0) continue;
						if (id1 == id2 && sprout2Lives <= 1) continue;
						
						for (List<Integer> inner : innersPowerset) {
							IdMove move = new IdMove();
							move.fromId = id1;
							move.fromAscending = boundary.isAscending(s);
							move.toId = id2;
							move.toAscending = boundary.isAscending(k);
							
							if (isAmbiguous(id1, sprout1Lives, id2, sprout2Lives, boundary)) {
								move.inner.addAll(inners);
								move.inverted = true;
							} else {
								move.inner.addAll(inner);
								move.inverted = false;
							}
							
							moves.add(move);
						}
					}
				}
			}
		}

		return moves;
	}
	
	/*
	 *  a boundary containing 2 sprouts is ambiguous, because it is not possible
	 *  from ascending/descending to determine the side.
	 */
	private boolean isAmbiguous(int id1, int id1Lives, int id2, int id2Lives, Boundary boundary) {
		if (boundary.size() != 2) return false;
		if (id1Lives != 1) return false;
		if (id2Lives != 1) return false;
		return true;
	}
	
	private <T> Set<List<T>> powerSet(List<T> originalSet) {
		Set<List<T>> sets = new HashSet<List<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new LinkedList<>());
	        return sets;
	    }
	    
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    List<T> rest = new ArrayList<T>(list.subList(1, list.size()));
	    
	    for (List<T> set : powerSet(rest)) {
	        List<T> newSet = new LinkedList<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }
	    
	    return sets;
	}
}
