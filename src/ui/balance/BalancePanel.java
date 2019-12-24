package ui.balance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import business.balance.Dataset;
import common.Dates;

public class BalancePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pnYear;
	private JLabel lblYear;
	private JPanel pnGraph;
	private JPanel pnOpciones;
	private JLabel lblYearSpinner;
	private JSpinner spinner;
	private JCheckBox chckbxBalance;
	private JCheckBox chckbxGastos;
	private JCheckBox chckbxGanancias;

	private int year;
	private DefaultCategoryDataset dataset;
	private Dataset dataLogic;

	public BalancePanel() {
		year = Integer.parseInt(Dates.toString(new Date()).split("/")[2]);
		dataset = new DefaultCategoryDataset();
		dataLogic = new Dataset();

		setLayout(new BorderLayout(0, 0));
		add(getPnYear(), BorderLayout.NORTH);
		add(getPnGraph(), BorderLayout.CENTER);
		add(getPnOpciones(), BorderLayout.SOUTH);

		paint();
	}

	private JPanel getPnYear() {
		if (pnYear == null) {
			pnYear = new JPanel();
			pnYear.add(getLblYear());
		}
		return pnYear;
	}

	private JLabel getLblYear() {
		if (lblYear == null) {
			lblYear = new JLabel(String.valueOf(year));
			lblYear.setFont(new Font("Tahoma", Font.PLAIN, 24));
		}
		return lblYear;
	}

	private JPanel getPnGraph() {
		if (pnGraph == null) {
			pnGraph = new JPanel();
			pnGraph.setLayout(new BorderLayout(0, 0));
		}
		return pnGraph;
	}

	private JPanel getPnOpciones() {
		if (pnOpciones == null) {
			pnOpciones = new JPanel();
			pnOpciones.add(getLblYearSpinner());
			pnOpciones.add(getSpinner());
			pnOpciones.add(getChckbxBalance());
			pnOpciones.add(getChckbxGastos());
			pnOpciones.add(getChckbxGanancias());
		}
		return pnOpciones;
	}

	private JLabel getLblYearSpinner() {
		if (lblYearSpinner == null) {
			lblYearSpinner = new JLabel("Año:");
			lblYearSpinner.setDisplayedMnemonic('a');
			lblYearSpinner.setLabelFor(getSpinner());
		}
		return lblYearSpinner;
	}

	private JSpinner getSpinner() {
		if (spinner == null) {
			spinner = new JSpinner();
			spinner.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					year = (int) spinner.getValue();
					lblYear.setText(String.valueOf(year));
					paint();
				}
			});
			spinner.setModel(new SpinnerNumberModel(year, 1900, 2100, 1));
		}
		return spinner;
	}

	private JCheckBox getChckbxBalance() {
		if (chckbxBalance == null) {
			chckbxBalance = new JCheckBox("Balance");
			chckbxBalance.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paint();
				}
			});
			chckbxBalance.setSelected(true);
			chckbxBalance.setMnemonic('b');
		}
		return chckbxBalance;
	}

	private JCheckBox getChckbxGastos() {
		if (chckbxGastos == null) {
			chckbxGastos = new JCheckBox("Gastos");
			chckbxGastos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paint();
				}
			});
			chckbxGastos.setMnemonic('s');
		}
		return chckbxGastos;
	}

	private JCheckBox getChckbxGanancias() {
		if (chckbxGanancias == null) {
			chckbxGanancias = new JCheckBox("Ganancias");
			chckbxGanancias.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paint();
				}
			});
		}
		return chckbxGanancias;
	}

	public void paint() {
		dataset = new DefaultCategoryDataset();

		if (chckbxBalance.isSelected())
			paintBalance();
		if (chckbxGanancias.isSelected())
			paintEarn();
		if (chckbxGastos.isSelected())
			paintSpend();

		JFreeChart chart = ChartFactory.createLineChart("", "meses", "euros", dataset);
		CategoryPlot catPlot = chart.getCategoryPlot();
		catPlot.setRangeGridlinePaint(Color.BLACK);
		catPlot.setBackgroundPaint(Color.WHITE);
		ChartPanel pnChart = new ChartPanel(chart);

		pnGraph.removeAll();
		pnGraph.add(pnChart);
		pnGraph.validate();
	}

	private void paintBalance() {
		dataset = dataLogic.addBalance(dataset, String.valueOf(year));
	}

	private void paintEarn() {
		dataset = dataLogic.addEarn(dataset, String.valueOf(year));
	}

	private void paintSpend() {
		dataset = dataLogic.addSpend(dataset, String.valueOf(year));
	}
}
