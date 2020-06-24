package sprouts.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sprouts.game.UidGenerator;
import sprouts.game.move.IdMove;
import sprouts.game.util.Assert;

/**
 * 
 * @author Rasmus Møller Larsen
 *
 */

public class Position {

	private UidGenerator sproutIdGenerator;

	private int initialNumberOfLives;

	private List<Region> regions;

	private Map<Integer, Integer> sproutIdToLives;
	
	public Position(UidGenerator sproutIdGenerator) {
		this(0, sproutIdGenerator);
	}

	public Position(int initialNumberOfSprouts, UidGenerator sproutIdGenerator) {
		this.sproutIdGenerator = sproutIdGenerator;
		
		sproutIdGenerator.reset();
		
		regions = new ArrayList<>();
		sproutIdToLives = new HashMap<>();
		
		initialNumberOfLives = 3;

		if (initialNumberOfSprouts > 0) {
			Region initialRegion = new Region();
			regions.add(initialRegion);
	
			for (int i = 0; i < initialNumberOfSprouts; i++) {
				int sprout = createSprout();
	
				Boundary boundary = new Boundary();
				boundary.add(sprout);
	
				initialRegion.add(boundary);
			}
		}
	}

	public void add(Region region) {
		regions.add(region);
		
		for (Boundary boundary : region) {
			for (int sprout : boundary) {
				Integer live = sproutIdToLives.remove(sprout);
				int updatedLive = (live == null) ? initialNumberOfLives : live - 1;
				sproutIdToLives.put(sprout, updatedLive);
			}
		}
	}

	private int createSprout() {
		int id = sproutIdGenerator.generate();
		sproutIdToLives.put(id, initialNumberOfLives);
		return id;
	}
	
	private void connect(int sprout1, int sprout2) {
		Integer s1Lives = sproutIdToLives.remove(sprout1);
		Integer s2Lives = sproutIdToLives.remove(sprout2);

		sproutIdToLives.put(sprout1, s1Lives-1);
		sproutIdToLives.put(sprout2, s2Lives-1);
	}

	// uses the definition from the paper to execute 1-boundary and 2-boundaries move.
	public void makeMove(IdMove move) {
		int from = move.fromId;
		int to = move.toId;
		
		boolean fromAscending = move.fromAscending;
		boolean toAscending = move.toAscending;
		
		boolean inverted = move.inverted;
		
		List<Integer> inner = move.inner;
		
		Region region = getRegion(from, fromAscending, to, toAscending, inner);

		Boundary x = region.getBoundary(from);
		Boundary y = region.getBoundary(to);
		
		int i = x.getIndex(from, fromAscending);
		int j = y.getIndex(to, toAscending);
		
		int z = createSprout();
		
		connect(from, z);
		connect(to, z);
		
		boolean isTwoBoundaryMove = !x.equals(y);
		if (isTwoBoundaryMove) {

			List<Integer> x1_xi = new ArrayList<>();
			List<Integer> xi_xm = new ArrayList<>();
			
			List<Integer> y1_yj = new ArrayList<>();
			List<Integer> yj_yn = new ArrayList<>();
			
			x1_xi.addAll(x.grabTo(i));
			y1_yj.addAll(y.grabTo(j));

			if (x.size() >= 2) xi_xm.addAll(x.grabFrom(i));
			if (y.size() >= 2) yj_yn.addAll(y.grabFrom(j));
			
			Boundary merged = new Boundary();
			merged.add(x1_xi);
			merged.add(z);
			merged.add(yj_yn);
			merged.add(y1_yj);
			merged.add(z);
			merged.add(xi_xm);

			region.remove(x);
			region.remove(y);
			region.add(merged);
			
			merged.outerBoundary = (x.outerBoundary || y.outerBoundary);

		} else {
			
			// swap i and j
			boolean swapped = false;
			if (j < i) {
				int tmp = j;
				j = i;
				i = tmp;
				swapped = true;
			}

			List<Integer> x1_xi = new ArrayList<>();
			List<Integer> xj_xn = new ArrayList<>();
			List<Integer> xi_xj = new ArrayList<>();
			
			x1_xi.addAll(x.grabTo(i));
			if (x.size() > 1) xj_xn.addAll(x.grabFrom(j));
			xi_xj.addAll(x.grabRange(i, j));
			
			Boundary boundary1 = new Boundary();
			boundary1.add(x1_xi);
			boundary1.add(z);
			boundary1.add(xj_xn);

			Boundary boundary2 = new Boundary();
			boundary2.add(xi_xj);
			boundary2.add(z);
			
			Region region1 = new Region();
			region1.add(boundary1);

			Region region2 = new Region();
			region2.add(boundary2);
			
			List<Boundary> otherBoundaries = new LinkedList<>();
			otherBoundaries.addAll(region);
			otherBoundaries.remove(x);
			
			List<Boundary> B = new ArrayList<>();
			
			if (!inverted) {
				for (int sproutId : inner) {
					Boundary boundary = region.getBoundary(sproutId);
					B.add(boundary);
				}
			}
			
			List<Boundary> B2 = new ArrayList<>();
			B2.addAll(B);
			
			List<Boundary> B1 = new ArrayList<>();
			B1.addAll(region);
			B1.remove(x);
			B1.removeAll(B);
			
			if (swapped) {
				region1.addAll(B2);
				region2.addAll(B1);
				
				boundary1.outerBoundary = true;
				if (x.outerBoundary) boundary2.outerBoundary = true;
				
			} else {
				region1.addAll(B1);
				region2.addAll(B2);
				
				boundary2.outerBoundary = true;
				if (x.outerBoundary) boundary1.outerBoundary = true;
			}
			
			regions.add(region1);
			regions.add(region2);

			regions.remove(region);
		}
	}
	
	private Region getRegion(int from, boolean fromAscending, int to, boolean toAscending, List<Integer> inner) {

		List<Region> fromRegions = getRegions(from, fromAscending);
		List<Region> toRegions = getRegions(to, toAscending);
		
		List<Region> regions = new ArrayList<>();
		regions.addAll(fromRegions);
		regions.retainAll(toRegions);
		
		for (int sprout : inner) {
			List<Region> innerRegion = getRegions(sprout);
			regions.retainAll(innerRegion);
		}
		
		if (regions.size() == 1) {
			return regions.get(0);
		} else if (regions.size() > 1) {
			Assert.that(inner.size() == 0);
			
			for (Region region : regions) {
				
				int boundariesCount = region.size();
				if (boundariesCount == 1 || boundariesCount == 2) {
					return region;
				}
			}
		}
		
		String error = String.format("found %d regions.", regions.size());
		throw new IllegalStateException(error);
	}

	private List<Region> getRegions(int sprout) {
		List<Region> found = new ArrayList<>();
		
		for (Region region : regions) {
			for (Boundary boundary : region) {
				if (boundary.contains(sprout)) {
					found.add(region);
					break;
				}
			}
		}
		
		return found;
	}
	
	private List<Region> getRegions(int sprout, boolean ascending) {
		List<Region> found = new ArrayList<>();

		outer: for (Region region : regions) {
			for (Boundary boundary : region) {
				List<Integer> indices = boundary.indicesOf(sprout);
				
				for (int index : indices) {
					if (boundary.isAscending(index) == ascending) {
						found.add(region);
						continue outer;
					}
				}
			}
		}
		
		return found;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Region region : regions) {
			builder.append(region.toString());
		}
		
		builder.append("!");
		
		return builder.toString();
	}
	
	public List<Region> getRegions() {
		return regions;
	}
	
	public Map<Integer, Integer> getSproutIdToLives() {
		return sproutIdToLives;
	}
}
