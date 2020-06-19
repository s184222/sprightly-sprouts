package sprouts.tests;

import junit.framework.TestCase;
import sprouts.game.GraphicalFacade;
import sprouts.game.model.Line;
import sprouts.game.model.Vertex;

public class Test extends TestCase {
	
	public void test1() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		String move0 = facade.executeMove("1<,2<");
		assertEquals("1<,2<,[]", move0);
		
		Line line1 = new Line();
		line1.add(new Vertex(213.93399f,346.066f));
		line1.add(new Vertex(229.60004f,324.0f));
		line1.add(new Vertex(240.80003f,297.59998f));
		line1.add(new Vertex(239.20003f,267.19998f));
		line1.add(new Vertex(232.00003f,242.4f));
		line1.add(new Vertex(231.83553f,242.00243f));
		line1.add(new Vertex(222.40002f,219.19998f));
		line1.add(new Vertex(212.00003f,193.59998f));
		line1.add(new Vertex(212.80003f,167.99998f));
		line1.add(new Vertex(216.00003f,142.4f));
		line1.add(new Vertex(213.93398f,133.93399f));
		String move1 = facade.executeLine(line1);
		assertEquals("3<,5<,[]", move1);
		
		Line line2 = new Line();
		line2.add(new Vertex(320.0f,90.0f));
		line2.add(new Vertex(326.40002f,118.39999f));
		line2.add(new Vertex(342.40002f,141.59999f));
		line2.add(new Vertex(357.36282f,149.08139f));
		line2.add(new Vertex(368.00003f,154.4f));
		line2.add(new Vertex(393.60004f,149.59998f));
		line2.add(new Vertex(416.80002f,137.59999f));
		line2.add(new Vertex(426.06598f,133.93396f));
		String move2 = facade.executeLine(line2);
		assertEquals("6<,7<,[]", move2);
		
		Line line3 = new Line();
		line3.add(new Vertex(231.83553f,242.00243f));
		line3.add(new Vertex(277.60004f,240.79999f));
		line3.add(new Vertex(303.2f,238.4f));
		line3.add(new Vertex(344.00003f,233.59998f));
		line3.add(new Vertex(350.96927f,233.59998f));
		line3.add(new Vertex(369.60004f,233.59998f));
		line3.add(new Vertex(396.80002f,236.79999f));
		line3.add(new Vertex(426.40002f,236.79999f));
		line3.add(new Vertex(454.40002f,236.79999f));
		line3.add(new Vertex(470.0f,240.0f));
		String move3 = facade.executeLine(line3);
		assertEquals("9<,0<,[]", move3);
		
		String move4 = facade.executeMove("2<,1<,[9,10,4]");
		assertEquals("2<,1<,[0,4,6]", move4);
		
	}
	
	public void test2() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		String move0 = facade.executeMove("1<,2<");
		assertEquals("1<,2<,[]", move0);
		
		Line line1 = new Line();
		line1.add(new Vertex(213.93399f,346.066f));
		line1.add(new Vertex(239.20003f,334.4f));
		line1.add(new Vertex(260.80005f,319.99997f));
		line1.add(new Vertex(273.60004f,297.59998f));
		line1.add(new Vertex(276.00003f,269.59998f));
		line1.add(new Vertex(276.00003f,241.59998f));
		line1.add(new Vertex(267.2f,215.19998f));
		line1.add(new Vertex(256.00003f,192.79999f));
		line1.add(new Vertex(244.00003f,170.39998f));
		line1.add(new Vertex(230.40002f,147.99998f));
		line1.add(new Vertex(213.93398f,133.93399f));
		String move1 = facade.executeLine(line1);
		assertEquals("3<,5<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(276.00003f,246.37297f));
		line2.add(new Vertex(248.00003f,246.39998f));
		line2.add(new Vertex(220.80003f,246.39998f));
		line2.add(new Vertex(193.60004f,243.19998f));
		line2.add(new Vertex(170.00002f,239.99998f));
		String move2 = facade.executeLine(line2);
		assertEquals("9>,4<,[]", move2);

		Line line3 = new Line();
		line3.add(new Vertex(320.0f,90.0f));
		line3.add(new Vertex(320.00003f,115.19999f));
		line3.add(new Vertex(323.20004f,140.79999f));
		line3.add(new Vertex(338.40002f,162.4f));
		line3.add(new Vertex(362.40002f,169.59998f));
		line3.add(new Vertex(387.20004f,176.79999f));
		line3.add(new Vertex(413.60004f,180.79999f));
		line3.add(new Vertex(440.00003f,182.39998f));
		line3.add(new Vertex(464.00003f,190.4f));
		line3.add(new Vertex(472.80002f,214.4f));
		line3.add(new Vertex(470.0f,240.0f));
		String move3 = facade.executeLine(line3);
		assertEquals("6<,0<,[]", move3);

		String move4 = facade.executeMove("2<,2<,[10,6,7]");
		assertEquals("2<,2<,[0,3,7]", move4);
	}
	
	public void test3() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		String move0 = facade.executeMove("1<,2<");
		assertEquals("1<,2<,[]", move0);
		
		Line line1 = new Line();
		line1.add(new Vertex(213.93399f,346.066f));
		line1.add(new Vertex(229.60004f,318.39996f));
		line1.add(new Vertex(241.60004f,288.8f));
		line1.add(new Vertex(242.40002f,263.19998f));
		line1.add(new Vertex(241.67867f,237.95181f));
		line1.add(new Vertex(241.60004f,235.19998f));
		line1.add(new Vertex(239.20003f,207.99998f));
		line1.add(new Vertex(233.60004f,180.79999f));
		line1.add(new Vertex(228.00003f,153.59999f));
		line1.add(new Vertex(216.80003f,131.19998f));
		line1.add(new Vertex(213.93398f,133.93399f));
		String move1 = facade.executeLine(line1);
		assertEquals("3<,5<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(320.0f,90.0f));
		line2.add(new Vertex(347.20004f,103.2f));
		line2.add(new Vertex(372.00003f,112.79998f));
		line2.add(new Vertex(374.4053f,113.49829f));
		line2.add(new Vertex(396.80002f,119.999985f));
		line2.add(new Vertex(421.60004f,123.99999f));
		line2.add(new Vertex(426.06598f,133.93396f));
		String move2 = facade.executeLine(line2);
		assertEquals("6<,7<,[]", move2);
		
		String move3 = facade.executeMove("2<,1<,[9,10,0]");
		assertEquals("2<,1<,[0,3,6]", move3);
	}
	
	public void test4() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		String move0 = facade.executeMove("1<,2<");
		assertEquals("1<,2<,[]", move0);
		
		Line input1 = new Line();
		input1.add(new Vertex(213.93399f,346.066f));
		input1.add(new Vertex(228.80003f,324.8f));
		input1.add(new Vertex(239.20003f,300.8f));
		input1.add(new Vertex(247.20003f,276.0f));
		input1.add(new Vertex(251.20003f,248.79999f));
		input1.add(new Vertex(251.4797f,239.29118f));
		input1.add(new Vertex(252.00003f,221.59998f));
		input1.add(new Vertex(248.80003f,195.19998f));
		input1.add(new Vertex(239.20003f,171.19998f));
		input1.add(new Vertex(224.80003f,150.4f));
		input1.add(new Vertex(213.93398f,133.93399f));
		String move1 = facade.executeLine(input1);
		assertEquals("3<,5<,[]", move1);
		
		Line input2 = new Line();
		input2.add(new Vertex(251.4797f,239.29118f));
		input2.add(new Vertex(224.00003f,239.19998f));
		input2.add(new Vertex(210.7343f,239.19998f));
		input2.add(new Vertex(198.40002f,239.19998f));
		input2.add(new Vertex(170.00002f,239.99998f));
		String move2 = facade.executeLine(input2);
		assertEquals("9>,4<,[]", move2);

		String move3 = facade.executeMove("2<,1<,[10]");
		assertEquals("2<,1<,[3]", move3);
	}
	
	public void test5() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(213.93399f,346.066f));
		line0.add(new Vertex(235.20003f,324.0f));
		line0.add(new Vertex(255.20003f,303.19998f));
		line0.add(new Vertex(252.80003f,275.19998f));
		line0.add(new Vertex(245.60004f,247.19998f));
		line0.add(new Vertex(244.99248f,245.94148f));
		line0.add(new Vertex(234.40002f,223.99998f));
		line0.add(new Vertex(222.40002f,198.4f));
		line0.add(new Vertex(220.80003f,171.19998f));
		line0.add(new Vertex(217.60004f,145.59998f));
		line0.add(new Vertex(213.93398f,133.93399f));
		String move0 = facade.executeLine(line0);
		assertEquals("3<,5<,[]", move0);
		
		String move1 = facade.executeMove("2<,2<,[8]");
		assertEquals("2<,2<,[3]", move1);
	}
	
	public void test6() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(213.93399f,346.066f));
		line0.add(new Vertex(232.80003f,329.59998f));
		line0.add(new Vertex(253.60004f,311.19998f));
		line0.add(new Vertex(272.00003f,292.0f));
		line0.add(new Vertex(282.40002f,268.0f));
		line0.add(new Vertex(279.2f,240.79999f));
		line0.add(new Vertex(264.00003f,219.19998f));
		line0.add(new Vertex(240.00003f,209.59998f));
		line0.add(new Vertex(217.60004f,197.59998f));
		line0.add(new Vertex(209.60004f,173.59998f));
		line0.add(new Vertex(212.00003f,147.99998f));
		line0.add(new Vertex(213.93398f,133.93399f));
		String move0 = facade.executeLine(line0);
		assertEquals("3<,5<,[]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(276.34195f,236.73851f));
		line1.add(new Vertex(301.60004f,232.79999f));
		line1.add(new Vertex(326.40002f,219.19998f));
		line1.add(new Vertex(348.00003f,203.19998f));
		line1.add(new Vertex(369.60004f,189.59999f));
		line1.add(new Vertex(394.40002f,186.4f));
		line1.add(new Vertex(420.80002f,187.99998f));
		line1.add(new Vertex(444.00003f,199.99998f));
		line1.add(new Vertex(464.00003f,219.19998f));
		line1.add(new Vertex(476.00003f,241.59998f));
		line1.add(new Vertex(470.0f,240.0f));
		String move1 = facade.executeLine(line1);
		assertEquals("8<,0<,[]", move1);

		String move2 = facade.executeMove("2<,2<,[8]");
		assertEquals("2<,2<,[0]", move2);
	}
	
	public void test7() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(320.0f,390.0f));
		line0.add(new Vertex(345.60004f,383.19995f));
		line0.add(new Vertex(369.60004f,372.0f));
		line0.add(new Vertex(373.809f,370.1586f));
		line0.add(new Vertex(395.20004f,360.8f));
		line0.add(new Vertex(417.60004f,349.59998f));
		line0.add(new Vertex(426.066f,346.066f));
		String move0 = facade.executeLine(line0);
		assertEquals("2<,1<,[]", move0);

		String move1 = facade.executeMove("2<,1<,[3,5,0,7]");
		assertEquals("2<,1<,[0,3,5,7]", move1);
	}
	
	public void test8() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);

		Line line0 = new Line();
		line0.add(new Vertex(213.93399f,346.066f));
		line0.add(new Vertex(234.40002f,327.19998f));
		line0.add(new Vertex(248.00003f,304.8f));
		line0.add(new Vertex(258.40002f,279.19998f));
		line0.add(new Vertex(264.80005f,251.19998f));
		line0.add(new Vertex(265.60004f,224.79999f));
		line0.add(new Vertex(263.2f,198.4f));
		line0.add(new Vertex(256.80005f,173.59998f));
		line0.add(new Vertex(245.60004f,151.19998f));
		line0.add(new Vertex(224.00003f,137.59999f));
		line0.add(new Vertex(213.93398f,133.93399f));
		String move0 = facade.executeLine(line0);
		assertEquals("3<,5<,[]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(265.2456f,236.49629f));
		line1.add(new Vertex(239.20003f,233.59998f));
		line1.add(new Vertex(212.00003f,232.79999f));
		line1.add(new Vertex(187.20003f,236.79999f));
		line1.add(new Vertex(170.00002f,239.99998f));
		String move1 = facade.executeLine(line1);
		assertEquals("8>,4<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(320.0f,90.0f));
		line2.add(new Vertex(324.00003f,115.99998f));
		line2.add(new Vertex(321.60004f,141.59999f));
		line2.add(new Vertex(320.80002f,167.99998f));
		line2.add(new Vertex(326.40002f,193.59998f));
		line2.add(new Vertex(339.20004f,216.79999f));
		line2.add(new Vertex(362.40002f,228.79999f));
		line2.add(new Vertex(387.20004f,235.99998f));
		line2.add(new Vertex(409.60004f,249.59998f));
		line2.add(new Vertex(422.40002f,272.8f));
		line2.add(new Vertex(428.00003f,297.59998f));
		line2.add(new Vertex(431.20004f,323.19998f));
		line2.add(new Vertex(426.066f,346.066f));
		String move2 = facade.executeLine(line2);
		assertEquals("6<,1<,[]", move2);

		Line line3 = new Line();
		line3.add(new Vertex(360.5938f,227.86575f));
		line3.add(new Vertex(377.60004f,208.79999f));
		line3.add(new Vertex(399.20004f,195.99998f));
		line3.add(new Vertex(424.80002f,191.19998f));
		line3.add(new Vertex(449.60004f,201.59999f));
		line3.add(new Vertex(460.80002f,223.99998f));
		line3.add(new Vertex(470.0f,240.0f));
		String move3 = facade.executeLine(line3);
		assertEquals("10<,0<,[]", move3);

		String move4 = facade.executeMove("4<,4<,[2,10]");
		assertEquals("4<,4<,[0,2]", move4);
	}
	
	public void test9() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(320.0f,390.0f));
		line0.add(new Vertex(350.59122f,371.75568f));
		line0.add(new Vertex(371.55548f,363.67188f));
		line0.add(new Vertex(382.79248f,359.3389f));
		line0.add(new Vertex(409.01352f,355.65985f));
		line0.add(new Vertex(426.066f,346.066f));
		String move0 = facade.executeLine(line0);
		assertEquals("2<,1<,[]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(426.066f,346.066f));
		line1.add(new Vertex(443.51486f,320.70895f));
		line1.add(new Vertex(460.0755f,296.7952f));
		line1.add(new Vertex(460.5962f,295.99704f));
		line1.add(new Vertex(473.87604f,275.64072f));
		line1.add(new Vertex(474.33606f,250.34729f));
		line1.add(new Vertex(470.0f,240.0f));
		String move1 = facade.executeLine(line1);
		assertEquals("1<,0<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(470.0f,240.0f));
		line2.add(new Vertex(461.45557f,210.3377f));
		line2.add(new Vertex(456.39536f,185.0443f));
		line2.add(new Vertex(455.37067f,182.22728f));
		line2.add(new Vertex(447.195f,159.75089f));
		line2.add(new Vertex(435.69455f,136.75687f));
		line2.add(new Vertex(426.06598f,133.93396f));
		String move2 = facade.executeLine(line2);
		assertEquals("0<,7<,[]", move2);

		String move3 = facade.executeMove("0>,5<");
		assertEquals("0>,5<,[]", move3);

		String move4 = facade.executeMove("11>,8<");
		assertEquals("11>,8<,[]", move4);

		String move5 = facade.executeMove("12>,2<");
		assertEquals("12>,2<,[]", move5);

		String move6 = facade.executeMove("13<,2<");
		assertEquals("13<,2<,[]", move6);
	}
	
	public void test10() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(213.93398f,133.93399f));
		line0.add(new Vertex(224.00003f,159.19998f));
		line0.add(new Vertex(233.60004f,183.99998f));
		line0.add(new Vertex(243.20003f,207.19998f));
		line0.add(new Vertex(255.20003f,233.59998f));
		line0.add(new Vertex(264.80005f,256.8f));
		line0.add(new Vertex(277.60004f,284.8f));
		line0.add(new Vertex(288.80005f,307.99997f));
		line0.add(new Vertex(301.60004f,332.8f));
		line0.add(new Vertex(314.40002f,354.39996f));
		line0.add(new Vertex(320.00003f,379.2f));
		line0.add(new Vertex(320.0f,390.0f));
		String move0 = facade.executeLine(line0);
		assertEquals("5<,2<,[]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(320.0f,390.0f));
		line1.add(new Vertex(332.00003f,361.59998f));
		line1.add(new Vertex(343.20004f,338.4f));
		line1.add(new Vertex(353.60004f,312.8f));
		line1.add(new Vertex(362.40002f,284.8f));
		line1.add(new Vertex(371.20004f,256.0f));
		line1.add(new Vertex(378.40002f,227.19998f));
		line1.add(new Vertex(385.60004f,201.59999f));
		line1.add(new Vertex(389.60004f,175.99998f));
		line1.add(new Vertex(402.40002f,153.59999f));
		line1.add(new Vertex(419.20004f,134.39998f));
		line1.add(new Vertex(426.06598f,133.93396f));
		String move1 = facade.executeLine(line1);
		assertEquals("2<,7<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(370.71698f,257.58096f));
		line2.add(new Vertex(344.00003f,264.0f));
		line2.add(new Vertex(317.60004f,268.8f));
		line2.add(new Vertex(292.00003f,270.39996f));
		line2.add(new Vertex(267.4576f,262.61343f));
		String move2 = facade.executeLine(line2);
		assertEquals("9>,8<,[]", move2);

		Line line3 = new Line();
		line3.add(new Vertex(170.00002f,239.99998f));
		line3.add(new Vertex(180.00003f,265.59998f));
		line3.add(new Vertex(189.60004f,289.59998f));
		line3.add(new Vertex(200.00003f,312.8f));
		line3.add(new Vertex(208.80003f,336.8f));
		line3.add(new Vertex(213.93399f,346.066f));
		String move3 = facade.executeLine(line3);
		assertEquals("4<,3<,[]", move3);

		String move4 = facade.executeMove("2<,5<,[11]");
		assertEquals("2<,5<,[3]", move4);
	}
	
	public void test11() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<,[1,2]");

		facade.executeMove("0<,8<,[1,2]!");
	}
	
	public void test12() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<,[1,2]");

		facade.executeMove("0<,8<,[3,4,5,6,7]!");
	}
	
	public void test13() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<,[1,2]");

		facade.executeMove("0<,8<,[3]!");
	}
	
	public void test14() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<");

		facade.executeMove("8<,0<,[]!");
	}
	
	public void test15() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<");

		facade.executeMove("8<,0<,[1]!");
	}
	
	public void test16() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<,[1,2,3,4,5,6,7]");

		facade.executeMove("0<,8<,[1]!");
	}
	
	public void test17() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("0<,0<,[1,2,3,4,5,6,7]");

		facade.executeMove("0<,8<,[]!");
	}
	
	public void test18() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);

		Line line0 = new Line();
		line0.add(new Vertex(320.0f,390.0f));
		line0.add(new Vertex(294.52533f,392.7652f));
		line0.add(new Vertex(269.4682f,391.09564f));
		line0.add(new Vertex(242.74065f,387.75653f));
		line0.add(new Vertex(216.01308f,385.25214f));
		line0.add(new Vertex(190.956f,378.5739f));
		line0.add(new Vertex(166.73413f,365.21738f));
		line0.add(new Vertex(146.68845f,343.51303f));
		line0.add(new Vertex(135.83038f,317.63477f));
		line0.add(new Vertex(129.14848f,287.58258f));
		line0.add(new Vertex(124.97229f,261.7043f));
		line0.add(new Vertex(124.137054f,234.99129f));
		line0.add(new Vertex(134.1599f,211.61737f));
		line0.add(new Vertex(148.35892f,189.91302f));
		line0.add(new Vertex(172.58078f,177.3913f));
		line0.add(new Vertex(197.63788f,183.23477f));
		line0.add(new Vertex(224.36545f,193.25217f));
		line0.add(new Vertex(243.57588f,216.62607f));
		line0.add(new Vertex(255.2692f,250.85217f));
		line0.add(new Vertex(266.12726f,275.06085f));
		line0.add(new Vertex(278.65582f,299.26956f));
		line0.add(new Vertex(290.34912f,321.80865f));
		line0.add(new Vertex(297.86627f,346.85217f));
		line0.add(new Vertex(308.72433f,371.89563f));
		line0.add(new Vertex(320.0f,390.0f));
		String move0 = facade.executeLine(line0);
		assertEquals("2<,2<,[3,4]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(134.97527f,210.371f));
		line1.add(new Vertex(120.79611f,189.07825f));
		line1.add(new Vertex(123.30182f,164.03476f));
		line1.add(new Vertex(138.33609f,143.1652f));
		line1.add(new Vertex(157.54652f,125.634766f));
		line1.add(new Vertex(178.42743f,111.44347f));
		line1.add(new Vertex(200.97882f,98.92174f));
		line1.add(new Vertex(226.03592f,90.5739f));
		line1.add(new Vertex(254.43396f,83.06087f));
		line1.add(new Vertex(279.49106f,80.5565f));
		line1.add(new Vertex(305.3834f,81.3913f));
		line1.add(new Vertex(320.0f,90.0f));
		String move1 = facade.executeLine(line1);
		assertEquals("8<,6<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(213.93398f,133.93399f));
		line2.add(new Vertex(231.88257f,155.68695f));
		line2.add(new Vertex(251.09302f,179.06085f));
		line2.add(new Vertex(268.633f,204.10434f));
		line2.add(new Vertex(284.50247f,228.31302f));
		line2.add(new Vertex(302.04245f,253.3565f));
		line2.add(new Vertex(314.57098f,281.7391f));
		line2.add(new Vertex(323.7586f,306.7826f));
		line2.add(new Vertex(333.78143f,331.82608f));
		line2.add(new Vertex(342.96902f,356.86954f));
		line2.add(new Vertex(351.3214f,382.7478f));
		line2.add(new Vertex(332.11096f,404.45215f));
		line2.add(new Vertex(307.8891f,415.30432f));
		line2.add(new Vertex(282.832f,416.1391f));
		line2.add(new Vertex(252.76349f,411.9652f));
		line2.add(new Vertex(221.0245f,403.61737f));
		line2.add(new Vertex(192.62646f,396.9391f));
		line2.add(new Vertex(167.56937f,389.4261f));
		line2.add(new Vertex(140.8418f,381.91302f));
		line2.add(new Vertex(114.11423f,368.55652f));
		line2.add(new Vertex(98.24472f,348.52173f));
		line2.add(new Vertex(87.38666f,322.64346f));
		line2.add(new Vertex(80.70476f,294.26086f));
		line2.add(new Vertex(79.86952f,263.3739f));
		line2.add(new Vertex(79.86952f,235.82608f));
		line2.add(new Vertex(85.71619f,204.10434f));
		line2.add(new Vertex(92.39807f,176.5565f));
		line2.add(new Vertex(97.409485f,145.66956f));
		line2.add(new Vertex(112.443756f,119.79129f));
		line2.add(new Vertex(131.65419f,99.756516f));
		line2.add(new Vertex(156.71129f,82.226074f));
		line2.add(new Vertex(176.75696f,67.2f));
		line2.add(new Vertex(197.63788f,48.834763f));
		line2.add(new Vertex(217.68355f,33.80867f));
		line2.add(new Vertex(240.23494f,22.121735f));
		line2.add(new Vertex(266.96252f,17.113022f));
		line2.add(new Vertex(297.031f,15.443466f));
		line2.add(new Vertex(321.2529f,26.295639f));
		line2.add(new Vertex(342.96902f,49.669556f));
		line2.add(new Vertex(354.66235f,73.04346f));
		line2.add(new Vertex(362.17947f,99.756516f));
		line2.add(new Vertex(363.0147f,124.79998f));
		line2.add(new Vertex(343.80426f,148.17389f));
		line2.add(new Vertex(322.08813f,164.03476f));
		line2.add(new Vertex(297.031f,164.03476f));
		line2.add(new Vertex(274.47964f,152.34781f));
		line2.add(new Vertex(250.25778f,140.66086f));
		line2.add(new Vertex(225.20068f,134.81738f));
		line2.add(new Vertex(213.93398f,133.93399f));
		String move2 = facade.executeLine(line2);
		assertEquals("5<,5<,[2]", move2);

		Line line3 = new Line();
		line3.add(new Vertex(426.066f,346.066f));
		line3.add(new Vertex(439.85648f,325.1478f));
		line3.add(new Vertex(451.54977f,302.6087f));
		line3.add(new Vertex(461.57263f,278.4f));
		line3.add(new Vertex(471.59546f,255.02608f));
		line3.add(new Vertex(470.0f,240.0f));
		String move3 = facade.executeLine(line3);
		assertEquals("1<,0<,[]", move3);

		Line line4 = new Line();
		line4.add(new Vertex(470.0f,240.0f));
		line4.add(new Vertex(459.06693f,214.12172f));
		line4.add(new Vertex(445.70312f,183.23477f));
		line4.add(new Vertex(433.1746f,157.3565f));
		line4.add(new Vertex(423.15173f,133.98259f));
		line4.add(new Vertex(426.06598f,133.93396f));
		String move4 = facade.executeLine(line4);
		assertEquals("0<,7<,[]", move4);

		Line line5 = new Line();
		line5.add(new Vertex(454.66693f,295.0797f));
		line5.add(new Vertex(480.78305f,276.7304f));
		line5.add(new Vertex(494.14685f,254.1913f));
		line5.add(new Vertex(499.9935f,226.64346f));
		line5.add(new Vertex(487.46497f,203.26956f));
		line5.add(new Vertex(464.91357f,190.7478f));
		line5.add(new Vertex(446.6261f,185.36801f));
		String move5 = facade.executeLine(line5);
		assertEquals("11>,12<,[]", move5);

		String move6 = facade.executeMove("5<,0>");
		assertEquals("5<,0>,[]", move6);

	}
	
	public void test19() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		facade.executeMove("2<,2<,[3,4]");
		facade.executeMove("8<,6<,[]");
		facade.executeMove("5<,5<,[8]");
		facade.executeMove("1<,0<,[]");
		facade.executeMove("0<,7<,[]");
		facade.executeMove("12<,11>,[]");
		facade.executeMove("5<,0<");
	}
	
	public void test20() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);

		Line line0 = new Line();
		line0.add(new Vertex(426.066f,346.066f));
		line0.add(new Vertex(373.60004f,343.19998f));
		line0.add(new Vertex(335.20004f,343.19998f));
		line0.add(new Vertex(319.61258f,343.59964f));
		line0.add(new Vertex(304.00003f,343.99997f));
		line0.add(new Vertex(271.2f,343.99997f));
		line0.add(new Vertex(244.00003f,343.99997f));
		line0.add(new Vertex(218.40002f,343.19998f));
		line0.add(new Vertex(213.93399f,346.066f));
		String move0 = facade.executeLine(line0);
		assertEquals("1<,3<,[]", move0);
		
		Line line1 = new Line();
		line1.add(new Vertex(213.93399f,346.066f));
		line1.add(new Vertex(206.40002f,320.8f));
		line1.add(new Vertex(197.60004f,295.19998f));
		line1.add(new Vertex(196.16174f,290.88513f));
		line1.add(new Vertex(188.80003f,268.8f));
		line1.add(new Vertex(176.00003f,245.59999f));
		line1.add(new Vertex(170.00002f,239.99998f));
		String move1 = facade.executeLine(line1);
		assertEquals("3<,4<,[]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(170.00002f,239.99998f));
		line2.add(new Vertex(232.80003f,238.4f));
		line2.add(new Vertex(270.40002f,239.19998f));
		line2.add(new Vertex(300.00003f,239.19998f));
		line2.add(new Vertex(321.08002f,239.19998f));
		line2.add(new Vertex(338.40002f,239.19998f));
		line2.add(new Vertex(365.60004f,239.19998f));
		line2.add(new Vertex(410.40002f,239.19998f));
		line2.add(new Vertex(443.20004f,242.4f));
		line2.add(new Vertex(469.60004f,242.4f));
		line2.add(new Vertex(470.0f,240.0f));
		String move2 = facade.executeLine(line2);
		assertEquals("4<,0<,[]", move2);

		Line line3 = new Line();
		line3.add(new Vertex(470.0f,240.0f));
		line3.add(new Vertex(455.20004f,215.99998f));
		line3.add(new Vertex(442.40002f,193.59998f));
		line3.add(new Vertex(441.12854f,189.53125f));
		line3.add(new Vertex(434.40002f,167.99998f));
		line3.add(new Vertex(431.20004f,142.4f));
		line3.add(new Vertex(426.06598f,133.93396f));
		String move3 = facade.executeLine(line3);
		assertEquals("0<,7<,[]", move3);

		Line line4 = new Line();
		line4.add(new Vertex(426.06598f,133.93396f));
		line4.add(new Vertex(362.40002f,124.79998f));
		line4.add(new Vertex(329.60004f,122.39998f));
		line4.add(new Vertex(313.70953f,119.547844f));
		line4.add(new Vertex(298.40002f,116.79999f));
		line4.add(new Vertex(270.40002f,108.79999f));
		line4.add(new Vertex(241.60004f,107.19998f));
		line4.add(new Vertex(227.20003f,127.99998f));
		line4.add(new Vertex(213.93398f,133.93399f));
		String move4 = facade.executeLine(line4);
		assertEquals("7<,5<,[]", move4);

		String move5 = facade.executeMove("9<,7<,[2,6]");
		assertEquals("9<,7<,[2,6]", move5);
	}
	
	public void test21() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(213.93399f,346.066f));
		line0.add(new Vertex(233.60004f,325.59998f));
		line0.add(new Vertex(252.00003f,300.8f));
		line0.add(new Vertex(264.80005f,278.4f));
		line0.add(new Vertex(272.00003f,253.59998f));
		line0.add(new Vertex(271.6569f,240.5607f));
		line0.add(new Vertex(271.2f,223.19998f));
		line0.add(new Vertex(257.60004f,194.39998f));
		line0.add(new Vertex(240.80003f,172.79999f));
		line0.add(new Vertex(224.00003f,151.19998f));
		line0.add(new Vertex(213.93398f,133.93399f));
		String move0 = facade.executeLine(line0);
		assertEquals("3<,5<,[]", move0);
		
		Line line1 = new Line();
		line1.add(new Vertex(271.6569f,240.5607f));
		line1.add(new Vertex(246.40002f,230.39998f));
		line1.add(new Vertex(220.80003f,221.59998f));
		line1.add(new Vertex(220.29173f,221.74754f));
		line1.add(new Vertex(196.00003f,228.79999f));
		line1.add(new Vertex(172.00003f,235.99998f));
		line1.add(new Vertex(170.00002f,239.99998f));
		String move1 = facade.executeLine(line1);
		assertEquals("8>,4<,[]", move1);
		
		Line line2 = new Line();
		line2.add(new Vertex(213.93398f,133.93399f));
		line2.add(new Vertex(236.80003f,123.19998f));
		line2.add(new Vertex(256.80005f,107.999985f));
		line2.add(new Vertex(279.2f,119.19998f));
		line2.add(new Vertex(306.40002f,125.59999f));
		line2.add(new Vertex(316.25677f,124.32815f));
		line2.add(new Vertex(331.20004f,122.39998f));
		line2.add(new Vertex(356.80002f,117.59999f));
		line2.add(new Vertex(384.80002f,119.999985f));
		line2.add(new Vertex(409.60004f,124.79998f));
		line2.add(new Vertex(426.06598f,133.93396f));
		String move2 = facade.executeLine(line2);
		assertEquals("5<,7<,[]", move2);
		
		Line line3 = new Line();
		line3.add(new Vertex(470.0f,240.0f));
		line3.add(new Vertex(450.40002f,268.8f));
		line3.add(new Vertex(432.00003f,290.4f));
		line3.add(new Vertex(415.20004f,310.4f));
		line3.add(new Vertex(402.3706f,322.75424f));
		line3.add(new Vertex(393.60004f,331.19998f));
		line3.add(new Vertex(368.80002f,348.0f));
		line3.add(new Vertex(347.20004f,361.59998f));
		line3.add(new Vertex(328.00003f,379.2f));
		line3.add(new Vertex(320.0f,390.0f));
		String move3 = facade.executeLine(line3);
		assertEquals("0<,2<,[]", move3);
		
		String move4 = facade.executeMove("2<,0<,[5]");
		assertEquals("2<,0<,[3]", move4);
	}
	
	public void test22() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);

		String move0 = facade.executeMove("2<,3<");
		assertEquals("2<,3<,[]", move0);

		String move1 = facade.executeMove("8<,1<");
		assertEquals("8<,1<,[]", move1);

		String move2 = facade.executeMove("1<,0<");
		assertEquals("1<,0<,[]", move2);

		String move3 = facade.executeMove("5<,6<");
		assertEquals("5<,6<,[]", move3);

		Line line4 = new Line();
		line4.add(new Vertex(170.00002f,239.99998f));
		line4.add(new Vertex(198.40002f,238.4f));
		line4.add(new Vertex(226.40002f,232.79999f));
		line4.add(new Vertex(253.60004f,225.59999f));
		line4.add(new Vertex(276.80005f,215.99998f));
		line4.add(new Vertex(300.80005f,202.4f));
		line4.add(new Vertex(324.80002f,187.99998f));
		line4.add(new Vertex(348.00003f,172.79999f));
		line4.add(new Vertex(373.60004f,158.39998f));
		line4.add(new Vertex(396.00003f,145.59998f));
		line4.add(new Vertex(420.80002f,139.19998f));
		line4.add(new Vertex(426.06598f,133.93396f));
		String move4 = facade.executeLine(line4);
		assertEquals("4<,7<,[]", move4);

		String move5 = facade.executeMove("9<,10<,[11,4]");
		assertEquals("9<,10<,[4,5]", move5);

		String move6 = facade.executeMove("3<,3<,[12]");
		assertEquals("3<,3<,[4]", move6);

		String move7 = facade.executeMove("4<,12<");
		assertEquals("4<,12<,[]", move7);
	}
	
	public void test23() {
		GraphicalFacade facade = new GraphicalFacade();
		facade.createFreshPosition(8, 320, 240, 150);
		
		Line line0 = new Line();
		line0.add(new Vertex(320.0f,390.0f));
		line0.add(new Vertex(340.80002f,375.19998f));
		line0.add(new Vertex(365.60004f,365.59998f));
		line0.add(new Vertex(391.20004f,356.8f));
		line0.add(new Vertex(416.00003f,348.8f));
		line0.add(new Vertex(426.066f,346.066f));
		String move0 = facade.executeLine(line0);
		assertEquals("2<,1<,[]", move0);

		Line line1 = new Line();
		line1.add(new Vertex(320.0f,390.0f));
		line1.add(new Vertex(294.40002f,393.59998f));
		line1.add(new Vertex(256.80005f,394.39996f));
		line1.add(new Vertex(232.00003f,391.2f));
		line1.add(new Vertex(202.40002f,386.4f));
		line1.add(new Vertex(179.20003f,367.99997f));
		line1.add(new Vertex(171.20003f,343.19998f));
		line1.add(new Vertex(164.80003f,317.59998f));
		line1.add(new Vertex(176.00003f,289.59998f));
		line1.add(new Vertex(204.00003f,268.8f));
		line1.add(new Vertex(222.40002f,248.79999f));
		line1.add(new Vertex(224.80003f,219.19998f));
		line1.add(new Vertex(202.40002f,188.79999f));
		line1.add(new Vertex(187.20003f,168.79999f));
		line1.add(new Vertex(175.20003f,144.79999f));
		line1.add(new Vertex(174.40002f,118.39999f));
		line1.add(new Vertex(188.80003f,97.59999f));
		line1.add(new Vertex(213.60004f,88.79997f));
		line1.add(new Vertex(238.40002f,85.59999f));
		line1.add(new Vertex(268.80005f,97.59999f));
		line1.add(new Vertex(296.80005f,111.19998f));
		line1.add(new Vertex(324.80002f,121.59998f));
		line1.add(new Vertex(355.20004f,120.79999f));
		line1.add(new Vertex(388.00003f,112.79998f));
		line1.add(new Vertex(414.40002f,104.79999f));
		line1.add(new Vertex(442.40002f,98.39998f));
		line1.add(new Vertex(476.80002f,106.39998f));
		line1.add(new Vertex(499.20004f,126.39998f));
		line1.add(new Vertex(513.60004f,155.99998f));
		line1.add(new Vertex(520.80005f,180.79999f));
		line1.add(new Vertex(525.60004f,208.79999f));
		line1.add(new Vertex(516.0f,238.4f));
		line1.add(new Vertex(504.00003f,260.8f));
		line1.add(new Vertex(484.00003f,279.19998f));
		line1.add(new Vertex(456.00003f,283.99997f));
		line1.add(new Vertex(429.60004f,288.0f));
		line1.add(new Vertex(416.80002f,312.8f));
		line1.add(new Vertex(419.20004f,338.4f));
		line1.add(new Vertex(426.066f,346.066f));
		String move1 = facade.executeLine(line1);
		assertEquals("2<,1<,[0,3,5,7]", move1);

		Line line2 = new Line();
		line2.add(new Vertex(426.06598f,133.93396f));
		line2.add(new Vertex(410.40002f,165.59999f));
		line2.add(new Vertex(394.40002f,191.99998f));
		line2.add(new Vertex(379.20004f,215.99998f));
		line2.add(new Vertex(359.20004f,245.59999f));
		line2.add(new Vertex(343.20004f,268.0f));
		line2.add(new Vertex(325.60004f,291.19998f));
		line2.add(new Vertex(305.60004f,312.8f));
		line2.add(new Vertex(280.80005f,321.59998f));
		line2.add(new Vertex(254.40002f,321.59998f));
		line2.add(new Vertex(232.00003f,332.8f));
		line2.add(new Vertex(213.93399f,346.066f));
		String move2 = facade.executeLine(line2);
		assertEquals("7<,3<,[]", move2);
		
		String move3 = facade.executeMove("8<,9>,[10,0]");
		assertEquals("8<,9>,[0,3]", move3);
	}
}
