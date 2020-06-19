package sprouts.game;

import java.util.ArrayList;
import java.util.List;

import sprouts.game.model.DebugIdGenerators;
import sprouts.game.model.Edge;
import sprouts.game.model.Line;
import sprouts.game.model.Position;
import sprouts.game.model.PositionBuilder;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.move.IdMove;
import sprouts.game.move.MoveException;
import sprouts.game.move.MoveNotationException;
import sprouts.game.move.advanced.AdvanceMoveGenerationPipeline;
import sprouts.game.move.pathfinder.AStarPathFinder;
import sprouts.game.move.pathfinder.PathFinder;
import sprouts.game.move.pipe.MoveGenerationPipeline;
import sprouts.game.move.pipe.MovePathResult;
import sprouts.game.move.simple.SimpleMoveGenerationPipeline;
import sprouts.game.move.triangles.TriangleGenerator;
import sprouts.game.util.MathUtil;

public class GraphicalFacade {
	
	private Position position;
	private PositionBuilder builder;
	private MoveHistory history;
	
	private List<MoveGenerationPipeline> moveGenerationPipes;
	
	public Line currentLine;
	boolean drawingLine;
	public Sprout from;
	public float minimumLineSegmentDistance;
	public int sproutRadius;
	
	public GraphicalFacade() {
		DebugIdGenerators.reset();
		
		currentLine = new Line();
		
		builder = new PositionBuilder();
		
		minimumLineSegmentDistance = 25;
		drawingLine = false;
		sproutRadius = 12;
		
		history = new MoveHistory();
		
		moveGenerationPipes = new ArrayList<>();
		
		PathFinder pathfinder = new AStarPathFinder();
		TriangleGenerator triangleGenerator = new TriangleGenerator();
		
		MoveGenerationPipeline simplePipe = new SimpleMoveGenerationPipeline(pathfinder, triangleGenerator);
		moveGenerationPipes.add(simplePipe);
		
		MoveGenerationPipeline advancePipe = new AdvanceMoveGenerationPipeline(pathfinder, triangleGenerator);
		moveGenerationPipes.add(advancePipe);
	}
	
	public void createFreshPosition(int numberOfSprouts) {
		position = builder.createSproutsCircle(8, 320, 240, 150).build();
	}
	
	public void createFreshPosition(int numberOfSprouts, float x, float y, float radius) {
		position = builder.createSproutsCircle(numberOfSprouts, x, y, radius).build();
	}

	public MovePathResult generateMove(String rawMove) {
		for (MoveGenerationPipeline pipe : moveGenerationPipes) {
			try {
				MovePathResult result = pipe.process(rawMove, position);
				return result;
			} catch (MoveException | MoveNotationException e) {
			}
		}

		System.out.println("could not parse move!");
		
		return null;
	}
	
	public String executeMove(String rawMove) {
		MovePathResult result = generateMove(rawMove);
		IdMove actualMove = position.update(result.line);
		return actualMove.toString();
	}
	
	public String executeLine(Line line) {
		//Util.require(!intersectsNewLine(line));
		IdMove move = position.update(line);
		return move.toString();
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
			
			if (line2.intersects(pline)) return true;
		}
		
		for (Edge edge : from.neighbours) {
			Line pline = new Line();
			pline.addAll(edge.line);
			
			if (line2.intersects(pline)) return true;
		}
		
		return false;
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
