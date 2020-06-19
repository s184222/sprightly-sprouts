package com.sprouts.composition.view.text;

import java.awt.Rectangle;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionContext;
import com.sprouts.composition.CompositionSize;
import com.sprouts.composition.Timer;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.event.FocusEvent;
import com.sprouts.composition.event.IFocusEventListener;
import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.KeyButtonStroke;
import com.sprouts.composition.event.KeyEvent;
import com.sprouts.composition.text.editable.BasicTextCaret;
import com.sprouts.composition.text.editable.ITextCaret;
import com.sprouts.composition.text.editable.ITextCaretListener;
import com.sprouts.composition.text.editable.ITextModel;
import com.sprouts.composition.text.editable.TextFieldComposition;
import com.sprouts.composition.view.BorderViewHandler;
import com.sprouts.composition.view.DrawableViewHandler;
import com.sprouts.composition.view.MaterialViewHandler;
import com.sprouts.graphic.clip.ClipShape;
import com.sprouts.graphic.font.Font;
import com.sprouts.graphic.font.TextBounds;
import com.sprouts.graphic.tessellator2d.ITessellator2D;
import com.sprouts.math.LinMath;

public class BasicTextFieldView extends TextFieldView implements IKeyEventListener, IFocusEventListener,
                                                                 ITextCaretListener {

	private static final int PRINTABLE_CHARACTERS_START = 0x20;
	private static final int DELETE_CONTROL_CHARACTER = 0x7F;
	
	private static final int BACKSPACE_CONTROL_CHARACTER = 0x08;

	private static KeyButtonStroke sharedCopyKeyButton;
	private static KeyButtonStroke sharedCutKeyButton;
	private static KeyButtonStroke sharedPasteKeyButton;

	static {
		sharedCopyKeyButton = new KeyButtonStroke(GLFW.GLFW_KEY_C, KeyEvent.MODIFIER_CONTROL);
		sharedCutKeyButton = new KeyButtonStroke(GLFW.GLFW_KEY_X, KeyEvent.MODIFIER_CONTROL);
		sharedPasteKeyButton = new KeyButtonStroke(GLFW.GLFW_KEY_V, KeyEvent.MODIFIER_CONTROL);
	}
	
	private final DrawableViewHandler background;
	private final BorderViewHandler border;
	private final MaterialViewHandler textColor;
	private final DrawableViewHandler caretDrawable;
	private final DrawableViewHandler selectionBackground;
	private final MaterialViewHandler selectionTextColor;
	
	private TextFieldComposition textField;

	private ITextCaret caret;
	private Timer caretTimer;

	
	private String clippedText;
	private int clippedModelStart;
	private int clippedModelEnd;
	private int clippedViewOffset;
	
	private int oldCaretLocation;
	private int oldCaretPointX;
	
	public BasicTextFieldView() {
		background = new DrawableViewHandler();
		border = new BorderViewHandler();
		textColor = new MaterialViewHandler();
		
		caretDrawable = new DrawableViewHandler();
		
		selectionBackground = new DrawableViewHandler();
		selectionTextColor = new MaterialViewHandler();
		
		registerViewHandler(background);
		registerViewHandler(border);
		registerViewHandler(textColor);
		registerViewHandler(caretDrawable);
		registerViewHandler(selectionBackground);
		registerViewHandler(selectionTextColor);
	}
	
	@Override
	protected void onBindView(TextFieldComposition textField) {
		if (this.textField != null)
			throw new IllegalStateException("View is already bound!");
		
		this.textField = textField;
		
		installResources(textField);
		
		textField.addKeyEventListener(this);
		textField.addFocusEventListener(this);
		
		caret = new BasicTextCaret();
		caret.install(textField);
		caret.addTextCaretListener(this);
		
		caretTimer = new Timer(caret.getBlinkRate(), (timer) -> {
			// Blink rate might have changed.
			timer.setIntervalMillis(caret.getBlinkRate());
			
			if (caretDrawable.getResourceType() != null) {
				caretDrawable.setResourceType(null);
			} else {
				caretDrawable.setResourceType(TextFieldComposition.CARET_DRAWABLE_RESOURCE);
			}
		});
		
		background.setResourceType(TextFieldComposition.BACKGROUND_RESOURCE);
		border.setResourceType(TextFieldComposition.BORDER_RESOURCE);
		textColor.setResourceType(TextFieldComposition.TEXT_COLOR_RESOURCE);

		if (textField.isEditable() && textField.isFocused()) {
			caretDrawable.setResourceType(TextFieldComposition.CARET_DRAWABLE_RESOURCE);

			caretTimer.start();
		}
	}

	@Override
	public void onUnbindView(TextFieldComposition textField) {
		if (this.textField == null)
			throw new IllegalStateException("View is not bound!");
		
		caret.removeTextCaretListener(this);
		caret.uninstall(textField);
		caret = null;

		this.textField.removeFocusEventListener(this);
		this.textField.removeKeyEventListener(this);

		uninstallResources(this.textField);
		
		clippedText = null;
		clippedModelStart = clippedModelEnd = 0;
		clippedViewOffset = 0;
		
		this.textField = null;
	}
	
	@Override
	public void layoutChanged(Composition comp) {
		super.layoutChanged(comp);
		
		reconstructClippedModel();
	}
	
	private float expandClippedModelLeft(float availableWidth) {
		ITextModel model = textField.getTextModel();
		Font font = textField.getFont();
		
		while (availableWidth > 0.0f && clippedModelStart > 0) {
			clippedModelStart--;
			
			char c = model.getChar(clippedModelStart);
			availableWidth -= font.getCharWidth(c);
		}
		
		return availableWidth;
	}

	private float expandClippedModelRight(float availableWidth) {
		ITextModel model = textField.getTextModel();
		Font font = textField.getFont();
		
		while (availableWidth > 0.0f && clippedModelEnd < model.getLength()) {
			char c = model.getChar(clippedModelEnd);
			availableWidth -= font.getCharWidth(c);
			
			clippedModelEnd++;
		}
		
		return availableWidth;
	}
	
	private void reconstructClippedModel() {
		ITextModel model = textField.getTextModel();
		int caretLocation = LinMath.clamp(getCaretLocation(), 0, model.getLength());

		// Calculate everything assuming padding is zero and
		// width is the text field width without the padding.
		Margin padding = textField.getPadding();
		int width = textField.getWidth() - padding.left - padding.right;
		
		int caretX;
		if (caretLocation <= clippedModelStart) {
			caretX = 0;
		} else if (caretLocation >= clippedModelEnd) {
			caretX = width;
		} else {
			caretX = LinMath.clamp(oldCaretPointX - padding.left, 0, width);
		}
		
		clippedModelStart = caretLocation;
		float clippedViewOffset = expandClippedModelLeft(caretX);
		
		if (clippedViewOffset > 0) {
			caretX -= clippedViewOffset;
			clippedViewOffset = 0;
		}
		
		clippedModelEnd = caretLocation;
		float availableWidth = expandClippedModelRight(width - caretX);
		
		if (availableWidth > 0.0f && clippedModelStart > 0) {
			availableWidth += clippedViewOffset;
			clippedViewOffset = expandClippedModelLeft(availableWidth);

			availableWidth = 0;
		}
		
		// Fix alignments
		availableWidth += clippedViewOffset;
		if (availableWidth > 0) {
			switch (textField.getTextAlignment()) {
			case RIGHT:
				clippedViewOffset = availableWidth;
				break;
			case CENTER:
				clippedViewOffset = availableWidth / 2;
				break;
			case LEFT:
			default:
				clippedViewOffset = 0;
			}
		}
		
		this.clippedViewOffset = (int)clippedViewOffset;
		
		int count = clippedModelEnd - clippedModelStart;
		clippedText = (count != 0) ? model.getText(clippedModelStart, count) : "";
		
		textField.requestDraw(false);
	}
	
	private int getCaretLocation() {
		return caret.getCaretLocation();
	}
	
	private int getCaretSelectionStart() {
		return Math.min(caret.getCaretDot(), caret.getCaretMark());
	}

	private int getCaretSelectionEnd() {
		return Math.max(caret.getCaretDot(), caret.getCaretMark());
	}
	
	@Override
	public void drawView(Composition comp, ITessellator2D tessellator) {
		drawBackground(comp, tessellator, background);
		drawBorder(comp, tessellator, border);

		int selectStart = getCaretSelectionStart();
		int selectEnd = getCaretSelectionEnd();
		
		boolean hasSelection = caret.hasCaretSelection();
		
		// Only draw text if it is not all selected.
		if (!hasSelection || selectStart > clippedModelStart || selectEnd < clippedModelEnd) {
			tessellator.clearMaterial();
			textColor.applyMaterial(tessellator);
			
			if (hasSelection) {
				drawVisibleTextSegment(tessellator, clippedModelStart, selectStart);
				drawVisibleTextSegment(tessellator, selectEnd, clippedModelEnd);
			} else {
				drawVisibleTextSegment(tessellator, clippedModelStart, clippedModelEnd);
			}
		}
		
		if (hasSelection && selectEnd > clippedModelStart && selectStart < clippedModelEnd)
			drawCaretSelection(tessellator, selectStart, selectEnd);
		
		if (textField.isEditable() && textField.isFocused())
			drawCaret(tessellator, caret);
	}
	
	protected void drawVisibleTextSegment(ITessellator2D tessellator, int modelStart, int modelEnd) {
		int clipOffset = modelStart - clippedModelStart;
		if (clipOffset < 0)
			clipOffset = 0;
		
		int clipLength = modelEnd - modelStart;
		if (clipLength > clippedText.length() - clipOffset)
			clipLength = clippedText.length() - clipOffset;

		// No need to render an empty string.
		if (clipOffset >= clippedText.length() || clipLength <= 0)
			return;
		
		Margin padding = textField.getPadding();
		float x = textField.getX() + padding.left;
		float y = textField.getY() + padding.top;
		float w = textField.getWidth() - padding.left - padding.right;
		float h = textField.getHeight() - padding.top - padding.bottom;
		
		ClipShape oldClipShape = tessellator.getClipShape();
		tessellator.setClipRect(x, y, x + w, y + h);
		
		String text = clippedText;
		Font font = textField.getFont();
		
		x += clippedViewOffset;
		if (clipLength != clippedText.length()) {
			text = clippedText.substring(clipOffset, clipOffset + clipLength);
			
			if (clipOffset != 0)
				x += font.getStringWidth(clippedText.substring(0, clipOffset));
		}
		
		TextBounds textBounds = font.getTextBounds(text);
		x -= textBounds.x;
		y -= textBounds.y;
		
		font.drawString(tessellator, text, x, y);
	
		tessellator.setClipShape(oldClipShape);
	}
	
	protected void drawCaretSelection(ITessellator2D tessellator, int selectStart, int selectEnd) {
		Margin padding = textField.getPadding();
		
		int x0 = textField.getX() + padding.left;
		int x1 = textField.getX() + textField.getWidth() - padding.right;
		
		if (selectStart >= clippedModelStart) {
			Rectangle startBounds = modelToView(selectStart);
			if (startBounds.x > x0)
				x0 = startBounds.x;
		}
		
		if (selectEnd <= clippedModelEnd) {
			Rectangle endBounds = modelToView(selectEnd);
			if (endBounds.x < x1)
				x1 = endBounds.x;
		}
		
		int y = textField.getY() + padding.top;
		int h = textField.getHeight() - padding.top - padding.bottom;

		selectionBackground.draw(tessellator, x0, y, x1 - x0, h);

		tessellator.clearMaterial();
		selectionTextColor.applyMaterial(tessellator);
		drawVisibleTextSegment(tessellator, selectStart, selectEnd);
	}
	
	private void drawCaret(ITessellator2D tessellator, ITextCaret caret) {
		Rectangle bounds = modelToView(caret.getCaretDot());
		if (bounds != null) {
			int caretInsets = caret.getCaretInsets();
			int caretHeight = bounds.height - caretInsets * 2;

			if (caretHeight > 0) {
				int caretWidth = caret.getCaretWidth();
				
				int mx = textField.getX() + textField.getWidth() - caretWidth;
				
				int x = LinMath.clamp(bounds.x, textField.getX(), mx);
				int y = bounds.y + caretInsets;
				
				caretDrawable.draw(tessellator, x, y, caretWidth, caretHeight);
			}
		}
	}

	@Override
	public Rectangle modelToView(int location) {
		// Make sure we're within view
		if (location < clippedModelStart || location > clippedModelEnd)
			return null;

		Margin padding = textField.getPadding();
		
		Rectangle bounds = new Rectangle();
		bounds.x = textField.getX() + clippedViewOffset + padding.left;
		bounds.y = textField.getY() + padding.top;
		
		if (clippedText.isEmpty()) {
			CompositionSize minimumSize = textField.getMinimumSize();
			
			bounds.width = 0;
			bounds.height = minimumSize.getHeight() - padding.top - padding.bottom;
			
			return bounds;
		}

		Font font = textField.getFont();
		
		int offset = location - clippedModelStart;
		bounds.x += font.getStringWidth(clippedText.substring(0, offset));
		
		char c;
		if (offset == clippedText.length()) {
			c = clippedText.charAt(offset - 1);
			bounds.width = 0;
		} else {
			c = clippedText.charAt(offset);
			bounds.width = (int)Math.ceil(font.getCharWidth(c));
		}
		
		// We may be dealing with special characters
		// with a different height than the usual font
		// height. Get the string bounds for that.
		TextBounds charBounds = font.getTextBounds(Character.toString(c));
		bounds.height = (int)Math.ceil(charBounds.height);
		
		return bounds;
	}

	@Override
	public int viewToModel(int x, int y) {
		if (!textField.isInBounds(x, y))
			return -1;

		Margin padding = textField.getPadding();
		x -= textField.getX() + padding.left;
		
		int baseDist = x - clippedViewOffset;
		int minimumDist = Math.abs(baseDist);

		Font font = textField.getFont();
		
		int index = 0;
		while (index < clippedText.length()) {
			String text = clippedText.substring(0, index + 1);
			int width = (int)Math.ceil(font.getStringWidth(text));
			int dist = Math.abs(baseDist - width);
		
			if (dist > minimumDist)
				break;
				
			minimumDist = dist;
			index++;
		}
		
		return index + clippedModelStart;
	}
	
	@Override
	public void caretLocationChanged(int dot, int mark) {
		checkViewChange();
		
		if (caret.hasCaretSelection()) {
			selectionBackground.setResourceType(TextFieldComposition.SELECTION_BACKGROUND_RESOURCE);
			selectionTextColor.setResourceType(TextFieldComposition.SELECTION_TEXT_COLOR_RESOURCE);
		} else {
			selectionBackground.setResourceType(null);
			selectionTextColor.setResourceType(null);
		}
		
		if (textField.isEditable() && textField.isFocused() && caretTimer.isRunning()) {
			caretTimer.resetTimer();

			caretDrawable.setResourceType(TextFieldComposition.CARET_DRAWABLE_RESOURCE);
		}
		
		textField.requestDraw(false);
	}
	
	private void checkViewChange() {
		int caretLocation = getCaretLocation();
		if (!isLocationInView(caretLocation))
			reconstructClippedModel();
		
		if (caretLocation != oldCaretLocation) {
			Rectangle caretBounds = modelToView(caretLocation);
			if (caretBounds != null) {
				oldCaretPointX = caretBounds.x - textField.getX();
			} else {
				int mid = (clippedModelStart + clippedModelEnd) / 2;
				oldCaretPointX = (caretLocation < mid) ? 0 : textField.getWidth();
			}
			
			oldCaretLocation = caretLocation;
		}
	}
	
	private boolean isLocationInView(int caretLocation) {
		if (caretLocation > clippedModelStart && caretLocation < clippedModelEnd)
			return true;
		
		if (caretLocation == clippedModelStart) {
			return (clippedViewOffset >= 0);
		} else if (caretLocation == clippedModelEnd) {
			Margin padding = textField.getPadding();
			int fieldWidth = textField.getWidth() - padding.left - padding.right;
			
			int clipWidth = (int)textField.getFont().getStringWidth(clippedText);
			return (clipWidth + clippedViewOffset <= fieldWidth);
		}
		
		return false;
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		if (sharedCopyKeyButton.isMatching(event))
			copyToClipboard();
		if (sharedCutKeyButton.isMatching(event))
			cutToClipboard();
		if (sharedPasteKeyButton.isMatching(event))
			pasteFromClipboard();
	}
	
	private void copyToClipboard() {
		if (caret.hasCaretSelection()) {
			int cs = getCaretSelectionStart();
			int ce = getCaretSelectionEnd();
			
			ITextModel textModel = textField.getTextModel();
			if (cs >= 0 && ce <= textModel.getLength()) {
				String selectedText = textModel.getText(cs, ce - cs);
				CompositionContext.setClipboardString(selectedText);
			}
		}
	}
	
	private void cutToClipboard() {
		copyToClipboard();
		removeCaretSelection();
	}
	
	private void pasteFromClipboard() {
		String clipboard = CompositionContext.getClipboardString();
		if (clipboard != null && !clipboard.isEmpty()) {
			ITextModel textModel = textField.getTextModel();
			
			removeCaretSelection();
			
			int cl = getCaretLocation();
			if (cl >= 0 && cl <= textModel.getLength())
				textModel.insertText(cl, clipboard);
		}
	}

	@Override
	public void keyRepeated(KeyEvent event) {
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if (textField.isEditable() && textField.isFocused())
			handleTypedCodePoint(event.getCodePoint());
	}
	
	protected void handleTypedCodePoint(int codePoint) {
		if (Character.isBmpCodePoint(codePoint)) {
			char c = (char)codePoint;
			
			if (isTypeableCharacter(c)) {
				removeCaretSelection();
				
				if (!caret.hasCaretSelection() || !isControlCharacter(c))
					insertTypedChar(getCaretLocation(), c);
			}
		}
	}
	
	private boolean isTypeableCharacter(char c) {
		if (!isControlCharacter(c))
			return true;
		
		return c == BACKSPACE_CONTROL_CHARACTER ||
		       c == DELETE_CONTROL_CHARACTER;
	}

	private void removeCaretSelection() {
		if (caret.hasCaretSelection()) {
			int cs = getCaretSelectionStart();
			int ce = getCaretSelectionEnd();
			
			ITextModel textModel = textField.getTextModel();
			if (cs >= 0 && ce <= textModel.getLength())
				textModel.removeText(cs, ce - cs);
		}
	}
	
	private void insertTypedChar(int offset, char c) {
		ITextModel model = textField.getTextModel();
		
		if (isControlCharacter(c)) {
			switch (c) {
			case BACKSPACE_CONTROL_CHARACTER:
				if (offset > 0)
					model.removeText(offset - 1, 1);
				break;
			case DELETE_CONTROL_CHARACTER:
				if (offset < model.getLength())
					model.removeText(offset, 1);
				break;
			}
		} else {
			model.insertChar(offset, c);
		}
	}
	
	private boolean isControlCharacter(char c) {
		return (c < PRINTABLE_CHARACTERS_START || c == DELETE_CONTROL_CHARACTER);
	}

	@Override
	public void focusGained(FocusEvent event) {
		if (textField.isEditable()) {
			background.setResourceType(TextFieldComposition.FOCUSED_BACKGROUND_RESOURCE);
			border.setResourceType(TextFieldComposition.FOCUSED_BORDER_RESOURCE);
			
			caretDrawable.setResourceType(TextFieldComposition.CARET_DRAWABLE_RESOURCE);
			caretTimer.start();
		}
	}

	@Override
	public void focusLost(FocusEvent event) {
		background.setResourceType(TextFieldComposition.BACKGROUND_RESOURCE);
		border.setResourceType(TextFieldComposition.BORDER_RESOURCE);

		caretTimer.stop();
		caretDrawable.setResourceType(null);
	}
}
