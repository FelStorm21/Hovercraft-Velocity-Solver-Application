package vmamakers.hcSolverApp;

import java.util.HashMap;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;

public class RiccatiHandler implements FixedStepHandler {

	private double stepsize;
	private double tmin = 0;
	private double tmax = 120;
	private HashMap<Double, Double> graphData;
	private StepNormalizer handler;
	private RiccatiSolver solver;
	private double h = 0.0001;

	@Override
	public void init(double t0, double[] y0, double t) {
		setGraphData(new HashMap<Double, Double>(100)); //initializes the size of the dataset
	}

	@Override
	public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
		graphData.put(t, y[0]);
	}

	public RiccatiHandler(double stepsize) {
		this.stepsize = stepsize;
		this.handler = new StepNormalizer(this.stepsize, this);
	}

	protected void generateData() {
		FirstOrderIntegrator rkIntegrator = new ClassicalRungeKuttaIntegrator(h);
		FirstOrderIntegrator dpIntegrator = new DormandPrince853Integrator(1.0e-8, 1.0, 1.0e-10, 1.0e-10);
		FirstOrderDifferentialEquations equation = new RiccatiDifferentialEquation(solver.getConstants());
		FirstOrderDifferentialEquations dragEquation = new DragCoefficientDiffEq(solver.getConstants(), solver.getRegCoeff());
		double[] y = { 0 };
		if (solver.isSolvingNormal()) {
			if (solver.isUsingDP()) {
				dpIntegrator.addStepHandler(handler);
				dpIntegrator.integrate(equation, tmin, y, tmax, y);
			}
			if (solver.isUsingRK()) {
				rkIntegrator.addStepHandler(handler);
				rkIntegrator.integrate(equation, tmin, y, tmax, y);
			}
		}
		if (solver.isSolvingDrag()) {
			if (solver.isUsingDP()) {
				dpIntegrator.addStepHandler(handler);
				dpIntegrator.integrate(dragEquation, tmin, y, tmax, y);
			}
			if (solver.isUsingRK()) {
				rkIntegrator.addStepHandler(handler);
				rkIntegrator.integrate(dragEquation, tmin, y, tmax, y);
			}
		}

	}

	public HashMap<Double, Double> getGraphData() {
		return graphData;
	}

	public void setGraphData(HashMap<Double, Double> graphData) {
		this.graphData = graphData;
	}

	public RiccatiSolver getSolver() {
		return solver;
	}

	public void setSolver(RiccatiSolver solver) {
		this.solver = solver;
	}

	public double getTmin() {
		return tmin;
	}

	public void setTmin(double tmin) {
		this.tmin = tmin;
	}

	public double getTmax() {
		return tmax;
	}

	public void setTmax(double tmax) {
		this.tmax = tmax;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getStepsize() {
		return stepsize;
	}

	public void setStepsize(double stepsize) {
		this.stepsize = stepsize;
		this.handler = new StepNormalizer(this.stepsize, this);
	}

}
