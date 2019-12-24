package ui.budget;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import business.serviceLayer.BudgetService;
import dtos.BudgetDto;
import dtos.SaleDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import ui.transportation.TransportationDialog;
import wrappers.BudgetDtoWrapper;

/**
 * Panel containing the information related to the pending budgets. It allows
 * the user to view all the information related to any unaccepted budget that
 * has client assign and hasn't expired yet.
 * 
 * It also allows him to select a budget and accept it, in order to create a new
 * sale, which will start the process of sale creation.
 * 
 * @author Daniel Adrian Mare
 *
 */
public class ShowPendingBudgetsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel pnTitle;
	private JPanel pnBudgetView;
	private JPanel pnSubtitle;
	private JPanel pnButtons;
	private JScrollPane spBudgetTable;
	private JLabel lblBudgets;
	private JLabel lblPresupuestosVlidosSin;
	private JButton btnAccept;
	private JPanel pnBudgets;
	private CustomJTable<BudgetDtoWrapper> tablePendingBudgets;
	CustomTableModel<BudgetDtoWrapper> budgetTableModel;

	TransportationDialog transportDialog = null;
	private List<BudgetDto> dtoList;
	private BudgetService budgetService;

	public ShowPendingBudgetsPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getPnTitle(), BorderLayout.NORTH);
		add(getPnPendingBudgets(), BorderLayout.CENTER);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				reloadPendingBudgetsTable();
			}
		});

	}

	private JPanel getPnTitle() {
		if (pnTitle == null) {
			pnTitle = new JPanel();
			pnTitle.add(getLblBudgets());
		}
		return pnTitle;
	}

	private JPanel getPnPendingBudgetsView() {
		if (pnBudgetView == null) {
			pnBudgetView = new JPanel();
			pnBudgetView.setLayout(new BorderLayout(0, 0));
			pnBudgetView.add(getPnSubtitle(), BorderLayout.NORTH);
			pnBudgetView.add(getSpBudgetList(), BorderLayout.CENTER);
		}
		return pnBudgetView;
	}

	private JPanel getPnSubtitle() {
		if (pnSubtitle == null) {
			pnSubtitle = new JPanel();
			pnSubtitle.add(getLblPendingBudgets());
		}
		return pnSubtitle;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.setLayout(new GridLayout(0, 1, 0, 0));
			pnButtons.add(getBtnAccept());
		}
		return pnButtons;
	}

	private JScrollPane getSpBudgetList() {
		if (spBudgetTable == null) {
			spBudgetTable = new JScrollPane();
			spBudgetTable.setViewportView(getTablePendingBudgets());
		}
		return spBudgetTable;
	}

	private JLabel getLblBudgets() {
		if (lblBudgets == null) {
			lblBudgets = new JLabel("Presupuestos");
		}
		return lblBudgets;
	}

	private JLabel getLblPendingBudgets() {
		if (lblPresupuestosVlidosSin == null) {
			lblPresupuestosVlidosSin = new JLabel("Presupuestos pendientes");
		}
		return lblPresupuestosVlidosSin;
	}

	private JButton getBtnAccept() {
		if (btnAccept == null) {
			btnAccept = new JButton("Aceptar");
			btnAccept.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!tablePendingBudgets.getSelectionModel().isSelectionEmpty()) {
						int selectedRow = tablePendingBudgets.getSelectedRow();
						BudgetDto selectedDto = getSelectedBudget(selectedRow);
						SaleDto saleDto = new SaleDto();
						saleDto.budgetId = selectedDto.id;
						createSaleWindow(saleDto);
					}
				}

			});
		}
		return btnAccept;
	}

	private BudgetDto getSelectedBudget(int selectedRow) {
		BudgetDto dto = new BudgetDto();
		dto.id = Integer.parseInt((String) tablePendingBudgets.getValueAt(selectedRow, 0));
		dto.creationDate = (String) tablePendingBudgets.getValueAt(selectedRow, 1);
		dto.expirationDate = (String) tablePendingBudgets.getValueAt(selectedRow, 2);
		dto.clientName = (String) tablePendingBudgets.getValueAt(selectedRow, 3);
		return dto;
	}

	private void createSaleWindow(SaleDto saleDto) {
		transportDialog = new TransportationDialog(saleDto, this);
		transportDialog.setModal(true);
		transportDialog.setLocationRelativeTo(this);
		transportDialog.setVisible(true);
	}

	private JPanel getPnPendingBudgets() {
		if (pnBudgets == null) {
			pnBudgets = new JPanel();
			pnBudgets.setLayout(new BorderLayout(0, 0));
			pnBudgets.add(getPnPendingBudgetsView(), BorderLayout.CENTER);
			pnBudgets.add(getPnButtons(), BorderLayout.EAST);
		}
		return pnBudgets;
	}

	private CustomJTable<BudgetDtoWrapper> getTablePendingBudgets() {
		if (tablePendingBudgets == null) {
			budgetTableModel = getPendingBudgetsTableModel();
			tablePendingBudgets = new CustomJTable<BudgetDtoWrapper>(budgetTableModel);
		}
		return tablePendingBudgets;
	}

	private CustomTableModel<BudgetDtoWrapper> getPendingBudgetsTableModel() {
		budgetService = ServiceFactory.createBudgetService();
		dtoList = budgetService.listPendingBudgets();

		List<BudgetDtoWrapper> wrappedDtos = new ArrayList<BudgetDtoWrapper>();
		for (BudgetDto dto : dtoList) {
			wrappedDtos.add(new BudgetDtoWrapper(dto));
		}

		CustomTableModel<BudgetDtoWrapper> model = new CustomTableModel<BudgetDtoWrapper>(wrappedDtos);

		return model;
	}

	public void reloadPendingBudgetsTable() {
		budgetTableModel.getDataVector().clear();
		budgetTableModel = getPendingBudgetsTableModel();
		budgetTableModel.fireTableDataChanged();
		tablePendingBudgets.setModel(budgetTableModel);

		tablePendingBudgets.revalidate();
	}

}
