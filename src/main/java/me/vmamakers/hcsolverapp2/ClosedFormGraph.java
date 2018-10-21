package me.vmamakers.hcsolverapp2;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ClosedFormGraph {  //DOESN'T NEED A SOLVER; REDUCE DOUBLES TO ARRAY

	private XYSeries series;
	private XYSeriesCollection dataset;
	private JFreeChart chart;
	private JFrame chartFrame;

	private double tMin = 0;
	private double tMax = 120;
	private double stepsize = (tMax - tMin) / 60;

	private ClosedFormFunction closedFormFunction;
	private RiccatiSolver solver;
//	private RiccatiHandler rhandler;

	private int maxCounter;
	private int minCounter;
	private int checkCounter;
	private Point lastLocation;

	private PropertyChangeSupport pcs;

	private HashMap<Double, Double> closedFormGraphData;

	public ClosedFormGraph() {
		pcs = new PropertyChangeSupport(this);
		series = new XYSeries("Predicted");
		closedFormGraphData = new HashMap<Double, Double>(100);
	}

	public void generateData() {
		//		stepsize = rhandler.getStepsize();
		//		tMin = rhandler.getTmin();
		//		tMax = rhandler.getTmax();
		for (double i = tMin; i <= tMax; i += stepsize) {
			series.add(i, closedFormFunction.value(i));
			closedFormGraphData.put(i, closedFormFunction.value(i));
		}
	}

	public void plot() {
		//		series = new XYSeries("Predicted");

		dataset = new XYSeriesCollection(series);
		chart = ChartFactory.createXYLineChart("Riccati D.E. Closed-Form Solution", "Time (s)", "Speed (m/s)", dataset);
		chartFrame = new ChartFrame("Graph", chart);
		chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartFrame.setSize(500, 500);
		chartFrame.setLocationRelativeTo(null);
		chartFrame.setVisible(true);

		chartFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				pcs.firePropertyChange("closedFormGraphClosed", false, true);
			}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}

		});
	}

	public double getStepsize() {
		return stepsize;
	}

	public RiccatiSolver getSolver() {
		return solver;
	}

	public void setSolver(RiccatiSolver solver) {
		this.solver = solver;
	}

	public void setStepsize(double stepsize) {
		this.stepsize = stepsize;
	}

	public double gettMin() {
		return tMin;
	}

	public void setMin(double tMin) {
		this.tMin = tMin;
	}

	public double gettMax() {
		return tMax;
	}

	public void settMax(double tMax) {
		this.tMax = tMax;
	}

//	public RiccatiHandler getRhandler() {
//		return rhandler;
//	}
//
//	public void setRhandler(RiccatiHandler rhandler) {
//		this.rhandler = rhandler;
//	}

	public ClosedFormFunction getClosedFormFunction() {
		return closedFormFunction;
	}

	public void setClosedFormFunction(ClosedFormFunction closedFormFunction) {
		this.closedFormFunction = closedFormFunction;
	}

	public XYSeries getSeries() {
		return series;
	}

	public void setSeries(XYSeries series) {
		this.series = series;
	}

	public JFrame getChartFrame() {
		return chartFrame;
	}

	public void setChartFrame(JFrame chartFrame) {
		this.chartFrame = chartFrame;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}

	public int getMinCounter() {
		return minCounter;
	}

	public void setMinCounter(int minCounter) {
		this.minCounter = minCounter;
	}

	public int getCheckCounter() {
		return checkCounter;
	}

	public void setCheckCounter(int checkCounter) {
		this.checkCounter = checkCounter;
	}
	/*
		public HashMap<Double, Double> getClosedFormGraphData() {
			return closedFormGraphData;
		}
	
		public void setClosedFormGraphData(HashMap<Double, Double> closedFormGraphData) {
			this.closedFormGraphData = closedFormGraphData;
		}
	*/

	public Point getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Point lastLocation) {
		this.lastLocation = lastLocation;
	}

	public PropertyChangeSupport getPcs() {
		return pcs;
	}

	public void setPcs(PropertyChangeSupport pcs) {
		this.pcs = pcs;
	}

	public XYSeriesCollection getDataset() {
		return dataset;
	}

	public void setDataset(XYSeriesCollection dataset) {
		this.dataset = dataset;
	}

	public HashMap<Double, Double> getClosedFormGraphData() {
		return closedFormGraphData;
	}

	public void setClosedFormGraphData(HashMap<Double, Double> closedFormGraphData) {
		this.closedFormGraphData = closedFormGraphData;
	}
}
