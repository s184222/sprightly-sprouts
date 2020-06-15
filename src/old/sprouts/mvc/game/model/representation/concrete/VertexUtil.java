package old.sprouts.mvc.game.model.representation.concrete;

public class VertexUtil {
	
	private static final char startCharacter = 'A';
	
	public static char getName(int id) {
		return (char) (startCharacter + id);
	}
	
	public static int getId(char name) {
		return name - startCharacter;
	}
}
