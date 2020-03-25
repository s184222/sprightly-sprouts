package sprouts.test.concrete;

import sprouts.representation.concrete.ConcreteFacade;
import sprouts.test.Test;

public class RunConcreteTest {
	
	public static void main(String[] args) {
		ConcreteFacade facade = new ConcreteFacade();
	
		facade.createFreshPosition(3);
		facade.makeMove("1,2");
		Test.equals("A.BDCD.}!", facade.getPosition());
		facade.makeMove("0,1");
		Test.equals("AEBDCDBE.}!", facade.getPosition());

		facade.buildPosition("A.BDCD.}!");
		facade.makeMove("1,1[0]{1,4}");
		Test.equals("BDCDBE.}BE.A.}!", facade.getPosition());
		facade.makeMove("2,4");
		Test.equals("BE.A.}EBDCF.}CDBEF.}!", facade.getPosition());

		facade.buildPosition("AE.B.C.D.}!");
		facade.makeMove("0,4[1,2]{0,4}");
		Test.equals("EAF.D.}AEF.B.C.}!", facade.getPosition());
		
		facade.buildPosition("AGBF.C.D.E.}AGBF.}!");
		facade.makeMove("0,1[2,3]{4,2,1,3}");
		Test.equals("AGBF.}BFAH.C.D.}AGBH.E.}!", facade.getPosition());
	}
}
