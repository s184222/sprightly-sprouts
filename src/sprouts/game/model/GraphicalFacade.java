package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.model.move.MoveException;
import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;
import sprouts.game.model.move.RawMovePathGenerator;
import sprouts.game.model.move.generators.MovePathResult;
import sprouts.game.model.move.notation.CompleteMoveNotationParser;
import sprouts.game.model.move.notation.MoveNotationParser;

public class GraphicalFacade {
	
	private RawMovePathGenerator pathGenerator;
	private Position position;
	private MoveHistory history;
	
	private MoveNotationParser notationParser;
	
	public Line currentLine;
	boolean drawingLine;
	public Sprout from;
	public float minimumLineSegmentDistance;
	public int sproutRadius;
	
	public GraphicalFacade() {
		DebugIdGenerators.reset();
		
		currentLine = new Line();
		
		pathGenerator = new RawMovePathGenerator();
		notationParser = new CompleteMoveNotationParser();
		
		minimumLineSegmentDistance = 25;
		drawingLine = false;
		sproutRadius = 12;
		
		history = new MoveHistory();

		// @hack to make tests work
		createFreshPosition(8);
	}
	
	public void createFreshPosition(int numberOfSprouts) {
		PositionBuilder builder = new PositionBuilder();
		position = builder.createSproutsCircle(numberOfSprouts, 320, 240, 150).build();
		//position = builder.createSproutsCircle(numberOfSprouts, 320, 240, 150).build();
		
		/*
		position = builder
			.createFreshSprout(140, 360)
			.createFreshSprout(340, 360)
			.createFreshSprout(140, 160)
			.createFreshSprout(440, 180)
			.createFreshSprout(420, 140)
			.build();
		*/
		
		//executeMove("5<,5<,[0,2,3]");
		//executeMove("2<,2<,[3]");
		//executeMove("1<,1<,[5,4,6]");

		//executeMove("2<,1<,[3,5,0,7]");
		
		
		//executeMove("0<,0<,[1,2]");
		//executeMove("0<,8<,[1,2]!");
		
		//executeMove("7<,7<,[0,1,2,3]");
		//executeMove("8<,7<,[0,1,2,3]!");
		
		
		/*
		executeMove("5<,5<,[0,2,3]");
		executeMove("2<,2<,[3]");
		executeMove("1<,1<,[5,4,6]");
		executeMove("1<,10<,[7]!");
		executeMove("5<,0<,[]");
		executeMove("2<,12>,[]");
		executeMove("9<,13>,[]");
		executeMove("6<,6<,[]");
		executeMove("15<,6<,[5,4]!");
		executeMove("0<,8<,[]");
		executeMove("0<,17<,[]");
		executeMove("7<,7<,[1]");
		executeMove("7<,11<,[]");
		*/
		//20<,19<,[]
		
		// 4<,4<,[12,6]
		
	}

	public MovePathResult generateMove(String rawMove) {
		MovePathResult result = null;

		try {
			RawMove move = notationParser.parse(rawMove);
		
			try {
				result = pathGenerator.generate(move, position);
				
				history.add(rawMove);

				if (intersectsNewLine(result.line)) {
					System.out.printf("problem!!!\n");
					history.printTestCode();
					Util.require(!intersectsNewLine(result.line));
				}
				
				
			} catch (MoveException e) {
				System.out.printf("Generation exception: %s\n", e.getMessage());
			}
			
		} catch (MoveNotationException e) {
			System.out.printf("Notation exception: illegal move notation %s\n", rawMove);
		}
				
		return result;
	}
	
	public String executeLine(Line line) {
		//Util.require(!intersectsNewLine(line));
		RawMove move = position.update(line);
		return move.toString();
	}
	
	public String executeMove(String rawMove) {
		MovePathResult result = generateMove(rawMove);
		String actualMove = "";
		if (result.line != null) {
			actualMove = executeLine(result.line);
		}
		return actualMove;
	}
	
	public MovePathResult executeMoveWithResult(String rawMove) {
		MovePathResult result = generateMove(rawMove);
		if(result != null) {
			String actualMove = executeLine(result.line);
		}
			
		return result;
	}

	public Sprout getSproutClicked(float mx, float my) {
		float minimumDistance = Float.MAX_VALUE;
		Sprout closest = null;

		for (Sprout sprout : position.getSprouts()) {
			if (sprout.getNeighbourCount() > 2) continue;
			
			float distance = MathUtil.distance(mx, my, sprout.position.x, sprout.position.y);
			
			if (distance <= sproutRadius && distance < minimumDistance) {
				distance = minimumDistance;
				closest = sprout;
			}
		}

		return closest;
	}

	// @todo: move into handler.
	public void touchDown(float worldX, float worldY) {
		Sprout sprout = getSproutClicked(worldX, worldY);
		
		if (sprout != null) {
			drawingLine = true;
			currentLine.add(sprout.position);
			from = sprout;
		}
	}

	public void touchDragged(float worldX, float worldY) {
		if (!drawingLine) return;
	
		Vertex at = currentLine.getLast();
		
		boolean shouldAddLinePoint = MathUtil.distance(worldX, worldY, at.x, at.y) >= minimumLineSegmentDistance;
		if (shouldAddLinePoint) {
			
			if (currentLine.size() == 1) {
				
				List<Line> nonNeighbours = new ArrayList<>();
				nonNeighbours.addAll(position.getLines());
				
				for (Edge edge : from.neighbours) {
					nonNeighbours.remove(edge.line);
					nonNeighbours.remove(edge.twin.line);
				}
				
				for (Line line : nonNeighbours) {
					if (line.intersects(at.x, at.y, worldX, worldY)) return;
				}
				
				for (Edge edge : from.neighbours) {
					Line lineFirst = new Line();
					lineFirst.addAll(edge.line);
	
					lineFirst.removeFirst();
					
					if (lineFirst.intersects(at.x, at.y, worldX, worldY)) return;
				}
				
			} else {
				
				// @TODO: do max rotation her?
				/*
				// the line cannot rotate more than 175 degrees
				Vertex v1 = currentLine.getLast();
				Vertex v2 = currentLine.getSemiLast();
				
				Vector2 angler = new Vector2();
				angler.set(v2.x, v2.y).sub(v1.x, v1.y);

				Vector2 angler2 = new Vector2();
				angler2.set(at.x, at.y).sub(v1.x, v1.y);

				float angle = angler2.angle(angler);
				if (angle < 0) angle += 180;
				
				float minAngle = 90;
				if (angle <= minAngle) return;
				*/
	
				for (Line line : position.getLines()) {
					if (line.intersects(at.x, at.y, worldX, worldY)) return;
				}
				
				Line line = new Line();
				line.addAll(currentLine);
				line.removeLast();

				if (line.intersects(at.x, at.y, worldX, worldY)) return;
	
			}
			
			Vertex vertex = new Vertex(worldX, worldY);
			currentLine.add(vertex);
		}
	}

	public void touchUp(float worldX, float worldY) {
		if (!drawingLine) return;
		drawingLine = false;
		
		Vertex at = currentLine.getLast();
		
		Sprout to = getSproutClicked(worldX, worldY);
		
		Line lineToAdd = new Line();
		lineToAdd.addAll(currentLine);
		currentLine.clear();
		
		if (to != null) {
			if (to.getNeighbourCount() > 2) return;
			if (to.equals(from) && to.getNeighbourCount() > 1) return;
			if (to.equals(from) && lineToAdd.size() < 3) return;
			
			// @speed @RegionLine
			// we could just use the lines in the region
			// and not check all.
			
			List<Line> nonNeighbours = new ArrayList<>();
			nonNeighbours.addAll(position.getLines());
			for (Edge edge : to.neighbours) {
				nonNeighbours.remove(edge.line);
				nonNeighbours.remove(edge.twin.line);
			}
			
			for (Line line : nonNeighbours) {
				if (line.intersects(at.x, at.y, to.position.x, to.position.y)) return;
			}
			
			for (Edge edge : to.neighbours) {
				Line line = new Line();
				line.addAll(edge.line);
				line.removeFirst();
				
				if (line.intersects(at.x, at.y, to.position.x, to.position.y)) return;
			}
			
			Line line = new Line();
			line.addAll(lineToAdd);
			line.removeLast();
			
			if (line.intersects(at.x, at.y, worldX, worldY)) return;
			
			//if (intersects) return;	// @TODO: do max rotation her?
			
			if (!at.equals(to.position)) lineToAdd.add(to.position);
			
			history.add(lineToAdd.copy());
			
			executeLine(lineToAdd);
			
			
		}
	}
	
	public boolean intersectsNewLine(Line line) {
		Vertex fromVertex = line.getFirst();
		Sprout from = position.getSprout(fromVertex);
		
		Vertex toVertex = line.getLast();
		Sprout to = position.getSprout(toVertex);
		
		ArrayList<Line> nonNeighbours = new ArrayList<>();
		nonNeighbours.addAll(position.getLines());
		for (Edge edge : to.neighbours) {
			nonNeighbours.remove(edge.line);
			nonNeighbours.remove(edge.twin.line);
		}
		
		for (Edge edge : from.neighbours) {
			nonNeighbours.remove(edge.line);
			nonNeighbours.remove(edge.twin.line);
		}
		
		for (Line pline : nonNeighbours) {
			if (line.intersects(pline)) return true;
		}
		
		for (Edge edge : to.neighbours) {
			Line pline = new Line();
			pline.addAll(edge.line);
			pline.removeFirst();
			pline.removeLast();
			
			if (line.intersects(pline)) return true;
		}
		
		for (Edge edge : from.neighbours) {
			Line pline = new Line();
			pline.addAll(edge.line);
			pline.removeFirst();
			pline.removeLast();
			
			if (line.intersects(pline)) return true;
		}
		
		Line line2 = new Line();
		line2.addAll(line);
		line2.removeFirst();
		line2.removeLast();
		
		for (Edge edge : to.neighbours) {
			Line pline = new Line();
			pline.addAll(edge.line);
			
			if (line2.intersects(pline))	return true;
		}
		
		for (Edge edge : from.neighbours) {
			Line pline = new Line();
			pline.addAll(edge.line);
			
			if (line2.intersects(pline))	return true;
		}
		
		return false;
	}
	
	// @TODO: refactor	// @HACK
	/*
	public static boolean isPointInPolygon(Vertex point, List<Vertex> vertices) {
		List<Vec2> outer = new ArrayList<>();
		for (Vertex vertex : vertices) outer.add(new Vec2(vertex.x, vertex.y));
		Vec2 point2 = new Vec2(point.x, point.y);
		return LinMath.contains(outer, point2);
	}
	*/
	
	public static boolean isPointInPolygon (Vertex point, List<Vertex> polygon) {
		Vertex last = polygon.get(polygon.size()-1);
		float x = point.x, y = point.y;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.size(); i++) {
			Vertex vertex = polygon.get(i);
			if ((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) {
				if (vertex.x + (y - vertex.y) / (last.y - vertex.y) * (last.x - vertex.x) < x) oddNodes = !oddNodes;
			}
			last = vertex;
		}
		return oddNodes;
	}
	
	public static boolean isPointInPolygon (Vertex point, Vertex[] polygon) {
		Vertex last = polygon[polygon.length-1];
		float x = point.x, y = point.y;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.length; i++) {
			Vertex vertex = polygon[i];
			if ((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) {
				if (vertex.x + (y - vertex.y) / (last.y - vertex.y) * (last.x - vertex.x) < x) oddNodes = !oddNodes;
			}
			last = vertex;
		}
		return oddNodes;
	}

	public void printHistoryTestCode() {
		history.printTestCode();
	}
	
	public Position getPosition() {
		return position;
	}

	public void printLives() {
		System.out.printf("=== lives G ===\n");
		for (int i = 0; i < position.getSprouts().size(); i++) {
			Sprout s = null;
			for (Sprout sprout : position.getSprouts()) {
				if (sprout.id == i) {
					s = sprout;
					break;
				}
			}
			
			System.out.printf("%d\n", s.getLives());
		}
		
		System.out.printf("\n");
	}

	public boolean isGameOver() {
		return position.isGameOver();
	}
}
