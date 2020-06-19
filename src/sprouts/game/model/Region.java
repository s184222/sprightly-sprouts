package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.util.Assert;

/**
 * 
 * A region is the empty space 
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class Region {
	
	public Edge outerBoundary;
	public List<Edge> innerBoundaries;
	public List<Sprout> innerSprouts;
	
	public Region() {
		innerBoundaries = new ArrayList<>();
		innerSprouts = new ArrayList<>();
	}
	
	public boolean isOuterRegion() {
		// the outer region has no boundary which it is confined with in
		return outerBoundary == null;
	}
	
	public boolean isInOuterBoundary(Sprout sprout) {
		Edge boundary = getBoundary(sprout);
		if (boundary == null) return false;
		return boundary.isOuterBoundary();
	}
	
	public boolean isInSameBoundary(Sprout sprout1, Sprout sprout2) {
		if (sprout1.equals(sprout2)) return true;
		Edge b1 = getBoundary(sprout1);
		Edge b2 = getBoundary(sprout2);
		if (b1 == null || b2 == null) return false;
		return b1.equals(b2);
	}

	/**
	 * 
	 * @param sprout
	 * @return the boundary representative of the sprout which is within the region
	 * 		   or null if the sprout has no boundary within the region.
	 */
	public Edge getBoundary(Sprout sprout) {
		for (Edge neighbour : sprout.neighbours) {
			if (!neighbour.region.equals(this)) continue;
			return neighbour.representative;
		}
		return null;
	}
	
	/**
	 * 
	 * Removes the boundary of the sprout from the region,
	 * if the sprout is not in a boundary, then remove the sprout
	 * from the region
	 * 
	 * @param sprout - sprout to free
	 */
	public void freeBoundary(Sprout sprout) {
		if (innerSprouts.contains(sprout)) {
			innerSprouts.remove(sprout);
		} else {
			Edge representative = getBoundary(sprout);
			
			if (representative.isOuterBoundary()) {
				outerBoundary = null;
			} else {
				Assert.that(innerBoundaries.contains(representative));
				innerBoundaries.remove(representative);
			}

			representative.setBoundaryRegion(null);
		}
	}

	/**
	 * 
	 * check that the point is within the region, but not inside any potential subregions,
	 * which boundaries within the region may create.
	 * 
	 * @param point
	 * @return true if inside region
	 */
	public boolean isInsideRegion(Vertex point) {
		if (!isOuterRegion()) {
			if (!outerBoundary.isInsideBoundary(point)) return false;
		}
	
		for (Edge innerEdge : innerBoundaries) {
			if (innerEdge.isInsideBoundary(point)) return false;
		}
		
		return true;
	}

	/**
	 * 
	 * @return the the sum of the lives of each sprout within the region
	 * 
	 */
	public int getLives() {
		int lives = 0;
		
		for (Edge innerBoundary : innerBoundaries) {
			int boundaryLives = innerBoundary.getBoundaryLives();
			lives += boundaryLives;
		}
		
		for (Sprout innerSprout : innerSprouts) {
			lives += innerSprout.getLives();
		}
		
		if (!isOuterRegion()) {
			int outerBoundaryLives = outerBoundary.getBoundaryLives();
			lives += outerBoundaryLives;
		}
		
		return lives;
	}
	
	/**
	 * If a region has at least 2 lives, then it is alive,
	 * because it is possible to draw a line between 2 sprouts,
	 * 
	 * @return true if alive
	 */
	public boolean isAlive() {
		return getLives() >= 2;
	}
	
	public boolean isInnerSprout(Sprout sprout) {
		return innerSprouts.contains(sprout);
	}

	public void verbose() {
		System.out.printf("=== Region ===\n");
		System.out.printf("Outer boundary:\n");
		
		if (!isOuterRegion()) {
			outerBoundary.verboseBoundaryTraverse();
		}
		
		System.out.printf("Inner boundaries:\n");
		
		for (Edge inner : innerBoundaries) {
			inner.verboseBoundaryTraverse();
		}
		
		System.out.printf("Inner sprouts:\n");
		for (Sprout sprout : innerSprouts) {
			System.out.printf("%d ", sprout.id);
		}
		
		System.out.printf("\n\n");
	}
	
}