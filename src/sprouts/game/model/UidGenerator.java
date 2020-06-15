package sprouts.game.model;

public class UidGenerator {
	
	public int nextId;
	
	public void reset() {
		nextId = 0;
	}
	
	public int generate() {
		int id = nextId;
		nextId += 1;
		return id;
	}
}
