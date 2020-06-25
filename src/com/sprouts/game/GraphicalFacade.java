package com.sprouts.game;

import java.util.ArrayList;
import java.util.List;

import com.sprouts.game.model.DebugIdGenerators;
import com.sprouts.game.model.Edge;
import com.sprouts.game.model.Line;
import com.sprouts.game.model.Position;
import com.sprouts.game.model.PositionBuilder;
import com.sprouts.game.model.Sprout;
import com.sprouts.game.model.Vertex;
import com.sprouts.game.move.IdMove;
import com.sprouts.game.move.MoveException;
import com.sprouts.game.move.MoveNotationException;
import com.sprouts.game.move.MovePipeLineException;
import com.sprouts.game.move.advanced.AdvancedLineGenerationPipeline;
import com.sprouts.game.move.pathfinder.AStarPathFinder;
import com.sprouts.game.move.pathfinder.PathFinder;
import com.sprouts.game.move.pipe.LineGenerationPipeline;
import com.sprouts.game.move.pipe.LinePathResult;
import com.sprouts.game.move.simple.SimpleLineGenerationPipeline;
import com.sprouts.game.move.triangles.TriangleGenerator;
import com.sprouts.game.util.MathUtil;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class GraphicalFacade {
	
	private Position position;
	private PositionBuilder builder;
	private MoveHistory history;
	
	private List<LineGenerationPipeline> moveGenerationPipes;
	
	public Line currentLine;
	boolean drawingLine;
	public Sprout from;
	public double minimumLineSegmentDistance;
	public int sproutRadius;
	
	public GraphicalFacade() {
		DebugIdGenerators.reset();
		
		currentLine = new Line();
		builder = new PositionBuilder();
		history = new MoveHistory();
		
		moveGenerationPipes = new ArrayList<>();
		
		PathFinder pathfinder = new AStarPathFinder();
		TriangleGenerator triangleGenerator = new TriangleGenerator();
		
		LineGenerationPipeline simplePipe = new SimpleLineGenerationPipeline(pathfinder, triangleGenerator);
		moveGenerationPipes.add(simplePipe);
		
		LineGenerationPipeline advancedPipe = new AdvancedLineGenerationPipeline(pathfinder, triangleGenerator);
		moveGenerationPipes.add(advancedPipe);
		
		minimumLineSegmentDistance = 25;
		drawingLine = false;
		sproutRadius = 12;
	}
	
	/**
	 * 
	 */
	public boolean executeMoves(List<String> moves) {
		for (String move : moves) {
			try {
				executeMove(move);
			} catch (MovePipeLineException e) {
				System.out.printf("not possible to execute the move: %s. Early termining the sequence.\n", move);
				return false;
			}
		}
		
		return true;
	}
	
	public void createFreshPosition(int numberOfSprouts) {
		double cx = 320;
		double cy = 240;
		//double radius = 80 + numberOfSprouts * 12;
		double radius = 300;
		position = builder.createSproutsCircle(numberOfSprouts, cx, cy, radius).build();
	}
	
	public void createFreshPosition(int numberOfSprouts, double x, double y, double radius) {
		position = builder.createSproutsCircle(numberOfSprouts, x, y, radius).build();
	}

	public LinePathResult generateMove(String rawMove) {
		for (LineGenerationPipeline pipe : moveGenerationPipes) {
			try {
				LinePathResult result = pipe.process(rawMove, position);
				return result;
			} catch (MoveException | MoveNotationException e) {
				//System.out.printf("%s\n", e.getMessage());
			}
		}
		
		throw new MovePipeLineException("no pipe line could execute the move: %s", rawMove);
	}
	
	public String executeMove(String rawMove) {
		LinePathResult result = generateMove(rawMove);
		
		IdMove actualMove = position.update(result.line);
		return actualMove.toString();
	}
	
	public String executeLine(Line line) {
		//Util.require(!intersectsNewLine(line));
		IdMove move = position.update(line);
		return move.toString();
	}
	
	public String executeLineish(Line line) {
		Vertex first = line.removeFirst();
		Vertex last = line.removeLast();
		
		Sprout fromSprout = getSproutClicked(first.x, first.y);
		Sprout toSprout = getSproutClicked(last.x, last.y);
		
		line.add(0, fromSprout.position);
		line.add(toSprout.position);
		
		//Util.require(!intersectsNewLine(line));
		IdMove move = position.update(line);
		return move.toString();
	}

	public Sprout getSproutClicked(double mx, double my) {
		double minimumDistance = Double.MAX_VALUE;
		Sprout closest = null;

		for (Sprout sprout : position.getSprouts()) {
			if (sprout.getNeighbourCount() > 2) continue;
			
			double distance = MathUtil.distance(mx, my, sprout.position.x, sprout.position.y);
			
			if (distance <= sproutRadius && distance < minimumDistance) {
				distance = minimumDistance;
				closest = sprout;
			}
		}

		return closest;
	}

	public void startMove(double worldX, double worldY) {
		Sprout sprout = getSproutClicked(worldX, worldY);
		
		if (sprout != null) {
			drawingLine = true;
			currentLine.add(sprout.position);
			from = sprout;
		}
	}

	public void dragMove(double worldX, double worldY) {
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

	public String finishMove(double worldX, double worldY) {
		if (!drawingLine) return null;
		drawingLine = false;
		
		Vertex at = currentLine.getLast();
		
		Sprout to = getSproutClicked(worldX, worldY);
		
		Line lineToAdd = new Line();
		lineToAdd.addAll(currentLine);
		currentLine.clear();
		
		if (to != null) {
			if (to.getNeighbourCount() > 2) return null;
			if (to.equals(from) && to.getNeighbourCount() > 1) return null;
			if (to.equals(from) && lineToAdd.size() < 3) return null;
			
			List<Line> nonNeighbours = new ArrayList<>();
			nonNeighbours.addAll(position.getLines());
			for (Edge edge : to.neighbours) {
				nonNeighbours.remove(edge.line);
				nonNeighbours.remove(edge.twin.line);
			}
			
			for (Line line : nonNeighbours) {
				if (line.intersects(at.x, at.y, to.position.x, to.position.y)) return null;
			}
			
			for (Edge edge : to.neighbours) {
				Line line = new Line();
				line.addAll(edge.line);
				line.removeFirst();
				
				if (line.intersects(at.x, at.y, to.position.x, to.position.y)) return null;
			}
			
			Line line = new Line();
			line.addAll(lineToAdd);
			line.removeLast();
			
			if (line.intersects(at.x, at.y, worldX, worldY)) return null;
			
			if (!at.equals(to.position)) lineToAdd.add(to.position);
			
			history.add(lineToAdd.copy());
			
			return executeLine(lineToAdd);
		}
		
		return null;
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

	public boolean isGameOver() {
		return position.isGameOver();
	}
}
