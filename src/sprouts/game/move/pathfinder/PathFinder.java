package sprouts.game.move.pathfinder;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public interface PathFinder {
	
	/**
	 *  @param source
	 *  @param goal
	 *  @param graph - a mapping from an object to its neighbors.
	 *  @param stepCostFunction - cost to arrive at neighbor.
	 *  @param minimumCostFunction - the minimum cost to {@code goal}.
	 *  
	 *  @return the path from source to goal, if no path exist return an empty list.
	 */
	public <T> List<T> find(T source, T goal, Map<T, List<T>> graph, BiFunction<T, T, Double> stepCostFunction, BiFunction<T, T, Double> minimumCostFunction);
}
