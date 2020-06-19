package com.sprouts.composition.view.text;

import com.sprouts.composition.Composition;
import com.sprouts.composition.event.IMouseEventListener;
import com.sprouts.composition.event.MouseEvent;
import com.sprouts.composition.text.ButtonComposition;
import com.sprouts.composition.view.BorderViewHandler;
import com.sprouts.composition.view.DrawableViewHandler;
import com.sprouts.composition.view.MaterialViewHandler;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class BasicButtonView extends ButtonView implements IMouseEventListener {

	private final DrawableViewHandler background;
	private final BorderViewHandler border;
	private final MaterialViewHandler textColor;

	private ButtonComposition button;
	
	private boolean pressedInside;
	
	private String trimmedText;

	public BasicButtonView() {
		background = new DrawableViewHandler();
		border = new BorderViewHandler();
		textColor = new MaterialViewHandler();

		registerViewHandler(background);
		registerViewHandler(border);
		registerViewHandler(textColor);
	}
	
	@Override
	protected void onBindView(ButtonComposition button) {
		if (this.button != null)
			throw new IllegalStateException("View is already bound!");
		
		this.button = button;
		
		button.addMouseEventListener(this);
		
		background.setResourceType(ButtonComposition.BACKGROUND_RESOURCE);
		border.setResourceType(ButtonComposition.BORDER_RESOURCE);
		textColor.setResourceType(ButtonComposition.TEXT_COLOR_RESOURCE);
	}
	
	@Override
	protected void onUnbindView(ButtonComposition button) {
		if (this.button == null)
			throw new IllegalStateException("View is not bound!");
		
		trimmedText = null;
		pressedInside = false;

		this.button.removeMouseEventListener(this);
		
		this.button = null;
	}
	
	@Override
	public void layoutChanged(Composition comp) {
		super.layoutChanged(comp);
		
		updateTrimmedText((ButtonComposition)comp);
	}
	
	private void updateTrimmedText(ButtonComposition button) {
		String text = button.getText();
		if (!text.isEmpty()) {
			trimmedText = button.getFont().trimText(text, button.getWidth(), TRIMMED_TEXT_ELLIPSIS);
		} else {
			trimmedText = text;
		}
	}

	@Override
	protected void drawView(Composition comp, ITessellator2D tessellator) {
		drawBackground(comp, tessellator, background);
		drawBorder(comp, tessellator, border);
		drawText((ButtonComposition)comp, tessellator);
	}
	
	private void drawText(ButtonComposition button, ITessellator2D tessellator) {
		tessellator.clearMaterial();
		textColor.applyMaterial(tessellator);

		drawAlignedText(button, tessellator, trimmedText);
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		if (pressedInside) {
			background.setResourceType(ButtonComposition.PRESSED_BACKGROUND_RESOURCE);
		} else {
			background.setResourceType(ButtonComposition.HOVERED_BACKGROUND_RESOURCE);
		}
	}

	@Override
	public void mouseExited(MouseEvent event) {
		background.setResourceType(ButtonComposition.BACKGROUND_RESOURCE);
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (button.getMouseButton().isMatching(event) && button.isInBounds(event.getX(), event.getY())) {
			background.setResourceType(ButtonComposition.PRESSED_BACKGROUND_RESOURCE);

			pressedInside = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (button.getMouseButton().isMatching(event)) {
			if (button.isInBounds(event.getX(), event.getY())) {
				button.dispatchButtonClickedEvent();

				background.setResourceType(ButtonComposition.HOVERED_BACKGROUND_RESOURCE);
			} else {
				background.setResourceType(ButtonComposition.BACKGROUND_RESOURCE);
			}
		
			pressedInside = false;
		}
	}

	@Override
	public void mouseScrolled(MouseEvent event) {
	}
}
