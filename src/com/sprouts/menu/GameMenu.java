package com.sprouts.menu;

import java.util.List;

import com.sprouts.SproutsMain;
import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.ParentComposition;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.TextureOverlayDrawable;
import com.sprouts.composition.event.IMouseEventListener;
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
import sprouts.game.move.pipe.MovePathResult;

public class GameMenu extends SproutsMenu {

	private static final float MOVE_LINE_WIDTH = 3.0f;
	
	/* Visible for debugging only! */
	protected GraphicalFacade facadeG;
	
	protected final Font font;
	protected final Vec2 mousePos;
	
	private final TextFieldComposition textField;
	private final ButtonComposition executeButton;
	
	private final DebugGameRenderer debugRenderer;
	
	private float offsetX;
	private float offsetY;
	
	public GameMenu(SproutsMain main) {
		super(main);
		
		facadeG = new GraphicalFacade();
		facadeG.createFreshPosition(8);
		
		font = getResourceManager().createFont(56.0f);
		mousePos = new Vec2();
		
		textField = new TextFieldComposition();
		executeButton = new ButtonComposition("Execute");
		
		debugRenderer = new DebugGameRenderer(this);
		
		uiLayout();
		uiEvents();
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
				Vertex vertex = viewToWorld(event.getX(), event.getY());
				facadeG.touchUp(vertex.x, vertex.y);
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				Vertex vertex = viewToWorld(event.getX(), event.getY());
				facadeG.touchDown(vertex.x, vertex.y);
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
				Vertex vertex = viewToWorld(event.getX(), event.getY());
				facadeG.touchDragged(vertex.x, vertex.y);
			}
		});
		
		executeButton.addButtonListener((source) -> {
			MovePathResult result = facadeG.generateMove(textField.getText());
			
			if (result != null) {
				facadeG.executeLine(result.line);
				textField.setText("");
			
				debugRenderer.onMoveExecuted(result);
			}
		});
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		
		// This is only temporary and will be replaced by projection.
		Vertex worldCenter = getWorldCenter();
		offsetX = width / 2 - worldCenter.x;
		offsetY = height / 2 - worldCenter.y;
	}
	
	public Vertex getWorldCenter() {
		float cx = 0.0f;
		float cy = 0.0f;
		
		List<Vertex> corners = facadeG.getPosition().getOuterCorners();
		
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
		drawCurrentMove(tessellator, facadeG.currentLine);

		debugRenderer.drawForeground(tessellator);
		
		tessellator.endBatch();
	}
	
	private void drawGameState(BatchedTessellator2D tessellator) {
		Position position = facadeG.getPosition();
		
		tessellator.setColor(VertexColor.RED);
		for (Line line : position.getLines())
			drawSproutLine(tessellator, line);
		
		for (Sprout sprout : position.getSprouts())
			drawSprout(tessellator, sprout);
	}
	
	private void drawSprout(ITessellator2D tessellator, Sprout sprout) {
		Vec2 pos = worldToView(sprout.position);
	
		float x0 = pos.x - facadeG.sproutRadius;
		float y0 = pos.y - facadeG.sproutRadius;
		float x1 = pos.x + facadeG.sproutRadius;
		float y1 = pos.y + facadeG.sproutRadius;

		tessellator.setColor(VertexColor.BLUE);
		tessellator.drawQuad(x0, y0, x1, y1);
		
		String idText = Integer.toString(sprout.id);
		TextBounds textBounds = font.getTextBounds(idText);
		
		float tx = pos.x - textBounds.width  * 0.5f - textBounds.x;
		float ty = pos.y - textBounds.height * 0.5f - textBounds.y;
		
		tessellator.setColor(VertexColor.WHITE);
		font.drawString(tessellator, idText, tx, ty);
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
		return worldToView(vertex.x, vertex.y);
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
