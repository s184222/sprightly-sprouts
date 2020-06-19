package com.sprouts.composition.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sprouts.composition.Composition;
import com.sprouts.composition.material.ColorGradientMaterial;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.composition.view.ICompositionView;
import com.sprouts.composition.view.text.TextCompositionView;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.font.Font;

/**
 * @author Christian
 */
public abstract class TextComposition extends Composition {

	public static final ResourceType<IColorMaterial> TEXT_COLOR_RESOURCE = 
			new ResourceType<IColorMaterial>("textColor", IColorMaterial.class);
	
	public static final ResourceType<Font> FONT_RESOURCE = 
			new ResourceType<Font>("font", Font.class);

	private static final List<ResourceType<?>> RESOURCE_TYPES = Collections.unmodifiableList(Arrays.asList(new ResourceType[] {
		BACKGROUND_RESOURCE, BORDER_RESOURCE, TEXT_COLOR_RESOURCE, FONT_RESOURCE
	}));
	
	protected Font font;
	protected IColorMaterial textColor;

	protected TextAlignment textAlignment;
	
	protected TextComposition() {
		super(false);

		// Note that all text compositions must set
		// their own view.
		
		font = null;
		textColor = null;
		
		textAlignment = TextAlignment.LEFT;
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (!(view instanceof TextCompositionView))
			throw new IllegalArgumentException("view is not a TextCompositionView!");
		
		super.setView(view);
	}
	
	@Override
	public TextCompositionView getView() {
		return (TextCompositionView)super.getView();
	}
	
	public abstract String getText();

	public abstract void setText(String text);
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		if (view != null && font == null)
			throw new IllegalArgumentException("font is null!");
		
		this.font = font;
		
		dispatchResourceChanged(FONT_RESOURCE);
		
		requestLayoutAndDraw();
	}
	
	public IColorMaterial getTextColor() {
		return textColor;
	}
	
	public void setTextColor(VertexColor textColor) {
		setTextColor(new ColorGradientMaterial(textColor));
	}
	
	public void setTextColor(IColorMaterial textColor) {
		if (view != null && textColor == null)
			throw new IllegalArgumentException("textColor is null!");
		
		this.textColor = textColor;

		dispatchResourceChanged(TEXT_COLOR_RESOURCE);
	}
	
	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	public void setTextAlignment(TextAlignment textAlignment) {
		if (textAlignment == null)
			throw new IllegalArgumentException("textAlignment is null!");
		
		if (textAlignment != this.textAlignment) {
			this.textAlignment = textAlignment;
			
			requestDraw(false);
		}
	}
	
	protected void requestLayoutAndDraw() {
		if (parent != null) {
			// Minimum size is very likely to have changed. Make
			// sure to request a re-layout from the parent.
			parent.requestLayout();
		}
		
		requestLayout();
		requestDraw(false);
	}
	
	@Override
	public <T> T getResource(ResourceType<T> type) {
		if (type == TEXT_COLOR_RESOURCE) {
			return type.cast(getTextColor());
		} else if (type == FONT_RESOURCE) {
			return type.cast(getFont());
		}
		
		return super.getResource(type);
	}
	
	@Override
	public <T> void setResource(ResourceType<T> type, T resource) {
		if (type == TEXT_COLOR_RESOURCE) {
			setTextColor(TEXT_COLOR_RESOURCE.cast(resource));
		} else if (type == FONT_RESOURCE) {
			setFont(FONT_RESOURCE.cast(resource));
		} else {
			super.setResource(type, resource);
		}
	}
	
	@Override
	public List<ResourceType<?>> getResourceTypes() {
		return RESOURCE_TYPES;
	}
}
