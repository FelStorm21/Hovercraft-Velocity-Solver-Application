package me.vmamakers.hcsolverapp2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class GUI {

	private RiccatiGraph graph;
	private RiccatiSolver solver;
//	private RiccatiHandler rhandler;
	private Smoother smoother;
	private @SuppressWarnings("unused") ClosedFormFunction closedFormFunction;
	private ClosedFormGraph closedFormGraph;

	private static final String[] SUB_CHARS = { "\u2080", "\u2081", "\u2082", "\u2083", "\u2084", "\u2085" };
	private String[] inputStrings;
	private String[] primeStrings = new String[3];

	private double result;
	private double cfResult;
	private double t;
	private int smoothingFactor = 8;
	private double defaultStepsize;

	private JDialog exceptionDialog;
	private JTextArea resultsTextArea;
	private JTextArea closedFormResultsTextArea;
	private JCheckBox graphBox;
	private JCheckBox closedFormGraphBox;
	private JCheckBox dragCoeffBox;

	protected PropertyChangeSupport propertyChangeSupport;
	boolean smoothingFactorEntered = false;
	boolean maxAngleEntered = false;

	private int dragCoeffCounter = 0;

	public GUI() {
		inputStrings = Arrays.stream(SUB_CHARS).map(sub -> "c" + sub).collect(Collectors.toList()).toArray(new String[0]);
		for (int i = 0; i < 3; i++) {
			primeStrings[i] = "c" + SUB_CHARS[i] + "'";
		}
		SwingUtilities.invokeLater(this::initGui);
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
					resultsTextArea.append("Smoothing...\n");
					closedFormResultsTextArea.append("Smoothing...\n");
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
					resultsTextArea.append("Smoothing complete.\n");
					closedFormResultsTextArea.append("Smoothing complete.\n");
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
					resultsTextArea.append("Smoothing...\n");
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
					resultsTextArea.append("Smoothing complete.\n");
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
					closedFormResultsTextArea.append("Smoothing...\n");
					closedFormGraph.setStepsize(defaultStepsize / smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
					closedFormGraph.generateData();
					smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
					closedFormResultsTextArea.append("Smoothing complete.\n");
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
					if (graphBox.isSelected()) {
						resultsTextArea.append("Smoothing...\n");
					}
					if (closedFormGraphBox.isSelected()) {
						closedFormResultsTextArea.append("Smoothing...\n");
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
					if (graphBox.isSelected()) {
						resultsTextArea.append("Smoothing complete.\n");
					}
					if (closedFormGraphBox.isSelected()) {
						closedFormResultsTextArea.append("Smoothing complete.\n");
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
			exceptionDialog.setVisible(true);
		}
	}

	private void initGui() {

		// set up all the objects we need to integrate into the gui	
		RiccatiGraph graph = new RiccatiGraph();
		this.graph = graph;
		
		defaultStepsize = (graph.getTmax() - graph.getTmin()) / 60;
		propertyChangeSupport = new PropertyChangeSupport(this);

		solver = new RiccatiSolver(defaultStepsize);
		solver.setFlag(3, true);
		solver.setFlag(1, true);
		solver.setFlag(2, false);

		graph.setSolver(solver);

//		RiccatiHandler rhandler = new RiccatiHandler((graph.getTmax() - graph.getTmin()) / 60); //uses arbitrary stepsize
//		this.rhandler = rhandler;
//		rhandler.setSolver(solver);

		Smoother smoother = new Smoother();
		this.smoother = smoother;

		ClosedFormFunction closedFormFunction = new ClosedFormFunction();
		this.closedFormFunction = closedFormFunction;

		ClosedFormGraph closedFormGraph = new ClosedFormGraph();
		this.closedFormGraph = closedFormGraph;
		closedFormGraph.setSolver(solver);
//		closedFormGraph.setRhandler(rhandler);
		closedFormGraph.setClosedFormFunction(closedFormFunction);

		//set up all the swing components

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(662, 380);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Riccati Differential Equation Solver");
		graph.setMainPanel(frame);

		final JPanel inputPanel = new JPanel();

		final JPanel displayPanel = new JPanel();

		JLabel displayLabel = new JLabel("This is the equation we are solving:");
		displayPanel.add(displayLabel);
		JLabel equationLabel = new JLabel("v'(t) = " + inputStrings[0] + " - " + inputStrings[1] + " [v(t)] - " + inputStrings[2] + " [v(t)]^2");
		displayPanel.add(equationLabel);
		frame.add(displayPanel, BorderLayout.NORTH);

		Dimension d = new Dimension(150, 5);

		JTextField[] inputs = new JTextField[6];
		JLabel[] inputLabels = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			JTextField input = i <= 2 ? new JTextField(10) : new JTextField(5);
			input.setMaximumSize(d);
			inputLabels[i] = new JLabel(inputStrings[i] + " = ");
			inputs[i] = input;
		}

		JTextField tInput = new JTextField(10);
		JTextField smoothingFactorInput = new JTextField(10);
		JTextField maxAngleInput = new JTextField(10);
		Arrays.asList(smoothingFactorInput, maxAngleInput, tInput).forEach((input) -> {
			input.setMaximumSize(d);
			input.setForeground(Color.gray);
		});

		JLabel smoothingFactorLabel = new JLabel("Smoothing factor:");
		smoothingFactorInput.setText(" (Default = 8)");

		JLabel maxAngleLabel = new JLabel("Max angle:");
		maxAngleInput.setText(" (Default = 10\u00b0)");

		JLabel tLabel = new JLabel("Enter value of t: ");

		JCheckBox graphBox = new JCheckBox("Show graph?");
		this.graphBox = graphBox;
		JCheckBox closedFormGraphBox = new JCheckBox("Show closed-form solution?");
		this.closedFormGraphBox = closedFormGraphBox;
		JCheckBox autoUpdateBox = new JCheckBox("Set automatic graph update?");
		JCheckBox dragCoeffBox = new JCheckBox("Use variable drag coefficient?");
		this.dragCoeffBox = dragCoeffBox;
		JButton manualUpdateButton = new JButton("Manual update");
		JLabel tMinLabel = new JLabel("t_min:");
		Dimension d2 = new Dimension(35, 5);
		JTextField tMinInput = new JTextField(3);
		tMinInput.setMaximumSize(d2);
		JLabel tMaxLabel = new JLabel("t_max:");
		JTextField tMaxInput = new JTextField(3);
		tMaxInput.setMaximumSize(d2);

		JPanel graphBoxPanel = new JPanel();
		graphBoxPanel.setLayout(new BoxLayout(graphBoxPanel, BoxLayout.Y_AXIS));
		graphBoxPanel.add(Box.createGlue());
		graphBoxPanel.add(graphBox);
		graphBoxPanel.add(Box.createGlue());
		graphBoxPanel.add(closedFormGraphBox);
		graphBoxPanel.add(Box.createGlue());
		graphBoxPanel.add(autoUpdateBox);
		graphBoxPanel.add(Box.createGlue());
		graphBoxPanel.add(dragCoeffBox);
		graphBoxPanel.add(Box.createGlue());
		graphBoxPanel.add(Box.createVerticalStrut(5));
		graphBoxPanel.add(manualUpdateButton);
		graphBoxPanel.add(Box.createGlue());

		JPanel checkPanel = new JPanel();
		GroupLayout layout2 = new GroupLayout(checkPanel);
		checkPanel.setLayout(layout2);
		layout2.setAutoCreateGaps(true);
		layout2.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hgroup2 = layout2.createSequentialGroup();
		hgroup2.addGroup(layout2.createParallelGroup().addComponent(tMinLabel).addComponent(tMaxLabel).addComponent(inputLabels[3]).addComponent(inputLabels[4]).addComponent(inputLabels[5]));
		hgroup2.addGroup(layout2.createParallelGroup().addComponent(tMinInput).addComponent(tMaxInput).addComponent(inputs[3]).addComponent(inputs[4]).addComponent(inputs[5]));
		layout2.setHorizontalGroup(hgroup2);

		GroupLayout.SequentialGroup vgroup2 = layout2.createSequentialGroup();
		vgroup2.addGroup(layout2.createParallelGroup().addComponent(tMinLabel).addComponent(tMinInput));
		vgroup2.addGroup(layout2.createParallelGroup().addComponent(tMaxLabel).addComponent(tMaxInput));
		vgroup2.addGroup(layout2.createParallelGroup().addComponent(inputLabels[3]).addComponent(inputs[3]));
		vgroup2.addGroup(layout2.createParallelGroup().addComponent(inputLabels[4]).addComponent(inputs[4]));
		vgroup2.addGroup(layout2.createParallelGroup().addComponent(inputLabels[5]).addComponent(inputs[5]));
		layout2.setVerticalGroup(vgroup2);

		for (int i = 3; i < inputLabels.length; i++) {
			inputLabels[i].setVisible(false);
			inputs[i].setVisible(false);
		}

		JDialog closedFormExceptionDialog = new JDialog();
		closedFormExceptionDialog.setSize(680, 140);
		closedFormExceptionDialog.setLocationRelativeTo(null);
		closedFormExceptionDialog.setTitle("Negative Discriminant Error");
		JPanel cfeDialogPane = new JPanel();
		cfeDialogPane.setLayout(new BoxLayout(cfeDialogPane, BoxLayout.Y_AXIS));
		JTextArea cfeText = new JTextArea(" The values chosen for c\u2080, c\u2081, and/or c\u2082 are not physically allowable. c\u2080 must be non-negative." + "\n Note that the closed form solution solves the equation: \n \t v'(t) = c\u2080 - c\u2081 [v(t)] - c\u2082 [v(t)]\u00b2 \n" + " Adjusting the signs of the coefficients could resolve differences between the Numerical and Closed-Form solutions.");
		cfeText.setEditable(false);
		cfeText.setBorder(BorderFactory.createLineBorder(Color.black));
		cfeDialogPane.setBackground(Color.lightGray);
		cfeDialogPane.add(Box.createVerticalGlue());
		cfeDialogPane.add(cfeText);
		cfeDialogPane.add(Box.createVerticalGlue());
		closedFormExceptionDialog.add(cfeDialogPane);

		this.exceptionDialog = new JDialog();
		JPanel dialogPane = new JPanel();
		dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.Y_AXIS));
		dialogPane.setBackground(Color.WHITE);
		exceptionDialog.setTitle("Stepsize Error");

		JLabel exceptionText = new JLabel("Minimum step size reached. Would you like to switch integrators?");
		JLabel exceptionText2 = new JLabel("You are currently using: Dormand Prince 853");
		JTextPane nanMessage = new JTextPane();
		nanMessage.setEditable(false);
		JTextPane hMessage = new JTextPane();
		hMessage.setEditable(false);
		JTextPane signMessage = new JTextPane();
		signMessage.setEditable(false);
		Dimension d3 = new Dimension(400, 170);
		nanMessage.setMaximumSize(d3);
		hMessage.setMaximumSize(d3);
		signMessage.setMaximumSize(d3);

		nanMessage.setText("*Note that if you are consistently seeing NaN results, the solution might be asymptotic in that region.");
		hMessage.setText("*Default h = 0.001");
		signMessage.setText("*c\u2080 must be non-negative.");

		exceptionText.setForeground(Color.red);
		exceptionText.setBackground(Color.WHITE);
		exceptionText2.setBackground(Color.WHITE);
		exceptionDialog.setSize(400, 200);
		exceptionDialog.setLocationRelativeTo(frame);
		dialogPane.add(exceptionText);
		exceptionText.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());
		dialogPane.add(exceptionText2);
		exceptionText2.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());

		JButton dpButton = new JButton("Dormand Prince 853");
		JButton rkButton = new JButton("Classical Runge Kutta");
		JLabel hLabel = new JLabel("h = ");
		JTextField hInput = new JTextField(5);
		Dimension d4 = new Dimension(400, 20);
		hInput.setMaximumSize(d4);

		JPanel rkRow = new JPanel();
		rkRow.setBackground(Color.WHITE);
		rkRow.setMaximumSize(d4);
		rkRow.setLayout(new BoxLayout(rkRow, BoxLayout.X_AXIS));
		rkRow.add(Box.createHorizontalGlue());
		rkRow.add(rkButton);
		rkRow.add(Box.createHorizontalStrut(10));
		rkRow.add(hLabel);
		rkRow.add(hInput);
		rkRow.add(Box.createHorizontalGlue());

		dialogPane.add(dpButton);
		dpButton.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());
		dialogPane.add(Box.createVerticalStrut(5));
		dialogPane.add(rkRow);
		rkButton.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());
		dialogPane.add(nanMessage);
		nanMessage.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());
		dialogPane.add(hMessage);
		hMessage.setAlignmentX((float) 0.5);
		dialogPane.add(Box.createVerticalGlue());
		dialogPane.add(signMessage);
		signMessage.setAlignmentX((float) 0.5);

		exceptionDialog.add(dialogPane);

		final JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
		final JPanel innerOutputPanel = new JPanel();
		innerOutputPanel.setLayout(new BoxLayout(innerOutputPanel, BoxLayout.X_AXIS));
		JTextArea resultsTextArea = new JTextArea(7, 12);
		this.resultsTextArea = resultsTextArea;
		resultsTextArea.setEditable(false);
		JScrollPane resultsPane = new JScrollPane(resultsTextArea);
		JTextArea closedFormResultsTextArea = new JTextArea(7, 12);
		this.closedFormResultsTextArea = closedFormResultsTextArea;
		closedFormResultsTextArea.setEditable(false);
		JScrollPane closedFormResultsPane = new JScrollPane(closedFormResultsTextArea);

		resultsPane.setBorder(BorderFactory.createTitledBorder("Numerical Results"));
		closedFormResultsPane.setBorder(BorderFactory.createTitledBorder("Closed-Form Results"));
		innerOutputPanel.add(resultsPane);
		innerOutputPanel.add(Box.createHorizontalStrut(10));
		innerOutputPanel.add(closedFormResultsPane);
		innerOutputPanel.add(Box.createHorizontalStrut(10));
		outputPanel.add(innerOutputPanel);
		outputPanel.add(Box.createVerticalStrut(10));

		GroupLayout layout = new GroupLayout(inputPanel);
		inputPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hgroup = layout.createSequentialGroup();
		hgroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).addComponent(inputLabels[0]).addComponent(inputLabels[1]).addComponent(inputLabels[2]).addComponent(tLabel).addComponent(smoothingFactorLabel).addComponent(maxAngleLabel).addComponent(checkPanel));
		hgroup.addGroup(layout.createParallelGroup().addComponent(inputs[0]).addComponent(inputs[1]).addComponent(inputs[2]).addComponent(tInput).addComponent(smoothingFactorInput).addComponent(maxAngleInput).addComponent(graphBoxPanel));
		layout.setHorizontalGroup(hgroup);

		GroupLayout.SequentialGroup vgroup = layout.createSequentialGroup();
		vgroup.addGroup(layout.createParallelGroup().addComponent(inputLabels[0]).addComponent(inputs[0]));
		vgroup.addGroup(layout.createParallelGroup().addComponent(inputLabels[1]).addComponent(inputs[1]));
		vgroup.addGroup(layout.createParallelGroup().addComponent(inputLabels[2]).addComponent(inputs[2]));
		vgroup.addGroup(layout.createParallelGroup().addComponent(tLabel).addComponent(tInput));
		vgroup.addGroup(layout.createParallelGroup().addComponent(smoothingFactorLabel).addComponent(smoothingFactorInput));
		vgroup.addGroup(layout.createParallelGroup().addComponent(maxAngleLabel).addComponent(maxAngleInput));
		vgroup.addGroup(layout.createParallelGroup().addComponent(checkPanel).addComponent(graphBoxPanel));
		layout.setVerticalGroup(vgroup);

		frame.add(inputPanel, BorderLayout.WEST);
		frame.add(outputPanel, BorderLayout.EAST);
		frame.pack();

		//set up all the action listeners

		for (int index = 0; index < inputs.length; index++) {
			final int i = index;
			JTextField input = inputs[i];
			input.addActionListener((e) -> {
				inputStrings[i] = input.getText().replaceAll(i < 3 ? " Enter number: " : " Retry: ", "");
				if (i < 3) {
					primeStrings[i] = inputStrings[i];
				}
				boolean inError = false;
				try {
					input.setForeground(Color.black);
					double value = Double.parseDouble(input.getText().replaceAll(i < 3 ? " Enter number: " : " Retry: ", ""));
					if (value < 0) {
						throw new NumberFormatException();
					}
					if (i < 3) {
						solver.setConstant(i, value);
						closedFormFunction.setConstant(i, value);
					} else {
						solver.setRegCoeff(i - 3, value);
					}
					input.setText("");
				} catch (NumberFormatException e4) {
					input.setForeground(Color.red);
					input.setText(i < 3 ? " Enter number: " : " Retry: ");
					inError = true;
				}
				if (dragCoeffBox.isSelected()) {
					equationLabel.setText("v'(t) = " + primeStrings[0] + " - " + primeStrings[1] + " [v(t)] - " + primeStrings[2] + " [v(t)]^2 - " + "(" + inputStrings[3] + " exp(-" + inputStrings[4] + " v(t)) + " + inputStrings[5] + ")[v(t)]^2");
				} else {
					equationLabel.setText("v'(t) = " + inputStrings[0] + " - " + inputStrings[1] + " [v(t)] - " + inputStrings[2] + " [v(t)]^2");
				}
				if (!inError) {
					if (i == 2) {
						tInput.requestFocus();
					}
					if (i < inputs.length - 1) {
						inputs[i + 1].requestFocus();
					} else {
						graphBox.requestFocus();
					}
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}

			});
		}

		smoothingFactorInput.addActionListener((e) -> {
			int smoothingFactorOld = smoothingFactor;
			boolean inError = false;
			try {
				smoothingFactorInput.setForeground(Color.black);
				smoothingFactor = Integer.parseInt(smoothingFactorInput.getText().replaceAll(" Enter integer: ", ""));
				if (smoothingFactor <= 0) {
					throw new NumberFormatException();
				}
				smoothingFactorInput.setText("");
			} catch (NumberFormatException e3) {
				inError = true;
				smoothingFactorInput.setForeground(Color.red);
				smoothingFactorInput.setText(" Enter integer: ");
			}
			if (!inError) {
				maxAngleInput.requestFocus();
			}
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
			propertyChangeSupport.firePropertyChange("smoothingFactorEntered", smoothingFactorOld, smoothingFactor);

		});

		smoothingFactorInput.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (smoothingFactorInput.getText().equals(" (Default = 8)")) {
					smoothingFactorInput.setText("");
					smoothingFactorInput.setForeground(Color.black);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

		});

		propertyChangeSupport.addPropertyChangeListener("smoothingFactorEntered", (evt) -> smoothingFactorEntered = true);

		smoothingFactorInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (smoothingFactorInput.getText().equals(" (Default = 8)")) {
					smoothingFactorInput.setText("");
					smoothingFactorInput.setForeground(Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (!smoothingFactorEntered) {
					smoothingFactorInput.setForeground(Color.gray);
					smoothingFactorInput.setText(" (Default = 8)");
				}
			}

		});

		maxAngleInput.addActionListener((e) -> {
			double maxAngleOld = smoother.getMaxAngle();
			boolean inError = false;
			try {
				maxAngleInput.setForeground(Color.black);
				smoother.setMaxAngle(Double.parseDouble(maxAngleInput.getText().replaceAll(" Enter number: ", "")));
				if (smoother.getMaxAngle() <= 0) {
					throw new NumberFormatException();
				}
				maxAngleInput.setText("");
			} catch (NumberFormatException e8) {
				inError = true;
				maxAngleInput.setForeground(Color.red);
				maxAngleInput.setText(" Enter number: ");
			}
			if (!inError) {
				tMinInput.requestFocus();
			}
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
			propertyChangeSupport.firePropertyChange("maxAngleEntered", maxAngleOld, smoother.getMaxAngle());

		});

		maxAngleInput.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (maxAngleInput.getText().equals(" (Default = 10\u00b0)")) {
					maxAngleInput.setText("");
					maxAngleInput.setForeground(Color.black);
				}

			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

		});

		propertyChangeSupport.addPropertyChangeListener("maxAngleEntered", (evt) -> maxAngleEntered = true);

		maxAngleInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (maxAngleInput.getText().equals(" (Default = 10\u00b0)")) {
					maxAngleInput.setText("");
					maxAngleInput.setForeground(Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (!maxAngleEntered) {
					maxAngleInput.setForeground(Color.gray);
					maxAngleInput.setText(" (Default = 10\u00b0)");
				}
			}

		});

		manualUpdateButton.addActionListener((e) -> updateGraph());

		Arrays.asList(false, true).forEach((max) -> {
			JTextField input = max ? tMaxInput : tMinInput;
			input.addActionListener((e) -> {
				graph.setMinCounter(graph.getMinCounter() + 1);
				closedFormGraph.setMinCounter(closedFormGraph.getMinCounter() + 1);
				boolean inError = false;
				try {
					double value = Double.parseDouble(input.getText().replaceAll(" #: ", ""));
					input.setForeground(Color.black);
					if (!max && (value < graph.getTmax()) && value >= 0) {
						graph.setTmin(value);
						solver.setBound(0, value);
//						rhandler.setTmin(value);
						closedFormGraph.setMin(value);
						input.setText("");
					} else if (max && (value > graph.getTmin()) && value >= 0) {
						graph.setTmax(value);
						solver.setBound(1, value);
//						rhandler.setTmax(value);
						closedFormGraph.settMax(value);
						input.setText("");
					} else {
						input.setForeground(Color.red);
					}
				} catch (NumberFormatException e6) {
					inError = true;
					input.setForeground(Color.red);
					input.setText(" #: ");
				}
				if (!inError) {
					if (!max) {
						tMaxInput.requestFocus();
					} else {
						graphBox.requestFocus();
					}
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}

			});
		});

		graphBox.addActionListener((e) -> {
			graph.setCheckCounter(graph.getCheckCounter() + 1);
			if (graph.getMaxCounter() == 0 & graph.getMinCounter() == 0) {
				solver.setBound(0, 0);
				solver.setBound(1, 120);
//				rhandler.setTmin(0);
//				rhandler.setTmax(120);
				graph.setTmin(0);
				graph.setTmax(120);
			}
			if (graph.getCheckCounter() % 2 != 0) {
				try {
//					if (rhandler.getGraphData() != null && graph.getGraphData() != null) {
//						rhandler.getGraphData().clear();
//						graph.getGraphData().clear();
//					}
					if (solver.getGraphData() != null && graph.getGraphData() != null) {
						solver.getGraphData().clear();
						graph.getGraphData().clear();
					}
					solver.solve(0, true);
//					rhandler.generateData();
//					System.out.println("Actual size of dataset: " + rhandler.getGraphData().size());
					graph.setGraphData(solver.getGraphData());
//					graph.setGraphData(rhandler.getGraphData());
					smoother.setGraphData(graph.getGraphData());
					if (!smoother.checkSmooth()) {
						resultsTextArea.append("Smoothing...\n");
						solver.setBound(2, defaultStepsize / smoothingFactor);
						solver.getGraphData().clear();
//						rhandler.setStepsize(defaultStepsize / smoothingFactor);
//						rhandler.getGraphData().clear();
						graph.getGraphData().clear();
						solver.solve(0, true);
//						rhandler.generateData();
						graph.setGraphData(solver.getGraphData());
//						graph.setGraphData(rhandler.getGraphData());
						smoother.setGraphData(graph.getGraphData());
						resultsTextArea.append("Smoothing complete.\n");
					}
					//						System.out.println("Plotting graph");
					graph.plot();

				} catch (NumberIsTooSmallException e2) {
					exceptionDialog.setVisible(true);
				}
				if (graph.getCheckCounter() != 1 && graph.getLastLocation() != null) {
					graph.getChartFrame().setLocation(graph.getLastLocation());
				}
			} else if (graph.getChartFrame() != null && graph.getCheckCounter() % 2 == 0) {
				graph.setLastLocation(graph.getChartFrame().getLocation());
				graph.getChartFrame().dispose();
				graph.setCheckCounter(graph.getCheckCounter() + 1);
			}

		});

		graph.getPropertyChangeSupport().addPropertyChangeListener((evt) -> {
			if (evt.getPropertyName().equals("graphClosed")) {
				graphBox.setSelected(false);
				graph.setCheckCounter(graph.getCheckCounter() + 1);
				graph.setLastLocation(graph.getChartFrame().getLocation());
			}

		});

		closedFormFunction.getPropertyChangeSupport().addPropertyChangeListener("nanExceptionThrown", (evt) -> {
			closedFormExceptionDialog.setVisible(true);
		});

		closedFormGraphBox.addActionListener((e) -> {
			closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
			if (closedFormGraph.getMaxCounter() == 0 & closedFormGraph.getMinCounter() == 0) {
				closedFormGraph.setMin(0);
				closedFormGraph.settMax(120);
			}
			if (closedFormGraph.getCheckCounter() % 2 != 0) {
				if (closedFormGraph.getClosedFormGraphData() != null && closedFormGraph.getSeries() != null /*&& rhandler.getGraphData() != null*/) {
					closedFormGraph.getSeries().clear();
					closedFormGraph.getClosedFormGraphData().clear();
				}
				closedFormGraph.generateData();
				smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
				if (!smoother.checkSmooth()) {
					closedFormResultsTextArea.append("Smoothing...\n");
					closedFormGraph.setStepsize(defaultStepsize / smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
					closedFormGraph.generateData();
					smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
					closedFormResultsTextArea.append("Smoothing complete.\n");
				}
				closedFormGraph.generateData();
				closedFormGraph.plot();
				if (closedFormGraph.getCheckCounter() != 1 && closedFormGraph.getLastLocation() != null) {
					closedFormGraph.getChartFrame().setLocation(closedFormGraph.getLastLocation());
				}
			} else if (closedFormGraph.getChartFrame() != null && closedFormGraph.getCheckCounter() % 2 == 0) {
				closedFormGraph.setLastLocation(closedFormGraph.getChartFrame().getLocation());
				closedFormGraph.getChartFrame().dispose();
				closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
			}
		});

		closedFormGraph.getPcs().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("closedFormGraphClosed")) {
					closedFormGraphBox.setSelected(false);
					closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
					closedFormGraph.setLastLocation(closedFormGraph.getChartFrame().getLocation());
				}
			}

		});

		dragCoeffBox.addActionListener((e) -> {
			boolean checked = dragCoeffBox.isSelected();
			dragCoeffCounter++;
			for (int i = 3; i < inputLabels.length; i++) {
				inputLabels[i].setVisible(checked);
				inputs[i].setVisible(checked);
				inputLabels[i].setText("c" + SUB_CHARS[i] + " = ");
			}
			solver.setFlag(1, !checked);
			solver.setFlag(2, checked);
			closedFormResultsTextArea.setVisible(!checked);
			closedFormGraphBox.setEnabled(!checked);
			if (checked) {
				frame.setSize(690, 400);

				smoother.setMaxAngle(smoother.getMaxAngle() / 5);
				maxAngleInput.setText("");
				equationLabel.setText("v'(t) = " + primeStrings[0] + " - " + primeStrings[1] + " [v(t)] - " + primeStrings[2] + " [v(t)]^2 - " + "(" + inputStrings[3] + " exp(-" + inputStrings[4] + " v(t)) + " + inputStrings[5] + ")[v(t)]^2");
			} else {
				frame.setSize(662, 380);
				smoother.setMaxAngle(10);
				maxAngleInput.setForeground(Color.gray);
				maxAngleInput.setText(" (Default = 10\u00b0)");
				equationLabel.setText("v'(t) = " + inputStrings[0] + " - " + inputStrings[1] + " [v(t)] - " + inputStrings[2] + " [v(t)]^2");
			}
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
			// ADD THIS TO MANUAL UPDATE PROCESS
		});

		dpButton.addActionListener((e) -> {
			exceptionText2.setText("You are currently using: Dormand Prince 853");
			solver.setFlag(4, false);
			solver.setFlag(3, true);
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
		});

		rkButton.addActionListener((e) -> {
			exceptionText2.setText("You are currently using: Classical Runge Kutta");
			solver.setFlag(3, false);
			solver.setFlag(4, true);
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
		});

		hInput.addActionListener((e) -> {
			try {
				hInput.setForeground(Color.black);
				solver.setH(Double.parseDouble(hInput.getText().replaceAll(" #: ", "")));
//				rhandler.setH(Double.parseDouble(hInput.getText().replaceAll(" #: ", "")));
				hInput.setText("");
			} catch (NumberFormatException e7) {
				hInput.setForeground(Color.red);
				hInput.setText(" #: ");
			}
			if (autoUpdateBox.isSelected()) {
				updateGraph();
			}
		});

		tInput.addActionListener((e) -> {
			try {
				tInput.setForeground(Color.BLACK);
				t = Double.parseDouble(tInput.getText().replaceAll(" Enter t \u2265 0: ", ""));
				solver.setFlag(0, true);

				if (t > 0) {
					try {
						tInput.setForeground(Color.black);
						cfResult = closedFormFunction.value(t);
						result = solver.solve(t, false);
						if (Double.isNaN(result)) {
							resultsTextArea.append("NaN" + "\n");
							throw new NotANumberException();
						}
						resultsTextArea.append(" v(" + tInput.getText().replaceAll(" Enter t \u2265 0: ", "") + ") = " + result + "\n");
						closedFormResultsTextArea.append(" v(" + tInput.getText().replaceAll(" Enter t \u2265 0: ", "") + ") = " + cfResult + "\n");
						tInput.setText("");
					} catch (NumberIsTooSmallException | NotANumberException e1) {
						exceptionDialog.setVisible(true);
						closedFormExceptionDialog.setVisible(true);
						tInput.setText("");
					}

				} else if (t == 0) {
					tInput.setForeground(Color.black);
					result = 0;
					cfResult = 0;
					resultsTextArea.append(" v(" + tInput.getText().replaceAll(" Enter t \u2265 0: ", "") + ") = " + result + "\n");
					closedFormResultsTextArea.append(" v(" + tInput.getText().replaceAll(" Enter t \u2265 0: ", "") + ") = " + result + "\n");
					tInput.setText("");
				} else {
					tInput.setForeground(Color.red);
					tInput.setText(" Enter t \u2265 0: ");
				}

			} catch (IllegalArgumentException e9) { //originally had numberformatexception
				tInput.setForeground(Color.red);
				tInput.setText(" Enter t \u2265 0: ");
			}
		});

		//		exceptionDialog.setVisible(true);  /* ---> this is for debugging */

		frame.setVisible(true);

	}
}