package sprouts.game.model;

public class DebugIdGenerators {
	
	private static UidGenerator edgesIds = new UidGenerator();
	
	public static int getEdgeId() {
		return edgesIds.generate();
	}
	
	public static void reset() {
		edgesIds.reset();
	}
}
