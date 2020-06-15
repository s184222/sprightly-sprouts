package sprouts.mvc.game.model.move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.BiFunction;

import sprouts.mvc.game.model.MathUtil;

public class AStarPathFinder implements PathFinder {

	private static class Node<T> {

		public T parent;
		public float shortestDistance;
		public float traveled;
		
		private Node() {}

		public static <T> Node<T> root(float shortestDistance) {
			Node<T> root = new Node<>();
			root.shortestDistance = shortestDistance;
			root.traveled = 0;
			root.parent = null;
			return root;
		}

		public static <T> Node<T> child(float traveled, float shortestDistance, T parent) {
			Node<T> child = new Node<>();
			child.shortestDistance = shortestDistance;
			child.traveled = traveled;
			child.parent = parent;
			return child;
		}
	}
	
	public <T> List<T> find(T source, T goal, Map<T, List<T>> graph, BiFunction<T, T, Float> stepCostFunction, BiFunction<T, T, Float> minimumCostFunction) {
		Map<T, Node<T>> itToNode = new HashMap<>();
		List<T> explored = new ArrayList<>();

		PriorityQueue<T> frontier = new PriorityQueue<>((t1, t2) -> {
			Node<T> n1 = itToNode.get(t1);
			Node<T> n2 = itToNode.get(t2);

			float c1 = n1.traveled + n1.shortestDistance;
			float c2 = n2.traveled + n2.shortestDistance;
			int sign = MathUtil.sign(c1 - c2);

			return sign;
		});
		
		Node<T> sourceNode = Node.root(minimumCostFunction.apply(source, goal));
		itToNode.put(source, sourceNode);
		frontier.add(source);

		while (frontier.size() > 0) {
			T current = frontier.remove();
			if (current.equals(goal)) break;

			explored.add(current);

			Node<T> currentNode = itToNode.get(current);
			List<T> children = graph.get(current);
			for (T child : children) {
				if (explored.contains(child)) continue;

				float stepCost = stepCostFunction.apply(current, child);
				float newTraveled = stepCost + currentNode.traveled;

				if (frontier.contains(child)) {
					Node<T> childNode = itToNode.get(child);
					if (newTraveled < childNode.traveled) {
						childNode.parent = current;
						childNode.traveled = newTraveled;

						// Priority Queue doesn't reorder unless the object is re-inserted.
						frontier.remove(child);	
						frontier.add(child);
					}
				} else {
					float shortestDistance = minimumCostFunction.apply(child, goal);
					Node<T> childNode = Node.child(newTraveled, shortestDistance, current);
					itToNode.put(child, childNode);
					frontier.add(child);
				}
			}
		}

		// backtrack from goal to source.
		List<T> path = new ArrayList<>();
		path.add(goal);
		Node<T> current = itToNode.get(goal);

		while (current.parent != null) {
			path.add(current.parent);
			current = itToNode.get(current.parent);
		}

		Collections.reverse(path);

		return path;

	}
}
