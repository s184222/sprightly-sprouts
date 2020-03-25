package sprouts.test;

public class Test {

	private static <T> void error(T is, T shouldBe) {
		String error = String.format("should be: '%s', but is: '%s'.", shouldBe, is);
		throw new TestException(error);
	}

	public static void equals(String shouldBe, String is) {
		if (!shouldBe.equals(is)) error(shouldBe, is);
	}
}
