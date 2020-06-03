package sprouts.test.concrete;

import sprouts.mvc.game.model.representation.concrete.ConcreteFacade;
import sprouts.test.Assert;

public class RunConcreteTest {
	
	public static void main(String[] args) {
		ConcreteFacade facade = new ConcreteFacade();
	
		facade.createFreshPosition(3);
		facade.makeMove("1,2");
		Assert.equals("A.BDCD.}!", facade.getPosition());
		facade.makeMove("0,1");
		Assert.equals("AEBDCDBE.}!", facade.getPosition());

		facade.buildPosition("A.BDCD.}!");
		facade.makeMove("1,1[0]{1}");
		Assert.equals("BDCDBE.}BE.A.}!", facade.getPosition());
		facade.makeMove("2,4[]{1,2,3,4}");
		Assert.equals("BE.A.}EBDCF.}CDBEF.}!", facade.getPosition());

		facade.buildPosition("AE.B.C.D.}!");
		facade.makeMove("0,4[1,2]{0,4}");
		Assert.equals("EAF.B.C.}AEF.D.}!", facade.getPosition());
		
		facade.buildPosition("AGBF.C.D.E.}AGBF.}!");
		facade.makeMove("0,1[2,3]{1,0,5}");
		Assert.equals("AGBF.}BFAH.C.D.}AGBH.E.}!", facade.getPosition());
		
		facade.createFreshPosition(5);
    facade.makeMove("0,1");
    Assert.equals("C.D.E.AFBF.}!", facade.getPosition());
    facade.makeMove("4,4[0,1,2,3,5]{4}");
    Assert.equals("EG.C.D.AFBF.}EG.}!", facade.getPosition());
    facade.makeMove("6,4[0,1,5]{4,6}");
    Assert.equals("EG}GEH.AFBF.}GEH.C.D.}!", facade.getPosition());
	}
}
