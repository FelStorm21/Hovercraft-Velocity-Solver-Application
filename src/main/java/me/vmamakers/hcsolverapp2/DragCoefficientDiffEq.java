package me.vmamakers.hcsolverapp2;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class DragCoefficientDiffEq implements FirstOrderDifferentialEquations {

	private double[] constants;
	private double[] regCoeff;

	public DragCoefficientDiffEq(double[] constants, double[] regCoeff) {
		this.constants = constants;
		this.regCoeff = regCoeff;
	}

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
		yDot[0] = constants[0] - constants[1] * y[0] - constants[2] * Math.pow(y[0], 2) - (regCoeff[0] * Math.exp(-regCoeff[1] * y[0]) + regCoeff[2]) * Math.pow(y[0], 2);
	}

	public double[] getConstants() {
		return constants;
	}

	public void setConstants(double[] constants) {
		this.constants = constants;
	}
	
	public void setConstant(int index, double constant) {
		constants[index] = constant;
	}

	public double[] getRegCoeff() {
		return regCoeff;
	}

	public void setRegCoeffs(double[] regCoeff) {
		this.regCoeff = regCoeff;
	}
	
	public void setRegCoeff(int index, double constant) {
		regCoeff[index] = constant;
	}

}
