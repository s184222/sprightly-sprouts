package com.sprouts.tests;

import com.sprouts.ai.AIFacade;

import junit.framework.TestCase;

/**
 * 
 * @author Rasmus Møller Larsen, s184190
 *
 */

public class AITest extends TestCase {
	
	public void test1() {
		AIFacade facade = new AIFacade();
		
		facade.createFreshPosition(3);
		facade.makeMove("2<,3<");
		assertEquals("1.2,4,3,4.}!", facade.getPositionString());
		
		facade.makeMove("1<,2<");
		assertEquals("1,5,2,4,3,4,2,5.}!", facade.getPositionString());
	}
	
	public void test2() {
		AIFacade facade = new AIFacade();
		
		facade.buildPosition("1.2,4,3,4.}!");
		facade.makeMove("2<,2<,[1]");
		assertEquals("2,5,2,4,3,4.}2,5.1.}!", facade.getPositionString());

		facade.makeMove("3<,5<");
		assertEquals("2,5.1.}2,5,6,3,4.}5,2,4,3,6.}!", facade.getPositionString());
	}
	
	public void test3() {
		AIFacade facade = new AIFacade();
		
		facade.buildPosition("1,5.2.3.4.}!");
		facade.makeMove("1<,5<,[2,3]");
		assertEquals("1,6,5.4.}1,5,6.2.3.}!", facade.getPositionString());
	}
	
	public void test4() {
		AIFacade facade = new AIFacade();
		
		facade.buildPosition("1,7,2,6.3.4.5.}1,6,2,7.}!");
		facade.makeMove("1<,2>,[3,4]");
		assertEquals("1,6,2,7.}1,8,2,6.5.}1,7,2,8.3.4.}!", facade.getPositionString());
	}
	
	public void test5() {
		AIFacade facade = new AIFacade();

		facade.createFreshPosition(5);
	    facade.makeMove("1<,2<");
	    assertEquals("3.4.5.1,6,2,6.}!", facade.getPositionString());
	    
	    facade.makeMove("5<,5<,[3,4,6]");
	    assertEquals("5,7.}5,7.3.4.1,6,2,6.}!", facade.getPositionString());
	    
	    facade.makeMove("7<,5<,[1]");
	    assertEquals("5,7.}5,8,7.1,6,2,6.}5,7,8.3.4.}!", facade.getPositionString());
	}
}
