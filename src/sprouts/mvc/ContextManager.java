package sprouts.mvc;

import java.util.HashMap;
import java.util.Map;

public class ContextManager {
	
	private Map<String, MVCContext> nameToMVC;
	private MVCContext active;
	
	public ContextManager() {
		nameToMVC = new HashMap<>();
	}
	
	public void addMVCContext(String name, Model model, View view, Controller controller) {
		MVCContext context = new MVCContext();
		context.model = model;
		context.view = view;
		context.controller = controller;
		
		model.manager = this;
		
		model.create();
		view.create();
		
		nameToMVC.put(name, context);
	}
	
	public void setActiveContext(String name) {
		MVCContext newActive = nameToMVC.get(name);
		
		if (newActive == null) {
			String error = String.format("unknown MVC-context '%s'\n", name);
			throw new IllegalStateException(error);
		}
		
		active = newActive;
		
		active.model.enter();
		active.view.enter();
		active.controller.enter();
		
		// @todo
		//active.view.resize(width, height);
	}

	public MVCContext getActiveContext() {
		return active;
	}
}
