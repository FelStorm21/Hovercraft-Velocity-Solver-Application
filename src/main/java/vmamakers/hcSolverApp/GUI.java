package vmamakers.hcSolverApp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
	private RiccatiHandler rhandler;
	private RiccatiSmoother smoother;
	@SuppressWarnings("unused")
	private ClosedFormFunction closedFormFunction;
	private ClosedFormGraph closedFormGraph;
	
	private final static String SUB_ZERO = "\u2080";
	private final static String SUB_ONE = "\u2081";
	private final static String SUB_TWO = "\u2082";
	private final static String SUB_THREE = "\u2083";
	private final static String SUB_FOUR = "\u2084";
	private final static String SUB_FIVE = "\u2085";
	
	private String c_zeroString = "c" + SUB_ZERO;
	private String c_oneString = "c" + SUB_ONE;
	private String c_twoString = "c" + SUB_TWO;
	private String c_threeString = "c" + SUB_THREE;
	private String c_fourString = "c" + SUB_FOUR;
	private String c_fiveString = "c" + SUB_FIVE;
	
	private String c_zeroPrimeString = "c" + SUB_ZERO + "'";
	private String c_onePrimeString = "c" + SUB_ONE + "'";
	private String c_twoPrimeString = "c" + SUB_TWO + "'";
	
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
	
	public void updateGraph() {
		try {
			if ((graph.getChartFrame() != null && graph.getChartFrame().isVisible()) && (closedFormGraph.getChartFrame() != null && closedFormGraph.getChartFrame().isVisible())) {
				graph.getDataset().removeAllSeries();
				closedFormGraph.getDataset().removeAllSeries();
				rhandler.getGraphData().clear();
				graph.getGraphData().clear();
				closedFormGraph.getSeries().clear();
				rhandler.generateData();
				closedFormGraph.generateData();
				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());
				
				if (!smoother.checkSmooth()) {
					resultsTextArea.append("Smoothing...\n");
					closedFormResultsTextArea.append("Smoothing...\n");
					rhandler.setStepsize(defaultStepsize/smoothingFactor);
					closedFormGraph.setStepsize(defaultStepsize/smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
					rhandler.getGraphData().clear();
					graph.getGraphData().clear();
					rhandler.generateData();
					closedFormGraph.generateData();
					graph.setGraphData(rhandler.getGraphData());
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
				
			}
			else if ((graph.getChartFrame() != null && graph.getChartFrame().isVisible())/* && (closedFormGraph.getChartFrame() == null && !closedFormGraph.getChartFrame().isVisible())*/) {
				graph.getDataset().removeAllSeries();
				rhandler.getGraphData().clear();
				graph.getGraphData().clear();
				rhandler.generateData();
				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());
				
				if (!smoother.checkSmooth()) {
					resultsTextArea.append("Smoothing...\n");
					rhandler.setStepsize(defaultStepsize/smoothingFactor);
					rhandler.getGraphData().clear();
					graph.getGraphData().clear();
					rhandler.generateData();
					graph.setGraphData(rhandler.getGraphData());
					smoother.setGraphData(graph.getGraphData());
					resultsTextArea.append("Smoothing complete.\n");	
				}
//				System.out.println("Plotting graph");
				graph.updateDataset();
				graph.getDataset().addSeries(graph.getSeries());
				graph.getChartFrame().repaint();				
			}
			else if (/*(graph.getChartFrame() == null && !graph.getChartFrame().isVisible()) && */(closedFormGraph.getChartFrame() != null && closedFormGraph.getChartFrame().isVisible())) {
				closedFormGraph.getDataset().removeAllSeries();
				closedFormGraph.getClosedFormGraphData().clear();
				closedFormGraph.getSeries().clear();
				closedFormGraph.generateData();
				smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
				
				if (!smoother.checkSmooth()) {
					closedFormResultsTextArea.append("Smoothing...\n");
					closedFormGraph.setStepsize(defaultStepsize/smoothingFactor);
					closedFormGraph.getClosedFormGraphData().clear();
					closedFormGraph.generateData();
					smoother.setGraphData(closedFormGraph.getClosedFormGraphData());
					closedFormResultsTextArea.append("Smoothing complete.\n");
				}
//				System.out.println("Plotting graph");
				closedFormGraph.generateData();
				closedFormGraph.getDataset().addSeries(closedFormGraph.getSeries());
				closedFormGraph.getChartFrame().repaint();
			}
			else {
				rhandler.generateData();
				closedFormGraph.generateData();
				graph.setGraphData(rhandler.getGraphData());
				smoother.setGraphData(graph.getGraphData());
				if (!smoother.checkSmooth()) {
					if (graphBox.isSelected()) {
						resultsTextArea.append("Smoothing...\n");
					}
					if (closedFormGraphBox.isSelected()) {
						closedFormResultsTextArea.append("Smoothing...\n");
					}
					rhandler.setStepsize(defaultStepsize/smoothingFactor);
					closedFormGraph.setStepsize(defaultStepsize/smoothingFactor);
					rhandler.generateData();
					closedFormGraph.generateData();
					graph.setGraphData(rhandler.getGraphData());
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
		} catch(NumberIsTooSmallException e) {
			exceptionDialog.setVisible(true);
		}
	}
	

	public GUI() {
		SwingUtilities.invokeLater(this::initGui);
	}
	
	private void initGui() {
		
// set up all the objects we need to integrate into the gui	
		
		propertyChangeSupport = new PropertyChangeSupport(this);
		
		RiccatiSolver solver = new RiccatiSolver();
		solver.setUsingDP(true);
		solver.setSolvingNormal(true);
		solver.setSolvingDrag(false);

		RiccatiGraph graph = new RiccatiGraph();
		this.graph = graph;
		graph.setSolver(solver);
		
		this.defaultStepsize = (graph.getTmax() - graph.getTmin())/60;
		
		RiccatiHandler rhandler = new RiccatiHandler((graph.getTmax() - graph.getTmin())/60);  //uses arbitrary stepsize
		this.rhandler = rhandler;
		rhandler.setSolver(solver);
		
		RiccatiSmoother smoother = new RiccatiSmoother();
		this.smoother = smoother;
		
		ClosedFormFunction closedFormFunction = new ClosedFormFunction();
		this.closedFormFunction = closedFormFunction;
		
		ClosedFormGraph closedFormGraph = new ClosedFormGraph();
		this.closedFormGraph = closedFormGraph;
		closedFormGraph.setRhandler(rhandler);
		closedFormGraph.setClosedFormFunction(closedFormFunction);
		
//set up all the swing components
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(662,380);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Riccati Differential Equation Solver");
		graph.setMainPanel(frame);
		
		final JPanel inputPanel = new JPanel();	
		
		final JPanel displayPanel = new JPanel();
		
		JLabel displayLabel = new JLabel("This is the equation we are solving:");
		displayPanel.add(displayLabel);
		JLabel equationLabel = new JLabel("v'(t) = "+c_zeroString+" - "+c_oneString+" [v(t)] - "+c_twoString+" [v(t)]^2");
		displayPanel.add(equationLabel);
		frame.add(displayPanel, BorderLayout.NORTH);		
		
		Dimension d = new Dimension(150, 5);
		
		JTextField c_zeroInput = new JTextField(10);
		JTextField c_oneInput = new JTextField(10);
		JTextField c_twoInput = new JTextField(10);
		JTextField c_threeInput = new JTextField(5);
		JTextField c_fourInput = new JTextField(5);
		JTextField c_fiveInput = new JTextField(5);
		JTextField tInput = new JTextField(10);
		JTextField smoothingFactorInput = new JTextField(10);
		JTextField maxAngleInput = new JTextField(10);
		
		c_zeroInput.setMaximumSize(d);
		JLabel c_zeroLabel = new JLabel(c_zeroString + " = ");
		
		c_oneInput.setMaximumSize(d);
		JLabel c_oneLabel = new JLabel(c_oneString + " = ");
		
		c_twoInput.setMaximumSize(d);
		JLabel c_twoLabel = new JLabel(c_twoString + " = ");
		
		c_threeInput.setMaximumSize(d);
		JLabel c_threeLabel = new JLabel(c_threeString + " = ");
		
		c_fourInput.setMaximumSize(d);
		JLabel c_fourLabel = new JLabel(c_fourString + " = ");
		
		c_fiveInput.setMaximumSize(d);
		JLabel c_fiveLabel = new JLabel(c_fiveString + " = ");
		
		smoothingFactorInput.setMaximumSize(d);
		JLabel smoothingFactorLabel = new JLabel("Smoothing factor:");
		smoothingFactorInput.setForeground(Color.gray);
		smoothingFactorInput.setText(" (Default = 8)");
		
		maxAngleInput.setMaximumSize(d);
		JLabel maxAngleLabel = new JLabel("Max angle:");
		maxAngleInput.setForeground(Color.gray);
		maxAngleInput.setText(" (Default = 10\u00b0)");
		
		tInput.setMaximumSize(d);
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
		Dimension d2 = new Dimension(35,5);
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
		hgroup2.addGroup(layout2.createParallelGroup().
				addComponent(tMinLabel).
				addComponent(tMaxLabel).
				addComponent(c_threeLabel).
				addComponent(c_fourLabel).
				addComponent(c_fiveLabel));
		hgroup2.addGroup(layout2.createParallelGroup().
				addComponent(tMinInput).
				addComponent(tMaxInput).
				addComponent(c_threeInput).
				addComponent(c_fourInput).
				addComponent(c_fiveInput));
		layout2.setHorizontalGroup(hgroup2);
		
		GroupLayout.SequentialGroup vgroup2 = layout2.createSequentialGroup();
		vgroup2.addGroup(layout2.createParallelGroup().
				addComponent(tMinLabel).
				addComponent(tMinInput));
		vgroup2.addGroup(layout2.createParallelGroup().
				addComponent(tMaxLabel).
				addComponent(tMaxInput));
		vgroup2.addGroup(layout2.createParallelGroup().
				addComponent(c_threeLabel).
				addComponent(c_threeInput));
		vgroup2.addGroup(layout2.createParallelGroup().
				addComponent(c_fourLabel).
				addComponent(c_fourInput));
		vgroup2.addGroup(layout2.createParallelGroup().
				addComponent(c_fiveLabel).
				addComponent(c_fiveInput));
		layout2.setVerticalGroup(vgroup2);
		
		c_threeLabel.setVisible(false);
		c_threeInput.setVisible(false);
		c_fourLabel.setVisible(false);
		c_fourInput.setVisible(false);
		c_fiveLabel.setVisible(false);
		c_fiveInput.setVisible(false);
		
		JDialog closedFormExceptionDialog = new JDialog();
		closedFormExceptionDialog.setSize(680, 140);
		closedFormExceptionDialog.setLocationRelativeTo(null);
		closedFormExceptionDialog.setTitle("Negative Discriminant Error");
		JPanel cfeDialogPane = new JPanel();
		cfeDialogPane.setLayout(new BoxLayout(cfeDialogPane, BoxLayout.Y_AXIS));
		JTextArea cfeText = new JTextArea(" The values chosen for c\u2080, c\u2081, and/or c\u2082 are not physically allowable. c\u2080 must be non-negative."
				+ "\n Note that the closed form solution solves the equation: \n \t v'(t) = c\u2080 - c\u2081 [v(t)] - c\u2082 [v(t)]\u00b2 \n"
				+ " Adjusting the signs of the coefficients could resolve differences between the Numerical and Closed-Form solutions.");
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
		JTextArea resultsTextArea = new JTextArea(7,12);
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
		hgroup.addGroup(layout.createParallelGroup(Alignment.TRAILING).
				addComponent(c_zeroLabel).
				addComponent(c_oneLabel).
				addComponent(c_twoLabel).
				addComponent(tLabel).
				addComponent(smoothingFactorLabel).
				addComponent(maxAngleLabel).
				addComponent(checkPanel));
		hgroup.addGroup(layout.createParallelGroup().
				addComponent(c_zeroInput).
				addComponent(c_oneInput).
				addComponent(c_twoInput).
				addComponent(tInput).
				addComponent(smoothingFactorInput).
				addComponent(maxAngleInput).
				addComponent(graphBoxPanel));
		layout.setHorizontalGroup(hgroup);
		
		GroupLayout.SequentialGroup vgroup = layout.createSequentialGroup();
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(c_zeroLabel).
				addComponent(c_zeroInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(c_oneLabel).
				addComponent(c_oneInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(c_twoLabel).
				addComponent(c_twoInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(tLabel).
				addComponent(tInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(smoothingFactorLabel).
				addComponent(smoothingFactorInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(maxAngleLabel).
				addComponent(maxAngleInput));
		vgroup.addGroup(layout.createParallelGroup().
				addComponent(checkPanel).
				addComponent(graphBoxPanel));
		layout.setVerticalGroup(vgroup);
		
		frame.add(inputPanel, BorderLayout.WEST);
		frame.add(outputPanel, BorderLayout.EAST);
		frame.pack();
	
//set up all the action listeners
		
		c_zeroInput.addActionListener((e) -> {
				c_zeroString = c_zeroInput.getText().replaceAll(" Enter number: ", "");
				c_zeroPrimeString = c_zeroString;
				boolean inError = false;
				try {
					c_zeroInput.setForeground(Color.black);
					if (Double.parseDouble(c_zeroInput.getText().replaceAll(" Enter number: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setC_zero(Double.parseDouble(c_zeroInput.getText().replaceAll(" Enter number: ", "")));
					closedFormFunction.setChi(Double.parseDouble(c_zeroInput.getText().replaceAll(" Enter number: ", "")));
					c_zeroInput.setText("");
				} catch(NumberFormatException e4) {
					c_zeroInput.setForeground(Color.red);
					c_zeroInput.setText(" Enter number: ");
					inError = true;
				}
					
				if (dragCoeffBox.isSelected()) {
					equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
							+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				}
				else {
					equationLabel.setText("v'(t) = "+c_zeroString+" - "+c_oneString+" [v(t)] - "+c_twoString+" [v(t)]^2");
				}
				if (!inError) {
					c_oneInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
			
		});

		c_oneInput.addActionListener((e) -> {
				c_oneString = c_oneInput.getText().replaceAll(" Enter number: ", "");
				c_onePrimeString = c_oneString;
				boolean inError = false;
				try {
					c_oneInput.setForeground(Color.black);
					if (Double.parseDouble(c_oneInput.getText().replaceAll(" Enter number: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setC_one(Double.parseDouble(c_oneInput.getText().replaceAll(" Enter number: ", "")));
					closedFormFunction.setPhi(Double.parseDouble(c_oneInput.getText().replaceAll(" Enter number: ", "")));
					c_oneInput.setText("");
				} catch(NumberFormatException e4) {
					c_oneInput.setForeground(Color.red);
					c_oneInput.setText(" Enter number: ");
					inError = true;
				}
					
				if (dragCoeffBox.isSelected()) {
					equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
							+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				}
				else {
					equationLabel.setText("v'(t) = "+c_zeroString+" - "+c_oneString+" [v(t)] - "+c_twoString+" [v(t)]^2");
				}
				if (!inError) {
					c_twoInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
			
		});

		c_twoInput.addActionListener((e) -> {
				c_twoString = c_twoInput.getText().replaceAll(" Enter number: ", "");
				c_twoPrimeString = c_twoString;
				boolean inError = false;
				try {
					c_twoInput.setForeground(Color.black);
					if (Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setC_two(Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")));
					closedFormFunction.setGamma(Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")));
					c_twoInput.setText("");
				} catch(NumberFormatException e4) {
					c_twoInput.setForeground(Color.red);
					c_twoInput.setText(" Enter number: ");
					inError = true;
				}
					
				if (dragCoeffBox.isSelected()) {
					equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
							+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				}
				else {
					equationLabel.setText("v'(t) = "+c_zeroString+" - "+c_oneString+" [v(t)] - "+c_twoString+" [v(t)]^2");
				}
				if (!inError) {
					tInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
			
		});
		
		c_threeInput.addActionListener((e) -> {
				c_threeString = c_threeInput.getText().replaceAll(" Retry: ", "");
				boolean inError = false;
				try {
					c_threeInput.setForeground(Color.black);
					if (Double.parseDouble(c_threeInput.getText().replaceAll(" Retry: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setA(Double.parseDouble(c_threeInput.getText().replaceAll(" Retry: ", "")));
//					closedFormFunction.setGamma(Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")));
					c_threeInput.setText("");
				} catch(NumberFormatException e4) {
					c_threeInput.setForeground(Color.red);
					c_threeInput.setText(" Retry: ");
					inError = true;
				}
				
				equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
						+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				if (!inError) {
					c_fourInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
			
		});
		
		c_fourInput.addActionListener((e) -> {
				c_fourString = c_fourInput.getText().replaceAll(" Retry: ", "");
				boolean inError = false;
				try {
					c_fourInput.setForeground(Color.black);
					if (Double.parseDouble(c_fourInput.getText().replaceAll(" Retry: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setB(Double.parseDouble(c_fourInput.getText().replaceAll(" Retry: ", "")));
//					closedFormFunction.setGamma(Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")));
					c_fourInput.setText("");
				} catch(NumberFormatException e4) {
					c_fourInput.setForeground(Color.red);
					c_fourInput.setText(" Retry: ");
					inError = true;
				}
				
				equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
						+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				if (!inError) {
					c_fiveInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
		});
		
		c_fiveInput.addActionListener((e) -> {
				c_fiveString = c_fiveInput.getText().replaceAll(" Retry: ", "");
				boolean inError = false;
				try {
					c_fiveInput.setForeground(Color.black);
					if (Double.parseDouble(c_fiveInput.getText().replaceAll(" Retry: ", "")) < 0) {
						throw new NumberFormatException();
					}
					solver.setC(Double.parseDouble(c_fiveInput.getText().replaceAll(" Retry: ", "")));
//					closedFormFunction.setGamma(Double.parseDouble(c_twoInput.getText().replaceAll(" Enter number: ", "")));
					c_fiveInput.setText("");
				} catch(NumberFormatException e4) {
					c_fiveInput.setForeground(Color.red);
					c_fiveInput.setText(" Retry: ");
					inError = true;
				}
				
				equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
						+ "("+ c_threeString + " exp(-" + c_fourString + " v(t)) + " + c_fiveString + ")[v(t)]^2");
				if (!inError) {
					graphBox.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}					
			
		});
		
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
				} catch(NumberFormatException e3) {
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

			@Override public void mouseClicked(MouseEvent e) {
				if (smoothingFactorInput.getText().equals(" (Default = 8)")) {
					smoothingFactorInput.setText("");
					smoothingFactorInput.setForeground(Color.black);
				}
			}

			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			
		});
		
		propertyChangeSupport.addPropertyChangeListener("smoothingFactorEntered", (evt) ->
				smoothingFactorEntered = true
			
			
		);
		
		smoothingFactorInput.addFocusListener(new FocusListener() {

			@Override public void focusGained(FocusEvent e) {
				if (smoothingFactorInput.getText().equals(" (Default = 8)")) {
					smoothingFactorInput.setText("");
					smoothingFactorInput.setForeground(Color.black);
				}
			}

			@Override public void focusLost(FocusEvent e) {
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
				} catch(NumberFormatException e8) {
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

			@Override public void mouseClicked(MouseEvent e) {
				if (maxAngleInput.getText().equals(" (Default = 10\u00b0)")) {
					maxAngleInput.setText("");
					maxAngleInput.setForeground(Color.black);
				}
					
			}

			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			
		});
		
		propertyChangeSupport.addPropertyChangeListener("maxAngleEntered", (evt) ->
				maxAngleEntered = true);
		
		maxAngleInput.addFocusListener(new FocusListener() {

			@Override public void focusGained(FocusEvent e) {
				if (maxAngleInput.getText().equals(" (Default = 10\u00b0)")) {
					maxAngleInput.setText("");
					maxAngleInput.setForeground(Color.black);
				}
			}
			
			@Override public void focusLost(FocusEvent e) {
				if (!maxAngleEntered) {
					maxAngleInput.setForeground(Color.gray);
					maxAngleInput.setText(" (Default = 10\u00b0)");
				}
			}
			
		});

		manualUpdateButton.addActionListener((e) -> updateGraph());

		tMinInput.addActionListener((e) -> {
				graph.setMinCounter(graph.getMinCounter() + 1);
				closedFormGraph.setMinCounter(closedFormGraph.getMinCounter() + 1);
				boolean inError = false;
				try {
					tMinInput.setForeground(Color.black);
					if ((Double.parseDouble(tMinInput.getText().replaceAll(" #: ", "")) < graph.getTmax()) && (Double.parseDouble(tMinInput.getText().replaceAll(" #: ", "")) >= 0 )) {
						graph.setTmin(Double.parseDouble(tMinInput.getText().replaceAll(" #: ", "")));
						rhandler.setTmin(Double.parseDouble(tMinInput.getText().replaceAll(" #: ", "")));
						closedFormGraph.settMin(Double.parseDouble(tMinInput.getText().replaceAll(" #: ", "")));
						tMinInput.setText("");
					}
					else {
						tMinInput.setForeground(Color.red);
					}
				} catch(NumberFormatException e6) {
					inError = true;
					tMinInput.setForeground(Color.red);
					tMinInput.setText(" #: ");
				}
				if (!inError) {
					tMaxInput.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}
			
		});

		tMaxInput.addActionListener((e) -> {
				graph.setMaxCounter(graph.getMaxCounter() + 1);
				closedFormGraph.setMaxCounter(closedFormGraph.getMaxCounter() + 1);
				boolean inError = false;
				try {
					tMaxInput.setForeground(Color.black);
					if ((Double.parseDouble(tMaxInput.getText().replaceAll(" #: ", "")) > graph.getTmin()) && (Double.parseDouble(tMaxInput.getText().replaceAll(" #: ", "")) >= 0)) {
						graph.setTmax(Double.parseDouble(tMaxInput.getText().replaceAll(" #: ", "")));
						rhandler.setTmax(Double.parseDouble(tMaxInput.getText().replaceAll(" #: ", "")));
						closedFormGraph.settMax(Double.parseDouble(tMaxInput.getText().replaceAll(" #: ", "")));
						tMaxInput.setText("");
					}
					else {
						tMaxInput.setForeground(Color.red);
					}
				} catch(NumberFormatException e6) {
					inError = true;
					tMaxInput.setForeground(Color.red);
					tMaxInput.setText(" #: ");
				}
				if (!inError) {
					graphBox.requestFocus();
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}	
			
		});

		graphBox.addActionListener((e) -> {
				graph.setCheckCounter(graph.getCheckCounter() + 1);
				if (graph.getMaxCounter() == 0  & graph.getMinCounter() == 0) {
					rhandler.setTmin(0);
					rhandler.setTmax(120);
					graph.setTmin(0);
					graph.setTmax(120);
				}
				if (graph.getCheckCounter() % 2 != 0) {
					try {
						if (rhandler.getGraphData() != null && graph.getGraphData() != null) {
							rhandler.getGraphData().clear();
							graph.getGraphData().clear();
						}
						rhandler.generateData();
//						System.out.println("Actual size of dataset: " + rhandler.getGraphData().size());
						graph.setGraphData(rhandler.getGraphData());
						smoother.setGraphData(graph.getGraphData());
						if (!smoother.checkSmooth()) {
							resultsTextArea.append("Smoothing...\n");
							rhandler.setStepsize(defaultStepsize/smoothingFactor);
							rhandler.getGraphData().clear();
							graph.getGraphData().clear();
							rhandler.generateData();
							graph.setGraphData(rhandler.getGraphData());
							smoother.setGraphData(graph.getGraphData());
							resultsTextArea.append("Smoothing complete.\n");	
						}
//						System.out.println("Plotting graph");
						graph.plot();
						
					} catch(NumberIsTooSmallException e2) {
						exceptionDialog.setVisible(true);
					}
					if (graph.getCheckCounter() != 1 && graph.getLastLocation() != null) {
						graph.getChartFrame().setLocation(graph.getLastLocation());
					}
				}
				else if (graph.getChartFrame() != null && graph.getCheckCounter() % 2 == 0) {
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
				if (closedFormGraph.getMaxCounter() == 0  & closedFormGraph.getMinCounter() == 0) {
					closedFormGraph.settMin(0);
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
						closedFormGraph.setStepsize(defaultStepsize/smoothingFactor);
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
				}
				else if (closedFormGraph.getChartFrame() != null && closedFormGraph.getCheckCounter() % 2 == 0) {
					closedFormGraph.setLastLocation(closedFormGraph.getChartFrame().getLocation());
					closedFormGraph.getChartFrame().dispose();
					closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
				}
		});
		
		closedFormGraph.getPcs().addPropertyChangeListener(new PropertyChangeListener() {

			@Override public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("closedFormGraphClosed")) {
					closedFormGraphBox.setSelected(false);
					closedFormGraph.setCheckCounter(closedFormGraph.getCheckCounter() + 1);
					closedFormGraph.setLastLocation(closedFormGraph.getChartFrame().getLocation());
				}
			}
			
		});
		
		dragCoeffBox.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				dragCoeffCounter++;
				if (dragCoeffCounter % 2 != 0) {
					solver.setSolvingNormal(false);
					solver.setSolvingDrag(true);
					closedFormResultsTextArea.setVisible(false);
					closedFormGraphBox.setEnabled(false);
					frame.setSize(690, 400);
					c_threeLabel.setVisible(true);
					c_threeInput.setVisible(true);
					c_fourLabel.setVisible(true);
					c_fourInput.setVisible(true);
					c_fiveLabel.setVisible(true);
					c_fiveInput.setVisible(true);
					smoother.setMaxAngle(smoother.getMaxAngle()/5);
					maxAngleInput.setText("");
					equationLabel.setText("v'(t) = "+c_zeroPrimeString+" - "+c_onePrimeString+" [v(t)] - "+c_twoPrimeString+" [v(t)]^2 - "
							+ "("+ c_threeString + " exp(-" + c_fourString + " [v(t)]) + " + c_fiveString + ")[v(t)]^2");
					c_zeroLabel.setText("c" + SUB_ZERO + "' = ");
					c_oneLabel.setText("c" + SUB_ONE + "' = ");
					c_twoLabel.setText("c" + SUB_TWO + "' = ");
				}
				if (dragCoeffCounter % 2 == 0) {
					solver.setSolvingDrag(false);
					solver.setSolvingNormal(true);
					closedFormResultsTextArea.setVisible(true);
					closedFormGraphBox.setEnabled(true);
					frame.setSize(662, 380);
					c_threeLabel.setVisible(false);
					c_threeInput.setVisible(false);
					c_fourLabel.setVisible(false);
					c_fourInput.setVisible(false);
					c_fiveLabel.setVisible(false);
					c_fiveInput.setVisible(false);
					smoother.setMaxAngle(10);
					maxAngleInput.setForeground(Color.gray);
					maxAngleInput.setText(" (Default = 10\u00b0)");
					equationLabel.setText("v'(t) = "+c_zeroString+" - "+c_oneString+" [v(t)] - "+c_twoString+" [v(t)]^2");
					c_zeroLabel.setText("c" + SUB_ZERO + " = ");
					c_oneLabel.setText("c" + SUB_ONE + " = ");
					c_twoLabel.setText("c" + SUB_TWO + " = ");
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}
				// ADD THIS TO MANUAL UPDATE PROCESS
			}
			
		});
		
		dpButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				exceptionText2.setText("You are currently using: Dormand Prince 853");
				solver.setUsingRK(false);
				solver.setUsingDP(true);
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}
			}
			
		});

		rkButton.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				exceptionText2.setText("You are currently using: Classical Runge Kutta");
				solver.setUsingDP(false);
				solver.setUsingRK(true);
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}
			}
			
		});
		
		hInput.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				try {
					hInput.setForeground(Color.black);
					solver.setH(Double.parseDouble(hInput.getText().replaceAll(" #: ", "")));
					rhandler.setH(Double.parseDouble(hInput.getText().replaceAll(" #: ", "")));
					hInput.setText("");
				} catch(NumberFormatException e7) {
					hInput.setForeground(Color.red);
					hInput.setText(" #: ");
				}
				if (autoUpdateBox.isSelected()) {
					updateGraph();
				}
			}
			
		});

		tInput.addActionListener(new ActionListener() {

			@Override public void actionPerformed(ActionEvent e) {
				try {
					tInput.setForeground(Color.BLACK);
					t = Double.parseDouble(tInput.getText().replaceAll(" Enter t \u2265 0: ", ""));
					solver.setReady(true);
					
					if (t > 0) {
						try {
							tInput.setForeground(Color.black);
							cfResult = closedFormFunction.value(t);
							result = solver.solve(t);
							if (Double.isNaN(result)) {
								resultsTextArea.append("NaN" + "\n");
								throw new NotANumberException();
							}
							resultsTextArea.append(" v("+ tInput.getText().replaceAll(" Enter t \u2265 0: ", "") +") = " + result + "\n");
							closedFormResultsTextArea.append(" v("+ tInput.getText().replaceAll(" Enter t \u2265 0: ", "") +") = " + cfResult + "\n");
							tInput.setText("");
						} catch (NumberIsTooSmallException | NotANumberException e1) {
							exceptionDialog.setVisible(true);
							closedFormExceptionDialog.setVisible(true);
							tInput.setText("");
						}
						
					}
					else if (t == 0) {
						tInput.setForeground(Color.black);
						result = 0;
						cfResult = 0;
						resultsTextArea.append(" v("+ tInput.getText().replaceAll(" Enter t \u2265 0: ", "") +") = " + result + "\n");
						closedFormResultsTextArea.append(" v(" + tInput.getText().replaceAll(" Enter t \u2265 0: ", "") +") = " + result + "\n");
						tInput.setText("");
					}
					else {
						tInput.setForeground(Color.red);
						tInput.setText(" Enter t \u2265 0: ");
					}
					
				} catch(IllegalArgumentException e9) {  //originally had numberformatexception
					tInput.setForeground(Color.red);
					tInput.setText(" Enter t \u2265 0: ");
				}
			}
			
		});
		
//		exceptionDialog.setVisible(true);  /* ---> this is for debugging */

		frame.setVisible(true);

	}
/*	
	public static void main(String[] args) {
		new RiccatiGUI();
	}
*/	
}