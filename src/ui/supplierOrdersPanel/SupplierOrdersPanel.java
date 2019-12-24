package ui.supplierOrdersPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import business.serviceLayer.SupplierOrderService;
import common.BusinessException;
import dtos.SupplierOrderDto;
import factories.ServiceFactory;
import ui.extraClasses.NotEditableTableModel;
import wrappers.ProductDtoWrapper;

public class SupplierOrdersPanel extends JPanel {

	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = -6537452185281962654L;

	// Fields for the components of the panel
	// ------------------------------------------------------------------------

	private JSplitPane spltPnSupplierOrders;
	private JPanel pnButtons;
	private JButton btnCreateNewOrder;
	private JPanel pnLeft;
	private JPanel pnRight;
	private JScrollPane spSupplierOrdersHistory;
	private JScrollPane spSupplierOrderContents;
	private JTable tSupplierOrdersHistory;
	private JTable tSupplierOrderContents;
	private JPanel pnSupplierOrderContents;
	private JButton btnMarkAsReceived;
	private JPanel pnSupplierOrderButtons;
	private JPanel pnSummaryAndReceive;
	private JLabel lblTotalOrder;
	private JTextField txtTotalOrder;
	private JPanel panel;
	private JLabel lblNewLabel;

	// Fields
	// ------------------------------------------------------------------------

	private NotEditableTableModel supplierOrdersHistoryModel, supplierOrderProductsModel;
	private List<ProductDtoWrapper> productsInCurrentOrder;
	private SupplierOrderCreation orderCreationWindow = null;
	private List<SupplierOrderDto> supplierOrders;

	// Constructor for the panel
	// ------------------------------------------------------------------------

	public SupplierOrdersPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getPanel(), BorderLayout.NORTH);
		add(getSpltPnSupplierOrders(), BorderLayout.CENTER);

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				populateTableModelWithSupplierOrders();
				supplierOrderProductsModel.getDataVector().clear();
				tSupplierOrderContents.revalidate();
			}
		});

		populateTableModelWithSupplierOrders();

		productsInCurrentOrder = new ArrayList<ProductDtoWrapper>();
		supplierOrders = new ArrayList<SupplierOrderDto>();

		supplierOrderProductsModel = new NotEditableTableModel(null, 0);
		supplierOrdersHistoryModel = new NotEditableTableModel(null, 0);
	}

	// Getters for the components of the panel
	// ------------------------------------------------------------------------

	private JSplitPane getSpltPnSupplierOrders() {
		if (spltPnSupplierOrders == null) {
			spltPnSupplierOrders = new JSplitPane();
			spltPnSupplierOrders.setLeftComponent(getPnLeft());
			spltPnSupplierOrders.setRightComponent(getPnRight());
			spltPnSupplierOrders.setResizeWeight(0.5d);
		}
		return spltPnSupplierOrders;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pnButtons.add(getBtnCreateNewOrder());
		}
		return pnButtons;
	}

	private JButton getBtnCreateNewOrder() {
		if (btnCreateNewOrder == null) {
			btnCreateNewOrder = new JButton("Crear nuevo pedido");
			btnCreateNewOrder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createNewSupplierOrder();
				}
			});
		}
		return btnCreateNewOrder;
	}

	private JPanel getPnLeft() {
		if (pnLeft == null) {
			pnLeft = new JPanel();
			pnLeft.setBorder(new TitledBorder(null, "Historial de pedidos al proveedor", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			pnLeft.setLayout(new BorderLayout(0, 0));
			pnLeft.add(getSpSupplierOrdersHistory(), BorderLayout.CENTER);
		}
		return pnLeft;
	}

	private JPanel getPnRight() {
		if (pnRight == null) {
			pnRight = new JPanel();
			pnRight.setLayout(new BorderLayout(0, 0));
			pnRight.add(getPnSupplierOrderContents());
			pnRight.add(getPnSupplierOrderButtons(), BorderLayout.SOUTH);
		}
		return pnRight;
	}

	private JScrollPane getSpSupplierOrdersHistory() {
		if (spSupplierOrdersHistory == null) {
			spSupplierOrdersHistory = new JScrollPane();
			spSupplierOrdersHistory.setViewportView(getTSupplierOrdersHistory());
		}
		return spSupplierOrdersHistory;
	}

	private JScrollPane getSpSupplierOrderContents() {
		if (spSupplierOrderContents == null) {
			spSupplierOrderContents = new JScrollPane();
			spSupplierOrderContents.setViewportView(getTSupplierOrderContents());
		}
		return spSupplierOrderContents;
	}

	private JTable getTSupplierOrdersHistory() {
		if (tSupplierOrdersHistory == null) {
			tSupplierOrdersHistory = new JTable();
			tSupplierOrdersHistory.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					int selectedRow = tSupplierOrdersHistory.getSelectedRow();
					if (selectedRow >= 0) {
						btnMarkAsReceived.setEnabled(false);
						populateSupplierOrderProductsTable(selectedRow);
					}
				}
			});
			tSupplierOrdersHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tSupplierOrdersHistory.getTableHeader().setReorderingAllowed(false);
		}
		return tSupplierOrdersHistory;
	}

	private JTable getTSupplierOrderContents() {
		if (tSupplierOrderContents == null) {
			tSupplierOrderContents = new JTable();
			tSupplierOrderContents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tSupplierOrderContents.getTableHeader().setReorderingAllowed(false);
		}
		return tSupplierOrderContents;
	}

	private JPanel getPnSupplierOrderContents() {
		if (pnSupplierOrderContents == null) {
			pnSupplierOrderContents = new JPanel();
			pnSupplierOrderContents.setBorder(new TitledBorder(null, "Productos en el pedido", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			pnSupplierOrderContents.setLayout(new BorderLayout(0, 0));
			pnSupplierOrderContents.add(getSpSupplierOrderContents(), BorderLayout.CENTER);
		}
		return pnSupplierOrderContents;
	}

	private JButton getBtnMarkAsReceived() {
		if (btnMarkAsReceived == null) {
			btnMarkAsReceived = new JButton("Marcar como recibido");
			btnMarkAsReceived.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selectedRow = tSupplierOrdersHistory.getSelectedRow();
					if (selectedRow >= 0) {
						tryToMarkOrderAsReceived(selectedRow);
					}
				}
			});
			btnMarkAsReceived.setEnabled(false);
		}
		return btnMarkAsReceived;
	}

	private JPanel getPnSupplierOrderButtons() {
		if (pnSupplierOrderButtons == null) {
			pnSupplierOrderButtons = new JPanel();
			pnSupplierOrderButtons.setLayout(new GridLayout(0, 1, 0, 0));
			pnSupplierOrderButtons.add(getPnSummaryAndReceive());
		}
		return pnSupplierOrderButtons;
	}

	private JPanel getPnSummaryAndReceive() {
		if (pnSummaryAndReceive == null) {
			pnSummaryAndReceive = new JPanel();
			FlowLayout fl_pnSummaryAndReceive = (FlowLayout) pnSummaryAndReceive.getLayout();
			fl_pnSummaryAndReceive.setAlignment(FlowLayout.RIGHT);
			pnSummaryAndReceive.add(getLblTotalOrder());
			pnSummaryAndReceive.add(getTxtTotalOrder());
			pnSummaryAndReceive.add(getBtnMarkAsReceived());
		}
		return pnSummaryAndReceive;
	}

	private JLabel getLblTotalOrder() {
		if (lblTotalOrder == null) {
			lblTotalOrder = new JLabel("Precio total del pedido: ");
			lblTotalOrder.setHorizontalAlignment(SwingConstants.LEFT);
		}
		return lblTotalOrder;
	}

	private JTextField getTxtTotalOrder() {
		if (txtTotalOrder == null) {
			txtTotalOrder = new JTextField();
			txtTotalOrder.setEditable(false);
			txtTotalOrder.setHorizontalAlignment(SwingConstants.CENTER);
			txtTotalOrder.setColumns(10);
		}
		return txtTotalOrder;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			panel.add(getLblNewLabel());
			panel.add(getPnButtons());
		}
		return panel;
	}

	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Historial de pedidos al proveedor");
			lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblNewLabel;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public void reset() {
		txtTotalOrder.setText("");
		supplierOrderProductsModel.getDataVector().clear();
		supplierOrdersHistoryModel.getDataVector().clear();
		populateTableModelWithSupplierOrders();
		tSupplierOrderContents.revalidate();
		tSupplierOrdersHistory.revalidate();
		btnMarkAsReceived.setEnabled(false);
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private void populateSupplierOrderProductsTable(int selectedRow) {
		productsInCurrentOrder = new ArrayList<ProductDtoWrapper>();
		String[] tableColumnNames = null;
		boolean isOrderRequested = false;

		try {
			productsInCurrentOrder = findProductsOfSelectedSupplierOrder(selectedRow);
			tableColumnNames = findProductsColumnNames();
			isOrderRequested = checkOrderIsRequested(selectedRow);
		} catch (BusinessException e) {
			JOptionPane.showMessageDialog(this, "Parece que algo salió mal conectando con nuestra base de datos."
					+ "\nLo sentimos. Vuelva a intentarlo más tarde.");
		}

		List<String> extendedColumnNames = new ArrayList<String>();
		for (String each : tableColumnNames) {
			extendedColumnNames.add(each);
		}
		extendedColumnNames.set(1, "PRECIO");
		extendedColumnNames.add("CANTIDAD SOLICITADA");

		supplierOrderProductsModel = new NotEditableTableModel(extendedColumnNames.toArray(), 0);

		Vector<String> row;
		for (ProductDtoWrapper each : productsInCurrentOrder) {
			row = new Vector<String>();
			row.add(each.getDto().name);
			row.add(String.valueOf(each.getDto().supplierPrice));
			row.add(each.getDto().category);
			row.add(String.valueOf(each.quantityOrdered));
			supplierOrderProductsModel.addRow(row);
		}

		tSupplierOrderContents.setModel(supplierOrderProductsModel);
		tSupplierOrderContents.revalidate();
		computeOrderTotalToTxtField();

		if (isOrderRequested) {
			btnMarkAsReceived.setEnabled(true);
		}
	}

	private boolean checkOrderIsRequested(int selectedRow) throws BusinessException {
		SupplierOrderDto dto = ServiceFactory.createSupplierOrderService()
				.findSupplierOrderById(Integer.parseInt(getSelectedOrderId(selectedRow)));
		return dto.status.equalsIgnoreCase("solicitado");
	}

	private String[] findProductsColumnNames() throws BusinessException {
		return ServiceFactory.createProductService().getColumnNames();
	}

	private List<ProductDtoWrapper> findProductsOfSelectedSupplierOrder(int selectedRow) throws BusinessException {
		return ServiceFactory.createProductService()
				.findProductsOnSupplierOrder(Integer.parseInt(getSelectedOrderId(selectedRow)));
	}

	private void computeOrderTotalToTxtField() {
		float totalAccumulated = 0;

		for (ProductDtoWrapper each : productsInCurrentOrder) {
			totalAccumulated += each.quantityOrdered * each.getDto().supplierPrice;
		}

		totalAccumulated = (totalAccumulated * 100f) / 100f;
		txtTotalOrder.setText(totalAccumulated + " €");
	}

	private String getSelectedOrderId(int selectedRow) {
		return String.valueOf(tSupplierOrdersHistory.getModel().getValueAt(selectedRow, 0));
	}

	private void populateTableModelWithSupplierOrders() {
		supplierOrders = new ArrayList<SupplierOrderDto>();

		try {
			supplierOrders = ServiceFactory.createSupplierOrderService().findAllSupplierOrders();
		} catch (BusinessException e) {
			JOptionPane.showMessageDialog(this, "Parece que algo salió mal conectando con nuestra base de datos."
					+ "\nLo sentimos. Vuelva a intentarlo más tarde.");
		}

		String[] columnNames = new String[] { "Identificador del pedido", "Estado del pedido",
				"Id de la venta relacionada", "Fecha del pedido" };
		supplierOrdersHistoryModel = new NotEditableTableModel(columnNames, 0);

		Vector<String> data;
		for (SupplierOrderDto each : supplierOrders) {
			data = new Vector<String>();
			data.add(String.valueOf(each.id));
			data.add(each.status);
			data.add(each.saleId == -1 ? "No hay venta relacionada" : String.valueOf(each.saleId));
			data.add(new SimpleDateFormat("dd/mm/YYYY").format(each.date));
			supplierOrdersHistoryModel.addRow(data);
		}

		tSupplierOrdersHistory.setModel(supplierOrdersHistoryModel);
		tSupplierOrdersHistory.revalidate();
	}

	private void tryToMarkOrderAsReceived(int selectedRow) {
		int answer = JOptionPane.showConfirmDialog(this, "Confirmar cambio de estado del pedido a Recibido",
				"Confirmar recepción del pedido", JOptionPane.YES_NO_CANCEL_OPTION);
		if (answer == JOptionPane.YES_OPTION) {
			int orderId = Integer.parseInt(getSelectedOrderId(selectedRow));

			try {
				ServiceFactory.createSupplierOrderService().markSupplierOrderAsReceived(orderId);
				if (!isSaleAssigned()) {
					ServiceFactory.createProductService()
							.addProductsToStorehouse(new ArrayList<ProductDtoWrapper>(productsInCurrentOrder));
				}
			} catch (BusinessException e) {
				JOptionPane.showMessageDialog(this,
						"Ese id no está asociado con ningún producto en nuestra base de datos.\nPrueba con otro e inténtalo de nuevo",
						"Error: Id no encontrado", JOptionPane.ERROR_MESSAGE);
			}

			populateTableModelWithSupplierOrders();
			btnMarkAsReceived.setEnabled(false);

			checkIfOrderForASale(orderId);
		}
	}

	private boolean isSaleAssigned() {
		return supplierOrders.get(tSupplierOrdersHistory.getSelectedRow()).saleId != -1;
	}

	private void checkIfOrderForASale(int orderId) {
		SupplierOrderService supplierService = ServiceFactory.createSupplierOrderService();
		int saleId = supplierService.assignedSaleFinder(orderId);
		if (saleId != -1) {
			ServiceFactory.createTransportService().unblockSaleTransportation(saleId);
		}

	}

	private void createNewSupplierOrder() {
		orderCreationWindow = new SupplierOrderCreation(this);
		orderCreationWindow.setModal(false);
		orderCreationWindow.setLocationRelativeTo(this);
		orderCreationWindow.setVisible(true);
	}

}
