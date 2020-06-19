package com.sprouts.composition.text.editable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sprouts.composition.CursorType;
import com.sprouts.composition.border.IBorder;
import com.sprouts.composition.drawable.IDrawable;
import com.sprouts.composition.material.IColorMaterial;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.composition.text.TextComposition;
import com.sprouts.composition.view.ICompositionView;
import com.sprouts.composition.view.text.EditableTextCompositionView;

public abstract class EditableTextComposition extends TextComposition implements ITextModelListener {

	public static final ResourceType<IDrawable> FOCUSED_BACKGROUND_RESOURCE = 
			new ResourceType<IDrawable>("focusedBackground", IDrawable.class);
	
	public static final ResourceType<IBorder> FOCUSED_BORDER_RESOURCE = 
			new ResourceType<IBorder>("focusedBorder", IBorder.class);

	public static final ResourceType<IDrawable> CARET_DRAWABLE_RESOURCE = 
			new ResourceType<IDrawable>("caretDrawable", IDrawable.class);

	public static final ResourceType<IDrawable> SELECTION_BACKGROUND_RESOURCE = 
			new ResourceType<IDrawable>("selectionBackground", IDrawable.class);

	public static final ResourceType<IColorMaterial> SELECTION_TEXT_COLOR_RESOURCE = 
			new ResourceType<IColorMaterial>("selectionTextColor", IColorMaterial.class);
	
	private static final List<ResourceType<?>> RESOURCE_TYPES = Collections.unmodifiableList(Arrays.asList(new ResourceType[] {
		BACKGROUND_RESOURCE, BORDER_RESOURCE, TEXT_COLOR_RESOURCE, FONT_RESOURCE, FOCUSED_BACKGROUND_RESOURCE,
		FOCUSED_BORDER_RESOURCE, CARET_DRAWABLE_RESOURCE, SELECTION_BACKGROUND_RESOURCE,
		SELECTION_TEXT_COLOR_RESOURCE
	}));
	
	private ITextModel textModel;
	private final List<ICompositionModelListener> modelListeners;

	private boolean editable;
	
	private IDrawable focusedBackground;
	private IBorder focusedBorder;
	
	private IDrawable caretDrawable;
	
	private IColorMaterial selectionTextColor;
	private IDrawable selectionBackground;
	
	public EditableTextComposition(ITextModel initialTextModel) {
		if (initialTextModel == null)
			throw new IllegalArgumentException("initialTextModel is null!");
		
		textModel = initialTextModel;
		modelListeners = new ArrayList<ICompositionModelListener>();
		
		editable = true;
		
		focusedBackground = null;
		focusedBorder = null;
		
		caretDrawable = null;

		selectionTextColor = null;
		selectionBackground = null;
		
		textModel.addTextModelListener(this);
		
		setCursor(CursorType.IBEAM);
	}
	
	@Override
	public void setView(ICompositionView view) {
		if (!(view instanceof EditableTextCompositionView))
			throw new IllegalArgumentException("view is not a EditableTextCompositionView!");
		
		super.setView(view);
	}
	
	@Override
	public EditableTextCompositionView getView() {
		return (EditableTextCompositionView)super.getView();
	}
	
	public void addModelListener(ICompositionModelListener listener) {
		modelListeners.add(listener);
	}

	public void removeModelListener(ICompositionModelListener listener) {
		modelListeners.remove(listener);
	}
	
	public ITextModel getTextModel() {
		return textModel;
	}
	
	public void setTextModel(ITextModel textModel) {
		if (textModel == null)
			throw new IllegalArgumentException("Text model can not be null!");
		
		this.textModel.removeTextModelListener(this);
		this.textModel = textModel;
		textModel.addTextModelListener(this);
		
		dispatchModelChangeEvent();
		
		requestLayoutAndDraw();
	}
	
	private void dispatchModelChangeEvent() {
		for (ICompositionModelListener listener : modelListeners)
			listener.modelChanged(this);
	}
	
	public IDrawable getFocusedBackground() {
		return focusedBackground;
	}
	
	public void setFocusedBackground(IDrawable focusedBackground) {
		if (view != null && focusedBackground == null)
			throw new IllegalArgumentException("focusedBackground is null!");
		
		this.focusedBackground = focusedBackground;

		dispatchResourceChanged(FOCUSED_BACKGROUND_RESOURCE);
	}
	
	public IBorder getFocusedBorder() {
		return focusedBorder;
	}
	
	public void setFocusedBorder(IBorder focusedBorder) {
		if (view != null && focusedBorder == null)
			throw new IllegalArgumentException("focusedBorder is null!");
		
		this.focusedBorder = focusedBorder;
		
		dispatchResourceChanged(FOCUSED_BORDER_RESOURCE);
	}

	public IDrawable getCaretDrawable() {
		return caretDrawable;
	}

	public void setCaretDrawable(IDrawable caretDrawable) {
		if (view != null && caretDrawable == null)
			throw new IllegalArgumentException("caretDrawable is null!");
		
		this.caretDrawable = caretDrawable;

		dispatchResourceChanged(CARET_DRAWABLE_RESOURCE);
	}
	
	public IColorMaterial getSelectionTextColor() {
		return selectionTextColor;
	}
	
	public void setSelectionTextColor(IColorMaterial selectionTextColor) {
		if (view != null && selectionTextColor == null)
			throw new IllegalArgumentException("selectionTextColor is null!");
		
		this.selectionTextColor = selectionTextColor;

		dispatchResourceChanged(SELECTION_TEXT_COLOR_RESOURCE);
	}
	
	public IDrawable getSelectionBackground() {
		return selectionBackground;
	}
	
	public void setSelectionBackground(IDrawable selectionBackground) {
		if (view != null && selectionBackground == null)
			throw new IllegalArgumentException("selectionBackground is null!");
		
		this.selectionBackground = selectionBackground;

		dispatchResourceChanged(SELECTION_BACKGROUND_RESOURCE);
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
		
		requestDraw(false);
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	@Override
	public <T> T getResource(ResourceType<T> type) {
		if (type == FOCUSED_BACKGROUND_RESOURCE) {
			return type.cast(getFocusedBackground());
		} else if (type == FOCUSED_BORDER_RESOURCE) {
			return type.cast(getFocusedBorder());
		} else if (type == CARET_DRAWABLE_RESOURCE) {
			return type.cast(getCaretDrawable());
		} else if (type == SELECTION_BACKGROUND_RESOURCE) {
			return type.cast(getSelectionBackground());
		} else if (type == SELECTION_TEXT_COLOR_RESOURCE) {
			return type.cast(getSelectionTextColor());
		}
		
		return super.getResource(type);
	}
	
	@Override
	public <T> void setResource(ResourceType<T> type, T resource) {
		if (type == FOCUSED_BACKGROUND_RESOURCE) {
			setFocusedBackground(FOCUSED_BACKGROUND_RESOURCE.cast(resource));
		} else if (type == FOCUSED_BORDER_RESOURCE) {
			setFocusedBorder(FOCUSED_BORDER_RESOURCE.cast(resource));
		} else if (type == CARET_DRAWABLE_RESOURCE) {
			setCaretDrawable(CARET_DRAWABLE_RESOURCE.cast(resource));
		} else if (type == SELECTION_BACKGROUND_RESOURCE) {
			setSelectionBackground(CARET_DRAWABLE_RESOURCE.cast(resource));
		} else if (type == SELECTION_TEXT_COLOR_RESOURCE) {
			setSelectionTextColor(SELECTION_TEXT_COLOR_RESOURCE.cast(resource));
		} else {
			super.setResource(type, resource);
		}
	}
	
	@Override
	public List<ResourceType<?>> getResourceTypes() {
		return RESOURCE_TYPES;
	}

	@Override
	public void textInserted(ITextModel model, int offset, int count) {
		requestLayoutAndDraw();
	}

	@Override
	public void textRemoved(ITextModel model, int offset, int count) {
		requestLayoutAndDraw();
	}
}
