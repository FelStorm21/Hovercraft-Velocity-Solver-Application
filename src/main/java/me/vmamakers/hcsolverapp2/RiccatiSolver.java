package me.vmamakers.hcsolverapp2;

import java.util.HashMap;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;

public class RiccatiSolver implements FixedStepHandler {

	private boolean[] flags = new boolean[5]; // [0]isReady, [1]isSolvingNormal, [2]isSolvingDrag, [3]isUsingDP, [4]isUsingRK
	private double[] constants = { 0, 0, 0 };
	private double[] regCoeff = { 0.9056, 0.4237, 0.8947 };
	
	private double h = 0.0001;
	private FirstOrderIntegrator dpIntegrator, rkIntegrator;
	private FirstOrderDifferentialEquations myEquation, dragEquation;
	
	private double[] bounds = {0, 120, 2};  // [0]tmin, [1]tmax, [2]stepsize
	private HashMap<Double, Double> graphData;
	private StepNormalizer handler;

	public RiccatiSolver(double handlerStepsize) {
		dpIntegrator = new DormandPrince853Integrator(1.0e-8, 1.0, 1.0e-10, 1.0e-10);
		rkIntegrator = new ClassicalRungeKuttaIntegrator(h);
		myEquation = new RiccatiDifferentialEquation(constants);
		dragEquation = new DragCoefficientDiffEq(constants, regCoeff);
		bounds[2] = handlerStepsize;
		handler = new StepNormalizer(bounds[2], this);
	}
	
	@Override 
	public void init(double t0, double[] y0, double t) {
		setGraphData(new HashMap<Double, Double>(100));
	}

	@Override 
	public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
		graphData.put(t, y[0]);
	}
	
	public double solve(double t, boolean isHandling) {
		
		double[] y = { 0 };
		if (t < 0) {
			throw new IllegalArgumentException();
		}
		if (flags[1] && flags[0]) {
			if (flags[3]) {
				if (isHandling) {
					dpIntegrator.addStepHandler(handler);
					dpIntegrator.integrate(myEquation, bounds[0], y, bounds[1], y);
				}
				else {
					dpIntegrator.integrate(myEquation, 0, y, t, y);
				}	
			}
			if (flags[4]) {
				if (isHandling) {
					rkIntegrator.addStepHandler(handler);
					rkIntegrator.integrate(myEquation, bounds[0], y, bounds[1], y);
				}
				else {
					rkIntegrator.integrate(myEquation, 0, y, t, y);
				}	
			}
		}
		if (flags[2] && flags[0]) {
			if (flags[3]) {
				if (isHandling) {
					dpIntegrator.addStepHandler(handler);
					dpIntegrator.integrate(dragEquation, bounds[0], y, bounds[1], y);
				}
				else {
					dpIntegrator.integrate(dragEquation, 0, y, t, y);
				}	
			}
			if (flags[4]) {
				rkIntegrator.integrate(dragEquation, 0, y, t, y);
				if (isHandling) {
					rkIntegrator.addStepHandler(handler);
					rkIntegrator.integrate(dragEquation, bounds[0], y, bounds[1], y);
				}
				else {
					rkIntegrator.integrate(dragEquation, 0, y, t, y);
				}	
			}
		}
		return y[0];
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getConstant(int index) {
		return constants[index];
	}
	
	public void setConstant(int index, double constant) {
		constants[index] = constant;
	}
	
	public boolean getFlag(int index) {
		return flags[index];
	}
	
	public void setFlag(int index, boolean flag) {
		flags[index] = flag;
	}
	
	public double[] getConstants() {
		return constants;
	}

	public void setConstants(double[] constants) {
		this.constants = constants;
		((RiccatiDifferentialEquation) myEquation).setConstants(constants);
		((DragCoefficientDiffEq) dragEquation).setConstants(constants);
	}

	public double[] getRegCoeff() {
		return regCoeff;
	}

	public void setRegCoeff(double[] regCoeff) {
		this.regCoeff = regCoeff;
		((DragCoefficientDiffEq) dragEquation).setRegCoeff(constants);
	}

	public double getRegCoeff(int index) {
		return regCoeff[index];
	}

	public void setRegCoeff(int index, double value) {
		regCoeff[index] = value;
	}

	public double[] getBounds() {
		return bounds;
	}

	public void setBounds(double[] bounds) {
		this.bounds = bounds;
	}
	
	public double getBound(int index) {
		return bounds[index];
	}
	
	public void setBound(int index, double bound) {
		bounds[index] = bound;
		if (index == 2) {
			handler = new StepNormalizer(bounds[2], this);
		}
	}

	public HashMap<Double, Double> getGraphData() {
		return graphData;
	}

	public void setGraphData(HashMap<Double, Double> graphData) {
		this.graphData = graphData;
	}

}
