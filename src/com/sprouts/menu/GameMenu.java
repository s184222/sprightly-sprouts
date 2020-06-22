package com.sprouts.menu;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.sprouts.SproutsMain;
import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.TextureOverlayDrawable;
import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.IMouseEventListener;
import com.sprouts.composition.event.KeyEvent;
import com.sprouts.composition.event.MouseEvent;
import com.sprouts.composition.layout.CompositionFill;
import com.sprouts.composition.layout.LayoutDirection;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.layout.LinearLayoutManager;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.text.LabelComposition;
import com.sprouts.composition.text.editable.TextFieldComposition;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.BatchedTessellator2D;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.math.Vec2;

import sprouts.game.GraphicalFacade;
import sprouts.game.model.Line;
import sprouts.game.model.Position;
import sprouts.game.model.Sprout;
import sprouts.game.model.Vertex;
import sprouts.game.move.MovePipeLineException;
import sprouts.game.move.pipe.MovePathResult;

public class GameMenu extends SproutsMenu {

	private static final float MOVE_LINE_WIDTH = 3.0f;
	
	/* Visible for debugging only! */
	protected final GraphicalFacade facade;
	
	protected final Font font;
	protected final Vec2 mousePos;
	
	private final TextFieldComposition textField;
	private final ButtonComposition executeButton;
	
	private final DebugGameRenderer debugRenderer;
	
	private float offsetX;
	private float offsetY;
	
	public GameMenu(SproutsMain main) {
		super(main);

		facade = new GraphicalFacade();
		
		font = getResourceManager().createFont(24.0f);
		mousePos = new Vec2();
		
		textField = new TextFieldComposition();
		executeButton = new ButtonComposition("Execute");
		
		debugRenderer = new DebugGameRenderer(this);
		
		uiLayout();
		uiEvents();
	}
	
	public void reset(int initialSproutCount) {
		facade.createFreshPosition(initialSproutCount);
	}
	
	public boolean executeMoves(List<String> rawMoves) {
		return facade.executeMoves(rawMoves);
	}

	private void uiLayout() {
		ParentComposition inputPanel = new ParentComposition(new LinearLayoutManager(LayoutDirection.VERTICAL, 20));
		inputPanel.setMinimumSize(new CompositionSize(300, 150));
		
		LayoutSpecification spec = new LayoutSpecification();
		spec.setHorizontalFill(CompositionFill.FILL_REMAINING);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);

		inputPanel.add(createFieldPanel(), spec);
	
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_BOTTOM);
		executeButton.setPadding(new Margin(30, 30, 10, 10));
		inputPanel.add(wrapOverlay(executeButton), spec);

		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_LEFT);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_TOP);
		
		setPadding(new Margin(50));
		add(inputPanel, spec);
	}
	
	private Composition createFieldPanel() {
		ParentComposition fieldPanel = new ParentComposition(new LinearLayoutManager(LayoutDirection.HORIZONTAL, 10));

		LayoutSpecification spec = new LayoutSpecification();
		spec.setVerticalFill(CompositionFill.FILL_MINIMUM);
		spec.setHorizontalAlignment(LayoutSpecification.ALIGN_CENTER);
		spec.setVerticalAlignment(LayoutSpecification.ALIGN_CENTER);
		
		spec.setHorizontalFill(CompositionFill.FILL_MINIMUM);
		fieldPanel.add(new LabelComposition("Move:"), spec);
		
		spec.setHorizontalFill(CompositionFill.FILL_REMAINING);
		fieldPanel.add(textField, spec);
		
		textField.setBackground(new TextureOverlayDrawable(main.getPostTexture(), textField.getBackground()));
		
		return fieldPanel;
	}
	
	private void uiEvents() {
		addMouseEventListener(new IMouseEventListener() {
			@Override
			public void mouseScrolled(MouseEvent event) {
			}
			
			@Override
			public void mouseReleased(MouseEvent event) {
				finishMove(viewToWorld(event.getX(), event.getY()));
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				startMove(viewToWorld(event.getX(), event.getY()));
			}
			
			@Override
			public void mouseMoved(MouseEvent event) {
				mousePos.set(event.getX(), event.getY());
			}
			
			@Override
			public void mouseExited(MouseEvent event) {
			}
			
			@Override
			public void mouseEntered(MouseEvent event) {
			}
			
			@Override
			public void mouseDragged(MouseEvent event) {
				dragMove(viewToWorld(event.getX(), event.getY()));
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
				if (event.getKeyCode() == GLFW.GLFW_KEY_ESCAPE)
					main.setMenu(new MainSproutsMenu(main));
			}
		});
		
		executeButton.addButtonListener((source) -> {
			executeMove();
		});
		
		textField.addKeyEventListener(new IKeyEventListener() {
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
				if (event.getKeyCode() == GLFW.GLFW_KEY_ENTER)
					executeMove();
			}
		});
	}
	
	private void executeMove() {
		try {
			MovePathResult result = facade.generateMove(textField.getText());
			
			if (result != null && result.line != null) {
				String move = facade.executeLine(result.line);

				if (move != null) {
					onMoveExecuted(move);

					textField.setText("");
				}

				debugRenderer.onMoveExecuted(result);
			}
		} catch (MovePipeLineException ignore) {
		}
	}
	
	protected void startMove(Vertex vertex) {
		facade.startMove(vertex.x, vertex.y);
	}

	protected void finishMove(Vertex vertex) {
		String move = facade.finishMove(vertex.x, vertex.y);
		if (move != null)
			onMoveExecuted(move);
	}

	protected void dragMove(Vertex vertex) {
		facade.dragMove(vertex.x, vertex.y);
	}
	
	protected void onMoveExecuted(String move) {
		if (facade.isGameOver())
			main.setMenu(new GameOverSproutsMenu(main, null));
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		// This is only temporary and will be replaced by projection.
		Vertex worldCenter = getWorldCenter();
		offsetX = width / 2 - (float) worldCenter.x;
		offsetY = height / 2 - (float) worldCenter.y;
	}
	
	public Vertex getWorldCenter() {
		float cx = 0.0f;
		float cy = 0.0f;
		
		List<Vertex> corners = facade.getPosition().getOuterCorners();
		
		int cornerCount = corners.size();
		
		if (cornerCount > 0) {
			for (Vertex corner : corners) {
				cx += corner.x;
				cy += corner.y;
			}
			
			cx /= cornerCount;
			cy /= cornerCount;
		}
		
		return new Vertex(cx, cy);
	}
	
	@Override
	public void update() {
	}

	@Override
	public void drawBackground(BatchedTessellator2D tessellator) {
		tessellator.beginBatch();
		
		debugRenderer.drawBackground(tessellator);
		
		drawGameState(tessellator);
		drawCurrentMove(tessellator, facade.currentLine);

		debugRenderer.drawForeground(tessellator);
		
		tessellator.endBatch();
	}
	
	private void drawGameState(BatchedTessellator2D tessellator) {
		Position position = facade.getPosition();
		
		tessellator.setColor(VertexColor.LIME_GREEN);
		for (Line line : position.getLines())
			drawSproutLine(tessellator, line);
		
		for (Sprout sprout : position.getSprouts())
			drawSprout(tessellator, sprout);
	}
	
	private void drawSprout(ITessellator2D tessellator, Sprout sprout) {
		Vec2 pos = worldToView(sprout.position);
	
		float x0 = pos.x - facade.sproutRadius * 2.0f;
		float y0 = pos.y - facade.sproutRadius * 2.0f;
		float x1 = pos.x + facade.sproutRadius * 2.0f;
		float y1 = pos.y + facade.sproutRadius * 2.0f;

		tessellator.setColor(VertexColor.WHITE);
		tessellator.setTextureRegion(main.getFlowerTexture(sprout.getNeighbourCount()));
		tessellator.drawQuad(x0, y0, x1, y1);
		tessellator.setTextureRegion(null);
		
		if (mousePos.x >= x0 && mousePos.y >= y0 && mousePos.x < x1 && mousePos.y < y1) {
			String idText = Integer.toString(sprout.id);
			TextBounds textBounds = font.getTextBounds(idText);
			
			float tx = pos.x - textBounds.width  * 0.5f - textBounds.x;
			float ty = pos.y - textBounds.height * 0.5f - textBounds.y;
			
			tessellator.setColor(VertexColor.BLACK);
			font.drawString(tessellator, idText, tx, ty);
		}
	}
	
	private void drawCurrentMove(BatchedTessellator2D tessellator, Line currentLine) {
		tessellator.setColor(VertexColor.ORANGE);
		drawSproutLine(tessellator, currentLine);
		
		// Draw line from last vertex to the current mouse position.
		if (!currentLine.isEmpty()) {
			Vec2 pos = worldToView(currentLine.getLast());

			tessellator.setColor(VertexColor.RED);
			tessellator.drawLine(pos, mousePos, MOVE_LINE_WIDTH);
		}
	}
	
	public void drawSproutLine(ITessellator2D tessellator, Line line) {
		for (int i = 1; i < line.size(); i++) {
			Vec2 v0 = worldToView(line.get(i - 1));
			Vec2 v1 = worldToView(line.get(i));
			
			tessellator.drawLine(v0, v1, MOVE_LINE_WIDTH);
		}
	}

	/* The following methods are visible for debugging */

	protected Vec2 worldToView(Vertex vertex) {
		return worldToView((float) vertex.x, (float) vertex.y);
	}

	protected Vertex viewToWorld(Vec2 pos) {
		return viewToWorld(pos.x, pos.y);
	}
	
	protected Vec2 worldToView(float wx, float wy) {
		return new Vec2(wx + offsetX, wy + offsetY);
	}

	protected Vertex viewToWorld(float vx, float vy) {
		return new Vertex(vx - offsetX, vy - offsetY);
	}
}
