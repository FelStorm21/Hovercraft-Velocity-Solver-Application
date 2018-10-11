package vmamakers.hcSolverApp;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class RiccatiDifferentialEquation implements FirstOrderDifferentialEquations {

	private double[] constants;
	
	public RiccatiDifferentialEquation(double[] constants) {
		this.constants = constants;
	}
	
	@Override public int getDimension() {
		return 1;
	}

	@Override public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
		yDot[0] = constants[0] - constants[1]*y[0] - constants[2]*Math.pow(y[0], 2); 
	}	
}

//IT WORKS! IT GIVES THE SAME ANSWER AS THE PICARD ITERATES.
