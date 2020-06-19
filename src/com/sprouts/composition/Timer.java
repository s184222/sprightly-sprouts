package com.sprouts.composition;

import java.util.function.Consumer;

public class Timer {

	private int intervalMillis;
	
	private final Consumer<Timer> listener;
	
	private int timerMillis;
	
	private boolean stopRunning;
	private boolean running;

	public Timer(int intervalMillis, Consumer<Timer> listener) {
		if (intervalMillis <= 0)
			throw new IllegalArgumentException("intervalMillis must be positive!");
		if (listener == null)
			throw new IllegalArgumentException("listener is null!");

		this.intervalMillis = intervalMillis;
		this.listener = listener;
		
		timerMillis = 0;
		
		stopRunning = running = false;
	}
	
	protected boolean update(int deltaMillis) {
		if (!running || stopRunning) {
			stopRunning = running = false;
			return false;
		}
		
		timerMillis += deltaMillis;
		
		if (timerMillis >= intervalMillis) {
			timerMillis %= intervalMillis;
			
			listener.accept(this);
		}
		
		return running;
	}

	public int getIntervalMillis() {
		return intervalMillis;
	}
	
	public void setIntervalMillis(int intervalMillis) {
		if (intervalMillis <= 0)
			throw new IllegalArgumentException("intervalMillis must be positive!");
		
		this.intervalMillis = intervalMillis;
	}

	public void resetTimer() {
		timerMillis = 0;
	}
	
	public void stop() {
		if (isRunning())
			stopRunning = true;
	}

	public void start() {
		if (isRunning()) {
			stopRunning = false;
		} else {
			resetTimer();
			running = true;
			
			CompositionContext.startTimer(this);
		}
	}
	
	public boolean isRunning() {
		return running;
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}
}
