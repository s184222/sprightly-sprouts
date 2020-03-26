package sprouts.representation.concrete;

import java.util.LinkedList;
import java.util.List;

public class Boundary {

	private List<Integer> vertices;

	public Boundary() {
		vertices = new LinkedList<>();
	}

	public Boundary(List<Integer> other) {
		vertices = new LinkedList<>();
		vertices.addAll(other);
	}

	public void add(int vertex) {
		vertices.add(vertex);
	}

	public void add(List<Integer> vs) {
		vertices.addAll(vs);
	}

	public List<Integer> getVertices() {
		return vertices;
	}

	public int size() {
		return vertices.size();
	}
	
	private int getIndexOfFirstMatch(int id) {
		for (int i = 0; i < vertices.size(); i++) {
			int vertex = vertices.get(i);
			if (vertex == id) return i;
		}
		
		throw new IllegalStateException("no match");
	}

	public List<Integer> grabTo(int toId) {
		return vertices.subList(0, getIndexOfFirstMatch(toId) + 1);
	}
	
	public List<Integer> grabFrom(int toId) {
		return vertices.subList(getIndexOfFirstMatch(toId), vertices.size());
	}
	
	public List<Integer> grabRange(int fromId, int toId) {
		int index1 = getIndexOfFirstMatch(fromId);
		int index2 = getIndexOfFirstMatch(toId);
		
		List<Integer> subset = (index1 < index2) ? vertices.subList(index1, index2 + 1) : vertices.subList(index2, index1 + 1);
		
		return subset;
	}
	
	public boolean contains(int vertexId) {
		return vertices.contains(vertexId);
	}
	
	public boolean containsSameVertices(List<Integer> vertexIds) {
		for (int vertex : vertices) {
			if (!vertexIds.contains(vertex)) return false;
		}
		for (int vertex : vertexIds) {
			if (!vertices.contains(vertex)) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String string = "";
		for (int vertex : vertices) {
			string += VertexUtil.getName(vertex);
		}
		string += ".";
		return string;
	}

}
