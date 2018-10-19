package vmamakers.hcSolverApp;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class RiccatiGraph {

	private double tmin = 0;
	private double tmax = 120;
	private RiccatiSolver solver;
	private HashMap<Double, Double> graphData;
	private JFrame mainPanel;
	private JFrame chartFrame;
	private int checkCounter;
	private int minCounter;
	private int maxCounter;
	private Point lastLocation;
	private XYSeries series;
	private XYSeriesCollection dataset;
	private JFreeChart chart;

	protected PropertyChangeSupport propertyChangeSupport;

	public RiccatiGraph() { //default constructor that sets interval to two minutes long
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void plot() {
		series = new XYSeries("Predicted");
		for (Map.Entry<Double, Double> entry : graphData.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
		}
		dataset = new XYSeriesCollection(series);
		chart = ChartFactory.createXYLineChart("Riccati D.E. Numerical Solution", "Time (s)", "Speed (m/s)", dataset);
		chartFrame = new ChartFrame("Graph", chart);
		chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartFrame.setSize(500, 500);
		chartFrame.setLocationRelativeTo(mainPanel);
		chartFrame.setVisible(true);

		chartFrame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				propertyChangeSupport.firePropertyChange("graphClosed", false, true);
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

	public void updateDataset() {
		series.clear();
		for (Map.Entry<Double, Double> entry : graphData.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
		}
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

	public RiccatiSolver getSolver() {
		return solver;
	}

	public void setSolver(RiccatiSolver solver) {
		this.solver = solver;
	}

	public HashMap<Double, Double> getGraphData() {
		return graphData;
	}

	public void setGraphData(HashMap<Double, Double> graphData) {
		this.graphData = graphData;
	}

	public JFrame getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JFrame mainPanel) {
		this.mainPanel = mainPanel;
	}

	public int getCheckCounter() {
		return checkCounter;
	}

	public void setCheckCounter(int checkCounter) {
		this.checkCounter = checkCounter;
	}

	public JFrame getChartFrame() {
		return chartFrame;
	}

	public void setChartFrame(JFrame chartFrame) {
		this.chartFrame = chartFrame;
	}

	public Point getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Point lastLocation) {
		this.lastLocation = lastLocation;
	}

	public int getMinCounter() {
		return minCounter;
	}

	public void setMinCounter(int minCounter) {
		this.minCounter = minCounter;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}

	public XYSeries getSeries() {
		return series;
	}

	public void setSeries(XYSeries series) {
		this.series = series;
	}

	public XYSeriesCollection getDataset() {
		return dataset;
	}

	public void setDataset(XYSeriesCollection dataset) {
		this.dataset = dataset;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}
}
