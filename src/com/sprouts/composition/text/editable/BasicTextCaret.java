package com.sprouts.composition.text.editable;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.Composition;
import com.sprouts.composition.event.Event;
import com.sprouts.composition.event.IButtonStroke;
import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.IMouseEventListener;
import com.sprouts.composition.event.KeyButtonStroke;
import com.sprouts.composition.event.KeyEvent;
import com.sprouts.composition.event.MouseButtonStroke;
import com.sprouts.composition.event.MouseEvent;
import com.sprouts.composition.view.text.EditableTextCompositionView;
import com.sprouts.math.LinMath;

/**
 * A basic text caret used by the user to navigate the text area on which this
 * caret is installed. This text caret will handle almost all navigational
 * properties of a modern text caret. This includes navigating by arrow keys,
 * selection modifiers and much more. To see all the functionality of the user
 * selection and navigational tools, see the setter-methods for these inputs.
 * <br><br>
 * The caret itself is defined by two numbers; the dot and the mark. To query
 * these, use the methods {@link #getCaretDot()} and {@link #getCaretMark()} 
 * respectively. The caret dot represents the location in the document at which
 * the cursor itself is located, usually represented by a vertical line. This
 * vertical line will always be located just before the index of the character
 * that it points to. For example {@code dot = 0} would represent the position
 * before the first character in the view. If the user decides to create a
 * selection, this will be represented by the {@code mark}. The selection itself
 * is <i>not</i> drawn in this text caret and should therefore be handled 
 * elsewhere. To see the full functionality of the {@code mark} and how it is
 * activated by the user see {@link #setSelectionModifiers(int)}.
 * <br><br>
 * To install this text caret onto an {@code EditableTextComposition} one can
 * use the following code-snippet:
 * <pre>
 * ...
 * {@literal //} Initialize a new text caret
 * BasicTextCaret caret = new BasicTextCaret();
 * {@literal //} Set caret properties. For example the width = 4
 * caret.setWidth(4);
 * {@literal //} Install the caret on the specified
 * {@literal //} editable text composition
 * textComposition.setCaret(caret);
 * ...
 * </pre>
 * The above code will automatically set and install the caret of the specified
 * {@link com.g4mesoft.composition.text.editable.EditableTextComposition
 * EditableTextComposition}.
 * 
 * @author Christian
 * 
 * @see #setNavigateForwardButton(IButtonStroke)
 * @see #setNavigateBackwardButton(IButtonStroke)
 * @see #setSelectionModifiers(int)
 * @see #setSelectAllButton(IButtonStroke)
 * @see #setHomeButton(IButtonStroke)
 * @see #setEndButton(IButtonStroke)
 * @see #setNavigateMouseButton(MouseButtonStroke)
 */
public class BasicTextCaret implements ITextCaret, ITextModelListener,  ICompositionModelListener,
                                       IMouseEventListener, IKeyEventListener {

	private static final int DEFAULT_BLINK_RATE = 500;
	private static final int DEFAULT_CARET_WIDTH = 2;
	private static final int DEFAULT_CARET_INSETS = 1;
	
	private EditableTextComposition textComp;
	private ITextModel textModel;
	
	private int dot;
	private int mark;
	
	private int caretInsets;
	private int caretWidth;
	
	private int blinkRate;
	
	private IButtonStroke navigateForwardButton;
	private IButtonStroke navigateBackwardButton;
	
	private int selectionModifiers;
	private IButtonStroke selectAllButton;
	
	private IButtonStroke homeButton;
	private IButtonStroke endButton;
	
	private MouseButtonStroke navigateMouseButton;

	private final List<ITextCaretListener> caretListeners;
	
	public BasicTextCaret() {
		dot = mark = 0;
		
		caretWidth = DEFAULT_CARET_WIDTH;
		caretInsets = DEFAULT_CARET_INSETS;
		
		blinkRate = DEFAULT_BLINK_RATE;
		
		navigateForwardButton = new KeyButtonStroke(GLFW.GLFW_KEY_RIGHT);
		navigateBackwardButton = new KeyButtonStroke(GLFW.GLFW_KEY_LEFT);
		
		selectionModifiers = Event.MODIFIER_SHIFT;
		selectAllButton = new KeyButtonStroke(GLFW.GLFW_KEY_A, Event.MODIFIER_CONTROL);
		
		homeButton = new KeyButtonStroke(GLFW.GLFW_KEY_HOME);
		endButton = new KeyButtonStroke(GLFW.GLFW_KEY_END);
		
		navigateMouseButton = new MouseButtonStroke(MouseEvent.BUTTON_LEFT);
	
		caretListeners = new ArrayList<ITextCaretListener>(1);
	}
	
	@Override
	public void install(EditableTextComposition textComposition) {
		if (this.textComp != null)
			throw new IllegalStateException("Caret already bound!");
	
		this.textComp = textComposition;
		
		installTextModel(textComposition.getTextModel());

		textComposition.addModelListener(this);
		textComposition.addMouseEventListener(this);
		textComposition.addKeyEventListener(this);
	}
	
	private void installTextModel(ITextModel textModel) {
		this.textModel = textModel;
		
		textModel.addTextModelListener(this);
		
		dot = textModel.getLength();
		mark = dot;
	}

	@Override
	public void uninstall(EditableTextComposition textComposition) {
		if (this.textComp == null)
			throw new IllegalStateException("Caret not bound!");
	
		this.textComp.removeModelListener(this);
		this.textComp.removeMouseEventListener(this);
		this.textComp.removeKeyEventListener(this);
		
		uninstallTextModel(this.textComp.getTextModel());

		this.textComp = null;
	}
	
	private void uninstallTextModel(ITextModel textModel) {
		textModel.removeTextModelListener(this);
		
		this.textModel = null;
	}
	
	@Override
	public void modelChanged(Composition owner) {
		if (textComp == owner) {
			uninstallTextModel(textModel);
			installTextModel(textComp.getTextModel());
		}
	}
	
	@Override
	public void addTextCaretListener(ITextCaretListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener is null!");
		
		caretListeners.add(listener);
	}

	@Override
	public void removeTextCaretListener(ITextCaretListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener is null!");

		caretListeners.remove(listener);
	}
	
	private void dispatchCaretLocationChangedEvent() {
		for (ITextCaretListener caretListener : caretListeners)
			caretListener.caretLocationChanged(dot, mark);
	}

	/**
	 * Navigates the model either forward or backward depending on the given
	 * {@code backward} parameter. If the parameter is true, then the navigation
	 * will be backward, otherwise it will be forward. If the caret currently
	 * has a selection, but the selectionModifier is not active, then the cursor
	 * will be set to the backward / forward location of the selection, again
	 * depending on the backward parameter.
	 * 
	 * @param backward - a parameter defining whether the navigation should be
	 *                   backward or forward.
	 * @param selectionModifier - whether the selection modifier is held.
	 */
	protected void navigateStep(boolean backward, boolean selectionModifier) {
		if (!selectionModifier && hasCaretSelection()) {
			if (backward) {
				setCaretLocation(Math.min(dot, mark));
			} else {
				setCaretLocation(Math.max(dot, mark));
			}
		} else {
			navigateToLocation(backward ? (dot - 1) : (dot + 1), selectionModifier);
		}
	}
	
	/**
	 * Navigates the cursor to the given location. If the selectionModifier is
	 * currently active, this function will only set the dot location.
	 * 
	 * @param location - the new location of the caret, or the dot, if the
	 *                   selectionModifier is not active.
	 * @param selectionModifier - whehter the selection modifier is held.
	 */
	protected void navigateToLocation(int location, boolean selectionModifier) {
		if (selectionModifier) {
			setCaretDot(location);
		} else {
			setCaretLocation(location);
		}
	}
	
	/**
	 * Navigates the caret to the specified point.
	 * 
	 * @param navX - the x-position of the navigation point
	 * @param navY - the y-position of the navigation point
	 */
	protected void navigateToPoint(int navX, int navY, boolean selectionModifier) {
		EditableTextCompositionView editableView = textComp.getView();
		
		int indexOffset = 0;
		if (selectionModifier) {
			if (navX < textComp.getX()) {
				navX = textComp.getX();
				indexOffset = -1;
			} else if (navX > textComp.getX() + textComp.getWidth() - 1) {
				navX = textComp.getX() + textComp.getWidth() - 1;
				indexOffset = 1;
			}
		}
		
		if (editableView != null && textComp.isInBounds(navX, navY)) {
			int navigationIndex = editableView.viewToModel(navX, navY);
			
			if (navigationIndex != -1)
				navigateToLocation(navigationIndex + indexOffset, selectionModifier);
		}
	}
	
	/**
	 * Calculates a bounded location in the text area, meaning that if the given
	 * location is outside of the text model, then it will be clamped to ensure
	 * a valid caret location.
	 * 
	 * @param location - the location to be clamped within the text model bounds
	 * 
	 * @return A bounded location found by clamping the given {@code location}.
	 */
	private int getBoundedLocation(int location) {
		if (location <= 0) {
			return 0;
		} else {
			ITextModel model = textComp.getTextModel();
			if (location > model.getLength())
				return model.getLength();
		}
		
		return location;
	}
	
	@Override
	public int getCaretLocation() {
		return dot;
	}
	
	@Override
	public void setCaretLocation(int location) {
		location = getBoundedLocation(location);
		
		if (location != dot || location != mark) {
			dot = mark = location;

			dispatchCaretLocationChangedEvent();
		}
	}
	
	@Override
	public int getCaretDot() {
		return dot;
	}
	
	@Override
	public void setCaretDot(int dot) {
		dot = getBoundedLocation(dot);
		
		if (dot != this.dot) {
			this.dot = dot;
			
			dispatchCaretLocationChangedEvent();
		}
	}

	@Override
	public int getCaretMark() {
		return mark;
	}
	
	@Override
	public void setCaretMark(int mark) {
		mark = getBoundedLocation(mark);
		
		if (mark != this.mark) {
			this.mark = mark;
			
			dispatchCaretLocationChangedEvent();
		}
	}

	/**
	 * Sets the selection of the caret to the specified dot and mark locations.
	 * 
	 * @param dot - the dot at which the selection ends
	 * @param mark - the mark at which the selection begins.
	 */
	private void setSelection(int dot, int mark) {
		dot = getBoundedLocation(dot);
		mark = getBoundedLocation(mark);
	
		if (dot != this.dot || mark != this.mark) {
			this.dot = dot;
			this.mark = mark;
			
			dispatchCaretLocationChangedEvent();
		}
	}
	
	@Override
	public boolean hasCaretSelection() {
		return (dot != mark);
	}

	@Override
	public void textInserted(ITextModel model, int offset, int count) {
		int dot = this.dot;
		int mark = this.mark;
		
		if (offset <= dot)
			dot += count;
		if (offset <= mark)
			mark += count;
	
		setSelection(dot, mark);
	}

	@Override
	public void textRemoved(ITextModel model, int offset, int count) {
		int dot = this.dot;
		int mark = this.mark;
		
		if (offset + count < dot) {
			dot -= count;
		} else if (offset < dot) {
			dot = offset;
		}

		if (offset + count < mark) {
			mark -= count;
		} else if (offset < mark) {
			mark = offset;
		}

		setSelection(dot, mark);
	}

	@Override
	public int getBlinkRate() {
		return blinkRate;
	}
	
	/**
	 * Sets the blink rate to the specified amount of milliseconds. A full cycle
	 * of the caret blinking will be double the amount given in this method. The
	 * default value of this parameter is {@code 500}ms.
	 * 
	 * @param blinkRate - the new blink rate of this caret in milliseconds.
	 * 
	 * @throws IllegalArgumentException if the blinkRate is non-positive.
	 */
	@Override
	public void setBlinkRate(int blinkRate) {
		if (blinkRate <= 0)
			throw new IllegalArgumentException("blinkRate <= 0");
		
		this.blinkRate = blinkRate;
	}

	/**
	 * @return The width of the graphical caret.
	 */
	@Override
	public int getCaretWidth() {
		return caretWidth;
	}
	
	/**
	 * Sets the graphical width of the caret to the specified width. The default
	 * value of this parameter is {@code 2}.
	 * 
	 * @param width - the new width of the graphical caret.
	 * 
	 * @throws IllegalArgumentException if the given {@code width} is negative.
	 */
	@Override
	public void setCaretWidth(int width) {
		if (width < 0)
			throw new IllegalArgumentException("Caret width is negative!");

		caretWidth = width;
	}
	
	/**
	 * @return The insets of the graphical caret.
	 */
	@Override
	public int getCaretInsets() {
		return caretInsets;
	}
	
	/**
	 * Sets the caret insets to the specified amount. The insets define the
	 * amount of pixels on the top and bottom of the caret that wont be rendered
	 * when painting the caret. If one wishes to disable these insets, they
	 * should set this value to zero. If this value is too large, the caret will
	 * not be rendered. The default value of this parameter is {@code 0}.
	 * 
	 * @param insets - a non-negative integer defining the new caret insets
	 */
	@Override
	public void setCaretInsets(int insets) {
		if (insets < 0)
			throw new IllegalArgumentException("Caret insets are negative!");
		
		caretInsets = insets;
	}
	
	/**
	 * Sets the navigate forward button of this text caret. The navigate forward
	 * button allows the user to move the caret forward in the text model. The
	 * default button is the RIGHT ARROW key button.
	 * 
	 * @param navigateForwardButton - the new navigate forward button
	 * 
	 * @throws IllegalArgumentException if the given {@code navigateForwardButton}
	 *                                  is null
	 */
	public void setNavigateForwardButton(IButtonStroke navigateForwardButton) {
		if (navigateForwardButton == null)
			throw new IllegalArgumentException("navigateForwardButton is null");
		this.navigateForwardButton = navigateForwardButton;
	}

	/**
	 * Sets the navigate backward button of this text caret. The navigate backward
	 * button allows the user to move the caret backward in the text model. The
	 * default button is the LEFT ARROW key button.
	 * 
	 * @param navigateBackwardButton - the new navigate backward button
	 * 
	 * @throws IllegalArgumentException if the given {@code navigateBackwardButton}
	 *                                  is null
	 */
	public void setNavigateBackwardButton(IButtonStroke navigateBackwardButton) {
		if (navigateBackwardButton == null)
			throw new IllegalArgumentException("navigateBackwardButton is null");
		this.navigateBackwardButton = navigateBackwardButton;
	}

	/**
	 * Sets the selection modifiers of this text caret. The selection modifiers
	 * allows the user to make selections. In short, when the user holds these
	 * modifiers, the caret will remember the caret location when navigating.
	 * This <i>mark</i> will then serve as a starting point for the caret selection.
	 * When the caret is then moved by navigating forward or backward, the mark
	 * will stay put and form a text selection. The default value of the modifiers
	 * is Event.MODIFIER_SHIFT.
	 * 
	 * @param selectionModifiers - the new selection modifiers
	 * 
	 * @throws IllegalArgumentException if the given parameter contains invalid
	 *                                  modifiers.
	 */
	public void setSelectionModifiers(int selectionModifiers) {
		if ((selectionModifiers & (~Event.ALL_MODIFIERS)) != 0)
			throw new IllegalArgumentException("Invalid modifiers: " + Integer.toHexString(selectionModifiers));
		this.selectionModifiers = selectionModifiers;
	}

	/**
	 * Sets the select all button of this text caret. The  select all button
	 * allows the user to select all the text in the text area with the simple
	 * press of a (or more) key(s). When the user presses this button, the
	 * caret will set the dot to zero and the mark to the end of the model.
	 * The default button is defined as CONTROL modifier plus A key.
	 * 
	 * @param selectAllKey - the new select all button
	 * 
	 * @throws IllegalArgumentException if the given {@code selectAllButton}
	 *                                  is null
	 */
	public void setSelectAllButton(IButtonStroke selectAllButton) {
		if (selectAllButton == null)
			throw new IllegalArgumentException("selectAllKey is null");
		this.selectAllButton = selectAllButton;
	}
	
	/**
	 * Sets the home button of this text caret. The home button allows the user
	 * to navigate to the beginning of the text model with the simple press of
	 * a (or more) button(s). If the selection modifiers are held whilst pressing
	 * this button, all the text from the current location of the cursor to the
	 * beginning of the text area will be selected. The default button is the HOME
	 * key button.
	 * 
	 * @param homeKey - the new home button
	 * 
	 * @throws IllegalArgumentException if the given {@code homeButton} is null
	 */
	public void setHomeButton(IButtonStroke homeButton) {
		if (homeButton == null)
			throw new IllegalArgumentException("homeButton is null");
		this.homeButton = homeButton;
	}

	/**
	 * Sets the end button of this text caret. The end button allows the user to
	 * navigate to the end of the text model with the simple press of a (or more)
	 * button(s). If the selection modifiers are held whilst pressing this button,
	 * all the text from the current location of the cursor to the end of the text
	 * area will be selected. The default button is the END key button.
	 * 
	 * @param endKey - the new end button
	 * 
	 * @throws IllegalArgumentException if the given {@code endButton} is null
	 */
	public void setEndButton(IButtonStroke endButton) {
		if (endButton == null)
			throw new IllegalArgumentException("endButton is null");
		this.endButton = endButton;
	}
	
	/**
	 * Sets the navigation mouse button of this text caret. This navigational
	 * button allows the user to set the current location of the caret as well
	 * as select text within the model. The default mouse button is the LEFT
	 * mouse button.
	 * 
	 * @param navigateMouseButton - the new navigational mouse button
	 * 
	 * @throws IllegalArgumentException if the given {@code navigateMouseButton}
	 *                                  is null
	 */
	public void setNavigateMouseButton(MouseButtonStroke navigateMouseButton) {
		if (navigateMouseButton == null)
			throw new IllegalArgumentException("navigateMouseButton is null");
		this.navigateMouseButton = navigateMouseButton;
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void mouseMoved(MouseEvent event) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (navigateMouseButton.isMatching(event)) {
			int y = LinMath.clamp(event.getY(), textComp.getY(), textComp.getY() + textComp.getHeight() - 1);
			navigateToPoint(event.getX(), y, true);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		boolean selectionModifier = event.isModifierHeld(selectionModifiers);
		
		if (navigateMouseButton.isMatching(event))
			navigateToPoint(event.getX(), event.getY(), selectionModifier);
	
		handlePressEvent(event, selectionModifier);
	}

	@Override
	public void mouseReleased(MouseEvent event) {
	}

	@Override
	public void mouseScrolled(MouseEvent event) {
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		handlePressEvent(event, event.isModifierHeld(selectionModifiers));
	}

	@Override
	public void keyRepeated(KeyEvent event) {
		handlePressEvent(event, event.isModifierHeld(selectionModifiers));
	}

	@Override
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	/**
	 * Handles all navigational buttons that are added to this text caret. If
	 * one wishes to see the functionality of these buttons, or change them, 
	 * they can be set by the following setter-methods:
	 * 
	 * @see #setNavigateForwardButton(IButtonStroke)
	 * @see #setNavigateBackwardButton(IButtonStroke)
	 * @see #setSelectAllButton(IButtonStroke)
	 * @see #setHomeButton(IButtonStroke)
	 * @see #setEndButton(IButtonStroke)
	 */
	private void handlePressEvent(Event event, boolean selectionModifier) {
		if (navigateForwardButton.isMatching(event))
			navigateStep(false, selectionModifier);
		if (navigateBackwardButton.isMatching(event))
			navigateStep(true, selectionModifier);
		
		if (homeButton.isMatching(event))
			navigateToLocation(0, selectionModifier);
		if (endButton.isMatching(event))
			navigateToLocation(textComp.getTextModel().getLength(), selectionModifier);
		
		if (selectAllButton.isMatching(event))
			setSelection(0, textComp.getTextModel().getLength());
	}
}
