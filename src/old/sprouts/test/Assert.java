package old.sprouts.test;

public class Assert {

	private static <T> void error(T shouldBe, T is) {
		String error = String.format("should be: '%s', but is: '%s'.", shouldBe, is);
		throw new TestException(error);
	}

	public static void equals(String shouldBe, String is) {
		if (!shouldBe.equals(is)) error(shouldBe, is);
	}
	
	public static void assertTrue(boolean condition) {
		if (!condition) error(true, condition);
	}
	
}
