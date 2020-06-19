package com.sprouts.composition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.sprouts.composition.event.EventDispatcher;
import com.sprouts.composition.layout.LayoutSpecification;
import com.sprouts.composition.resource.IResourceManager;
import com.sprouts.composition.resource.ResourceManager;
import com.sprouts.graphic.Display;
import com.sprouts.graphic.DisplayListener;
import com.sprouts.graphic.DisplaySize;
import com.sprouts.graphic.tessellator.VertexLayerID;
import com.sprouts.graphic.tessellator2d.LayeredTessellator2D;
import com.sprouts.graphic.tessellator2d.shader.BasicTessellator2DShader;
import com.sprouts.graphic.tessellator2d.shader.Tessellator2DShader;
import com.sprouts.input.Keyboard;
import com.sprouts.input.Mouse;

import sun.awt.DisplayChangedListener;

/**
 * @author Christian
 */
public final class CompositionContext implements DisplayListener {

	private static final int MAX_VALID_DELTA_MILLIS = 10 * 1000; /* 10 seconds */
	
	private static CompositionContext instance = null;
	
	private final Display display;
	private final Keyboard keyboard;
	private final Mouse mouse;
	
	private LayeredTessellator2D tessellator;
	private ResourceManager resourceManager;
	
	private final Set<Composition> dynamicCompositions;
	private final RootComposition rootComposition;
	
	private final EventDispatcher eventDispatcher;
	
	private final List<Timer> timers;

	private long prevFrameMillis;
	
	private CompositionContext(Display display, Keyboard keyboard, Mouse mouse) {
		this.display = display;
		this.keyboard = keyboard;
		this.mouse = mouse;
		
		tessellator = null;
		resourceManager = null;
		
		dynamicCompositions = new HashSet<Composition>();
		rootComposition = new RootComposition();
		rootComposition.setVisible(true);

		eventDispatcher = new EventDispatcher(rootComposition);
		
		timers = new ArrayList<Timer>();
		
		prevFrameMillis = 0L;
	}
	
	public static void init(Display display, Keyboard keyboard, Mouse mouse) {
		if (instance == null) {
			instance = new CompositionContext(display, keyboard, mouse);
			instance.initImpl();
		}
	}
	
	public static void dispose() {
		if (instance != null)
			instance.disposeImpl();
	}
	
	public static void setContent(Composition content) {
		getContext().setContentImpl(content);
	}
	
	public static void setContent(Composition content, LayoutSpecification spec) {
		getContext().setContentImpl(content, spec);
	}

	public static void registerDynamic(Composition composition) {
		getContext().registerDynamicImpl(composition);
	}

	public static void unregisterDynamic(Composition composition) {
		getContext().unregisterDynamicImpl(composition);
	}

	public static void draw() {
		getContext().drawImpl();
	}
	
	public static IResourceManager getResourceManager() {
		return getContext().getResourceManagerImpl();
	}
	
	public static void startTimer(Timer timer) {
		getContext().startTimerImpl(timer);
	}

	public static void stopTimer(Timer timer) {
		getContext().stopTimerImpl(timer);
	}
	
	public static void setCursor(CursorType cursor) {
		getContext().setCursorImpl(cursor);
	}
	
	public static String getClipboardString() {
		return getContext().getClipboardStringImpl();
	}

	public static void setClipboardString(String clipboard) {
		getContext().setClipboardStringImpl(clipboard);
	}
	
	public static int getDisplayWidth() {
		return getContext().display.getDisplaySize().width;
	}

	public static int getDisplayHeight() {
		return getContext().display.getDisplaySize().height;
	}
	
	@Override
	public void sizeChanged(int width, int height) {
		setViewportImpl(0, 0, width, height);
	}
	
	private void initImpl() {
		try {
			instance.loadResources();
		} catch (IOException e) {
			e.printStackTrace();
			
			System.exit(1);
		}
		
		eventDispatcher.install(mouse, keyboard);
		
		DisplaySize size = display.getDisplaySize();
		sizeChanged(size.width, size.height);
		
		display.addDisplayListener(this);
	}
	
	private void loadResources() throws IOException {
		Tessellator2DShader shader = new BasicTessellator2DShader();
		tessellator = new LayeredTessellator2D(shader);

		resourceManager = new ResourceManager();
		resourceManager.loadResources();
		
		resourceManager.registerResource(shader);
		resourceManager.registerResource(tessellator);
		
		prevFrameMillis = System.currentTimeMillis();
	}
	
	private void disposeImpl() {
		eventDispatcher.uninstall(mouse, keyboard);

		resourceManager.dispose();
	}
	
	private void setContentImpl(Composition content) {
		rootComposition.setContent(content);
	}

	private void setContentImpl(Composition content, LayoutSpecification spec) {
		rootComposition.setContent(content, spec);
	}
	
	private void registerDynamicImpl(Composition composition) {
		dynamicCompositions.add(composition);
	}

	private void unregisterDynamicImpl(Composition composition) {
		dynamicCompositions.remove(composition);
	}

	public void setViewportImpl(int x, int y, int width, int height) {
		rootComposition.setBounds(x, y, width, height);
		tessellator.setViewport(x, y, x + width, y + height);
	}
	
	public void drawImpl() {
		long now = System.currentTimeMillis();
		long deltaMillis = Math.max(now - prevFrameMillis, 0L);
		prevFrameMillis = now;
	
		if (deltaMillis > MAX_VALID_DELTA_MILLIS) {
			// Time has changed drastically. Make sure we do
			// not get any overflows down the draw-chain.
			deltaMillis = MAX_VALID_DELTA_MILLIS;
		}
		
		updateTimers((int)deltaMillis);

		layoutAndDrawCompositions((int)deltaMillis);
	}
	
	private void updateTimers(int deltaMillis) {
		Iterator<Timer> itr = timers.iterator();
		while (itr.hasNext()) {
			Timer timer = itr.next();
			if (!timer.update(deltaMillis))
				itr.remove();
		}
	}
	
	private void layoutAndDrawCompositions(int deltaMillis) {
		updateDynamicCompositions(deltaMillis);

		if (rootComposition.isLayoutRequired())
			rootComposition.layout();

		if (rootComposition.isDrawRequired())
			rootComposition.draw(tessellator);
		
		drawDynamicCompositions();

		tessellator.drawLayers();
	}
	
	private void updateDynamicCompositions(int deltaMillis) {
		Iterator<Composition> itr = dynamicCompositions.iterator();

		while (itr.hasNext()) {
			Composition dynamicComposition = itr.next();
			if (!dynamicComposition.dynamicUpdate(deltaMillis))
				itr.remove();
		}
	}
	
	private void drawDynamicCompositions() {
		if (dynamicCompositions.isEmpty())
			return;
		
		for (Composition composition : dynamicCompositions) {
			if (composition.isVisible()) {
				VertexLayerID layerId = composition.getCachedLayerId();
				tessellator.rebuildLayer(layerId, false);

				composition.drawComposition(tessellator);

				tessellator.finishRebuilding();
			}
		}
	}
	
	private IResourceManager getResourceManagerImpl() {
		if (resourceManager == null)
			throw new IllegalStateException("Resource manager has not been initialized!");
		return resourceManager;
	}
	
	private void startTimerImpl(Timer timer) {
		timers.add(timer);
	}

	private void stopTimerImpl(Timer timer) {
		timers.remove(timer);
	}
	
	private void setCursorImpl(CursorType cursor) {
		if (cursor == null)
			throw new IllegalArgumentException("cursor is null!");
		
		switch (cursor) {
		case DEFAULT:
			display.setCursor(GLFW.GLFW_ARROW_CURSOR);
			break;
		case IBEAM:
			display.setCursor(GLFW.GLFW_IBEAM_CURSOR);
			break;
		case CROSSHAIR:
			display.setCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
			break;
		case HAND:
			display.setCursor(GLFW.GLFW_HAND_CURSOR);
			break;
		case HRESIZE:
			display.setCursor(GLFW.GLFW_HRESIZE_CURSOR);
			break;
		case VRESIZE:
			display.setCursor(GLFW.GLFW_VRESIZE_CURSOR);
			break;
		default:
			throw new IllegalStateException("Unsupported cursor");
		}
	}
	
	private String getClipboardStringImpl() {
		return display.getClipboardString();
	}

	private void setClipboardStringImpl(String clipboard) {
		display.setClipboardString(clipboard);
	}
	
	private static CompositionContext getContext() {
		if (instance == null)
			throw new IllegalStateException("Context is not initialized!");
		return instance;
	}
}
