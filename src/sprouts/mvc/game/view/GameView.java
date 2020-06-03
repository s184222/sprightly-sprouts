package sprouts.mvc.game.view;

import java.util.List;

import sprouts.mvc.View;
import sprouts.mvc.game.controller.GameController;
import sprouts.mvc.game.model.representation.graphical.Edge;
import sprouts.mvc.game.model.representation.graphical.GameModel;
import sprouts.mvc.game.model.representation.graphical.GraphicalFacade;
import sprouts.mvc.game.model.representation.graphical.Line;
import sprouts.mvc.game.model.representation.graphical.LineSegment;
import sprouts.mvc.game.model.representation.graphical.Sprout;
import sprouts.mvc.game.model.representation.graphical.Triangle;
import sprouts.mvc.game.model.representation.graphical.Vertex;

public class GameView extends View {
	
	// @TODO: reimplement in the sprouts engine.
	
	public GameController controller;
	public GameModel model;
	
	/*

	public FitViewport viewport;

	private OrthographicCamera ortho;
	private Camera cam;
	
	private BitmapFont font;
	private GlyphLayout glyph;
	
	private ShapeRenderer shape;
	private SpriteBatch batch;
	
	public List<Triangle> triangles;
	
	public boolean drawEdgeIndices = false;
	public boolean drawTriangles = true;
	
	public GameView() {
		int viewportWidth = 640;
		int viewportHeight = 480;
		
		font = new BitmapFont();
		glyph = new GlyphLayout();

		batch = new SpriteBatch();
		
		cam = new Camera();
		cam.zoomToNow(0.8f);
		
		ortho = new OrthographicCamera();
		viewport = new FitViewport(viewportWidth, viewportHeight, ortho);

		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
	}
	
	
	@Override
	public void draw() {
		Gdx.gl.glClearColor(.9f, .9f, .9f, .9f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		GraphicalFacade facade = model.graphicalFacade;
		
		Vector2 mouse = controller.mouse;
		Vector2 world = viewport.unproject(mouse);
		
		ortho.position.set(cam.cx, cam.cy, 0);
		ortho.zoom = cam.cz;

		viewport.apply(true);
		shape.setProjectionMatrix(viewport.getCamera().combined);
		shape.begin();
		
		shape.set(ShapeType.Filled);
		
		if (drawTriangles && triangles != null) {
			shape.setColor(new Color(.4f, 1f, .4f, 1f));
			for (Triangle triangle : triangles) {
				Vertex p1 = triangle.p1;
				Vertex p2 = triangle.p2;
				Vertex p3 = triangle.p3;
				
				shape.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
			}
			
			shape.setColor(new Color(.3f, .3f, .3f, 1f));
			for (Triangle triangle : triangles) {
				Vertex p1 = triangle.p1;
				Vertex p2 = triangle.p2;
				Vertex p3 = triangle.p3;
				
				float width = 2;
				
				shape.rectLine(p1.x, p1.y, p2.x, p2.y, width);
				shape.rectLine(p2.x, p2.y, p3.x, p3.y, width);
				shape.rectLine(p3.x, p3.y, p1.x, p1.y, width);
			}
			
			Triangle selected = null;
			for (Triangle triangle : triangles) {
				Vertex p1 = triangle.p1;
				Vertex p2 = triangle.p2;
				Vertex p3 = triangle.p3;
				
				
				if (Intersector.isPointInTriangle(world.x, world.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)) {
					selected = triangle;
					break;
				}
			}
			
			if (selected != null) {
				shape.setColor(Color.FOREST);
				
				Vertex p1 = selected.p1;
				Vertex p2 = selected.p2;
				Vertex p3 = selected.p3;
				shape.triangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);

			}
		}

		shape.setColor(Color.BLUE);
		for (Sprout sprout : facade.sprouts) {
			float x = sprout.position.x;
			float y = sprout.position.y;

			shape.circle(x, y, facade.sproutRadius);
		}
		
		shape.setColor(Color.RED);

		for (Line line : facade.lines) {
			for (int i = 0; i < line.size() - 1; i++) {
				Vertex v1 = line.get(i);
				Vertex v2 = line.get(i + 1);

				float x1 = v1.x;
				float y1 = v1.y;
				float x2 = v2.x;
				float y2 = v2.y;

				float width = 2;

				shape.rectLine(x1, y1, x2, y2, width);
			}
		}
	
		boolean showLineOrientation = false;
		if (showLineOrientation) {
			shape.setColor(Color.BLUE);
			Vector2 rotater = new Vector2();
			for (Edge edge : facade.edges) {
				float rotate = 90;
				float scale = 10;
				
				LineSegment segment = facade.getQuarterSegment(edge);
				rotater.set(segment.to.x, segment.to.y);
				rotater.sub(segment.from.x, segment.from.y);
				rotater.nor().rotate(rotate).scl(scale);
				
				Vector2 a = rotater.cpy().add(segment.from.x, segment.from.y);
				Vector2 b = rotater.cpy().add(segment.to.x, segment.to.y);
				
				float width = 5f;
				shape.rectLine(a.x, a.y, b.x, b.y, width, Color.RED, Color.WHITE);
			}
		}
		
		{
			shape.setColor(Color.ORANGE);
			
			for (int i = 0; i < facade.currentLine.size() - 1; i++) {
				Vertex v1 = facade.currentLine.get(i);
				Vertex v2 = facade.currentLine.get(i + 1);

				float x1 = v1.x;
				float y1 = v1.y;
				float x2 = v2.x;
				float y2 = v2.y;

				float width = 2;

				shape.rectLine(x1, y1, x2, y2, width);
			}
			
			shape.setColor(Color.RED);

			if (facade.currentLine.size() > 0) {
				Vertex v1 = facade.currentLine.get(facade.currentLine.size() - 1);
				float x1 = v1.x;
				float y1 = v1.y;
				float x2 = controller.mouse.x;
				float y2 = controller.mouse.y;

				float width = 2;

				shape.rectLine(x1, y1, x2, y2, width);
			}
		}
		
		shape.end();
		
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		
		font.setColor(Color.CORAL);
		for (Sprout sprout : facade.sprouts) {
			String str = String.format("%d", sprout.id);
			glyph.setText(font, str);
			
			float x = sprout.position.x - glyph.width / 2f;
			float y = sprout.position.y + glyph.height / 2f;
			
			font.draw(batch, glyph, x, y);
		}
		
		Vector2 rotater = new Vector2();
		if (drawEdgeIndices) {
			font.setColor(Color.WHITE);
			for (Edge edge : facade.edges) {
				float rotate = 90;
				float scale = 10;
				
				LineSegment segment = edge.line.size() > 2 ? facade.getQuarterSegment(edge) : facade.getMiddleSegment(edge);
				rotater.set(segment.to.x, segment.to.y);
				rotater.sub(segment.from.x, segment.from.y);
				rotater.nor().rotate(rotate).scl(scale);
				
				Vector2 position = new Vector2();
				position.set(segment.to.x, segment.to.y);
				position.sub(segment.from.x, segment.from.y);
				position.scl(0.6f);
				position.add(segment.from.x, segment.from.y);
				position.add(rotater);
				
				String edgeId = String.format("%d", edge.id);
				glyph.setText(font, edgeId);
				
				float x = position.x - glyph.width / 2f;
				float y = position.y + glyph.height / 2f;
				
				font.draw(batch, glyph, x, y);
			}
		}
		
		if (drawTriangles && triangles != null) {
			for (Triangle triangle : triangles) {
				Vertex p1 = triangle.p1;
				Vertex p2 = triangle.p2;
				Vertex p3 = triangle.p3;
				
				{
					
					float rotate = -90;
					float scale = 10;
					
					Vertex from = p1;
					Vertex to = p2;
					
					rotater.set(to.x, to.y);
					rotater.sub(from.x, from.y);
					rotater.nor().rotate(rotate).scl(scale);
					
					Vector2 position = new Vector2();
					position.set(to.x, to.y);
					position.sub(from.x, from.y);
					position.scl(0.6f);
					position.add(from.x, from.y);
					position.add(rotater);
					
					String triangleIndex = String.format("%d", 1);
					glyph.setText(font, triangleIndex);
					
					float x = position.x - glyph.width / 2f;
					float y = position.y + glyph.height / 2f;
					
					//font.draw(batch, glyph, x, y);
				}
				
				{
					
					float rotate = -90;
					float scale = 10;
					
					Vertex from = p2;
					Vertex to = p3;
					
					rotater.set(to.x, to.y);
					rotater.sub(from.x, from.y);
					rotater.nor().rotate(rotate).scl(scale);
					
					Vector2 position = new Vector2();
					position.set(to.x, to.y);
					position.sub(from.x, from.y);
					position.scl(0.6f);
					position.add(from.x, from.y);
					position.add(rotater);
					
					String triangleIndex = String.format("%d", 2);
					glyph.setText(font, triangleIndex);
					
					float x = position.x - glyph.width / 2f;
					float y = position.y + glyph.height / 2f;
					
					//font.draw(batch, glyph, x, y);
				}
				
				{
					
					float rotate = -90;
					float scale = 10;
					
					Vertex from = p3;
					Vertex to = p1;
					
					rotater.set(to.x, to.y);
					rotater.sub(from.x, from.y);
					rotater.nor().rotate(rotate).scl(scale);
					
					Vector2 position = new Vector2();
					position.set(to.x, to.y);
					position.sub(from.x, from.y);
					position.scl(0.6f);
					position.add(from.x, from.y);
					position.add(rotater);
					
					String triangleIndex = String.format("%d", 3);
					glyph.setText(font, triangleIndex);
					
					float x = position.x - glyph.width / 2f;
					float y = position.y + glyph.height / 2f;
					
					//font.draw(batch, glyph, x, y);
				}
			}
		}
		
		
		batch.end();
	}


	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	*/
}
