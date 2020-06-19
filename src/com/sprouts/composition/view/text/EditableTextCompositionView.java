package com.sprouts.composition.view.text;

import java.awt.Rectangle;

public abstract class EditableTextCompositionView extends TextCompositionView {

	public abstract Rectangle modelToView(int location);

	public abstract int viewToModel(int x, int y);

}
