package sprouts.test.concrete;

import sprouts.representation.concrete.ConcreteFacade;
import sprouts.test.Test;

public class RunConcreteTest {
	
	public static void main(String[] args) {
		ConcreteFacade facade = new ConcreteFacade();
	
		facade.createFreshGame(3);
		facade.makeMove("1,2");
		Test.equals("A.BDCD.}!", facade.getPosition());
		facade.makeMove("0,1");
		Test.equals("AEBDCDBE.}!", facade.getPosition());

		facade.buildGame("A.BDCD.}!");
		facade.makeMove("1,1[0]");
		Test.equals("BDCDBE.A.}BE.}!", facade.getPosition());
	}
}