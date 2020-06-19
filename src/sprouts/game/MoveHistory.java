package sprouts.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprouts.game.model.Vertex;

/**
 * 
 * Auto-generating Unit test code of the moves which have been drawn or request.
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */
public class MoveHistory {
	
	private Map<Integer, String> moveHistory;
	private Map<Integer, List<Vertex>> lineHistory;
	
	private int at;
	
	public MoveHistory() {
		moveHistory = new HashMap<>();
		lineHistory = new HashMap<>();
		
		at = 0;
	}
	
	public void add(List<Vertex> lines) {
		lineHistory.put(at, lines);
		at += 1;
	}
	
	public void add(String move) {
		moveHistory.put(at, move);
		at += 1;
	}

	public void printTestCode() {
		System.out.printf("=== history ===\n");
		
		System.out.printf("GraphicalFacade facade = new GraphicalFacade();\n");
		System.out.printf("\n");
		
		for (int i = 0; i < at; i++) {
			String move = moveHistory.get(i);
			if (move != null) {
				System.out.printf("String move%d = facade.executeMove(\"%s\");\n", i, move);
			} else {
				List<Vertex> line = lineHistory.get(i);
				
				System.out.printf("Line line%d = new Line();\n", i);
				
				for (Vertex v : line) {
					System.out.printf("line%d.add(new Vertex(%sf,%sf));\n", i, v.x, v.y);
				}
				
				System.out.printf("String move%d = facade.executeLine(line%d);\n", i, i);
			}
			
			System.out.printf("assertEquals(\"TODO\", move%d);\n", i);
			
			System.out.printf("\n");
		}
		
		System.out.printf("============\n");
	}
}
