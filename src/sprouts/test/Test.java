package sprouts.test;

public class Test {

	private static <T> void error(T is, T shouldBe) {
		String error = String.format("is: '%s', but should be: '%s'.", is, shouldBe);
		throw new TestException(error);
	}

	public static void equals(String is, String shouldBe) {
		if (!shouldBe.equals(is)) error(is, shouldBe);
	}
}
