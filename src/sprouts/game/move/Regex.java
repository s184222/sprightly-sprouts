package sprouts.game.move;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class Regex {
	
	public static List<String> match(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		
		boolean match = matcher.matches();
		if (!match) throw new IllegalStateException("no match!");
		
		List<String> matches = new LinkedList<>();
		for (int i = 0; i <= matcher.groupCount(); i++) {
			String stringMatch = matcher.group(i);
			matches.add(stringMatch);
		}
		
		return matches;
	}
}
