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

import sprouts.game.model.Edge;
import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.Position;
import sprouts.game.model.Region;
import sprouts.game.model.Vertex;
import sprouts.game.move.advanced.OneBoundaryLineGeneratorData;
import sprouts.game.move.advanced.TwoBoundaryLineGeneratorData;
import sprouts.game.move.pipe.LinePathResult;
import sprouts.game.move.simple.SimpleLineGeneratorData;
import sprouts.game.move.triangles.Triangle;
import sprouts.game.util.MathUtil;

public class DebugGameRenderer implements IKeyEventListener {

	public static final float OUTLINE_WIDTH = 2.0f;
	private static final float CENTER_LINE_WIDTH = 2.0f;
	private static final float ORIENTATION_LINE_WIDTH = 4.0f;
	
	private final GameMenu gameMenu;
	
	private List<Triangle> triangles;
	private Map<Vertex, List<Vertex>> twoBoundaryGraph;
	private Map<Triangle, List<Triangle>> oneBoundaryGraph;
	private List<Triangle> slither;
	private List<Triangle> wrapper;

	private Line previousMove;

	private boolean shouldDrawEdgeIndices;
	private boolean showLineOrientation;
	
	private boolean shouldDraw;
	
	private boolean shouldDrawPreviousMove;
	private boolean shouldDrawTriangles;
	private boolean shouldDrawOneGraph;
	
	private boolean shouldDrawTwoGraph;
	private boolean shouldDrawSlither;
	private boolean shouldDrawWrapper;
	
	public DebugGameRenderer(GameMenu gameMenu) {
		this.gameMenu = gameMenu;
		
		shouldDraw = false;
		
		shouldDrawEdgeIndices = false;
		showLineOrientation = false;
		
		shouldDrawOneGraph = false;
		shouldDrawTwoGraph = false;

		shouldDrawTriangles = false;
	
		shouldDrawSlither = false;
		shouldDrawWrapper = false;
		
		shouldDrawPreviousMove = false;
		
		gameMenu.addKeyEventListener(this);
	}
	
	public void onMoveExecuted(LinePathResult result) {
		previousMove = result.line;

		triangles = null;
		twoBoundaryGraph = null;
		oneBoundaryGraph = null;
		slither = null;
		wrapper =  null;
		
		switch (result.generatorType) {
		case "oneBoundary": {
			OneBoundaryLineGeneratorData data = (OneBoundaryLineGeneratorData) result.customData;
			
			triangles = data.triangles;
			oneBoundaryGraph = data.oneBoundaryGraph;
			slither = data.slither;
			wrapper = data.wrapper;
			
			break;
		}
		case "twoBoundary": {
			TwoBoundaryLineGeneratorData data = (TwoBoundaryLineGeneratorData) result.customData;
			
			triangles = data.triangles;
			twoBoundaryGraph = data.twoBoundaryGraph;
			
			break;
		}
		case "simple": {
			SimpleLineGeneratorData data = (SimpleLineGeneratorData) result.customData;
			
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
		if (shouldDraw) {
			
			if (shouldDrawTriangles) {
				if (shouldDrawTriangles) {
					if (triangles != null) {
						Vertex mouseVertex = gameMenu.viewToWorld(gameMenu.mousePos);
						
						for (Triangle triangle : triangles) {
							VertexColor fillColor = VertexColor.LIGHT_GREEN;
							
							if (LinMath.isPointInPolygon(mouseVertex, triangle.getCorners()))
								fillColor = VertexColor.GREEN_YELLOW;
							
							tessellator.setColor(fillColor);
							drawOutlinedTriangle(tessellator, triangle, fillColor, VertexColor.GREEN);
						}
					}
				}
				
			}
			
			if (shouldDrawOneGraph)
				drawOneGraph(tessellator);
			
			if (shouldDrawTwoGraph)
				drawTwoGraph(tessellator);
		}
	}
	
	public void drawForeground(ITessellator2D tessellator) {
		if (shouldDrawPreviousMove) {
			if (previousMove != null) {
				tessellator.setColor(VertexColor.RED);
				gameMenu.drawSproutLine(tessellator, previousMove);
			}
		}
		
		if (showLineOrientation)
			drawLineOrientations(tessellator);
		
		if (shouldDrawEdgeIndices) {
			Vec2 rotator = new Vec2();

			tessellator.setColor(VertexColor.WHITE);

			Position position = gameMenu.facade.getPosition();
			
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
		
		/*
		if (oneBoundaryGraph != null) {
			tessellator.setColor(VertexColor.ORANGE);
			
			for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
				Triangle source = entry.getKey();
				Vec2 sourcePos = gameMenu.worldToView(source.getCenter());
				
				for (Triangle target : entry.getValue()) {
					Vec2 targetPos = gameMenu.worldToView(target.getCenter());
					
					tessellator.drawLine(sourcePos, targetPos, CENTER_LINE_WIDTH);
				}
			}
		}
		*/

		if (shouldDrawSlither) {
			if (slither != null) {
				tessellator.setColor(VertexColor.ORANGE);
				
				for (Triangle triangle : slither)
					drawTriangle(tessellator, triangle);
				
				tessellator.setColor(VertexColor.DARK_ORANGE);
				for (Triangle triangle : slither)
					drawTriangleOutline(tessellator, triangle);

			}
		}
		
		if (shouldDrawWrapper) {
			if (wrapper != null) {
				for (int i = 0; i < wrapper.size(); i++) {
					Triangle triangle = wrapper.get(i);
					
					tessellator.setColor(new VertexColor(1f, 1f /  wrapper.size() * i, 0f));
					drawTriangle(tessellator, triangle);
				}
				
				tessellator.setColor(VertexColor.DARK_GRAY);
				drawTriangleCenterLines(tessellator, wrapper);
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
		
		for (Edge edge : gameMenu.facade.getPosition().getEdges()) {
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
			gameMenu.reset(5);
			break;
		}
		
		case GLFW.GLFW_KEY_F1: {
			shouldDraw = !shouldDraw;
			
			if (!shouldDraw) {
				shouldDrawTriangles = false;
				shouldDrawTwoGraph = false;
				shouldDrawOneGraph = false;
				shouldDrawSlither = false;
				shouldDrawWrapper = false;
				shouldDrawPreviousMove = false;
			}
			
			break;
		}
		
		case GLFW.GLFW_KEY_F2: {
			shouldDrawTriangles = !shouldDrawTriangles;
			break;
		}

		case GLFW.GLFW_KEY_F3: {
			shouldDrawTwoGraph = !shouldDrawTwoGraph;
			break;
		}
		
		case GLFW.GLFW_KEY_F4: {
			shouldDrawOneGraph = !shouldDrawOneGraph;
			break;
		}
		
		case GLFW.GLFW_KEY_F5: {
			shouldDrawSlither = !shouldDrawSlither;
			break;
		}
		
		case GLFW.GLFW_KEY_F6: {
			shouldDrawWrapper = !shouldDrawWrapper;
			break;
		}
		
		case GLFW.GLFW_KEY_F7: {
			shouldDrawPreviousMove = !shouldDrawPreviousMove;
			break;
		}
		
		case GLFW.GLFW_KEY_H: {
			gameMenu.facade.printHistoryTestCode();
			break;
		}
		
		case GLFW.GLFW_KEY_S: {
			Position position = gameMenu.facade.getPosition();
			for (Region region : position.getRegions()) {
				region.verbose();
			}
	
			break;
		}
		}
	}
}
