package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.model.move.MoveException;
import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;
import sprouts.game.model.move.RawMovePathGenerator;
import sprouts.game.model.move.generators.MovePathResult;

public class GraphicalFacade {
	
	private RawMovePathGenerator pathGenerator;
	private Position position;
	private MoveHistory history;

	public Line currentLine;
	boolean drawingLine;
	public Sprout from;
	public float minimumLineSegmentDistance;
	public int sproutRadius;
	
	public GraphicalFacade() {
		DebugIdGenerators.reset();
		
		currentLine = new Line();
		
		pathGenerator = new RawMovePathGenerator();
		
		minimumLineSegmentDistance = 25;
		drawingLine = false;
		sproutRadius = 12;
		
		history = new MoveHistory();
	}
	
	public void createFreshPosition(int numberOfSprouts) {
		PositionBuilder builder = new PositionBuilder();
		position = builder.createSproutsCircle(numberOfSprouts, 320, 240, 150).build();
		
		/*
		position = builder
			.createFreshSprout(140, 360)
			.createFreshSprout(340, 360)
			.createFreshSprout(140, 160)
			.createFreshSprout(440, 180)
			.createFreshSprout(420, 140)
			.build();
		*/
		
		/*
		Line line0 = new Line();
		line0.add(new Vertex(213.93398f,133.93399f));
		line0.add(new Vertex(239.0f,133.0f));
		line0.add(new Vertex(263.0f,144.0f));
		line0.add(new Vertex(281.0f,162.0f));
		line0.add(new Vertex(287.0f,191.0f));
		line0.add(new Vertex(284.0f,218.0f));
		line0.add(new Vertex(275.0f,245.0f));
		line0.add(new Vertex(259.0f,265.0f));
		line0.add(new Vertex(230.0f,278.0f));
		line0.add(new Vertex(194.0f,287.0f));
		line0.add(new Vertex(163.0f,286.0f));
		line0.add(new Vertex(133.0f,278.0f));
		line0.add(new Vertex(110.0f,266.0f));
		line0.add(new Vertex(90.0f,249.0f));
		line0.add(new Vertex(85.0f,206.0f));
		line0.add(new Vertex(90.0f,171.0f));
		line0.add(new Vertex(104.0f,146.0f));
		line0.add(new Vertex(127.0f,133.0f));
		line0.add(new Vertex(150.0f,122.0f));
		line0.add(new Vertex(179.0f,119.0f));
		line0.add(new Vertex(203.0f,126.0f));
		line0.add(new Vertex(213.93398f,133.93399f));
		String move0 = executeLine(line0);
		*/
		
	}

	public MovePathResult generateMove(String rawMove) {
		MovePathResult result = null;

		try {
			RawMove move = new RawMove(rawMove);
		
			try {
				result = pathGenerator.generate(move, position);
				
				if (result.line == null) return result;

				Util.require(!intersectsNewLine(result.line));
				
				history.add(rawMove);
				
			} catch (MoveException e) {
				System.out.printf("Generation exception: %s\n", e.getMessage());
			}
			
		} catch (MoveNotationException e) {
			System.out.printf("Notation exception: illegal move notation %s\n", rawMove);
		}
				
		return result;
	}
	
	public String executeLine(Line line) {
		Util.require(!intersectsNewLine(line));
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
		String actualMove = "";
		if (result.line != null) {
			actualMove = executeLine(result.line);
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
			
			if (position.isGameOver()) System.out.printf("game over!!!!\n");
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
}
