package ui.supplierOrdersPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/*
 * 
 * TODOLIST:Volver a Revisar que añadir/quitar productos del pedido funcione bien.
 * Logica de crear el pedido al proveedor.
 * Action listeners para cancelar y aceptar con sus JOptionPane respectivos.
 * 
 * 
 * 
 * 
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import business.serviceLayer.ProductService;
import business.serviceLayer.SupplierOrderService;
import common.Conf;
import common.Round;
import dtos.ProductDto;
import dtos.SupplierOrderedProductDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import ui.filters.FilterProducts;
import ui.filters.FiltrablePanel;
import wrappers.SupplierOrderedProductDtoWrapper;
import wrappers.SupplierProductDtoWrapper;

public class SupplierOrderCreation extends JDialog implements FiltrablePanel {

	private static final long serialVersionUID = 1L;
	private JPanel pnTitle;
	private JLabel lblCreacinDePedido;
	private JPanel pnTables;
	private JPanel pnButtons;
	private JScrollPane spProducts;
	private JScrollPane spOrder;
	private JPanel pnOptions;
	private JLabel lblUnidades;
	private JSpinner spUnits;
	private JButton btnAddUnits;
	private JButton btnDeleteUnits;
	private JLabel lblTotal;
	private JTextField txtTotal;
	private JLabel lblEuros;
	private JLabel lblProductos;
	private JLabel lblPedido;
	private JButton btnAccept;
	private JButton btnCancel;
	private JButton btnFilter;
	private CustomJTable<SupplierProductDtoWrapper> tProducts;
	private CustomJTable<SupplierOrderedProductDtoWrapper> tOrder;

	private List<ProductDto> filteredProductList;
	private List<SupplierOrderedProductDtoWrapper> orderedProductList = new ArrayList<SupplierOrderedProductDtoWrapper>();
	private List<SupplierProductDtoWrapper> supplierProducts = new ArrayList<SupplierProductDtoWrapper>();

	CustomTableModel<SupplierProductDtoWrapper> filteredProductsModel = new CustomTableModel<SupplierProductDtoWrapper>();
	CustomTableModel<SupplierProductDtoWrapper> defaultProductsModel;
	CustomTableModel<SupplierOrderedProductDtoWrapper> orderModel;
	private double total = 0.0;
	DecimalFormat df = new DecimalFormat("#.##");

	private FilterProducts filterDialog;
	private Conf parameters = Conf.getInstance("configs/parameters.properties");
	private DoubleClickOnTablelListener dcotL = new DoubleClickOnTablelListener();
	SupplierOrdersPanel supplierOrdersPanel;

	/**
	 * Create the dialog.
	 * 
	 * @param supplierOrdersPanel
	 */
	public SupplierOrderCreation(SupplierOrdersPanel supplierOrdersPanel) {
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModal(true);
		this.supplierOrdersPanel = supplierOrdersPanel;
		setSize(new Dimension(1000, 600));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("Mueblerías Pepín: Creación de pedido al proveedor");
		getContentPane().add(getPnTitle(), BorderLayout.NORTH);
		getContentPane().add(getPnTables(), BorderLayout.CENTER);
		getContentPane().add(getPnButtons(), BorderLayout.SOUTH);
		getRootPane().setDefaultButton(btnAccept);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}

		});
	}

	private class DoubleClickOnTablelListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			checkForDoubleClickOnTables(e.getComponent().getName(), e);
		}

	}

	private void checkForDoubleClickOnTables(String name, MouseEvent e) {
		if (name.equals(parameters.getProperty("ACTION_COMMAND_TBL_SUPPLIER_ORDER")) && e.getClickCount() == 2
				&& tOrder.getSelectedRow() != -1) {
			deleteUnitsFromOrder(tOrder.getSelectedRow());
		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TBL_SUPPLIER_PRODUCTS")) && e.getClickCount() == 2
				&& tProducts.getSelectedRow() != -1) {
			addUnitsToOrder(tProducts.getSelectedRow());
			;
		}

	}

	private JPanel getPnTitle() {
		if (pnTitle == null) {
			pnTitle = new JPanel();
			pnTitle.add(getLblCreacinDePedido());
		}
		return pnTitle;
	}

	private JLabel getLblCreacinDePedido() {
		if (lblCreacinDePedido == null) {
			lblCreacinDePedido = new JLabel("Creación de pedido al proveedor");
		}
		return lblCreacinDePedido;
	}

	private JPanel getPnTables() {
		if (pnTables == null) {
			pnTables = new JPanel();
			GridBagLayout gbl_pnTables = new GridBagLayout();
			gbl_pnTables.columnWidths = new int[] { 0, 0, 2, 0, 0, 0 };
			gbl_pnTables.rowHeights = new int[] { 0, 0, 105, 0, 0 };
			gbl_pnTables.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_pnTables.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			pnTables.setLayout(gbl_pnTables);
			GridBagConstraints gbc_lblProductos = new GridBagConstraints();
			gbc_lblProductos.anchor = GridBagConstraints.WEST;
			gbc_lblProductos.insets = new Insets(0, 0, 5, 5);
			gbc_lblProductos.gridx = 1;
			gbc_lblProductos.gridy = 1;
			pnTables.add(getLblProductos(), gbc_lblProductos);
			GridBagConstraints gbc_lblPedido = new GridBagConstraints();
			gbc_lblPedido.anchor = GridBagConstraints.WEST;
			gbc_lblPedido.insets = new Insets(0, 0, 5, 5);
			gbc_lblPedido.gridx = 3;
			gbc_lblPedido.gridy = 1;
			pnTables.add(getLblPedido(), gbc_lblPedido);
			GridBagConstraints gbc_spProducts = new GridBagConstraints();
			gbc_spProducts.insets = new Insets(0, 0, 5, 5);
			gbc_spProducts.fill = GridBagConstraints.BOTH;
			gbc_spProducts.gridx = 1;
			gbc_spProducts.gridy = 2;
			pnTables.add(getSpProducts(), gbc_spProducts);
			GridBagConstraints gbc_pnOptions = new GridBagConstraints();
			gbc_pnOptions.insets = new Insets(0, 0, 5, 5);
			gbc_pnOptions.fill = GridBagConstraints.BOTH;
			gbc_pnOptions.gridx = 2;
			gbc_pnOptions.gridy = 2;
			pnTables.add(getPnOptions(), gbc_pnOptions);
			GridBagConstraints gbc_spOrder = new GridBagConstraints();
			gbc_spOrder.insets = new Insets(0, 0, 5, 5);
			gbc_spOrder.fill = GridBagConstraints.BOTH;
			gbc_spOrder.gridx = 3;
			gbc_spOrder.gridy = 2;
			pnTables.add(getSpOrder(), gbc_spOrder);
		}
		return pnTables;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnButtons.getLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			pnButtons.add(getBtnCancel());
			pnButtons.add(getBtnAccept());
		}
		return pnButtons;
	}

	private JScrollPane getSpProducts() {
		if (spProducts == null) {
			spProducts = new JScrollPane();
			spProducts.setViewportView(getTProducts());
		}
		return spProducts;
	}

	private JScrollPane getSpOrder() {
		if (spOrder == null) {
			spOrder = new JScrollPane();
			spOrder.setViewportView(getTOrder());
		}
		return spOrder;
	}

	private JPanel getPnOptions() {
		if (pnOptions == null) {
			pnOptions = new JPanel();
			GridBagLayout gbl_pnOptions = new GridBagLayout();
			gbl_pnOptions.columnWidths = new int[] { 31, 0, 22, 0 };
			gbl_pnOptions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_pnOptions.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_pnOptions.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
					0.0, 1.0, 0.0, Double.MIN_VALUE };
			pnOptions.setLayout(gbl_pnOptions);
			GridBagConstraints gbc_lblUnidades = new GridBagConstraints();
			gbc_lblUnidades.insets = new Insets(0, 0, 5, 5);
			gbc_lblUnidades.anchor = GridBagConstraints.NORTH;
			gbc_lblUnidades.gridx = 1;
			gbc_lblUnidades.gridy = 1;
			pnOptions.add(getLblUnidades(), gbc_lblUnidades);
			GridBagConstraints gbc_spUnits = new GridBagConstraints();
			gbc_spUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_spUnits.insets = new Insets(0, 0, 5, 5);
			gbc_spUnits.anchor = GridBagConstraints.NORTH;
			gbc_spUnits.gridx = 1;
			gbc_spUnits.gridy = 2;
			pnOptions.add(getSpUnits(), gbc_spUnits);
			GridBagConstraints gbc_btnAddUnits = new GridBagConstraints();
			gbc_btnAddUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAddUnits.insets = new Insets(0, 0, 5, 5);
			gbc_btnAddUnits.gridx = 1;
			gbc_btnAddUnits.gridy = 4;
			pnOptions.add(getBtnAddUnits(), gbc_btnAddUnits);
			GridBagConstraints gbc_btnDeleteUnits = new GridBagConstraints();
			gbc_btnDeleteUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnDeleteUnits.insets = new Insets(0, 0, 5, 5);
			gbc_btnDeleteUnits.gridx = 1;
			gbc_btnDeleteUnits.gridy = 6;
			pnOptions.add(getBtnDeleteUnits(), gbc_btnDeleteUnits);
			GridBagConstraints gbc_btnDeleteOrder = new GridBagConstraints();
			gbc_btnDeleteOrder.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnDeleteOrder.insets = new Insets(0, 0, 5, 5);
			gbc_btnDeleteOrder.gridx = 1;
			gbc_btnDeleteOrder.gridy = 8;
			GridBagConstraints gbc_lblTotal = new GridBagConstraints();
			gbc_lblTotal.insets = new Insets(0, 0, 5, 5);
			gbc_lblTotal.gridx = 1;
			gbc_lblTotal.gridy = 11;
			pnOptions.add(getLblTotal(), gbc_lblTotal);
			GridBagConstraints gbc_txtTotal = new GridBagConstraints();
			gbc_txtTotal.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTotal.insets = new Insets(0, 0, 5, 5);
			gbc_txtTotal.gridx = 1;
			gbc_txtTotal.gridy = 12;
			pnOptions.add(getTxtTotal(), gbc_txtTotal);
			GridBagConstraints gbc_lblEuros = new GridBagConstraints();
			gbc_lblEuros.insets = new Insets(0, 0, 5, 0);
			gbc_lblEuros.anchor = GridBagConstraints.WEST;
			gbc_lblEuros.gridx = 2;
			gbc_lblEuros.gridy = 12;
			pnOptions.add(getLblEuros(), gbc_lblEuros);
			GridBagConstraints gbc_btnFilter = new GridBagConstraints();
			gbc_btnFilter.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnFilter.insets = new Insets(0, 0, 0, 5);
			gbc_btnFilter.gridx = 1;
			gbc_btnFilter.gridy = 15;
			pnOptions.add(getBtnFilter(), gbc_btnFilter);
		}
		return pnOptions;
	}

	private JLabel getLblUnidades() {
		if (lblUnidades == null) {
			lblUnidades = new JLabel("Unidades:");
			lblUnidades.setDisplayedMnemonic('U');
			lblUnidades.setLabelFor(getSpUnits());
		}
		return lblUnidades;
	}

	private JSpinner getSpUnits() {
		if (spUnits == null) {
			spUnits = new JSpinner();
			spUnits.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		}
		return spUnits;
	}

	private JButton getBtnAddUnits() {
		if (btnAddUnits == null) {
			btnAddUnits = new JButton("Agregar Unidades");
			btnAddUnits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!tProducts.getSelectionModel().isSelectionEmpty()) {
						int row = tProducts.getSelectedRow();
						addUnitsToOrder(row);
						spUnits.setValue(
							((SpinnerNumberModel) spUnits.getModel()).getMinimum());
					}
				}

			});
			btnAddUnits.setMnemonic('A');
		}
		return btnAddUnits;
	}

	private void addUnitsToOrder(int row) {
		SupplierOrderService service = ServiceFactory.createSupplierOrderService();
		SupplierProductDtoWrapper product = tProducts.getCustomModel().getValueAtRow(row);
		int units = (Integer) spUnits.getValue();
		int productId = product.getDto().id;
		orderModel.getDataVector().clear();
		if (!product.isOrdered) {
			product.getDto().supplierPrice = Round.twoCents(
				service.computeDiscountedProductPrice(productId, units));
			product.isOrdered = true;
			SupplierOrderedProductDtoWrapper orderedProduct = new SupplierOrderedProductDtoWrapper(product.getDto());
			orderedProductList.add(orderedProduct);
			orderedProduct.quantityOrdered += units;
		} else {
			for (SupplierOrderedProductDtoWrapper each : orderedProductList) {
				if (each.getDto().equals(product.getDto())) {
					int totalUnits = each.quantityOrdered + units;
					product.getDto().supplierPrice = Round.twoCents(
						service.computeDiscountedProductPrice(productId,
							totalUnits));
					each.quantityOrdered = totalUnits;
				}
			}
		}
		computeTotal();
		orderModel = new CustomTableModel<SupplierOrderedProductDtoWrapper>(
			orderedProductList);
		orderModel.fireTableDataChanged();
		tOrder.setModel(orderModel);
		tOrder.revalidate();

		btnAccept.setEnabled(true);
	}

	private JButton getBtnDeleteUnits() {
		if (btnDeleteUnits == null) {
			btnDeleteUnits = new JButton("Borrar Unidades");
			btnDeleteUnits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!tOrder.getSelectionModel().isSelectionEmpty()) {
						int row = tOrder.getSelectedRow();
						deleteUnitsFromOrder(row);
						spUnits.setValue(
							((SpinnerNumberModel) spUnits.getModel()).getMinimum());
					}
				}

			});
			btnDeleteUnits.setMnemonic('B');
		}
		return btnDeleteUnits;
	}

	protected void deleteUnitsFromOrder(int row) {
		SupplierOrderService service = ServiceFactory.createSupplierOrderService();
		SupplierOrderedProductDtoWrapper product = tOrder.getCustomModel().getValueAtRow(row);
		int units = (Integer) spUnits.getValue();
		int productId = product.getDto().id;
		orderModel.getDataVector().clear();
		if (units >= product.quantityOrdered) {

			product.getDto().supplierPrice = service.computeDiscountedProductPrice(
				productId, 0);
			product.quantityOrdered = 0;
			deleteProductFromOrder(product);
		} else {
			int totalUnits = product.quantityOrdered - units;
			product.quantityOrdered = totalUnits;

			product.getDto().supplierPrice = Round.twoCents(
				service.computeDiscountedProductPrice(productId, totalUnits));
		}
		computeTotal();
		orderModel = new CustomTableModel<SupplierOrderedProductDtoWrapper>(
			orderedProductList);
		orderModel.fireTableDataChanged();
		tOrder.setModel(orderModel);
		tOrder.revalidate();
		if (orderedProductList.isEmpty()) {
			btnAccept.setEnabled(false);
		}

	}

	private void computeTotal() {
		total = 0.0;
		for (SupplierOrderedProductDtoWrapper each : orderedProductList) {
			total += each.getDto().supplierPrice * each.quantityOrdered;
		}
		txtTotal.setText(df.format(total));

	}

	private void deleteProductFromOrder(SupplierOrderedProductDtoWrapper product) {
		product.quantityOrdered = 0;
		orderedProductList.remove(product);
		for (SupplierProductDtoWrapper each : supplierProducts) {
			if (each.getDto().equals(product.getDto()))
				each.isOrdered = false;
		}
	}

	private JLabel getLblTotal() {
		if (lblTotal == null) {
			lblTotal = new JLabel("Total:");
		}
		return lblTotal;
	}

	private JTextField getTxtTotal() {
		if (txtTotal == null) {
			txtTotal = new JTextField();
			txtTotal.setHorizontalAlignment(SwingConstants.RIGHT);
			txtTotal.setEditable(false);
			txtTotal.setText("0");
			txtTotal.setColumns(10);
		}
		return txtTotal;
	}

	private JLabel getLblEuros() {
		if (lblEuros == null) {
			lblEuros = new JLabel("€");
		}
		return lblEuros;
	}

	private JLabel getLblProductos() {
		if (lblProductos == null) {
			lblProductos = new JLabel("Productos");
			lblProductos.setLabelFor(getTProducts());
			lblProductos.setDisplayedMnemonic('P');
		}
		return lblProductos;
	}

	private JLabel getLblPedido() {
		if (lblPedido == null) {
			lblPedido = new JLabel("Pedido");
			lblPedido.setDisplayedMnemonic('D');
			lblPedido.setLabelFor(getTOrder());
		}
		return lblPedido;
	}

	private JButton getBtnAccept() {
		if (btnAccept == null) {
			btnAccept = new JButton("Realizar Pedido");
			btnAccept.setEnabled(false);
			btnAccept.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createOrderAndExit();
				}

			});
		}
		return btnAccept;
	}

	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton("Cancelar");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					closeDialog();
				}

			});
		}
		return btnCancel;
	}

	private JButton getBtnFilter() {
		if (btnFilter == null) {
			btnFilter = new JButton("Filtro de productos");
			btnFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createFilterWindow();
				}

			});
			btnFilter.setMnemonic('F');
		}
		return btnFilter;
	}

	private void createFilterWindow() {
		filterDialog = new FilterProducts(this);
		filterDialog.setLocationRelativeTo(getParent());
		filterDialog.setVisible(true);
	}

	private CustomJTable<SupplierProductDtoWrapper> getTProducts() {
		if (tProducts == null) {
			defaultProductsModel = defaultProductModel();
			tProducts = new CustomJTable<SupplierProductDtoWrapper>(defaultProductsModel);
			tProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tProducts.setName(parameters.getProperty("ACTION_COMMAND_TBL_SUPPLIER_PRODUCTS"));
			tProducts.addMouseListener(dcotL);
		}
		return tProducts;
	}

	private CustomJTable<SupplierOrderedProductDtoWrapper> getTOrder() {
		if (tOrder == null) {
			orderModel = initialOrderModel();
			tOrder = new CustomJTable<SupplierOrderedProductDtoWrapper>(orderModel);
			tOrder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tOrder.setName(parameters.getProperty("ACTION_COMMAND_TBL_SUPPLIER_ORDER"));
			tOrder.addMouseListener(dcotL);
		}
		return tOrder;
	}

	private CustomTableModel<SupplierOrderedProductDtoWrapper> initialOrderModel() {
		orderModel = new CustomTableModel<>();
		return orderModel;
	}

	private CustomTableModel<SupplierProductDtoWrapper> defaultProductModel() {
		ProductService service = ServiceFactory.createProductService();
		List<ProductDto> dtos = service.getAllProducts();

		supplierProducts = new ArrayList<SupplierProductDtoWrapper>();
		for (ProductDto dto : dtos) {
			supplierProducts.add(new SupplierProductDtoWrapper(dto));
		}

		defaultProductsModel = new CustomTableModel<SupplierProductDtoWrapper>(supplierProducts);

		return defaultProductsModel;
	}

	private void filterProductsModel() {

		supplierProducts = new ArrayList<SupplierProductDtoWrapper>();
		for (ProductDto dto : filteredProductList) {
			supplierProducts.add(new SupplierProductDtoWrapper(dto));
		}
		filteredProductsModel.getDataVector().clear();
		filteredProductsModel = new CustomTableModel<SupplierProductDtoWrapper>(supplierProducts);
		filteredProductsModel.fireTableDataChanged();
		tProducts.setModel(filteredProductsModel);
		tProducts.revalidate();
	}

	@Override
	public void setFilteredProductList(List<ProductDto> filteredProducts) {
		this.filteredProductList = filteredProducts;

		filterProductsModel();
	}

	private boolean checkUnsavedChanges() {
		if (!orderedProductList.isEmpty())
			return true;
		return false;
	}

	private void closingOnUnsavedChanges() {
		int answer = JOptionPane.showConfirmDialog(this,
				"El pedido no se ha realizado, ¿seguro que quiere salir? Esto borrara el progreso actual",
				"Cancelar pedido", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (answer == JOptionPane.YES_OPTION) {
			this.dispose();
		}
	}

	private void closeDialog() {
		boolean unsavedChanges = checkUnsavedChanges();
		if (unsavedChanges)
			closingOnUnsavedChanges();
		else {
			supplierOrdersPanel.reset();
			this.dispose();
		}
	}

	private void createOrderAndExit() {
		boolean orderIsOk = checkOrderIsOk();
		if (orderIsOk) {
			placeOrder();
			JOptionPane.showMessageDialog(this, "El pedido se ha realizado correctamente");
			supplierOrdersPanel.reset();
			this.dispose();
		}
	}

	private boolean checkOrderIsOk() {
		int answer = JOptionPane.showConfirmDialog(this, "¿Está seguro de que quiere realizar el pedido actual?",
				"Realizar pedido", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (answer == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	private void placeOrder() {
		SupplierOrderService service = ServiceFactory.createSupplierOrderService();
		List<SupplierOrderedProductDto> productsInOrder = new ArrayList<SupplierOrderedProductDto>();
		SupplierOrderedProductDto orderedProduct;
		for (SupplierOrderedProductDtoWrapper each : orderedProductList) {
			orderedProduct = new SupplierOrderedProductDto();
			orderedProduct.productId = each.getDto().id;
			orderedProduct.quantity = each.quantityOrdered;
			orderedProduct.price = each.getDto().supplierPrice;
			productsInOrder.add(orderedProduct);

		}
		service.createSupplierOrderToStock(productsInOrder);
	}

}
