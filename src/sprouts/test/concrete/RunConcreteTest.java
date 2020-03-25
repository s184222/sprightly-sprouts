package sprouts.test.concrete;

import sprouts.representation.concrete.ConcreteFacade;
import sprouts.test.Test;

public class RunConcreteTest {
	
	public static void main(String[] args) {
		ConcreteFacade facade = new ConcreteFacade();
	
		facade.buildGame("BDAE.}ADBE.C.}!");
		
		facade.createFreshGame(3);
		
		facade.makeMove("0,1");
		Test.equals("C.ADBD.}!", facade.getPosition());
		
		facade.makeMove("0,1");
		Test.equals("BDAE.}ADBE.C.}!", facade.getPosition());
	}
}