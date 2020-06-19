package com.sprouts.composition.event;

import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.Composition;
import com.sprouts.composition.CompositionContext;
import com.sprouts.composition.CursorType;
import com.sprouts.input.IKeyboardListener;
import com.sprouts.input.IMouseListener;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;

public class EventDispatcher implements IMouseListener, IKeyboardListener {

	private Composition rootComp;
	
	private Composition focusedComp;
	
	private int prevX;
	private int prevY;
	
	private CursorType cursor;

	public EventDispatcher(Composition rootComp) {
		this.rootComp = rootComp;
		
		focusedComp = null;
	
		prevX = prevY = -1;
		
		cursor = CursorType.DEFAULT;
	}
	
	public void install(Mouse mouse, Keyboard keyboard) {
		mouse.addListener(this);
		keyboard.addListener(this);
	}

	public void uninstall(Mouse mouse, Keyboard keyboard) {
		mouse.removeListener(this);
		keyboard.removeListener(this);
	
		focusedComp = null;
		
		prevX = prevY = -1;

		cursor = CursorType.DEFAULT;
	}
	
	@Override
	public void mouseMoved(float mouseX, float mouseY) {
		int x = convertMouseX(mouseX);
		int y = convertMouseY(mouseY);
		
		if (rootComp.isInBounds(prevX, prevY)) {
			Composition comp = rootComp;
			
			while (comp != null) {
				if (!comp.isInBounds(x, y)) {
					MouseEvent event = MouseEvent.createMouseExitedEvent(comp, x, y);
					dispatchMouseEvent(event, IMouseEventListener::mouseExited);
				}
				
				comp = comp.getChildAt(prevX, prevY);
			}
		}

		if (rootComp.isInBounds(x, y)) {
			Composition comp = rootComp;
			
			while (comp != null) {
				if (!comp.isInBounds(prevX, prevY)) {
					MouseEvent event = MouseEvent.createMouseEnteredEvent(comp, x, y);
					dispatchMouseEvent(event, IMouseEventListener::mouseEntered);
				} else {
					MouseEvent event = MouseEvent.createMouseMovedEvent(comp, x, y);
					dispatchMouseEvent(event, IMouseEventListener::mouseMoved);
				}

				Composition child = comp.getChildAt(x, y);
				if (child == null) {
					// We are at the highest layer. Check what cursor is
					// set on that composition.
					setCurrentCursor(comp.getCursor());
				}
				
				comp = child;
			}
		} else {
			setCurrentCursor(CursorType.DEFAULT);
		}
		
		prevX = x;
		prevY = y;
	}
	
	private void setCurrentCursor(CursorType cursor) {
		if (cursor != this.cursor) {
			this.cursor = cursor;
			
			CompositionContext.setCursor(cursor);
		}
	}

	@Override
	public void mouseDragged(int button, float mouseX, float mouseY, float dragX, float dragY) {
		if (focusedComp != null && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			int x = convertMouseX(mouseX);
			int y = convertMouseY(mouseY);
			
			MouseEvent event = MouseEvent.createMouseDraggedEvent(focusedComp, x, y, button, dragX, dragY);
			dispatchMouseEvent(event, IMouseEventListener::mouseDragged);
		}
	}

	@Override
	public void mousePressed(int button, float mouseX, float mouseY, int modifiers) {
		int x = convertMouseX(mouseX);
		int y = convertMouseY(mouseY);
		
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			Composition pressedComp = getChildAtDeep(rootComp, x, y);
	
			if (pressedComp != focusedComp) {
				if (focusedComp != null) {
					focusedComp.setFocused(false);

					FocusEvent event = FocusEvent.createFocusLostEvent(focusedComp);
					dispatchFocusEvent(event, IFocusEventListener::focusLost);
				}
				if (pressedComp != null) {
					pressedComp.setFocused(true);

					FocusEvent event = FocusEvent.createFocusGainedEvent(pressedComp);
					dispatchFocusEvent(event, IFocusEventListener::focusGained);
				}
				
				focusedComp = pressedComp;
			}
		}
		
		if (focusedComp != null) {
			MouseEvent event = MouseEvent.createMousePressedEvent(focusedComp, x, y, button, modifiers);
			dispatchMouseEvent(event, IMouseEventListener::mousePressed);
		}
	}

	private Composition getChildAtDeep(Composition comp, int x, int y) {
		if (comp.isInBounds(x, y)) {
			Composition childComp;
			while ((childComp = comp.getChildAt(x, y)) != null)
				comp = childComp;
			return comp;
		}
		
		return null;
	}

	@Override
	public void mouseReleased(int button, float mouseX, float mouseY, int modifiers) {
		if (focusedComp != null) {
			int x = convertMouseX(mouseX);
			int y = convertMouseY(mouseY);
			
			MouseEvent event = MouseEvent.createMouseReleasedEvent(focusedComp, x, y, button, modifiers);
			dispatchMouseEvent(event, IMouseEventListener::mouseReleased);
		}
	}

	@Override
	public void mouseScroll(float mouseX, float mouseY, float scrollX, float scrollY) {
		int x = convertMouseX(mouseX);
		int y = convertMouseY(mouseY);
		
		if (rootComp.isInBounds(x, y)) {
			Composition comp = rootComp;
			
			while (comp != null) {
				MouseEvent event = MouseEvent.createMouseScrolledEvent(comp, x, y, scrollX, scrollY);
				dispatchMouseEvent(event, IMouseEventListener::mouseScrolled);
				
				comp = comp.getChildAt(x, y);
			}
		}
	}
	
	@Override
	public void keyPressed(int key, int mods) {
		if (focusedComp != null) {
			KeyEvent event = KeyEvent.createKeyPressedEvent(focusedComp, key, mods);
			dispatchKeyEvent(event, IKeyEventListener::keyPressed);
		}
	}

	@Override
	public void keyRepeated(int key, int mods) {
		if (focusedComp != null) {
			KeyEvent event = KeyEvent.createKeyRepeatedEvent(focusedComp, key, mods);
			dispatchKeyEvent(event, IKeyEventListener::keyRepeated);
		}
	}

	@Override
	public void keyReleased(int key, int mods) {
		if (focusedComp != null) {
			KeyEvent event = KeyEvent.createKeyReleasedEvent(focusedComp, key, mods);
			dispatchKeyEvent(event, IKeyEventListener::keyReleased);
		}
	}

	@Override
	public void keyTyped(int codePoint) {
		if (focusedComp != null) {
			KeyEvent event = KeyEvent.createKeyTypedEvent(focusedComp, codePoint);
			dispatchKeyEvent(event, IKeyEventListener::keyTyped);
		}
	}
	
	private void dispatchMouseEvent(MouseEvent event, BiConsumer<IMouseEventListener, MouseEvent> method) {
		Composition source = event.getSource();
		
		for (IMouseEventListener listener : source.getMouseEventListeners())
			method.accept(listener, event);
	}
	
	private void dispatchKeyEvent(KeyEvent event, BiConsumer<IKeyEventListener, KeyEvent> method) {
		Composition source = event.getSource();
		
		for (IKeyEventListener listener : source.getKeyEventListeners())
			method.accept(listener, event);
	}

	private void dispatchFocusEvent(FocusEvent event, BiConsumer<IFocusEventListener, FocusEvent> method) {
		Composition source = event.getSource();
		
		for (IFocusEventListener listener : source.getFocusEventListeners())
			method.accept(listener, event);
	}
	
	private int convertMouseX(float mouseX) {
		return (int)mouseX;
	}

	private int convertMouseY(float mouseY) {
		return (int)mouseY;
	}
}
