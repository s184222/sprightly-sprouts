package com.sprouts.game;

/**
 * Simple Uid generator
 * 
 * @author Rasmus Møller Larsen
 *
 */

public class UidGenerator {
	
	private int nextUid;
	
	public UidGenerator() {
		nextUid = 1;
	}
	
	public int generate() {
		int uid = nextUid;
		nextUid += 1;
		return uid;
	}
	
	public void reset() {
		nextUid = 1;
	}

	public void update(int id) {
		if (id >= nextUid) nextUid = id + 1;
	}
	
	public int peek() {
		return nextUid;
	}
}
