package sprouts.ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sprouts.game.util.MathUtil;

public class Boundary extends LinkedList<Integer> {
	
	public boolean outerBoundary;
	
	public void add(List<Integer> sprouts) {
		addAll(sprouts);
	}
	
	public int getIndex(int targetId, boolean ascending) {
		for (int i = 0; i < size(); i++) {
			int id = get(i);
			if (id == targetId && isAscending(i) == ascending) return i;
		}
		
		throw new IllegalStateException("no match");
	}
	
	public boolean isAscending(int index) {
		if (size() == 1) return true;
		int beforeId = get(MathUtil.wrap(index-1, size()));
		int afterId = get(MathUtil.wrap(index+1, size()));
		return beforeId <= afterId;
	}
	
	public List<Integer> grabTo(int toIndex) {
		return subList(0, toIndex + 1);
	}
	
	public List<Integer> grabFrom(int toIndex) {
		return subList(toIndex, size());
	}
	
	public List<Integer> grabRange(int fromIndex, int toIndex) {
		return subList(fromIndex, toIndex + 1);
	}
	
	public List<Integer> indicesOf(Integer searchId) {
		List<Integer> indices = new ArrayList<>();
		
		for (int i = 0; i < size(); i++) {
			int id = get(i);
			if (searchId == id) indices.add(i);
		}
		
		return indices;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < size() - 1; i++) {
			int id = get(i);
			builder.append(id + ",");
		}
		
		int last = get(size()-1);
		builder.append(last);
		
		builder.append(".");
		
		
		return builder.toString();
	}
}
