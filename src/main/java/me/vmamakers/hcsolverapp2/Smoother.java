package me.vmamakers.hcsolverapp2;

import java.util.ArrayList;
import java.util.HashMap;

public class Smoother {
	private HashMap<Double, Double> graphData;
	private double maxAngle = 10;
	private boolean isSmooth = false;
	
	public Smoother() {
		graphData = new HashMap<Double, Double>(100);
	}

	public boolean checkSmooth() {
		ArrayList<Double> angleArrayList = new ArrayList<Double>();
		boolean smooth = false;
		//		System.out.println("Total data pair #: " + graphData.size());
		for (int i = 1; i < graphData.keySet().size(); i++) {
			double deltaY = graphData.get(graphData.keySet().toArray()[i]) - graphData.get(graphData.keySet().toArray()[i - 1]);
			double deltaX = (double) graphData.keySet().toArray()[i] - (double) graphData.keySet().toArray()[i - 1];
			double angle = 180 / Math.PI * Math.atan(deltaY / deltaX);
			angleArrayList.add(angle);
			if (i > 1) {
				double difAngle = Math.abs((angleArrayList.get(i - 1) - angleArrayList.get(i - 2)));
				//				System.out.println(difAngle);
				if (difAngle > maxAngle) {
					smooth = false;
					//					System.out.println("Maximum angle exceeded: smooth = " + smooth);
					break;
				} else {
					smooth = true;
					//					System.out.println("Smoothness conditions satisfied: smooth = " + smooth);
				}
			}
			//			System.out.println("Data pair # " + i);
		}
		isSmooth = smooth;
		angleArrayList.clear();
		return isSmooth;
	}

	public HashMap<Double, Double> getGraphData() {
		return graphData;
	}

	public void setGraphData(HashMap<Double, Double> graphData) {
		this.graphData = graphData;
	}

	public double getMaxAngle() {
		return maxAngle;
	}

	public void setMaxAngle(double maxAngle) {
		this.maxAngle = maxAngle;
	}

	public boolean isSmooth() {
		return isSmooth;
	}

	public void setSmooth(boolean isSmooth) {
		this.isSmooth = isSmooth;
	}

}
