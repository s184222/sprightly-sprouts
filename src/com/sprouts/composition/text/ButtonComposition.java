package com.sprouts.composition.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.sprouts.composition.drawable.IDrawable;
import com.sprouts.composition.event.MouseButtonStroke;
import com.sprouts.composition.event.MouseEvent;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.composition.view.ICompositionView;
import com.sprouts.composition.view.text.BasicButtonView;
import com.sprouts.composition.view.text.ButtonView;

/**
 * @author Christian
 */
public class ButtonComposition extends TextComposition {

	public static final ResourceType<IDrawable> HOVERED_BACKGROUND_RESOURCE = 
			new ResourceType<IDrawable>("hoveredBackground", IDrawable.class);

	public static final ResourceType<IDrawable> PRESSED_BACKGROUND_RESOURCE = 
			new ResourceType<IDrawable>("pressedBackground", IDrawable.class);
	
	private static final List<ResourceType<?>> RESOURCE_TYPES = Collections.unmodifiableList(Arrays.asList(new ResourceType[] {
		BACKGROUND_RESOURCE, BORDER_RESOURCE, TEXT_COLOR_RESOURCE, FONT_RESOURCE,
		HOVERED_BACKGROUND_RESOURCE, PRESSED_BACKGROUND_RESOURCE
	}));
	
	protected String text;
	
	protected IDrawable hoveredBackground;
	protected IDrawable pressedBackground;

	protected MouseButtonStroke mouseButton;
	
	private List<IButtonListener> buttonListeners;
	
	public ButtonComposition() {
		this("");
	}
	
	public ButtonComposition(String text) {
		this.text = text;
	
		hoveredBackground = null;
		pressedBackground = null;
	
		mouseButton = new MouseButtonStroke(MouseEvent.BUTTON_LEFT);
		
		buttonListeners = null;
		
		setView(new BasicButtonView());
	}

	public void setView(ButtonView view) {
		super.setView(view);
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (!(view instanceof ButtonView))
			throw new IllegalArgumentException("view is not a ButtonView!");
		
		super.setView(view);
	}
	
	@Override
	public ButtonView getView() {
		return (ButtonView)super.getView();
	}
	
	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public void setText(String text) {
		if (!Objects.equals(text, this.text)) {
			this.text = text;
			
			requestLayoutAndDraw();
		}
	}
	
	public IDrawable getHoveredBackground() {
		return hoveredBackground;
	}

	public void setHoveredBackground(IDrawable hoveredBackground) {
		if (view != null && hoveredBackground == null)
			throw new IllegalArgumentException("hoveredBackground is null!");
		
		this.hoveredBackground = hoveredBackground;
		
		dispatchResourceChanged(HOVERED_BACKGROUND_RESOURCE);
	}

	public IDrawable getPressedBackground() {
		return pressedBackground;
	}

	public void setPressedBackground(IDrawable pressedBackground) {
		if (view != null && pressedBackground == null)
			throw new IllegalArgumentException("pressedBackground is null!");
		
		this.pressedBackground = pressedBackground;
		
		dispatchResourceChanged(PRESSED_BACKGROUND_RESOURCE);
	}
	
	public MouseButtonStroke getMouseButton() {
		return mouseButton;
	}

	public void setMouseButton(MouseButtonStroke mouseButton) {
		if (mouseButton == null)
			throw new IllegalArgumentException("mouseButton is null!");
		
		this.mouseButton = mouseButton;
	}
	
	public void addButtonListener(IButtonListener buttonListener) {
		if (buttonListeners == null)
			buttonListeners = new ArrayList<IButtonListener>(1);
		
		buttonListeners.add(buttonListener);
	}
	
	public void removeButtonListener(IButtonListener buttonListener) {
		if (buttonListeners != null) {
			buttonListeners.remove(buttonListener);
			
			if (buttonListeners.isEmpty())
				buttonListeners = null;
		}
	}

	public List<IButtonListener> getButtonListeners() {
		if (buttonListeners == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(buttonListeners);
	}
	
	public void dispatchButtonClickedEvent() {
		for (IButtonListener listener : getButtonListeners())
			listener.buttonClicked(this);
	}
	
	@Override
	public <T> T getResource(ResourceType<T> type) {
		if (type == HOVERED_BACKGROUND_RESOURCE) {
			return type.cast(getHoveredBackground());
		} else if (type == PRESSED_BACKGROUND_RESOURCE) {
			return type.cast(getPressedBackground());
		}
		
		return super.getResource(type);
	}
	
	@Override
	public <T> void setResource(ResourceType<T> type, T resource) {
		if (type == HOVERED_BACKGROUND_RESOURCE) {
			setHoveredBackground(HOVERED_BACKGROUND_RESOURCE.cast(resource));
		} else if (type == PRESSED_BACKGROUND_RESOURCE) {
			setPressedBackground(PRESSED_BACKGROUND_RESOURCE.cast(resource));
		} else {
			super.setResource(type, resource);
		}
	}
	
	@Override
	public List<ResourceType<?>> getResourceTypes() {
		return RESOURCE_TYPES;
	}
}
