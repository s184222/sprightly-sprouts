package sprouts.mvc.game.model.move;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface PathFinder {
	public <T> List<T> find(T source, T goal, Map<T, List<T>> graph, BiFunction<T, T, Float> stepCostFunction, BiFunction<T, T, Float> minimumCostFunction);
}
