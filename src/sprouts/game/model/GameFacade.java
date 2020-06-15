package sprouts.game.model;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.model.move.MoveException;
import sprouts.game.model.move.MoveNotationException;
import sprouts.game.model.move.RawMove;
import sprouts.game.model.move.RawMovePathGenerator;
import sprouts.game.model.move.generators.MovePathResult;
import sprouts.tests.MoveHistory;

public class GameFacade {
	
	private RawMovePathGenerator pathGenerator;
	private Position position;
	private MoveHistory history;

	public Line currentLine;
	boolean drawingLine;
	public Sprout from;
	public float minimumLineSegmentDistance;
	public int sproutRadius;
	
	public GameFacade() {
		DebugIdGenerators.reset();
		
		currentLine = new Line();
		
		PositionBuilder builder = new PositionBuilder();
		
		/*
		position = builder
			.createFreshSprout(140, 360)
			.createFreshSprout(340, 360)
			.createFreshSprout(140, 160)
			.createFreshSprout(440, 180)
			.createFreshSprout(420, 140)
			.build();
		*/
		
		position = builder.createSproutsCircle(8, 320, 240, 150).build();
		pathGenerator = new RawMovePathGenerator();
		
		minimumLineSegmentDistance = 25;
		drawingLine = false;
		sproutRadius = 12;
		
		history = new MoveHistory();
		
		/*
		executeMove("1<,2<");

		Line line1 = new Line();
		line1.add(new Vertex(140.0f,160.0f));
		line1.add(new Vertex(172.00003f,159.19998f));
		line1.add(new Vertex(203.20003f,155.99998f));
		line1.add(new Vertex(240.80003f,151.99998f));
		line1.add(new Vertex(268.00003f,149.59998f));
		line1.add(new Vertex(293.60004f,145.59998f));
		line1.add(new Vertex(322.40002f,133.59998f));
		line1.add(new Vertex(350.40002f,127.19999f));
		line1.add(new Vertex(375.20004f,121.59998f));
		line1.add(new Vertex(400.00003f,117.59999f));
		line1.add(new Vertex(427.20004f,115.99998f));
		line1.add(new Vertex(455.20004f,122.39998f));
		line1.add(new Vertex(476.00003f,136.79999f));
		line1.add(new Vertex(481.60004f,164.79999f));
		line1.add(new Vertex(479.20004f,190.4f));
		line1.add(new Vertex(458.40002f,204.79999f));
		line1.add(new Vertex(434.40002f,211.99998f));
		line1.add(new Vertex(408.80002f,214.4f));
		line1.add(new Vertex(383.20004f,223.19998f));
		line1.add(new Vertex(368.00003f,244.79999f));
		line1.add(new Vertex(389.60004f,267.19998f));
		line1.add(new Vertex(417.60004f,283.99997f));
		line1.add(new Vertex(444.80002f,299.19998f));
		line1.add(new Vertex(467.20004f,316.8f));
		line1.add(new Vertex(475.20004f,343.19998f));
		line1.add(new Vertex(477.60004f,369.59998f));
		line1.add(new Vertex(450.40002f,366.39996f));
		line1.add(new Vertex(422.40002f,353.59998f));
		line1.add(new Vertex(400.00003f,336.0f));
		line1.add(new Vertex(377.60004f,315.19998f));
		line1.add(new Vertex(358.40002f,297.59998f));
		line1.add(new Vertex(338.40002f,278.4f));
		line1.add(new Vertex(339.20004f,304.0f));
		line1.add(new Vertex(359.20004f,322.4f));
		line1.add(new Vertex(364.00003f,347.19998f));
		line1.add(new Vertex(341.60004f,358.4f));
		line1.add(new Vertex(340.0f,360.0f));
		executeLine(line1);
		*/
		//executeMove("6>,5<,[3,4]");
		
	}

	public MovePathResult generateMove(String rawMove) {
		MovePathResult result = null;

		try {
			RawMove move = new RawMove(rawMove);
		
			try {
				result = pathGenerator.generate(move, position);

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
		RawMove move = position.getMove(line);
		return move.toString();
	}
	
	public String executeMove(String rawMove) {
		MovePathResult result = generateMove(rawMove);
		String actualMove = executeLine(result.line);
		return actualMove;
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
