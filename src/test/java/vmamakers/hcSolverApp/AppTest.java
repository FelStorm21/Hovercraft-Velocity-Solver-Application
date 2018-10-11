package vmamakers.hcSolverApp;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class AppTest {
	
	private ClosedFormFunction cff;
	private RiccatiSolver solver;
	private RiccatiSmoother smoother;
	private HashMap<Double, Double> smoothValues;
	private HashMap<Double, Double> roughValues;
	private RiccatiHandler rhandler;
	private double stepsize;
	private RiccatiDifferentialEquation diffeq;
	private double[] constants;
	private double[] y;
	private double[] yDot;
	
	@BeforeMethod
	public void setUp() {
		stepsize = 0.25;
		
		constants = new double[] {10, 0.1, 0.01};
		y = new double[] {10};
		yDot = new double[] {0};
		
		cff = new ClosedFormFunction();
		cff.setChi(10);
		cff.setPhi(0.1);
		cff.setGamma(0.01);
		solver = new RiccatiSolver();
		solver.setConstants(constants);
		solver.setReady(true);
		solver.setUsingDP(true);
		solver.setSolvingDrag(false);
		solver.setSolvingNormal(true);
		smoother = new RiccatiSmoother();
		roughValues = new HashMap<Double, Double>(3);
			roughValues.put(1.0, 1.0);
			roughValues.put(2.0, 2.0);
			roughValues.put(3.0, 2.0);
		smoothValues = new HashMap<Double, Double>(3);
			for (int i = 1; i <=3; i++) {
				smoothValues.put((double) i, (double) i);
			}
		rhandler = new RiccatiHandler(stepsize);
		rhandler.setSolver(solver);
		diffeq = new RiccatiDifferentialEquation(constants);
	}
	
	@Test
	public void cothEquals() {
		final double expected = 10;
		final double actual = Math.round(cff.coth(0.1));
		Assert.assertEquals(actual, expected);
		
	}
	
	@Test
	public void cothArgZero() {
		boolean caught = false;
		try {
			cff.coth(0);
		} catch(IllegalArgumentException e) {
			caught = true;
		}
		Assert.assertTrue(caught);
	}
	
	@Test
	public void valueEquals() {
		final double expected = 27;
		final double actual = Math.round(cff.value(10));
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void valueChiLessThanZero() {
		boolean caught = false;
		try {
			cff.setChi(-1);
		} catch(IllegalArgumentException e) {
			caught = true;
		}
		Assert.assertTrue(caught);
	}
	
	@Test
	public void valueArgZero() {
		final double expected = 0;
		final double actual = cff.value(0);
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void solveEquals() {
		final double expected = 27;
		final double actual = Math.round(solver.solve(15));
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void solveIsNaN() {
		boolean caught = false;
		try {
			solver.solve(-1);
		} catch(IllegalArgumentException e) {
			caught = true;
		}
		Assert.assertTrue(caught);
	}
	
	@Test
	public void smoothTrue() {
		final boolean expected = true;
		smoother.setGraphData(smoothValues);
		final boolean actual = smoother.checkSmooth();
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void smoothFalse() {
		final boolean expected = false;
		smoother.setGraphData(roughValues);
		final boolean actual = smoother.checkSmooth();
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void handlerGenerateExpectedData() {
		final double expected = 25;
		rhandler.generateData();
		final double actual = Math.round(rhandler.getGraphData().get(5.0));
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void correctlyComputeDerivatives() {
		final double expected = 8;
		diffeq.computeDerivatives(1.1, y, yDot);
		final double actual = Math.round(yDot[0]);
		Assert.assertEquals(actual, expected);
		
	} 
	
	@AfterMethod
	public void tearDown() {
		cff = null;
		solver = null;
		smoother = null;
		rhandler = null;
		diffeq = null;
	}
	
}