package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

public class Region {
	
	public Edge outerBoundary;
	public List<Edge> innerBoundaries;
	public List<Sprout> innerSprouts;
	
	public Region() {
		innerBoundaries = new ArrayList<>();
		innerSprouts = new ArrayList<>();
	}
	
	public boolean isOuterRegion() {
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

	public Edge getBoundary(Sprout sprout) {
		for (Edge neighbour : sprout.neighbours) {
			if (!neighbour.region.equals(this)) continue;
			return neighbour.representative;
		}
		return null;
	}
	
	public void freeBoundary(Sprout sprout) {
		if (innerSprouts.contains(sprout)) {
			innerSprouts.remove(sprout);
		} else {
			Edge representative = getBoundary(sprout);
			
			if (representative.isOuterBoundary()) {
				outerBoundary = null;
			} else {
				Util.require(innerBoundaries.contains(representative));
				innerBoundaries.remove(representative);
			}

			representative.setBoundaryRegion(null);
		}
	}
	
	public boolean isInsideRegion(Vertex point) {
		if (!isOuterRegion()) {
			if (!outerBoundary.isInsideBoundary(point)) return false;
		}
	
		for (Edge innerEdge : innerBoundaries) {
			if (innerEdge.isInsideBoundary(point)) return false;
		}
		
		return true;
	}

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