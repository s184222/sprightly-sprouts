package com.sprouts.composition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sprouts.composition.border.IBorder;
import com.sprouts.composition.border.Margin;
import com.sprouts.composition.drawable.ColorDrawable;
import com.sprouts.composition.drawable.IDrawable;
import com.sprouts.composition.event.IFocusEventListener;
import com.sprouts.composition.event.IKeyEventListener;
import com.sprouts.composition.event.IMouseEventListener;
import com.sprouts.composition.resource.IResourceManager;
import com.sprouts.composition.resource.ResourceType;
import com.sprouts.composition.view.BasicCompositionView;
import com.sprouts.composition.view.ICompositionView;
import com.sprouts.graphic.color.VertexColor;
import com.sprouts.graphic.tessellator.VertexLayerID;
import com.sprouts.graphic.tessellator2d.ILayeredTessellator2D;
import com.sprouts.graphic.tessellator2d.ITessellator2D;

/**
 * @author Christian
 */
public class Composition {

	public static final int NO_DRAW_FLAG           = 0x00;
	
	public static final int SELF_DRAW_FLAG         = 0x01;
	public static final int RESTRUCTURE_DRAW_FLAG  = 0x02;
	public static final int CHILDREN_DRAW_FLAG     = 0x04;
	
	public static final ResourceType<IDrawable> BACKGROUND_RESOURCE = 
			new ResourceType<IDrawable>("background", IDrawable.class);

	public static final ResourceType<IBorder> BORDER_RESOURCE = 
			new ResourceType<IBorder>("border", IBorder.class);
	
	private static final List<ResourceType<?>> RESOURCE_TYPES = Collections.unmodifiableList(Arrays.asList(new ResourceType[] {
		BACKGROUND_RESOURCE, BORDER_RESOURCE
	}));
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;

	protected Composition parent;
	private boolean visible;

	protected boolean layoutRequired;
	protected boolean layoutChildrenRequired;
	
	protected int drawFlags;
	protected VertexLayerID cachedLayerId;
	
	protected ICompositionView view;
	protected IDrawable background;
	protected IBorder border;
	
	private boolean dynamicDrawing;
	
	protected Margin padding;
	
	protected CompositionSize minimumSize;
	protected CompositionSize maximumSize;
	
	private IResourceManager resourceManager;
	
	private List<IMouseEventListener> mouseEventListeners;
	private List<IKeyEventListener> keyEventListeners;
	private List<IFocusEventListener> focusEventListeners;
	
	private boolean focused;
	
	protected CursorType cursor;
	
	public Composition() {
		this(true);
	}
	
	protected Composition(boolean setInitialView) {
		width = 0;
		height = 0;
		
		parent = null;
		visible = false;
		
		layoutRequired = false;
		layoutChildrenRequired = false;
		
		drawFlags = NO_DRAW_FLAG;
		cachedLayerId = null;
		
		view = null;
		background = null;
		border = null;
		
		dynamicDrawing = false;
		
		padding = new Margin(0);
		
		minimumSize = null;
		maximumSize = null;
		
		resourceManager = null;
		
		mouseEventListeners = null;
		keyEventListeners = null;
		focusEventListeners = null;

		focused = false;
	
		cursor = CursorType.DEFAULT;
		
		if (setInitialView)
			setView(new BasicCompositionView());
	}

	public void onAdded(Composition parent) {
		if (this.parent != null)
			throw new IllegalStateException("Composition already has a parent!");
		if (parent == this)
			throw new IllegalArgumentException("Can not set parent to self!");
		
		this.parent = parent;
		
		requestLayout();
		requestDraw(true);
	}

	public void onRemoved(Composition parent) {
		if (parent != this.parent)
			throw new IllegalStateException("Composition does not have the specified parent!");

		this.parent = null;
	}
	
	public void setBounds(int x, int y, int width, int height) {
		if (width < 0 || height < 0)
			throw new IllegalArgumentException("width and height must be non-negative!");
		
		if (this.x != x || this.y != y || this.width != width || this.height != height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			
			requestLayout();
			requestDraw(true);
		}
	}
	
	protected void doLayout() {
		view.layoutChanged(this);
	}
	
	public void layout() {
		if (layoutRequired) {
			doLayout();
			layoutRequired = false;
		}
		
		if (layoutChildrenRequired) {
			layoutChildren();
			layoutChildrenRequired = false;
		}
	}
	
	protected void layoutChildren() {
	}
	
	public void draw(ILayeredTessellator2D tessellator) {
		if (tessellator.isBuilding()) {
			// When the tessellator is building we know that all
			// previous drawing that had been done has been removed.
			drawAll(tessellator);
		} else if (isDrawFlagSet(RESTRUCTURE_DRAW_FLAG)) {
			tessellator.rebuildLayer(cachedLayerId, true);
			drawAll(tessellator);
			tessellator.finishRebuilding();
		} else {
			if (!dynamicDrawing && isDrawFlagSet(SELF_DRAW_FLAG)) {
				// Only rebuild the vertices of this composition, since
				// the structure of the composition has not changed.
				tessellator.rebuildLayer(cachedLayerId, false);
				
				cachedLayerId = tessellator.pushLayer();
				drawComposition(tessellator);
				tessellator.popLayer();
				
				tessellator.finishRebuilding();
			}
			
			if (isDrawFlagSet(CHILDREN_DRAW_FLAG))
				drawChildren(tessellator);
		}
		
		// Clear all flags. This is to ensure that we do
		// not have any leftovers that were not handled.
		clearDrawFlags(drawFlags);
	}
	
	protected void drawAll(ILayeredTessellator2D tessellator) {
		cachedLayerId = tessellator.pushLayer();
		if (!dynamicDrawing)
			drawComposition(tessellator);
		drawChildren(tessellator);
		tessellator.popLayer();
	}
	
	public boolean dynamicUpdate(int deltaMillis) {
		if (dynamicDrawing)
			dynamicDrawing = view.dynamicUpdate(this, deltaMillis);
		return dynamicDrawing;
	}
	
	public void drawComposition(ITessellator2D tessellator) {
		view.draw(this, tessellator);
	}

	protected void drawChildren(ILayeredTessellator2D tessellator) {
	}

	public void requestLayout() {
		layoutRequired = true;
		
		if (parent != null)
			parent.requestChildrenLayout();
	}
	
	protected void requestChildrenLayout() {
		if (!layoutChildrenRequired) {
			layoutChildrenRequired = true;
			
			if (parent != null)
				parent.requestChildrenLayout();
		}
	}
	
	public boolean isLayoutRequired() {
		return (layoutRequired || layoutChildrenRequired);
	}

	/**
	 * Requests a draw update from the parent or main context. If the structure of the
	 * composition is likely to have changed, and children are likely to also need a
	 * redraw then it is encouraged to have {@code structureChanged} as true.
	 * <br><br>
	 * <b>NOTE:</b> If new children have been added to the composition then the parameter
	 * to this method must be {@code true}. Otherwise the children will either not get
	 * drawn or their drawing will have undefined behavior.
	 * 
	 * @param structureChanged - whether the structure of the composition has changed
	 *                           and any children are likely to also need to be drawn.
	 */
	public void requestDraw(boolean structureChanged) {
		if (structureChanged) {
			setDrawFlags(SELF_DRAW_FLAG | RESTRUCTURE_DRAW_FLAG);
		} else {
			setDrawFlags(SELF_DRAW_FLAG);
		}
		
		if (parent != null)
			parent.requestChildrenDraw();
	}

	public void requestChildrenDraw() {
		if (!isDrawFlagSet(CHILDREN_DRAW_FLAG)) {
			setDrawFlags(CHILDREN_DRAW_FLAG);
			
			if (parent != null)
				parent.requestChildrenDraw();
		}
	}
	
	boolean isDrawFlagSet(int flag) {
		return (drawFlags & flag) == flag;
	}
	
	void setDrawFlags(int flags) {
		drawFlags |= flags;
	}

	void clearDrawFlags(int flags) {
		drawFlags &= ~flags;
	}
	
	public boolean isDrawRequired() {
		return (drawFlags != NO_DRAW_FLAG);
	}
	
	public void requestDynamic() {
		if (!dynamicDrawing) {
			dynamicDrawing = true;
			
			CompositionContext.registerDynamic(this);
		}
	}

	protected void dispatchResourceChanged(ResourceType<?> resource) {
		if (view != null) {
			// Simply propagate the change to the view
			view.resourceChanged(this, resource);
		}
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public VertexLayerID getCachedLayerId() {
		return cachedLayerId;
	}
	
	public Composition getParent() {
		return parent;
	}
	
	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			
			requestDraw(true);
		}
	}
	
	public boolean isVisible() {
		return visible;
	}

	public ICompositionView getView() {
		return view;
	}

	public void setView(ICompositionView view) {
		if (view == null)
			throw new IllegalArgumentException("view is null!");
		
		ICompositionView oldView = this.view;
		this.view = null;

		if (oldView != null) {
			// Note that the old view is null on the initial
			// call to #setView(ICompositionView)
			oldView.unbindView(this);
		}

		// Bind the view before it is set. This is to ensure
		// that resources are properly set in the view.
		view.bindView(this);

		this.view = view;
		
		requestLayout();
		requestDraw(false);
	}
	
	public IDrawable getBackground() {
		return background;
	}

	public void setBackground(VertexColor background) {
		setBackground(new ColorDrawable(background));
	}
	
	public void setBackground(IDrawable background) {
		if (view != null && background == null) {
			// It is allowed for the view to set resources
			// to null when it unbinds from the composition.
			throw new IllegalArgumentException("background is null!");
		}
		
		this.background = background;
		
		requestDraw(false);
		
		dispatchResourceChanged(BACKGROUND_RESOURCE);
	}
	
	public IBorder getBorder() {
		return border;
	}

	public void setBorder(IBorder border) {
		if (view != null && border == null)
			throw new IllegalArgumentException("border is null!");
	
		this.border = border;
		
		if (parent != null)
			parent.requestLayout();
		requestDraw(false);
		
		dispatchResourceChanged(BORDER_RESOURCE);
	}
	
	public boolean isDynamic() {
		return dynamicDrawing;
	}

	public Margin getPadding() {
		return padding;
	}

	public void setPadding(Margin padding) {
		if (padding == null)
			throw new IllegalArgumentException("padding is null!");
		
		if (!padding.equals(this.padding)) {
			this.padding = padding;
			
			if (parent != null)
				parent.requestLayout();

			requestLayout();
			requestDraw(false);
		}
	}
	
	public CompositionSize getMinimumSize() {
		if (minimumSize == null)
			return view.getMinimumSize(this);
		return minimumSize;
	}

	public void setMinimumSize(CompositionSize minimumSize) {
		this.minimumSize = minimumSize;

		if (parent != null)
			parent.requestLayout();
	}

	public CompositionSize getMaximumSize() {
		if (maximumSize == null)
			return view.getMaximumSize(this);
		return maximumSize;
	}
	
	public void setMaximumSize(CompositionSize maximumSize) {
		this.maximumSize = maximumSize;

		if (parent != null)
			parent.requestLayout();
	}
	
	public IResourceManager getResourceManager() {
		if (resourceManager == null)
			resourceManager = CompositionContext.getResourceManager();
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public void addMouseEventListener(IMouseEventListener eventListener) {
		if (mouseEventListeners == null)
			mouseEventListeners = new ArrayList<IMouseEventListener>(1);
		
		mouseEventListeners.add(eventListener);
	}

	public void removeMouseEventListener(IMouseEventListener eventListener) {
		if (mouseEventListeners != null) {
			mouseEventListeners.remove(eventListener);
			
			if (mouseEventListeners.isEmpty())
				mouseEventListeners = null;
		}
	}

	public List<IMouseEventListener> getMouseEventListeners() {
		if (mouseEventListeners == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(mouseEventListeners);
	}
	
	public void addKeyEventListener(IKeyEventListener eventListener) {
		if (keyEventListeners == null)
			keyEventListeners = new ArrayList<IKeyEventListener>(1);
		
		keyEventListeners.add(eventListener);
	}
	
	public void removeKeyEventListener(IKeyEventListener eventListener) {
		if (keyEventListeners != null) {
			keyEventListeners.remove(eventListener);
			
			if (keyEventListeners.isEmpty())
				keyEventListeners = null;
		}
	}

	public List<IKeyEventListener> getKeyEventListeners() {
		if (keyEventListeners == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(keyEventListeners);
	}

	public Composition getChildAt(int x, int y) {
		return null;
	}
	
	public boolean isInBounds(int x, int y) {
		if (x < this.x || x >= this.x + width)
			return false;
		if (y < this.y || y >= this.y + height)
			return false;
		return true;
	}

	public void addFocusEventListener(IFocusEventListener eventListener) {
		if (focusEventListeners == null)
			focusEventListeners = new ArrayList<IFocusEventListener>(1);
		
		focusEventListeners.add(eventListener);
	}
	
	public void removeFocusEventListener(IFocusEventListener eventListener) {
		if (focusEventListeners != null) {
			focusEventListeners.remove(eventListener);
			
			if (focusEventListeners.isEmpty())
				focusEventListeners = null;
		}
	}

	public List<IFocusEventListener> getFocusEventListeners() {
		if (focusEventListeners == null)
			return Collections.emptyList();
		return Collections.unmodifiableList(focusEventListeners);
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
	public <T> T getResource(ResourceType<T> type) {
		if (type == BACKGROUND_RESOURCE) {
			return type.cast(getBackground());
		} else if (type == BORDER_RESOURCE) {
			return type.cast(getBorder());
		}
	
		return null;
	}
	
	public <T> void setResource(ResourceType<T> type, T resource) {
		if (type == BACKGROUND_RESOURCE) {
			setBackground(BACKGROUND_RESOURCE.cast(resource));
		} else if (type == BORDER_RESOURCE) {
			setBorder(BORDER_RESOURCE.cast(resource));
		}
	}
	
	public List<ResourceType<?>> getResourceTypes() {
		return RESOURCE_TYPES;
	}
	
	public CursorType getCursor() {
		return cursor;
	}

	public void setCursor(CursorType cursor) {
		if (cursor == null)
			throw new IllegalArgumentException("cursor is null!");
		
		this.cursor = cursor;
	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object other) {
		return super.equals(other);
	}
}
