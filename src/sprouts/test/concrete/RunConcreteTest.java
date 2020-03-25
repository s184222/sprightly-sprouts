package sprouts.test.concrete;

import sprouts.representation.concrete.ConcreteFacade;
import sprouts.test.Test;

public class RunConcreteTest {
	
	public static void main(String[] args) {
		ConcreteFacade facade = new ConcreteFacade();
	
		facade.buildGame("BDAE.}ADBE.C.}!");
		
		facade.createFreshGame(3);
		
		facade.makeMove("1,2");
		Test.equals("A.BDCD.}!", facade.getPosition());
		
		facade.makeMove("1,1");
		Test.equals("BDCDBE.}BE.A.}!", facade.getPosition());
	}
}