package sprouts.mvc.game.model;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Console {
	
	private Scanner scanner;
	private boolean running;
	
	public Console() {
		scanner = new Scanner(System.in);
	}
	
	public abstract void loop();
	
	private void mainLoop() {
		while (running) {
			loop();
		}
	}
	
	public void start() {
		ExecutorService threadPool = Executors.newSingleThreadExecutor();

		running = true;
		threadPool.execute(() -> mainLoop());
	}
	
	public String prompt(String message) {
		System.out.printf(">%s\n", message);
		String answer = scanner.nextLine();
		return answer;
	}
	
	public void stop() {
		running = false;
	}
}
