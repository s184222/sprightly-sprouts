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

	public void addVertex(int vertex) {
		vertices.add(vertex);
	}

	public void addVertices(List<Integer> vs) {
		for (int vertex : vs) {
			addVertex(vertex);
		}
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

	public Boundary grabTo(int toId) {
		return new Boundary(vertices.subList(0, getIndexOfFirstMatch(toId) + 1));
	}
	
	public Boundary grabFrom(int toId) {
		return new Boundary(vertices.subList(getIndexOfFirstMatch(toId), vertices.size()));
	}
	
	public Boundary grabRange(int fromId, int toId) {
		int index1 = getIndexOfFirstMatch(fromId);
		int index2 = getIndexOfFirstMatch(toId);
		
		List<Integer> subset = (index1 < index2) ? vertices.subList(index1, index2 + 1) : vertices.subList(index2, index1 + 1);
		
		return new Boundary(subset);
	}

	public boolean containsVertex(int otherId) {
		for (int id : vertices) {
			if (id == otherId) return true;
		}
		return false;
	}
	
	public boolean containsAllVertices(List<Integer> vertexIds) {
		for (Integer id : vertexIds) {
			if (!containsVertex(id)) return false;
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
