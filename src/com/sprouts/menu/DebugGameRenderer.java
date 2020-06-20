package com.sprouts.menu;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.KeyEvent;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.graphic.tessellator2d.color.LinearColorGradient2D;
import com.sprouts.math.LinMath;
import com.sprouts.math.Vec2;

import sprouts.ai.AbstractFacade;
import sprouts.ai.player.Player;
import sprouts.ai.player.RandomPlayer;
import sprouts.game.GraphicalFacade;
import sprouts.game.model.Edge;
import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.Position;
import sprouts.game.model.Region;
import sprouts.game.model.Vertex;
import sprouts.game.move.IdMove;
import sprouts.game.move.advanced.OneBoundaryMoveGeneratorData;
import sprouts.game.move.advanced.TwoBoundaryMoveGeneratorData;
import sprouts.game.move.pipe.MovePathResult;
import sprouts.game.move.simple.SimpleMoveGeneratorData;
import sprouts.game.move.triangles.Triangle;
import sprouts.game.util.MathUtil;

public class DebugGameRenderer implements IKeyEventListener {

	public static final float OUTLINE_WIDTH = 2.0f;
	private static final float CENTER_LINE_WIDTH = 2.0f;
	private static final float ORIENTATION_LINE_WIDTH = 4.0f;
	
	private final GameMenu gameMenu;
	
	private AbstractFacade facadeA;
	private final Player ai;
	
	private List<Triangle> triangles;
	private Map<Vertex, List<Vertex>> twoBoundaryGraph;
	private Map<Triangle, List<Triangle>> oneBoundaryGraph;
	private List<Triangle> slither;
	private List<Triangle> wrapper;
	private List<Triangle> condense;

	private Line previousMove;

	private boolean shouldDrawEdgeIndices;
	private boolean showLineOrientation;
	
	private boolean shouldDrawMoveTriangles;
	private boolean shouldDrawPreviousMove;
	private boolean shouldDrawOneGraph;
	private boolean shouldDrawTwoGraph;
	
	public DebugGameRenderer(GameMenu gameMenu) {
		this.gameMenu = gameMenu;
		
		facadeA = new AbstractFacade();
		ai = new RandomPlayer();

		facadeA.createFreshPosition(8);
		
		shouldDrawEdgeIndices = false;
		showLineOrientation = false;
		
		shouldDrawMoveTriangles = true;
		shouldDrawPreviousMove = true;
		shouldDrawOneGraph = false;
		shouldDrawTwoGraph = true;
	
		gameMenu.addKeyEventListener(this);
	}
	
	public void onMoveExecuted(MovePathResult result) {
		previousMove = result.line;

		triangles = null;
		twoBoundaryGraph = null;
		oneBoundaryGraph = null;
		slither = null;
		wrapper =  null;
		
		switch (result.generatorType) {
		case "oneBoundary": {
			OneBoundaryMoveGeneratorData data = (OneBoundaryMoveGeneratorData) result.customData;
			
			triangles = data.triangles;
			oneBoundaryGraph = data.oneBoundaryGraph;
			slither = data.slither;
			wrapper = data.wrapper;
			condense = data.condense;
			
			break;
		}
		case "twoBoundary": {
			TwoBoundaryMoveGeneratorData data = (TwoBoundaryMoveGeneratorData) result.customData;
			
			triangles = data.triangles;
			twoBoundaryGraph = data.twoBoundaryGraph;
			
			break;
		}
		case "simple": {
			SimpleMoveGeneratorData data = (SimpleMoveGeneratorData) result.customData;
			
			triangles = data.triangles;
			twoBoundaryGraph = data.twoBoundaryGraph;
			
			break;
		}
		default: {
			throw new IllegalStateException("unknown generator type: " +  result.generatorType);
		}
		}
	}
	
	public void drawBackground(BatchedTessellator2D tessellator) {
		if (shouldDrawMoveTriangles && triangles != null) {
			Vertex mouseVertex = gameMenu.viewToWorld(gameMenu.mousePos);
			
			for (Triangle triangle : triangles) {
				VertexColor fillColor = VertexColor.LIGHT_GREEN;
				
				if (LinMath.isPointInPolygon(mouseVertex, triangle.getCorners())) {
					fillColor = VertexColor.GREEN_YELLOW;
					System.out.println(triangle.toString());
					
					
					if (gameMenu.facadeG.getPosition().getSprouts().size() == 10) {
						Vertex to = gameMenu.facadeG.getPosition().getSprout(10).position;
						
						System.out.println(to);
						if (triangle.isCorner(to)) System.out.println("!!!!!!!!!");
					}
					
				}
				
				tessellator.setColor(fillColor);
				drawOutlinedTriangle(tessellator, triangle, fillColor, VertexColor.GREEN);
			}
		}
		
		if (shouldDrawOneGraph)
			drawOneGraph(tessellator);
		
		if (shouldDrawTwoGraph)
			drawTwoGraph(tessellator);
	}
	
	public void drawForeground(ITessellator2D tessellator) {
		if (shouldDrawPreviousMove) {
			if (previousMove != null) {
				tessellator.setColor(VertexColor.RED);
				gameMenu.drawSproutLine(tessellator, previousMove);
				
				/*
				Vec2 v0 = gameMenu.worldToView(previousMove.get(8));
				Vec2 v1 = gameMenu.worldToView(previousMove.get(9));
				Vec2 v2 = gameMenu.worldToView(previousMove.get(11));
				Vec2 v3 = gameMenu.worldToView(previousMove.get(12));
				
				float width = 4;
				
						//boolean in = LinMath.intersect(v0.x,v0.y,v1.x,v1.y,v2.x,v2.y,v3.x,v3.y);
						//System.out.printf("%b\n", in);
				
				tessellator.setColor(new VertexColor(0f, 0f, 1f));
				tessellator.drawLine(v0, v1, width);
				tessellator.setColor(new VertexColor(0, 1f, 0));
				tessellator.drawLine(v2, v3, width);
				*/
			}
		}
		
		if (showLineOrientation)
			drawLineOrientations(tessellator);
		
		if (shouldDrawEdgeIndices) {
			Vec2 rotator = new Vec2();

			tessellator.setColor(VertexColor.WHITE);

			Position position = gameMenu.facadeG.getPosition();
			
			for (Edge edge : position.getEdges()) {
				LineSegment segment = (edge.line.size() > 2) ? edge.line.get1stQuarterSegment() : 
				                                               edge.line.getMiddleSegment();
				
				Vec2 from = gameMenu.worldToView(segment.from);
				Vec2 to = gameMenu.worldToView(segment.to);
				
				rotator.set(to).sub(from).normalize();
				rotator.rotate(90.0f).mul(10.0f);
				
				Vec2 pos = new Vec2();
				pos.set(to).sub(from).mul(0.6f);
				pos.add(from).add(rotator);
				
				String idText = Integer.toString(edge.id);
				TextBounds textBounds = gameMenu.font.getTextBounds(idText);

				float tx = pos.x - textBounds.width / 2.0f - textBounds.x;
				float ty = pos.y - textBounds.height / 2.0f - textBounds.y;
				gameMenu.font.drawString(tessellator, idText, tx, ty);
			}
		}
	}
	
	private void drawOneGraph(BatchedTessellator2D tessellator) {
		if (oneBoundaryGraph != null) {
			tessellator.setColor(VertexColor.DARK_GREEN);
			
			for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
				drawTriangleOutline(tessellator, entry.getKey());
				
				for (Triangle target : entry.getValue())
					drawTriangleOutline(tessellator, target);
			}
			
			tessellator.setColor(VertexColor.BROWN);
			
			for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
				Triangle source = entry.getKey();
				Vec2 sourcePos = gameMenu.worldToView(source.getCenter());
				
				for (Triangle target : entry.getValue()) {
					Vec2 targetPos = gameMenu.worldToView(target.getCenter());
					
					//tessellator.drawLine(sourcePos, targetPos, CENTER_LINE_WIDTH);
				}
			}
		}

		if (slither != null && false) {
			tessellator.setColor(VertexColor.ORANGE);
			
			for (Triangle triangle : slither)
				drawTriangleOutline(tessellator, triangle);
			
			tessellator.setColor(VertexColor.DARK_ORANGE);
			drawTriangleCenterLines(tessellator, slither);
		}
		
		if (wrapper != null && false) {
			for (int i = 0; i < wrapper.size(); i++) {
				Triangle triangle = wrapper.get(i);
				
				tessellator.setColor(new VertexColor(1f, 1f /  wrapper.size() * i, 0f));
				drawTriangle(tessellator, triangle);
			}
			
			tessellator.setColor(VertexColor.DARK_GRAY);
			drawTriangleCenterLines(tessellator, wrapper);
		}
		
		if (condense != null && !condense.isEmpty()) {
			tessellator.setColor(VertexColor.PURPLE);
			
			for (Triangle triangle : condense)
				drawTriangle(tessellator, triangle);
			
			tessellator.setColor(VertexColor.WHITE);
			drawTriangle(tessellator, condense.get(0));
			
			tessellator.setColor(VertexColor.DARK_GRAY);
			drawTriangleCenterLines(tessellator, condense);
		}
		
		if (slither != null) {
			tessellator.setColor(VertexColor.WHITE);
			for (int i = 0; i < slither.size() - 1; i++) {
				Triangle t1 = slither.get(i);
				
				Vertex[] corners = t1.getCorners();
				for (int j = 0; j < corners.length; j++) {
					Vertex v0 = corners[j];
					Vertex v1 = corners[MathUtil.wrap(j + 1, 3)];
					
					LineSegment s = new LineSegment(v0, v1);
					
					Vec2 c1 = gameMenu.worldToView(s.getMiddle());

					float width = 3;
					tessellator.drawQuad(c1.x - width, c1.y - width, c1.x + width, c1.y + width);
				}
			}
		}
	}
	
	private void drawTwoGraph(BatchedTessellator2D tessellator) {
		if (twoBoundaryGraph != null) {
			tessellator.setColor(VertexColor.BROWN);
			
			for (Entry<Vertex, List<Vertex>> entry : twoBoundaryGraph.entrySet()) {
				Vec2 sourcePos = gameMenu.worldToView(entry.getKey());
				
				for (Vertex target : entry.getValue()) {
					Vec2 targetPos = gameMenu.worldToView(target);
					
					tessellator.drawLine(sourcePos, targetPos, CENTER_LINE_WIDTH);
				}
			}
		}
	}
	
	private void drawLineOrientations(ITessellator2D tessellator) {
		Vec2 rotator = new Vec2();
		
		for (Edge edge : gameMenu.facadeG.getPosition().getEdges()) {
			LineSegment segment = edge.line.get1stQuarterSegment();
			Vec2 from = gameMenu.worldToView(segment.from);
			Vec2 to   = gameMenu.worldToView(segment.to);
			
			rotator.set(to).sub(from).normalize();
			rotator.rotate(90.0f).mul(10.0f);
			
			Vec2 p0 = rotator.copy().add(from);
			Vec2 p1 = rotator.copy().add(to);
			
			tessellator.setColorGradient(new LinearColorGradient2D(p0, VertexColor.RED, p1, VertexColor.BLUE));
			tessellator.drawLine(p0, p1, ORIENTATION_LINE_WIDTH);
		}
	}

	private void drawTriangleCenterLines(ITessellator2D tessellator, List<Triangle> slither2) {
		for (int i = 1; i < slither.size(); i++) {
			Triangle t1 = slither.get(i - 1);
			Triangle t2 = slither.get(i);
			
			Vec2 c1 = gameMenu.worldToView(t1.getCenter());
			Vec2 c2 = gameMenu.worldToView(t2.getCenter());
			
			tessellator.drawLine(c1, c2, CENTER_LINE_WIDTH);
		}
	}
	
	private void drawOutlinedTriangle(ITessellator2D tessellator, Triangle triangle, VertexColor fillColor, VertexColor outlineColor) {
		Vec2 p1 = gameMenu.worldToView(triangle.getP1());
		Vec2 p2 = gameMenu.worldToView(triangle.getP2());
		Vec2 p3 = gameMenu.worldToView(triangle.getP3());
	
		tessellator.setColor(fillColor);
		tessellator.drawTriangle(p1, p2, p3);

		tessellator.setColor(outlineColor);
		tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, OUTLINE_WIDTH);
		tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, OUTLINE_WIDTH);
		tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, OUTLINE_WIDTH);
	}

	private void drawTriangle(ITessellator2D tessellator, Triangle triangle) {
		Vec2 p1 = gameMenu.worldToView(triangle.getP1());
		Vec2 p2 = gameMenu.worldToView(triangle.getP2());
		Vec2 p3 = gameMenu.worldToView(triangle.getP3());
	
		tessellator.drawTriangle(p1, p2, p3);
	}

	private void drawTriangleOutline(ITessellator2D tessellator, Triangle triangle) {
		Vec2 p1 = gameMenu.worldToView(triangle.getP1());
		Vec2 p2 = gameMenu.worldToView(triangle.getP2());
		Vec2 p3 = gameMenu.worldToView(triangle.getP3());
		
		tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, OUTLINE_WIDTH);
		tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, OUTLINE_WIDTH);
		tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, OUTLINE_WIDTH);
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
	}
	
	@Override
	public void keyRepeated(KeyEvent event) {
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.getKeyCode()) {
		case GLFW.GLFW_KEY_R: {
			gameMenu.facadeG = new GraphicalFacade();
			gameMenu.facadeG.createFreshPosition(8);

			facadeA = new AbstractFacade();
			facadeA.createFreshPosition(8);
			break;
		}
		
		case GLFW.GLFW_KEY_F1: {
			shouldDrawPreviousMove = !shouldDrawPreviousMove;
			break;
		}
		
		case GLFW.GLFW_KEY_H: {
			gameMenu.facadeG.printHistoryTestCode();
			break;
		}
		
		case GLFW.GLFW_KEY_S: {
			Position position = gameMenu.facadeG.getPosition();
			for (Region region : position.getRegions()) {
				region.verbose();
			}
	
			break;
		}
		
		case GLFW.GLFW_KEY_A: {
			System.out.printf("thinking...\n");
			IdMove move = ai.getMove(facadeA.getPosition());
			System.out.printf(">>ai: %s\n", move.toString());

			MovePathResult result = gameMenu.facadeG.generateMove(move.toString());
			System.out.println("here");
			System.out.println(result.line.size());
			
			gameMenu.facadeG.executeLine(result.line);
			facadeA.makeMove(move.toString());
			
			//saveDebug(result);

			//facadeA.printLives();
			//facadeG.printLives();
			
			if (gameMenu.facadeG.isGameOver())
				System.out.printf("game over\n");
	
			break;
		}
		}
	}
}
