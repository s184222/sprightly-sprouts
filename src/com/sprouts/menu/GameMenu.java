package com.sprouts.menu;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import com.sprouts.SproutsMain;
import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.IMouseEventListener;
import com.sprouts.composition.event.KeyEvent;
import com.sprouts.composition.event.MouseEvent;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.color.LinearColorGradient2D;
import com.sprouts.math.LinMath;
import com.sprouts.math.Vec2;

import sprouts.ai.AbstractFacade;
import sprouts.ai.player.Player;
import sprouts.ai.player.RandomPlayer;
import sprouts.game.Console;
import sprouts.game.GraphicalFacade;
import sprouts.game.model.Edge;
import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.Position;
import sprouts.game.model.Region;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.move.IdMove;
import sprouts.game.move.advanced.OneBoundaryMoveGeneratorData;
import sprouts.game.move.advanced.TwoBoundaryMoveGeneratorData;
import sprouts.game.move.pipe.MovePathResult;
import sprouts.game.move.simple.SimpleMoveGeneratorData;
import sprouts.game.move.triangles.Triangle;
import sprouts.game.util.MathUtil;

public class GameMenu extends SproutsMenu {

	private GraphicalFacade facadeG;
	private AbstractFacade facadeA;
	private Player ai;
	
	private final Font font;
	
	private List<Triangle> triangles;
	private Map<Vertex, List<Vertex>> twoBoundaryGraph;
	private Map<Triangle, List<Triangle>> oneBoundaryGraph;
	private List<Triangle> slither;
	private List<Triangle> wrapper;
	private Line path;
	private List<Triangle> condense;
	
	private boolean drawEdgeIndices = false;
	private boolean drawTriangles = false;
	private boolean showLineOrientation = false;
	private boolean drawPath = true;
	private boolean drawOneGraph = true;
	private boolean drawTwoGraph = false;
	
	private int mouseX;
	private int mouseY;
	
	public GameMenu(SproutsMain main) {
		super(main);
		
		facadeG = new GraphicalFacade();
		facadeA = new AbstractFacade();
		facadeA.createFreshPosition(8);
		facadeG.createFreshPosition(8);
		ai = new RandomPlayer();

		font = getResourceManager().createFont(56.0f);
		
		uiEvents();
		
		Console console = new Console() {
			
			@Override
			public void loop() {
				String rawMove = prompt("move:");
				
				MovePathResult result = facadeG.generateMove(rawMove);
				
				if (result != null) {
					String move = facadeG.executeLine(result.line);
					//facadeA.makeMove(move);
				}

				saveDebug(result);
				
			}
		};
		
		console.start();
	}

	private void uiEvents() {
		addMouseEventListener(new IMouseEventListener() {
			@Override
			public void mouseScrolled(MouseEvent event) {
			}
			
			@Override
			public void mouseReleased(MouseEvent event) {
				facadeG.touchUp(event.getX(), event.getY());
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				facadeG.touchDown(event.getX(), event.getY());
			}
			
			@Override
			public void mouseMoved(MouseEvent event) {
				mouseX = event.getX();
				mouseY = event.getY();
			}
			
			@Override
			public void mouseExited(MouseEvent event) {
			}
			
			@Override
			public void mouseEntered(MouseEvent event) {
			}
			
			@Override
			public void mouseDragged(MouseEvent event) {
				facadeG.touchDragged(event.getX(), event.getY());
			}
		});
		
		addKeyEventListener(new IKeyEventListener() {
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
					facadeG = new GraphicalFacade();
					break;
				}
				
				case GLFW.GLFW_KEY_F1: {
					drawPath = !drawPath;
					break;
				}
				
				case GLFW.GLFW_KEY_H: {
					facadeG.printHistoryTestCode();
					break;
				}
				
				case GLFW.GLFW_KEY_S: {
					Position position = facadeG.getPosition();
					for (Region region : position.getRegions()) {
						region.verbose();
					}
			
					break;
				}
				
				case GLFW.GLFW_KEY_A: {
					System.out.printf("thinking...\n");
					IdMove move = ai.getMove(facadeA.getPosition());
					System.out.printf("ai: %s\n", move.toString());
					
					MovePathResult result = facadeG.generateMove(move.toString());
					facadeG.executeLine(result.line);
					facadeA.makeMove(move.toString());
					
					//saveDebug(result);

					//facadeA.printLives();
					//facadeG.printLives();
					
					if (facadeG.isGameOver()) System.out.printf("game over\n");
			
					break;
				}
				}
				
				
			}
		});
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
		tessellator.beginBatch();
		
		Position position = facadeG.getPosition();
		//tessellator.scale(1.7f, 3f);
		
		if (drawTriangles) {
			if (triangles != null) {
				tessellator.setColor(VertexColor.LIGHT_GREEN);
				for (Triangle triangle : triangles) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
				
					tessellator.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				tessellator.setColor(VertexColor.GREEN);
				for (Triangle triangle : triangles) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					
					float width = 2;
					
					tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, width);
					tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, width);
					tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, width);
				}
				
				Triangle selected = null;
				for (Triangle triangle : triangles) {
					Vertex mouseVertex = new Vertex();
					mouseVertex.x = mouseX;
					mouseVertex.y = mouseY;
					
					if (LinMath.isPointInPolygon(mouseVertex, triangle.getCorners())) {
						selected = triangle;
						break;
					}
				}
				
				if (selected != null) {
					tessellator.setColor(VertexColor.GREEN_YELLOW);
					
					Vertex p1 = selected.getP1();
					Vertex p2 = selected.getP2();
					Vertex p3 = selected.getP3();
					tessellator.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
			}
		}
		
		if (drawOneGraph) {
			
			if (oneBoundaryGraph != null) {
				
				tessellator.setColor(VertexColor.DARK_GREEN);
				
				for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
					Triangle source = entry.getKey();
					
					{
						float width = 2;

						Vertex p1 = source.getP1();
						Vertex p2 = source.getP2();
						Vertex p3 = source.getP3();

						tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, width);
						tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, width);
						tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, width);
						
					}
					
					for (Triangle target : entry.getValue()) {
						
						float width = 2;

						Vertex p1 = target.getP1();
						Vertex p2 = target.getP2();
						Vertex p3 = target.getP3();
						
						tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, width);
						tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, width);
						tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, width);
					}
				}
				
				tessellator.setColor(VertexColor.BROWN);
				
				for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
					Triangle source = entry.getKey();
					Vertex s = source.getCenter();
					
					float width = 2;

					for (Triangle target : entry.getValue()) {
						Vertex t = target.getCenter();
						tessellator.drawLine(s.x, s.y, t.x, t.y, width);
					}
				}
			}

			if (slither != null) {

				tessellator.setColor(VertexColor.WHITE);
				for (Triangle triangle : slither) {
					
					float width = 2;
					
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					
					tessellator.drawLine(p1.x, p1.y, p2.x, p2.y, width);
					tessellator.drawLine(p2.x, p2.y, p3.x, p3.y, width);
					tessellator.drawLine(p3.x, p3.y, p1.x, p1.y, width);
				}
				
				tessellator.setColor(VertexColor.DARK_ORANGE);
				for (int i = 0; i < slither.size() - 1; i++) {
					Triangle t1 = slither.get(i);
					Triangle t2 = slither.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					tessellator.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
			}
			//4<,4<,[0,1,7]			
			if (wrapper != null) {
				for (int i = 0; i < wrapper.size(); i++) {
					tessellator.setColor(new VertexColor(1f, 1f /  wrapper.size() * i, 0f));
					Triangle triangle = wrapper.get(i);
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					tessellator.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				tessellator.setColor(VertexColor.DARK_GRAY);
				for (int i = 0; i < wrapper.size() - 1; i++) {
					Triangle t1 = wrapper.get(i);
					Triangle t2 = wrapper.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					tessellator.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
			}
			
			if (condense != null) {
				tessellator.setColor(VertexColor.PURPLE);
				for (Triangle triangle : condense) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					tessellator.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				tessellator.setColor(VertexColor.WHITE);
				Triangle first = condense.get(0);
				Vertex p1 = first.getP1();
				Vertex p2 = first.getP2();
				Vertex p3 = first.getP3();
				tessellator.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				
				tessellator.setColor(VertexColor.DARK_GRAY);
				for (int i = 0; i < condense.size() - 1; i++) {
					Triangle t1 = condense.get(i);
					Triangle t2 = condense.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					tessellator.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
				
			}
		}
		
		if (slither != null) {

			tessellator.setColor(VertexColor.WHITE);
			for (int i = 0; i < slither.size() - 1; i++) {
				Triangle t1 = slither.get(i);
				
				Vertex[] corners = t1.getCorners();
				for (int j = 0; j < corners.length; j++) {
					Vertex v0 = corners[j];
					Vertex v1 = corners[MathUtil.wrap(j+1, 3)];
					
					LineSegment s = new LineSegment(v0, v1);
					Vertex c1 = s.getMiddle();
					float width = 3;
					tessellator.drawQuad(c1.x-width, c1.y-width, c1.x+width, c1.y+width);
				}
			}
		}
		
		if (drawTwoGraph) {
			if (twoBoundaryGraph != null) {
				tessellator.setColor(VertexColor.BROWN);
				
				for (Entry<Vertex, List<Vertex>> entry : twoBoundaryGraph.entrySet()) {
					Vertex source = entry.getKey();
					
					float width = 2;
					
					for (Vertex target : entry.getValue()) {
						tessellator.drawLine(source.x, source.y, target.x, target.y, width);
					}
				}
			}
		}
		
		tessellator.setColor(VertexColor.BLUE);
		
		List<Sprout> sprouts = position.getSprouts();
		
		for (Sprout sprout : sprouts) {
			float size = facadeG.sproutRadius * 2f;

			float x0 = sprout.position.x - size / 2f;
			float y0 = sprout.position.y - size / 2f;
			
			float x1 = sprout.position.x + size / 2f;
			float y1 = sprout.position.y + size / 2f;

			tessellator.drawQuad(x0, y0, x1, y1);
		}
		
		tessellator.setColor(VertexColor.RED);

		for (Line line : position.getLines()) {
			for (int i = 0; i < line.size() - 1; i++) {
				Vertex v0 = line.get(i);
				Vertex v1 = line.get(i + 1);

				float x0 = v0.x;
				float y0 = v0.y;
				float x1 = v1.x;
				float y1 = v1.y;

				float width = 2;

				tessellator.drawLine(x0, y0, x1, y1, width);
			}
		}
		
		if (showLineOrientation) {
			tessellator.setColor(VertexColor.BLUE);
			
			Vec2 rotater = new Vec2();
			for (Edge edge : position.getEdges()) {
				float rotate = 90;
				float scale = 10;
				
				LineSegment segment = edge.line.get1stQuarterSegment();
				rotater.set(segment.to.x, segment.to.y);
				rotater.sub(segment.from.x, segment.from.y);
				rotater.normalize().rotate(rotate).mul(scale);
				
				Vec2 a = rotater.copy().add(segment.from.x, segment.from.y);
				Vec2 b = rotater.copy().add(segment.to.x, segment.to.y);
				
				float width = 4f;
				tessellator.setColorGradient(new LinearColorGradient2D(a, VertexColor.RED, b, VertexColor.BLUE));
				
				tessellator.drawLine(a.x, a.y, b.x, b.y, width);
			}
		}
		
		if (drawPath) {
			if (path != null) {
				
				for (int i = 0; i < path.size() - 1; i++) {
					float red = 1f / path.size() * i;
					tessellator.setColor(new VertexColor(red, 0f, 0f));
					
					float width = 3;
					Vertex v0 = path.get(i);
					Vertex v1 = path.get(i+1);
					tessellator.drawLine(v0.x, v0.y, v1.x, v1.y, width);
				}
				
				/*
				Vertex v0 = path.get(0);
				Vertex v1 = path.get(1);
				Vertex v2 = path.get(7);
				Vertex v3 = path.get(8);
				
				float width = 4;
				
						//boolean in = LinMath.intersect(v0.x,v0.y,v1.x,v1.y,v2.x,v2.y,v3.x,v3.y);
						//System.out.printf("%b\n", in);
				
				tessellator.setColor(new VertexColor(0f, 0f, 1f));
				tessellator.drawLine(v0.x, v0.y, v1.x, v1.y, width);
				tessellator.setColor(new VertexColor(0, 1f, 0));
				tessellator.drawLine(v2.x, v2.y, v3.x, v3.y, width);
				*/
			}
			
	
		}
		
		tessellator.setColor(VertexColor.ORANGE);
		
		Line currentLine = facadeG.currentLine;
		
		for (int i = 0; i < currentLine.size() - 1; i++) {
			Vertex v0 = currentLine.get(i);
			Vertex v1 = currentLine.get(i + 1);

			float x0 = v0.x;
			float y0 = v0.y;
			float x1 = v1.x;
			float y1 = v1.y;

			float width = 2;

			tessellator.drawLine(x0, y0, x1, y1, width);
		}
		
		tessellator.setColor(VertexColor.RED);
		if (currentLine.size() > 0) {
			Vertex v1 = currentLine.getLast();
			float x1 = v1.x;
			float y1 = v1.y;
			float x2 = mouseX;
			float y2 = mouseY;

			float width = 2;

			tessellator.drawLine(x1, y1, x2, y2, width);
		}
		
		tessellator.setColor(VertexColor.YELLOW);

		for (Sprout sprout : sprouts) {
			String id = String.format("%d", sprout.id);
			
			TextBounds textBounds = font.getTextBounds(id);
			
			float x = sprout.position.x - (textBounds.width - textBounds.x) / 2f;
			float y = sprout.position.y + (textBounds.height - textBounds.y) / 2f;
			y = sprout.position.y;
			
			
			font.drawString(tessellator, id, x, y);
		}
		
		if (drawEdgeIndices) {
			tessellator.setColor(VertexColor.BLACK);
			Vec2 rotater = new Vec2();

			for (Edge edge : position.getEdges()) {
				float rotate = 90;
				float scale = 10;
				
				LineSegment segment = edge.line.size() > 2 ? edge.line.get1stQuarterSegment() : edge.line.getMiddleSegment();
				rotater.set(segment.to.x, segment.to.y);
				rotater.sub(segment.from.x, segment.from.y);
				rotater.normalize().rotate(rotate).mul(scale);
				
				Vec2 pos = new Vec2();
				pos.set(segment.to.x, segment.to.y);
				pos.sub(segment.from.x, segment.from.y);
				pos.mul(0.6f);
				pos.add(segment.from.x, segment.from.y);
				pos.add(rotater);
				
				String edgeId = String.format("%d", edge.id);
				TextBounds textBounds = font.getTextBounds(edgeId);

				float x = pos.x;
				float y = pos.y;
				
				font.drawString(tessellator, edgeId, x, y);
			}
		}
		
		tessellator.endBatch();
	}
	
	private void saveDebug(MovePathResult result) {
		if (result != null) {
			
			path = result.line;

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
	}
}
