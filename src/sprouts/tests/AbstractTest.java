package sprouts.tests;

import junit.framework.TestCase;
import sprouts.game.ai.AbstractFacade;

public class AbstractTest extends TestCase {
	
	public void test1() {
		AbstractFacade facade = new AbstractFacade();
		
		facade.createFreshPosition(3);
		facade.makeMove("1<,2<");
		assertEquals("0.1,3,2,3.}!", facade.getPositionString());
		
		facade.makeMove("0<,1<");
		assertEquals("0,4,1,3,2,3,1,4.}!", facade.getPositionString());
	}
	
	/*
	 * 0.1.3 2.4.1 3.1}7 1 }!
	 */
	
	public void test2() {
		AbstractFacade facade = new AbstractFacade();
		
		facade.buildPosition("0.1,3,2,3.}!");
		facade.makeMove("1<,1<,[0]");
		assertEquals("1,4,1,3,2,3.}1,4.0.}!", facade.getPositionString());

		facade.makeMove("2<,4<");
		assertEquals("1,4.0.}1,4,5,2,3.}4,1,3,2,5.}!", facade.getPositionString());
	}
	
	public void test3() {
		AbstractFacade facade = new AbstractFacade();
		
		facade.buildPosition("0,4.1.2.3.}!");
		facade.makeMove("0<,4<,[1,2]");
		assertEquals("0,5,4.3.}0,4,5.1.2.}!", facade.getPositionString());
	}
	
	public void test4() {
		AbstractFacade facade = new AbstractFacade();
		
		facade.buildPosition("0,6,1,5.2.3.4.}0,5,1,6.}!");
		facade.makeMove("0<,1>,[2,3]");
		assertEquals("0,5,1,6.}0,7,1,5.4.}0,6,1,7.2.3.}!", facade.getPositionString());
	}
	
	public void test5() {
		AbstractFacade facade = new AbstractFacade();

		facade.createFreshPosition(5);
    facade.makeMove("0<,1<");
    assertEquals("2.3.4.0,5,1,5.}!", facade.getPositionString());
    
    facade.makeMove("4<,4<,[2,3,5]");
    assertEquals("4,6.}4,6.2.3.0,5,1,5.}!", facade.getPositionString());
    
    facade.makeMove("6<,4<,[0]");
    assertEquals("4,6.}4,7,6.0,5,1,5.}4,6,7.2.3.}!", facade.getPositionString());
	}
}
