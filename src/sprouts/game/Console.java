package sprouts.game;

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
		running = true;
		
		Thread thread = new Thread(this::mainLoop);
		thread.setDaemon(true);
		thread.start();
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
