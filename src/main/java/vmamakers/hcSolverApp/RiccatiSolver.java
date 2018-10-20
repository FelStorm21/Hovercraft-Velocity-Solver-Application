package vmamakers.hcSolverApp;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class RiccatiSolver {
	private boolean isReady;
	private boolean isSolvingNormal;
	private boolean isSolvingDrag;
	private double[] constants = { 0, 0, 0 };
	private double[] regCoeff = { 0.9056, 0.4237, 0.8947 };
	private double c_zero;
	private double c_one;
	private double c_two;
	private boolean isUsingDP;
	private boolean isUsingRK;
	private double h = 0.0001;

	public double solve(double t) {
		FirstOrderIntegrator dpIntegrator = new DormandPrince853Integrator(1.0e-8, 1.0, 1.0e-10, 1.0e-10);

		FirstOrderIntegrator rkIntegrator = new ClassicalRungeKuttaIntegrator(h);

		//		double[] actualHovercraftConstants = {0.642, -0.0707, -0.00275};  //enter in ascending order
		RiccatiDifferentialEquation myEquation = new RiccatiDifferentialEquation(constants);
		DragCoefficientDiffEq dragEquation = new DragCoefficientDiffEq(constants, regCoeff);
		double[] y = { 0 };
		if (t < 0) {
			throw new IllegalArgumentException();
		}
		if (isSolvingNormal) {
			if (isReady & isUsingDP) {
				dpIntegrator.integrate(myEquation, 0, y, t, y);
				//				System.out.println(y[0]);
			}
			if (isReady & isUsingRK) {
				rkIntegrator.integrate(myEquation, 0, y, t, y);
				//				System.out.println(y[0]);
			}
		}
		if (isSolvingDrag) {
			if (isReady & isUsingDP) {
				dpIntegrator.integrate(dragEquation, 0, y, t, y);
				//				System.out.println(y[0]);
			}
			if (isReady & isUsingRK) {
				rkIntegrator.integrate(dragEquation, 0, y, t, y);
				//				System.out.println(y[0]);
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
	
	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public double[] getConstants() {
		return constants;
	}

	public void setConstants(double[] constants) {
		this.constants = constants;
	}

	public boolean isUsingDP() {
		return isUsingDP;
	}

	public void setUsingDP(boolean isUsingDP) {
		this.isUsingDP = isUsingDP;
	}

	public boolean isUsingRK() {
		return isUsingRK;
	}

	public void setUsingRK(boolean isUsingRK) {
		this.isUsingRK = isUsingRK;
	}

	public boolean isSolvingNormal() {
		return isSolvingNormal;
	}

	public boolean isSolvingDrag() {
		return isSolvingDrag;
	}

	public double[] getRegCoeff() {
		return regCoeff;
	}

	public void setSolvingNormal(boolean isSolvingNormal) {
		this.isSolvingNormal = isSolvingNormal;
	}

	public void setSolvingDrag(boolean isSolvingDrag) {
		this.isSolvingDrag = isSolvingDrag;
	}

	public void setRegCoeff(double[] regCoeff) {
		this.regCoeff = regCoeff;
	}

	public double getCoeff(int index) {
		return regCoeff[index];
	}

	public void setCoeff(int index, double value) {
		regCoeff[index] = value;
	}

}
