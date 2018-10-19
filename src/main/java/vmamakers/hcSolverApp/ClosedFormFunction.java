package vmamakers.hcSolverApp;

import java.beans.PropertyChangeSupport;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NotANumberException;

public class ClosedFormFunction implements UnivariateFunction {
	private double chi;
	private double phi;
	private double gamma;
	private double value;

	private PropertyChangeSupport pcs;

	public ClosedFormFunction() {
		pcs = new PropertyChangeSupport(this);
	}

	public double coth(double x) {
		if (x == 0) {
			throw new IllegalArgumentException();
		}
		return Math.cosh(x) / Math.sinh(x);
	}

	@Override
	public double value(double x) throws NotANumberException {
		boolean exceptionThrown = false;
		try {
			if (x == 0) {
				return 0;
			}
			if ((0.5 * x * Math.sqrt(Math.pow(phi, 2) + 4 * gamma * chi)) == 0) {
				return 0;
			}
			value = 2 * chi / (phi + Math.sqrt(Math.pow(phi, 2) + 4 * gamma * chi) * coth(0.5 * x * Math.sqrt(Math.pow(phi, 2) + 4 * gamma * chi)));

			if (Double.isNaN(value)) {
				throw new NotANumberException();
			}
		} catch (NotANumberException e) {
			exceptionThrown = true;
			pcs.firePropertyChange("nanExceptionThrown", false, exceptionThrown);
		}
		return value;
	}

	public double getChi() {
		return chi;
	}

	public void setChi(double chi) throws IllegalArgumentException {
		if (chi < 0) {
			throw new IllegalArgumentException();
		}
		this.chi = chi;
	}

	public double getPhi() {
		return phi;
	}

	public void setPhi(double phi) {
		this.phi = phi;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport pcs) {
		this.pcs = pcs;
	}
}
