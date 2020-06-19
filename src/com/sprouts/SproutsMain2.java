package com.sprouts;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.FontData;
import com.sprouts.graphic.font.FontLoader;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.color.LinearColorGradient2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.graphic.texture.Texture;
import com.sprouts.graphic.texture.TextureLoader;
import com.sprouts.input.IKeyboardListener;
import com.sprouts.input.IMouseListener;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;
import com.sprouts.math.LinMath;
import com.sprouts.math.Vec2;
import com.sprouts.util.LibUtil;

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

public class SproutsMain2 {

	static {
		LibUtil.loadNatives();
	}
	
	private static final String WINDOW_TITLE = "Sprightly Sprouts";
	private static final int WINDOW_WIDTH  = 1000;
	private static final int WINDOW_HEIGHT = 1000;

	private final Display display;
	private final Mouse mouse;
	private final Keyboard keyboard;
	
	private Tessellator2DShader tessellator2DShader;
	private BatchedTessellator2D batchedTessellator2D;
	private Texture spongeBobTexture;
	private Font arialFont;
	
	private GraphicalFacade facadeG;
	private AbstractFacade facadeA;
	private Player ai;
	
	private List<Triangle> triangles;
	private Map<Vertex, List<Vertex>> twoBoundaryGraph;
	private Map<Triangle, List<Triangle>> oneBoundaryGraph;
	private List<Triangle> slither;
	private List<Triangle> wrapper;
	private Line path;
	private List<Triangle> condense;
	
	private boolean drawEdgeIndices = false;
	private boolean drawTriangles = true;
	private boolean showLineOrientation = false;
	private boolean drawPath = true;
	private boolean drawOneGraph = true;
	private boolean drawTwoGraph = true;
	
	public SproutsMain2() {
		display = new Display();
		mouse = new Mouse(display);
		keyboard = new Keyboard(display);
	}
	
	public void run() {
		init();
		loop();
		
		display.dispose();
		
		tessellator2DShader.dispose();
		batchedTessellator2D.dispose();
		spongeBobTexture.dispose();
		arialFont.dispose();
	}

	private void loadResources() throws Exception {
		tessellator2DShader = new BasicTessellator2DShader();
		batchedTessellator2D = new BatchedTessellator2D(tessellator2DShader);
		spongeBobTexture = TextureLoader.loadTexture("/textures/spongebob.png");

		FontData arialData = FontLoader.loadFont("/fonts/arial.ttf");
		arialFont = arialData.createFont(18);
	}
	
	private void init() {
		display.initDisplay(WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
		display.addDisplayListener(this::onViewportChanged);
		
		mouse.init();
		keyboard.init();

		try {
			loadResources();
		} catch (Exception e) {
			e.printStackTrace();
			
			// TODO: do something else here
			System.exit(1);
		}
		
		mouse.addListener(new IMouseListener() {
			
			@Override
			public void mouseScroll(float mouseX, float mouseY, float scrollX, float scrollY) {
			}
			
			@Override
			public void mouseReleased(int button, float mouseX, float mouseY, int modifiers) {
				facadeG.touchUp(mouseX, mouseY);
			}
			
			@Override
			public void mousePressed(int button, float mouseX, float mouseY, int modifiers) {
				facadeG.touchDown(mouseX, mouseY);
			}
			
			@Override
			public void mouseMoved(float mouseX, float mouseY) {
			}
			
			@Override
			public void mouseDragged(int button, float mouseX, float mouseY, float dragX, float dragY) {
				facadeG.touchDragged(mouseX, mouseY);
			}
		});
		
		keyboard.addListener(new IKeyboardListener() {
			
			@Override
			public void keyTyped(int codePoint) {
			}
			
			@Override
			public void keyRepeated(int key, int mods) {
			}
			
			@Override
			public void keyReleased(int key, int mods) {
			}
			
			@Override
			public void keyPressed(int key, int mods) {
				
				switch (key) {
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
		
		facadeG = new GraphicalFacade();
		facadeA = new AbstractFacade();
		facadeA.createFreshPosition(8);
		facadeG.createFreshPosition(8);
		ai = new RandomPlayer();
		
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

	private void onViewportChanged(DisplaySize size) {
		onViewportChanged(size.width, size.height);
	}

	private void onViewportChanged(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		
		batchedTessellator2D.setViewport(0, 0, width, height);
	}
	
	private void loop() {
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		onViewportChanged(display.getDisplaySize());

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		while (!display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			render();
			
			checkGLErrors();
			
			display.update();
		}
	}
	
	private void render() {
		batchedTessellator2D.beginBatch();

		//batchedTessellator2D.translate(100, 100);
		/*
		{
			String text = "the brown fox";
			float tx = 200.0f;
			float ty = 170.0f;
			
			TextBounds textBounds = arialFont.getTextBounds(text);
			float x0 = tx + textBounds.x;
			float y0 = ty + textBounds.y;
			float x1 = x0 + textBounds.width;
			float y1 = y0 + textBounds.height;
	
			batchedTessellator2D.setColor(VertexColor.DARK_CYAN);
			batchedTessellator2D.drawQuad(x0, y0, x1, y1);
			batchedTessellator2D.setColorGradient(new LinearColorGradient2D(new Vec2(x0, y0), VertexColor.BLACK, new Vec2(x1, y1), VertexColor.WHITE));
			arialFont.drawString(batchedTessellator2D, tx, ty, text);
			
			batchedTessellator2D.translate(200.0f, 200.0f);
			batchedTessellator2D.setColorGradient(new LinearColorGradient2D(new Vec2(0.0f, 0.0f), VertexColor.WHITE, new Vec2(0.0f, 400.0f), VertexColor.PURPLE));
			batchedTessellator2D.setTextureRegion(spongeBobTexture);
			batchedTessellator2D.drawQuad(0.0f, 0.0f, 400.0f, 400.0f);
			batchedTessellator2D.translate(-200.0f, -200.0f);
			
			batchedTessellator2D.clearMaterial();
			batchedTessellator2D.setColor(VertexColor.BLUE_VIOLET);
			batchedTessellator2D.drawLine(50.0f, 150.0f, 150.0f, 250.0f, 2.0f);
			batchedTessellator2D.setColor(VertexColor.CHOCOLATE);
			batchedTessellator2D.drawLine(150.0f, 150.0f, 50.0f, 250.0f, 2.0f);
		}
		*/
		
		Position position = facadeG.getPosition();
		//batchedTessellator2D.scale(1.7f, 3f);
		
		if (drawTriangles) {
			if (triangles != null) {
				batchedTessellator2D.setColor(VertexColor.LIGHT_GREEN);
				for (Triangle triangle : triangles) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
				
					batchedTessellator2D.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				batchedTessellator2D.setColor(VertexColor.GREEN);
				for (Triangle triangle : triangles) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					
					float width = 2;
					
					batchedTessellator2D.drawLine(p1.x, p1.y, p2.x, p2.y, width);
					batchedTessellator2D.drawLine(p2.x, p2.y, p3.x, p3.y, width);
					batchedTessellator2D.drawLine(p3.x, p3.y, p1.x, p1.y, width);
				}
				
				Triangle selected = null;
				for (Triangle triangle : triangles) {
					Vertex mouseVertex = new Vertex();
					mouseVertex.x = mouse.getMouseX();
					mouseVertex.y = mouse.getMouseY();
					
					if (LinMath.isPointInPolygon(mouseVertex, triangle.getCorners())) {
						selected = triangle;
						break;
					}
				}
				
				if (selected != null) {
					batchedTessellator2D.setColor(VertexColor.GREEN_YELLOW);
					
					Vertex p1 = selected.getP1();
					Vertex p2 = selected.getP2();
					Vertex p3 = selected.getP3();
					batchedTessellator2D.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
			}
		}
		
		if (drawOneGraph) {
			
			if (oneBoundaryGraph != null) {
				
				batchedTessellator2D.setColor(VertexColor.DARK_GREEN);
				
				for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
					Triangle source = entry.getKey();
					
					{
						float width = 2;

						Vertex p1 = source.getP1();
						Vertex p2 = source.getP2();
						Vertex p3 = source.getP3();

						batchedTessellator2D.drawLine(p1.x, p1.y, p2.x, p2.y, width);
						batchedTessellator2D.drawLine(p2.x, p2.y, p3.x, p3.y, width);
						batchedTessellator2D.drawLine(p3.x, p3.y, p1.x, p1.y, width);
						
					}
					
					for (Triangle target : entry.getValue()) {
						
						float width = 2;

						Vertex p1 = target.getP1();
						Vertex p2 = target.getP2();
						Vertex p3 = target.getP3();
						
						batchedTessellator2D.drawLine(p1.x, p1.y, p2.x, p2.y, width);
						batchedTessellator2D.drawLine(p2.x, p2.y, p3.x, p3.y, width);
						batchedTessellator2D.drawLine(p3.x, p3.y, p1.x, p1.y, width);
					}
				}
				
				batchedTessellator2D.setColor(VertexColor.BROWN);
				
				for (Entry<Triangle, List<Triangle>> entry : oneBoundaryGraph.entrySet()) {
					Triangle source = entry.getKey();
					Vertex s = source.getCenter();
					
					float width = 2;

					for (Triangle target : entry.getValue()) {
						Vertex t = target.getCenter();
						batchedTessellator2D.drawLine(s.x, s.y, t.x, t.y, width);
					}
				}
			}

			if (slither != null) {

				batchedTessellator2D.setColor(VertexColor.WHITE);
				for (Triangle triangle : slither) {
					
					float width = 2;
					
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					
					batchedTessellator2D.drawLine(p1.x, p1.y, p2.x, p2.y, width);
					batchedTessellator2D.drawLine(p2.x, p2.y, p3.x, p3.y, width);
					batchedTessellator2D.drawLine(p3.x, p3.y, p1.x, p1.y, width);
				}
				
				batchedTessellator2D.setColor(VertexColor.DARK_ORANGE);
				for (int i = 0; i < slither.size() - 1; i++) {
					Triangle t1 = slither.get(i);
					Triangle t2 = slither.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					batchedTessellator2D.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
			}
			//4<,4<,[0,1,7]			
			if (wrapper != null) {
				for (int i = 0; i < wrapper.size(); i++) {
					batchedTessellator2D.setColor(new VertexColor(1f, 1f /  wrapper.size() * i, 0f));
					Triangle triangle = wrapper.get(i);
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					batchedTessellator2D.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				batchedTessellator2D.setColor(VertexColor.DARK_GRAY);
				for (int i = 0; i < wrapper.size() - 1; i++) {
					Triangle t1 = wrapper.get(i);
					Triangle t2 = wrapper.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					batchedTessellator2D.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
			}
			
			if (condense != null) {
				batchedTessellator2D.setColor(VertexColor.PURPLE);
				for (Triangle triangle : condense) {
					Vertex p1 = triangle.getP1();
					Vertex p2 = triangle.getP2();
					Vertex p3 = triangle.getP3();
					batchedTessellator2D.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				}
				
				batchedTessellator2D.setColor(VertexColor.WHITE);
				Triangle first = condense.get(0);
				Vertex p1 = first.getP1();
				Vertex p2 = first.getP2();
				Vertex p3 = first.getP3();
				batchedTessellator2D.drawTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
				
				batchedTessellator2D.setColor(VertexColor.DARK_GRAY);
				for (int i = 0; i < condense.size() - 1; i++) {
					Triangle t1 = condense.get(i);
					Triangle t2 = condense.get(i+1);
					
					Vertex c1 = t1.getCenter();
					Vertex c2 = t2.getCenter();
					
					float width = 2;
					
					batchedTessellator2D.drawLine(c1.x, c1.y, c2.x, c2.y, width);
				}
				
			}
		}
		
		if (slither != null) {

			batchedTessellator2D.setColor(VertexColor.WHITE);
			for (int i = 0; i < slither.size() - 1; i++) {
				Triangle t1 = slither.get(i);
				
				Vertex[] corners = t1.getCorners();
				for (int j = 0; j < corners.length; j++) {
					Vertex v0 = corners[j];
					Vertex v1 = corners[MathUtil.wrap(j+1, 3)];
					
					LineSegment s = new LineSegment(v0, v1);
					Vertex c1 = s.getMiddle();
					float width = 3;
					batchedTessellator2D.drawQuad(c1.x-width, c1.y-width, c1.x+width, c1.y+width);
				}
			}
		}
		
		if (drawTwoGraph) {
			if (twoBoundaryGraph != null) {
				batchedTessellator2D.setColor(VertexColor.BROWN);
				
				for (Entry<Vertex, List<Vertex>> entry : twoBoundaryGraph.entrySet()) {
					Vertex source = entry.getKey();
					
					float width = 2;
					
					for (Vertex target : entry.getValue()) {
						batchedTessellator2D.drawLine(source.x, source.y, target.x, target.y, width);
					}
				}
			}
		}
		
		batchedTessellator2D.setColor(VertexColor.BLUE);
		
		List<Sprout> sprouts = position.getSprouts();
		
		for (Sprout sprout : sprouts) {
			float size = facadeG.sproutRadius * 2f;

			float x0 = sprout.position.x - size / 2f;
			float y0 = sprout.position.y - size / 2f;
			
			float x1 = sprout.position.x + size / 2f;
			float y1 = sprout.position.y + size / 2f;

			batchedTessellator2D.drawQuad(x0, y0, x1, y1);
		}
		
		batchedTessellator2D.setColor(VertexColor.RED);

		for (Line line : position.getLines()) {
			for (int i = 0; i < line.size() - 1; i++) {
				Vertex v0 = line.get(i);
				Vertex v1 = line.get(i + 1);

				float x0 = v0.x;
				float y0 = v0.y;
				float x1 = v1.x;
				float y1 = v1.y;

				float width = 2;

				batchedTessellator2D.drawLine(x0, y0, x1, y1, width);
			}
		}
		
		if (showLineOrientation) {
			batchedTessellator2D.setColor(VertexColor.BLUE);
			
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
				batchedTessellator2D.setColorGradient(new LinearColorGradient2D(a, VertexColor.RED, b, VertexColor.BLUE));
				
				batchedTessellator2D.drawLine(a.x, a.y, b.x, b.y, width);
			}
		}
		
		if (drawPath) {
			if (path != null) {
				
				for (int i = 0; i < path.size() - 1; i++) {
					float red = 1f / path.size() * i;
					batchedTessellator2D.setColor(new VertexColor(red, 0f, 0f));
					
					float width = 3;
					Vertex v0 = path.get(i);
					Vertex v1 = path.get(i+1);
					batchedTessellator2D.drawLine(v0.x, v0.y, v1.x, v1.y, width);
				}
				
				/*
				Vertex v0 = path.get(0);
				Vertex v1 = path.get(1);
				Vertex v2 = path.get(7);
				Vertex v3 = path.get(8);
				
				float width = 4;
				
						//boolean in = LinMath.intersect(v0.x,v0.y,v1.x,v1.y,v2.x,v2.y,v3.x,v3.y);
						//System.out.printf("%b\n", in);
				
				batchedTessellator2D.setColor(new VertexColor(0f, 0f, 1f));
				batchedTessellator2D.drawLine(v0.x, v0.y, v1.x, v1.y, width);
				batchedTessellator2D.setColor(new VertexColor(0, 1f, 0));
				batchedTessellator2D.drawLine(v2.x, v2.y, v3.x, v3.y, width);
				*/
			}
			
	
		}
		
		batchedTessellator2D.setColor(VertexColor.ORANGE);
		
		Line currentLine = facadeG.currentLine;
		
		for (int i = 0; i < currentLine.size() - 1; i++) {
			Vertex v0 = currentLine.get(i);
			Vertex v1 = currentLine.get(i + 1);

			float x0 = v0.x;
			float y0 = v0.y;
			float x1 = v1.x;
			float y1 = v1.y;

			float width = 2;

			batchedTessellator2D.drawLine(x0, y0, x1, y1, width);
		}
		
		batchedTessellator2D.setColor(VertexColor.RED);
		if (currentLine.size() > 0) {
			Vertex v1 = currentLine.getLast();
			float x1 = v1.x;
			float y1 = v1.y;
			float x2 = mouse.getMouseX();
			float y2 = mouse.getMouseY();

			float width = 2;

			batchedTessellator2D.drawLine(x1, y1, x2, y2, width);
		}
		
		batchedTessellator2D.setColor(VertexColor.YELLOW);

		for (Sprout sprout : sprouts) {
			String id = String.format("%d", sprout.id);
			
			TextBounds textBounds = arialFont.getTextBounds(id);
			
			float x = sprout.position.x - (textBounds.width - textBounds.x) / 2f;
			float y = sprout.position.y + (textBounds.height - textBounds.y) / 2f;
			y = sprout.position.y;
			
			
			arialFont.drawString(batchedTessellator2D, id, x, y);
		}
		
		if (drawEdgeIndices) {
			batchedTessellator2D.setColor(VertexColor.BLACK);
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
				TextBounds textBounds = arialFont.getTextBounds(edgeId);

				float x = pos.x;
				float y = pos.y;
				
				arialFont.drawString(batchedTessellator2D, edgeId, x, y);
			}
		}
		
		batchedTessellator2D.endBatch();
	}
	
	private void checkGLErrors() {
		int err;
		while ((err = GL11.glGetError()) != GL11.GL_NO_ERROR) {
			System.err.println("OpenGL error: " + err);
		}
	}
	
	public Mouse getMouse() {
		return mouse;
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}

	public static void main(String[] args) {
		new SproutsMain2().run();
	}
}