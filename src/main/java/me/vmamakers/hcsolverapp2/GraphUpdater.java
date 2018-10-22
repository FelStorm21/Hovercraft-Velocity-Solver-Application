package me.vmamakers.hcsolverapp2;

import java.beans.PropertyChangeSupport;

import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class GraphUpdater {
	
	private RiccatiGraph graph;
	private ClosedFormGraph closedFormGraph;
	private RiccatiSolver solver;
	private Smoother smoother;
	private GUI gui;
	
	private double defaultStepsize;
	private double smoothingFactor;
	
	protected PropertyChangeSupport propertyChangeSupport;
	private boolean graphBoxIsSelected;
	private boolean closedFormGraphBoxIsSelected;
	
	public GraphUpdater(GUI gui) {
		this.gui = gui;
		defaultStepsize = gui.getDefaultStepsize();
		smoothingFactor = gui.getSmoothingFactor();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void updateGraph() {
		try {
			if ((graph.getChartFrame() != null && graph.getChartFrame().isVisible()) && (closedFormGraph.getChartFrame() != null && closedFormGraph.getChartFrame().isVisible())) {
				graph.getDataset().removeAllSeries();
				closedFormGraph.getDataset().removeAllSeries();
				solver.getGraphData().clear();
//				rhandler.getGraphData().clear();
				graph.getGraphData().clear();
				closedFormGraph.getSeries().clear();
				solver.solve(0, true);
//				rhandler.generateData();
				closedFormGraph.generateData();
				graph.setGraphData(solver.getGraphData());
//				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());

				if (!smoother.checkSmooth()) {
					propertyChangeSupport.firePropertyChange("Smoothing num only", false, true);
					propertyChangeSupport.firePropertyChange("Smoothing cf only", false, true);
//					rhandler.setStepsize(defaultStepsize / smoothingFactor);
					solver.setBound(2, defaultStepsize / smoothingFactor);
					closedFormGraph.setStepsize(defaultStepsize / smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
//					rhandler.getGraphData().clear();
					solver.getGraphData().clear();
					graph.getGraphData().clear();
					solver.solve(0, true);
//					rhandler.generateData();
					closedFormGraph.generateData();
//					graph.setGraphData(rhandler.getGraphData());
					graph.setGraphData(solver.getGraphData());
					smoother.setGraphData(graph.getGraphData());
					propertyChangeSupport.firePropertyChange("Smoothing num only Complete", false, true);
					propertyChangeSupport.firePropertyChange("Smoothing cf only Complete", false, true);
				}
				//				System.out.println("Plotting graph");
				graph.updateDataset();
				closedFormGraph.generateData();
				graph.getDataset().addSeries(graph.getSeries());
				closedFormGraph.getDataset().addSeries(closedFormGraph.getSeries());
				graph.getChartFrame().repaint();
				closedFormGraph.getChartFrame().repaint();

			} else if ((graph.getChartFrame() != null && graph.getChartFrame().isVisible())/* && (closedFormGraph.getChartFrame() == null && !closedFormGraph.getChartFrame().isVisible())*/) {
				graph.getDataset().removeAllSeries();
				solver.getGraphData().clear();
//				rhandler.getGraphData().clear();
				graph.getGraphData().clear();
				solver.solve(0, true);
//				rhandler.generateData();
				graph.setGraphData(solver.getGraphData());
//				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());

				if (!smoother.checkSmooth()) {
					propertyChangeSupport.firePropertyChange("Smoothing num only", false, true);
					solver.setBound(2, defaultStepsize / smoothingFactor);
//					rhandler.setStepsize(defaultStepsize / smoothingFactor);				
					solver.getGraphData().clear();
//					rhandler.getGraphData().clear();
					graph.getGraphData().clear();
					solver.solve(0, true);
//					rhandler.generateData();
					graph.setGraphData(solver.getGraphData());
//					graph.setGraphData(rhandler.getGraphData());
					smoother.setGraphData(graph.getGraphData());
					propertyChangeSupport.firePropertyChange("Smoothing num only Complete", false, true);
				}
				//				System.out.println("Plotting graph");
				graph.updateDataset();
				graph.getDataset().addSeries(graph.getSeries());
				graph.getChartFrame().repaint();
			} else if (/*(graph.getChartFrame() == null && !graph.getChartFrame().isVisible()) && */(closedFormGraph.getChartFrame() != null && closedFormGraph.getChartFrame().isVisible())) {
				closedFormGraph.getDataset().removeAllSeries();
				closedFormGraph.getClosedFormGraphData().clear();
				closedFormGraph.getSeries().clear();
				closedFormGraph.generateData();
				smoother.setGraphData(closedFormGraph.getClosedFormGraphData());

				if (!smoother.checkSmooth()) {
					propertyChangeSupport.firePropertyChange("Smoothing cf only", false, true);
					closedFormGraph.setStepsize(defaultStepsize / smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
					closedFormGraph.generateData();
					smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
					propertyChangeSupport.firePropertyChange("Smoothing cf only Complete", false, true);
				}
				//				System.out.println("Plotting graph");
				closedFormGraph.generateData();
				closedFormGraph.getDataset().addSeries(closedFormGraph.getSeries());
				closedFormGraph.getChartFrame().repaint();
			} else {
				solver.solve(0, true);
//				rhandler.generateData();
				closedFormGraph.generateData();
				graph.setGraphData(solver.getGraphData());
//				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());
				if (!smoother.checkSmooth()) {
					if (graphBoxIsSelected) {
						propertyChangeSupport.firePropertyChange("Smoothing num only", false, true);
					}
					if (closedFormGraphBoxIsSelected) {
						propertyChangeSupport.firePropertyChange("Smoothing cf only", false, true);
					}
					solver.setBound(2, defaultStepsize / smoothingFactor);
//					rhandler.setStepsize(defaultStepsize / smoothingFactor);
					closedFormGraph.setStepsize(defaultStepsize / smoothingFactor);
					solver.solve(0, true);
//					rhandler.generateData();
					closedFormGraph.generateData();
					graph.setGraphData(solver.getGraphData());
//					graph.setGraphData(rhandler.getGraphData());
					smoother.setGraphData(graph.getGraphData());
					if (graphBoxIsSelected) {
						propertyChangeSupport.firePropertyChange("Smoothing num only Complete", false, true);
					}
					if (closedFormGraphBoxIsSelected) {
						propertyChangeSupport.firePropertyChange("Smoothing cf only Complete", false, true);
					}

				}
				closedFormGraph.generateData();
				graph.plot();
				closedFormGraph.plot();
				if (graph.getLastLocation() != null) {
					graph.getChartFrame().setLocation(graph.getLastLocation());
				}
				if (closedFormGraph.getLastLocation() != null) {
					closedFormGraph.getChartFrame().setLocation(closedFormGraph.getLastLocation());
				}
				graph.setCheckCounter(graph.getCheckCounter() + 1);
				closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
			}
		} catch (NumberIsTooSmallException e) {
			propertyChangeSupport.firePropertyChange("exception dialog set visible", false, true);
		}
	}

	public boolean getGraphBoxIsSelected() {
		return graphBoxIsSelected;
	}

	public void setGraphBoxIsSelected(boolean graphBoxIsSelected) {
		this.graphBoxIsSelected = graphBoxIsSelected;
	}

	public boolean getClosedFormGraphBoxIsSelected() {
		return closedFormGraphBoxIsSelected;
	}

	public void setClosedFormGraphBoxIsSelected(boolean closedFormGraphBoxIsSelected) {
		this.closedFormGraphBoxIsSelected = closedFormGraphBoxIsSelected;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public void setPropertyChangeSupport(PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	public RiccatiGraph getGraph() {
		return graph;
	}

	public void setGraph(RiccatiGraph graph) {
		this.graph = graph;
	}

	public ClosedFormGraph getClosedFormGraph() {
		return closedFormGraph;
	}

	public void setClosedFormGraph(ClosedFormGraph closedFormGraph) {
		this.closedFormGraph = closedFormGraph;
	}

	public RiccatiSolver getSolver() {
		return solver;
	}

	public void setSolver(RiccatiSolver solver) {
		this.solver = solver;
	}

	public Smoother getSmoother() {
		return smoother;
	}

	public void setSmoother(Smoother smoother) {
		this.smoother = smoother;
	}
}
