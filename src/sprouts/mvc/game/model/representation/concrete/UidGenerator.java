package sprouts.mvc.game.model.representation.concrete;

public class UidGenerator {
	
	private int nextUid;
	
	public UidGenerator() {
		nextUid = 0;
	}
	
	public int generate() {
		int uid = nextUid;
		nextUid += 1;
		return uid;
	}
	
	public void reset() {
		nextUid = 0;
	}

	public void update(int id) {
		if (id >= nextUid) nextUid = id + 1;
	}
}
