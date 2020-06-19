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
import com.sprouts.math.Vec2;

import sprouts.game.model.Edge;
import sprouts.game.model.GameFacade;
import sprouts.game.model.Line;
import sprouts.game.model.LineSegment;
import sprouts.game.model.Position;
import sprouts.game.model.Region;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.model.move.Triangle;
import sprouts.game.model.move.generators.MovePathResult;
import sprouts.game.model.move.generators.one.OneBoundaryMoveGeneratorData;
import sprouts.game.model.move.two.TwoBoundaryMoveGeneratorData;

public class GameMenu extends SproutsMenu {

	private final GameFacade facade;
	
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
	private boolean drawPath = false;
	private boolean drawOneGraph = false;
	private boolean drawTwoGraph = false;
	
	private int mouseX;
	private int mouseY;
	
	public GameMenu(SproutsMain main) {
		super(main);
		
		facade = new GameFacade();

		font = getResourceManager().createFont(56.0f);
		
		uiEvents();
	}

	/*
	 * 			public void loop() {
				String rawMove = prompt("move:");
				
				MovePathResult result = facade.generateMove(rawMove);
				
				if (result != null) {
					facade.executeLine(result.line);
					
					path = result.line;
	
					triangles = null;
					twoBoundaryGraph = null;
					oneBoundaryGraph = null;
					slither = null;
					wrapper =  null;
					
					switch (result.generatorType) {
					case "oneBoundary":
						OneBoundaryMoveGeneratorData data = (OneBoundaryMoveGeneratorData) result.customData;
						
						triangles = data.triangles;
						oneBoundaryGraph = data.oneBoundaryGraph;
						slither = data.slither;
						wrapper = data.wrapper;
						condense = data.condense;
						
						break;
					case "twoBoundary":
						TwoBoundaryMoveGeneratorData data = (TwoBoundaryMoveGeneratorData) result.customData;
						
						triangles = data.triangles;
						twoBoundaryGraph = data.twoBoundaryGraph;
						
						break;
					default:
						throw new IllegalStateException("unknown generator type: " +  result.generatorType);
					}
				}
			}
	 */
	
	private void uiEvents() {
		addMouseEventListener(new IMouseEventListener() {
			@Override
			public void mouseScrolled(MouseEvent event) {
			}
			
			@Override
			public void mouseReleased(MouseEvent event) {
				facade.touchUp(event.getX(), event.getY());
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				facade.touchDown(event.getX(), event.getY());
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
				facade.touchDragged(event.getX(), event.getY());
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
				case GLFW.GLFW_KEY_F1:
					drawPath = !drawPath;
					break;
				case GLFW.GLFW_KEY_H:
					facade.printHistoryTestCode();
					break;
				case GLFW.GLFW_KEY_S:
					Position position = facade.getPosition();
					for (Region region : position.getRegions())
						region.verbose();
			
					break;
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
		
		Position position = facade.getPosition();
		
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
					
					if (GameFacade.isPointInPolygon(mouseVertex, triangle.getCorners())) {
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

				tessellator.setColor(VertexColor.ORANGE);
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
			
			if (wrapper != null) {
				tessellator.setColor(VertexColor.PURPLE);
				for (Triangle triangle : wrapper) {
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
			float size = facade.sproutRadius * 2f;

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
				tessellator.setColor(VertexColor.GREEN);
				
				for (int i = 0; i < path.size() - 1; i++) {
					float width = 3;
					Vertex v0 = path.get(i);
					Vertex v1 = path.get(i+1);
					tessellator.drawLine(v0.x, v0.y, v1.x, v1.y, width);
				}
			}
		}
		
		tessellator.setColor(VertexColor.ORANGE);
		
		Line currentLine = facade.currentLine;
		
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

			float width = 2;

			tessellator.drawLine(x1, y1, mouseX, mouseY, width);
		}
		
		if (drawEdgeIndices) {
			Vec2 rotater = new Vec2();

			tessellator.setColor(VertexColor.BLACK);

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

				float x = pos.x - textBounds.width / 2.0f - textBounds.x;
				float y = pos.y - textBounds.height / 2.0f - textBounds.y;
				
				font.drawString(tessellator, edgeId, x, y);
			}
		}
		
		tessellator.setColor(VertexColor.BLACK);
		for (Sprout sprout : sprouts) {
			String id = String.format("%d", sprout.id);
			
			TextBounds textBounds = font.getTextBounds(id);
			
			float x = sprout.position.x - textBounds.width / 2.0f - textBounds.x;
			float y = sprout.position.y - textBounds.height / 2.0f - textBounds.y;
			
			font.drawString(tessellator, id, x, y);
		}
		
		tessellator.endBatch();
	}
}
