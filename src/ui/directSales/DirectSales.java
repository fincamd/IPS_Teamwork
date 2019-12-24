package ui.directSales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import business.serviceLayer.BudgetService;
import business.serviceLayer.PaymentMethodsService;
import business.serviceLayer.ProductService;
import business.serviceLayer.SaleService;
import business.serviceLayer.SupplierOrderService;
import business.serviceLayer.implementation.SaleServiceImplementation;
import common.BusinessException;
import common.Conf;
import common.Dates;
import common.Round;
import dtos.CashPaymentDto;
import dtos.CreditCardPaymentDto;
import dtos.ProductDto;
import dtos.ProductSaleDto;
import dtos.SaleDto;
import dtos.SupplierOrderedProductDto;
import dtos.TransferPaymentDto;
import dtos.TransportDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import ui.filters.FilterProducts;
import ui.filters.FiltrablePanel;
import ui.transportation.TransportationDialog;
import wrappers.DirectSaleProductDtoWrapper;
import wrappers.StockProductDtoWrapper;
import javax.swing.SpinnerNumberModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DirectSales extends JPanel implements FiltrablePanel {

//--Fields----------------------------------------------------------------------
	private Conf parameters = Conf.getInstance("configs/parameters.properties");
	private final static int CREDIT_CARD_DURATION_IN_YEARS = 10;
	private double MAX_CASH_PAYMENT = Double.parseDouble(
		parameters.getProperty("MAX_PRICE_FOR_CASH"));
	private String DEFAULT_SHOP_ACCOUNT=parameters.getProperty("DEFAULT_ACCOUNT_NUMBER");
	private String DEFAULT_BENEFICIARY=parameters.getProperty("DEFAULT_BENEFICIARY");
	

	CustomTableModel<StockProductDtoWrapper> filteredProductsModel = new CustomTableModel<StockProductDtoWrapper>();
	CustomTableModel<StockProductDtoWrapper> defaultProductsModel;
	CustomTableModel<DirectSaleProductDtoWrapper> saleModel;
	private DefaultComboBoxModel<Integer> dueDateYearModel;
	private DefaultComboBoxModel<Integer> dueDateMonthModel;

	private List<ProductDto> filteredProductList;
	private List<StockProductDtoWrapper> fullStockProducts = new ArrayList<StockProductDtoWrapper>();
	private List<StockProductDtoWrapper> stockProducts = new ArrayList<StockProductDtoWrapper>();
	private List<DirectSaleProductDtoWrapper> orderedProducts = new ArrayList<DirectSaleProductDtoWrapper>();
	private List<SupplierOrderedProductDto> missingProducts;
	List<ProductSaleDto> fullOrder;

	private FilterProducts filterDialog;

	private DoubleClickOnTablelListener dcotL = new DoubleClickOnTablelListener();

	private double total = 0.0;
	DecimalFormat df = new DecimalFormat("#.##");

	TransportationDialog transport;

//--Constructor-----------------------------------------------------------------

	public DirectSales() {
		setLayout(new BorderLayout(0, 0));
		add(getPnTitle(), BorderLayout.NORTH);
		add(getPnButtons(), BorderLayout.SOUTH);
		add(getPnMain());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				reloadWindow();

			}

		});

	}
//--Listeners-------------------------------------------------------------------

	private class DoubleClickOnTablelListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			checkForDoubleClickOnTables(e.getComponent().getName(), e);
		}

	}

	private void checkForDoubleClickOnTables(String name, MouseEvent e) {
		if (name.equals(parameters.getProperty("ACTION_COMMAND_TBL_SALE"))
			&& e.getClickCount() == 2 && tSale.getSelectedRow() != -1) {
			deleteUnitsFromOrder(tSale.getSelectedRow());
		} else if (name.equals(
			parameters.getProperty("ACTION_COMMAND_TBL_STOCK_PRODUCTS"))
			&& e.getClickCount() == 2 && tProducts.getSelectedRow() != -1) {
			addUnitsToSale(tProducts.getSelectedRow());;
		}

	}

	public void reloadWindow() {
		// TPRODUCTS
		defaultProductsModel.getDataVector().clear();
		defaultProductsModel = defaultProductModel();
		defaultProductsModel.fireTableDataChanged();
		tProducts.setModel(defaultProductsModel);
		tProducts.revalidate();

		// TSALE
		saleModel.getDataVector().clear();
		saleModel = initialSaleModel();
		saleModel.fireTableDataChanged();
		tSale.setModel(saleModel);
		tSale.revalidate();
		orderedProducts = new ArrayList<DirectSaleProductDtoWrapper>();

		// TOTAL
		computeTotal();

		// PAYMENTMETHODS
		rbtnCash.setSelected(true);
		restoreInitialStateCashPanel();
		restoreInitialStateCreditCardPanel();
		restoreInitialStateTransferPanel();
		setPanelEnabled(pnPaymentMethods, false);
		txtTransferDestinedAccount.setText(DEFAULT_SHOP_ACCOUNT);
		txtTransferBeneficiary.setText(DEFAULT_BENEFICIARY);

		// CONFIRM BUTTON
		btnConfirmPayment.setEnabled(false);
	}

	private void restoreInitialStateTransferPanel() {
		txtTransferBeneficiary.setText("");
		txtTransferChargedAccount.setText("");
		txtTransferConcept.setText("");
		txtTransferDestinedAccount.setText("");
		txtTransferQuantity.setText("");
		taTransferNotations.setText("");

	}

	private void restoreInitialStateCreditCardPanel() {
		txtCreditCardCVV.setText("");
		txtCreditCardName.setText("");
		txtCreditCardNumber.setText("");
		if (dueDateMonthModel != null & dueDateYearModel != null) {
			cbCreditCardDueDateMonth.setSelectedIndex(0);
			cbCreditCardDueDateYear.setSelectedIndex(0);
		}

	}

	private void restoreInitialStateCashPanel() {
		txtCashChange.setText("");
		txtCashPaid.setText("");
	}

//--Methods---------------------------------------------------------------------

	/**
	 * Creates the Dialog for filtering products
	 */
	private void createFilterWindow() {
		filterDialog = new FilterProducts(this);
		filterDialog.setLocationRelativeTo(getParent());
		filterDialog.setVisible(true);
	}

	/**
	 * After filtering, substitutes the products in the product table for the
	 * filtered ones
	 */
	@Override
	public void setFilteredProductList(List<ProductDto> filteredProducts) {
		this.filteredProductList = filteredProducts;
		filterProductsModel();
	}

	private void filterProductsModel() {
		stockProducts = new ArrayList<StockProductDtoWrapper>();
		for (ProductDto dto : filteredProductList) {
			for (StockProductDtoWrapper each : fullStockProducts) {
				if (dto.id == each.getDto().id)
					stockProducts.add(each);
			}
		}
		filteredProductsModel.getDataVector().clear();
		filteredProductsModel = new CustomTableModel<StockProductDtoWrapper>(
			stockProducts);
		filteredProductsModel.fireTableDataChanged();
		tProducts.setModel(filteredProductsModel);
		tProducts.revalidate();
	}

	private void addUnitsToSale(int row) {
		StockProductDtoWrapper product = tProducts	.getCustomModel()
													.getValueAtRow(row);
		int units = (Integer) spUnits.getValue();
		product.remainingStock -= units;
		if (product.remainingStock < 0)
			product.remainingStock = 0;

		saleModel.getDataVector().clear();
		if (!product.isOrdered) {
			product.isOrdered = true;
			DirectSaleProductDtoWrapper orderedProduct = new DirectSaleProductDtoWrapper(
				product.getDto());
			orderedProducts.add(orderedProduct);
			orderedProduct.quantityOrdered += units;
		} else {
			for (DirectSaleProductDtoWrapper each : orderedProducts) {
				if (each.getDto().equals(product.getDto())) {
					each.quantityOrdered += units;

				}
			}
		}
		computeTotal();
		saleModel = new CustomTableModel<DirectSaleProductDtoWrapper>(
			orderedProducts);
		saleModel.fireTableDataChanged();
		tSale.setModel(saleModel);
		tSale.revalidate();

		defaultProductsModel = new CustomTableModel<StockProductDtoWrapper>(
			stockProducts);
		defaultProductsModel.fireTableDataChanged();
		tProducts.setModel(defaultProductsModel);
		tProducts.revalidate();

		enablePayments();
		btnConfirmPayment.setEnabled(true);
	}

	private void enablePayments() {
		if (total <= MAX_CASH_PAYMENT) {
			setPanelEnabled(pnCash, true);
		} else
			setPanelEnabled(pnCash, false);
		setPanelEnabled(pnCreditCardButton, true);
		setPanelEnabled(pnTransferButton, true);

	}

	protected void deleteUnitsFromOrder(int row) {
		DirectSaleProductDtoWrapper product = tSale	.getCustomModel()
													.getValueAtRow(row);
		int units = (Integer) spUnits.getValue();
		for (StockProductDtoWrapper each : fullStockProducts) {
			if (each.getDto().id == product.getDto().id) {
				each.remainingStock += units;
				if (each.remainingStock > each.getDto().stock)
					each.remainingStock = each.getDto().stock;
				break;
			}
		}
		saleModel.getDataVector().clear();
		if (units >= product.quantityOrdered) {
			product.quantityOrdered = 0;
			deleteProductFromOrder(product);
		} else {
			product.quantityOrdered -= units;
		}
		computeTotal();
		saleModel = new CustomTableModel<DirectSaleProductDtoWrapper>(
			orderedProducts);
		saleModel.fireTableDataChanged();
		tSale.setModel(saleModel);
		tSale.revalidate();
		if (orderedProducts.isEmpty()) {
			setPanelEnabled(pnPaymentMethods, false);
			btnConfirmPayment.setEnabled(false);
		} else {
			enablePayments();
		}
	}

	private void deleteProductFromOrder(DirectSaleProductDtoWrapper product) {
		product.quantityOrdered = 0;
		orderedProducts.remove(product);
		for (StockProductDtoWrapper each : stockProducts) {
			if (each.getDto().equals(product.getDto()))
				each.isOrdered = false;
		}
	}

	private void computeTotal() {
		total = 0.0;
		for (DirectSaleProductDtoWrapper each : orderedProducts) {
			total += each.getDto().publicPrice * each.quantityOrdered;
		}
		txtTotal.setText(df.format(total));
		if (total <= MAX_CASH_PAYMENT) {
			txtCashPaid.setText(df.format(total).toString());
			calculateChange();
		}
		total = Round.twoCents(total);
		txtTransferQuantity.setText(df.format(total).toString());
	}

	private CustomTableModel<DirectSaleProductDtoWrapper> initialSaleModel() {
		saleModel = new CustomTableModel<>();
		return saleModel;
	}

	private CustomTableModel<StockProductDtoWrapper> defaultProductModel() {
		ProductService service = ServiceFactory.createProductService();
		List<ProductDto> dtos = service.getAllProducts();

		stockProducts = new ArrayList<StockProductDtoWrapper>();
		for (ProductDto dto : dtos) {
			StockProductDtoWrapper wrapper = new StockProductDtoWrapper(dto);
			stockProducts.add(wrapper);
			fullStockProducts.add(wrapper);
		}

		defaultProductsModel = new CustomTableModel<StockProductDtoWrapper>(
			stockProducts);

		return defaultProductsModel;
	}

	private void setPanelEnabled(JPanel panel, Boolean isEnabled) {
		panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();

		for (Component component : components) {
			if (component instanceof JPanel) {
				setPanelEnabled((JPanel) component, isEnabled);
			}
			component.setEnabled(isEnabled);
		}
	}

	public void confirmSale(TransportDto transportDto) {
		SaleServiceImplementation s = (SaleServiceImplementation) ServiceFactory.createSaleService();
		int paymentMeanId = createPaymentMean();
		SaleDto saleDto = new SaleDto();
		saleDto.paymentMeanId = paymentMeanId;

		fullOrder = new ArrayList<ProductSaleDto>();
		for (DirectSaleProductDtoWrapper each : orderedProducts) {
			ProductSaleDto dto = new ProductSaleDto();
			dto.productId = each.getDto().id;
			dto.quantity = each.quantityOrdered;
			dto.price = each.getDto().publicPrice;
			fullOrder.add(dto);
		}
		checkAllSaleItemsInStock();

		if (!missingProducts.isEmpty()) {
			transportDto.status = "CANCELADO";
		}
		int saleId = 0;
		try {
			saleId = s.addDirectSale(saleDto, transportDto, fullOrder);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		if (!missingProducts.isEmpty())
			createOrderForMissingProducts(saleId);
	}

	private int createPaymentMean() {
		PaymentMethodsService ps = null;
		int paymentMeanId = 0;
		if (rbtnCash.isSelected()) {
			ps = ServiceFactory.createCashPaymentService();
			CashPaymentDto dto = new CashPaymentDto();
			paymentMeanId = ps.addPaymentMethod(dto);
		} else if (rbtnCreditCard.isSelected()) {
			ps = ServiceFactory.createCreditCardPaymentService();
			CreditCardPaymentDto dto = new CreditCardPaymentDto();
			dto.cardNumber = txtCreditCardNumber.getText();
			dto.cvv = Integer.parseInt(txtCreditCardCVV.getText());
			dto.dueDate = Dates.fromStringDueDate(String.valueOf(
				cbCreditCardDueDateMonth.getSelectedItem()) + "/"
				+ (String.valueOf(cbCreditCardDueDateYear.getSelectedItem())));
			dto.ownerName = txtCreditCardName.getText();
			paymentMeanId = ps.addPaymentMethod(dto);
		} else if (rbtnTransfer.isSelected()) {
			ps = ServiceFactory.createTransferPaymentService();
			TransferPaymentDto dto = new TransferPaymentDto();
			dto.beneficiary = txtTransferBeneficiary.getText();
			dto.chargedAccount = txtTransferChargedAccount.getText();
			dto.concept = txtTransferConcept.getText();
			dto.destinationAccount = txtTransferDestinedAccount.getText();
			dto.notations = taTransferNotations.getText();
			paymentMeanId = ps.addPaymentMethod(dto);
		}
		return paymentMeanId;
	}

	protected void createOrderForMissingProducts(int saleId) {
		SupplierOrderService service = ServiceFactory.createSupplierOrderService();
		service.createSupplierOrderToSale(missingProducts, saleId);

	}

	protected void checkAllSaleItemsInStock() {
		SaleService service = ServiceFactory.createSaleService();
		missingProducts = service.checkDirectSaleItemsInStock(fullOrder);
	}

	private void createTransportWindow() {
		transport = new TransportationDialog(this);
		transport.setModal(true);
		transport.setLocationRelativeTo(this);
		transport.setVisible(true);

	}

	private boolean checkFieldsAreCorrect() {
		boolean fieldsAreCorrect = false;
		if (rbtnCash.isSelected())
			fieldsAreCorrect = checkCashFields();
		else if (rbtnCreditCard.isSelected())
			fieldsAreCorrect = checkCreditCardFields();
		else if (rbtnTransfer.isSelected())
			fieldsAreCorrect = checkTransferFields();

		return fieldsAreCorrect;
	}

	private boolean checkTransferFields() {
		if (txtTransferDestinedAccount.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo de la cuenta de destino esta vacío, por favor, rellenelo");
			return false;
		}
		if (txtTransferChargedAccount.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo de la cuenta de cargo esta vacío, por favor, rellenelo");
			return false;
		}
		if (txtTransferBeneficiary.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo del beneficiario esta vacío, por favor, rellenelo");
			return false;
		}
		if (txtTransferConcept.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo del concepto de transferencia esta vacío, por favor, rellenelo");
			return false;
		}
		return true;
	}

	private boolean checkCreditCardFields() {
		if (txtCreditCardName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo del nombre de la tarjeta esta vacío, por favor, rellenelo");
			return false;
		}
		if (txtCreditCardNumber.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"El campo del número de la tarjeta esta vacío, por favor, rellenelo");
			return false;
		}
		try {
			int cvv = Integer.parseInt(txtCreditCardCVV.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"En el campo CVV solo se permiten números");
			return false;
		}
		if (txtCreditCardCVV.getText().length() != 3) {
			JOptionPane.showMessageDialog(this,
				"En el campo CVV solo se pueden incluir 3 digitos");
			return false;
		}
		return true;
	}

	private boolean checkCashFields() {
		double paid = 0;
		try {
			paid = df.parse(txtCashPaid.getText()).doubleValue();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"En el campo \"Pagado\" solo se permiten números");
			return false;
		}
		System.out.println(paid);
		System.out.println(total);
		if (paid < total) {
			JOptionPane.showMessageDialog(this,
				"No se ha pagado al completo el precio del pedido");
			return false;
		}
		return true;
	}

	private void calculateChange() {
		double paid = 0;

		try {
			paid = df.parse(txtCashPaid.getText()).doubleValue();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"En el campo \"Pagado\" solo se permiten números");
		}
		if (paid >= total) {
			txtCashChange.setText(df.format(paid - total).toString());
		}
	}

	private void resetMonthCb() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int selectedYear = (Integer) cbCreditCardDueDateYear.getSelectedItem();
		System.out.println(year);
		System.out.println(selectedYear);
		int startingMonth = 1;

		if (selectedYear == year) {
			startingMonth = now.get(Calendar.MONTH);
		}
		dueDateMonthModel = new DefaultComboBoxModel<Integer>();
		for (int i = startingMonth; i <= 12; i++) {
			dueDateMonthModel.addElement(i);
		}
		cbCreditCardDueDateMonth.setModel(dueDateMonthModel);
		cbCreditCardDueDateMonth.revalidate();
		cbCreditCardDueDateMonth.setEnabled(true);

	}

//--Components------------------------------------------------------------------

	private JLabel getLblTitle() {
		if (lblTitle == null) {
			lblTitle = new JLabel("Venta Directa");
		}
		return lblTitle;
	}

	private JButton getBtnConfirmPayment() {
		if (btnConfirmPayment == null) {
			btnConfirmPayment = new JButton("Confirmar Pago");
			btnConfirmPayment.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (checkFieldsAreCorrect())
						createTransportWindow();
				}

			});
			btnConfirmPayment.setMnemonic('C');
			btnConfirmPayment.setEnabled(false);
		}
		return btnConfirmPayment;
	}

	private JLabel getLblProducts() {
		if (lblProducts == null) {
			lblProducts = new JLabel("Productos");
			lblProducts.setHorizontalAlignment(SwingConstants.LEFT);
			lblProducts.setDisplayedMnemonic('P');
		}
		return lblProducts;
	}

	private JLabel getLblSale() {
		if (lblSale == null) {
			lblSale = new JLabel("Venta");
			lblSale.setHorizontalAlignment(SwingConstants.LEFT);
			lblSale.setDisplayedMnemonic('V');
		}
		return lblSale;
	}

	private JLabel getLblUnits() {
		if (lblUnits == null) {
			lblUnits = new JLabel("Unidades:");
			lblUnits.setDisplayedMnemonic('U');
		}
		return lblUnits;
	}

	private JSpinner getSpUnits() {
		if (spUnits == null) {
			spUnits = new JSpinner();
			spUnits.setModel(new SpinnerNumberModel(new Integer(1),
				new Integer(1), null, new Integer(1)));
		}
		return spUnits;
	}

	private JButton getBtnAdd() {
		if (btnAdd == null) {
			btnAdd = new JButton("Agregar");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!tProducts.getSelectionModel().isSelectionEmpty()) {
						int row = tProducts.getSelectedRow();
						addUnitsToSale(row);
						spUnits.setValue(
							((SpinnerNumberModel) spUnits.getModel()).getMinimum());
					}
				}

			});
			btnAdd.setMnemonic('A');
		}
		return btnAdd;
	}

	private JLabel getLblRight() {
		if (lblRight == null) {
			lblRight = new JLabel("=>");
		}
		return lblRight;
	}

	private JLabel getLblLeft() {
		if (lblLeft == null) {
			lblLeft = new JLabel("<=");
		}
		return lblLeft;
	}

	private JButton getBtnDelete() {
		if (btnDelete == null) {
			btnDelete = new JButton("Borrar");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!tSale.getSelectionModel().isSelectionEmpty()) {
						int row = tSale.getSelectedRow();
						deleteUnitsFromOrder(row);
						spUnits.setValue(
							((SpinnerNumberModel) spUnits.getModel()).getMinimum());
					}
				}

			});
			btnDelete.setMnemonic('B');
		}
		return btnDelete;
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
			txtTotal.setText("0");
			txtTotal.setEditable(false);
			txtTotal.setColumns(10);
		}
		return txtTotal;
	}

	private JLabel getLblTotalCurrency() {
		if (lblTotalCurrency == null) {
			lblTotalCurrency = new JLabel("€");
		}
		return lblTotalCurrency;
	}

	private JButton getBtnFilter() {
		if (btnFilter == null) {
			btnFilter = new JButton("Filtrar Productos");
			btnFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createFilterWindow();
				}

			});
			btnFilter.setMnemonic('F');
		}
		return btnFilter;
	}

	private CustomJTable<StockProductDtoWrapper> getTProducts() {
		if (tProducts == null) {
			defaultProductsModel = defaultProductModel();
			tProducts = new CustomJTable<StockProductDtoWrapper>(
				defaultProductsModel);
			tProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tProducts.setName(
				parameters.getProperty("ACTION_COMMAND_TBL_STOCK_PRODUCTS"));
			tProducts.addMouseListener(dcotL);
		}
		return tProducts;
	}

	private CustomJTable<DirectSaleProductDtoWrapper> getTSale() {
		if (tSale == null) {
			saleModel = initialSaleModel();
			tSale = new CustomJTable<DirectSaleProductDtoWrapper>(saleModel);
			tSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tSale.setName(parameters.getProperty("ACTION_COMMAND_TBL_SALE"));
			tSale.addMouseListener(dcotL);
		}
		return tSale;
	}

	private JRadioButton getRbtnCash() {
		if (rbtnCash == null) {
			rbtnCash = new JRadioButton("Efectivo");
			rbtnCash.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPanelEnabled(pnCashFields, true);
					setPanelEnabled(pnCreditCardFields, false);
					setPanelEnabled(pnTransferFields, false);
				}

			});
			rbtnCash.setEnabled(false);
			btngrpPaymentMethods.add(rbtnCash);
			rbtnCash.setSelected(true);
			rbtnCash.setMnemonic('E');
			rbtnCash.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return rbtnCash;
	}

	private JLabel getLblCashPaid() {
		if (lblCashPaid == null) {
			lblCashPaid = new JLabel("Pagado:");
			lblCashPaid.setEnabled(false);
		}
		return lblCashPaid;
	}

	private JTextField getTxtCashPaid() {
		if (txtCashPaid == null) {
			txtCashPaid = new JTextField();
			txtCashPaid.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					calculateChange();
				}

			});
			txtCashPaid.setEnabled(false);
			txtCashPaid.setText("0");
			txtCashPaid.setHorizontalAlignment(SwingConstants.RIGHT);
			txtCashPaid.setColumns(10);
		}
		return txtCashPaid;
	}

	private JLabel getLblCashCurrency() {
		if (lblCashCurrency == null) {
			lblCashCurrency = new JLabel("€");
			lblCashCurrency.setEnabled(false);
		}
		return lblCashCurrency;
	}

	private JLabel getLblCashChange() {
		if (lblCashChange == null) {
			lblCashChange = new JLabel("Cambio:");
			lblCashChange.setEnabled(false);
		}
		return lblCashChange;
	}

	private JTextField getTxtCashChange() {
		if (txtCashChange == null) {
			txtCashChange = new JTextField();
			txtCashChange.setEditable(false);
			txtCashChange.setEnabled(false);
			txtCashChange.setText("0");
			txtCashChange.setHorizontalAlignment(SwingConstants.RIGHT);
			txtCashChange.setColumns(10);
		}
		return txtCashChange;
	}

	private JLabel getLblCashChangeCurrency() {
		if (lblCashChangeCurrency == null) {
			lblCashChangeCurrency = new JLabel("€");
			lblCashChangeCurrency.setEnabled(false);
		}
		return lblCashChangeCurrency;
	}

	private JRadioButton getRbtnTransfer() {
		if (rbtnTransfer == null) {
			rbtnTransfer = new JRadioButton("Transferencia Bancaria");
			rbtnTransfer.setEnabled(false);
			btngrpPaymentMethods.add(rbtnTransfer);
			rbtnTransfer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPanelEnabled(pnCashFields, false);
					setPanelEnabled(pnCreditCardFields, false);
					setPanelEnabled(pnTransferFields, true);
					
				}

			});
			rbtnTransfer.setMnemonic('T');
			rbtnTransfer.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return rbtnTransfer;
	}

	private JLabel getLblTransferDestinedAccount() {
		if (lblTransferDestinedAccount == null) {
			lblTransferDestinedAccount = new JLabel("Cuenta Destino:");
			lblTransferDestinedAccount.setEnabled(false);
		}
		return lblTransferDestinedAccount;
	}

	private JTextField getTxtTransferDestinedAccount() {
		if (txtTransferDestinedAccount == null) {
			txtTransferDestinedAccount = new JTextField();
			txtTransferDestinedAccount.setEnabled(false);
			txtTransferDestinedAccount.setColumns(10);
		}
		return txtTransferDestinedAccount;
	}

	private JLabel getLblTransferChargedAccount() {
		if (lblTransferChargedAccount == null) {
			lblTransferChargedAccount = new JLabel("Cuenta de Cargo:");
			lblTransferChargedAccount.setEnabled(false);
		}
		return lblTransferChargedAccount;
	}

	private JTextField getTxtTransferChargedAccount() {
		if (txtTransferChargedAccount == null) {
			txtTransferChargedAccount = new JTextField();
			txtTransferChargedAccount.setEnabled(false);
			txtTransferChargedAccount.setColumns(10);
		}
		return txtTransferChargedAccount;
	}

	private JLabel getLblTransferBeneficiary() {
		if (lblTransferBeneficiary == null) {
			lblTransferBeneficiary = new JLabel("Beneficiario:");
			lblTransferBeneficiary.setEnabled(false);
		}
		return lblTransferBeneficiary;
	}

	private JTextField getTxtTransferBeneficiary() {
		if (txtTransferBeneficiary == null) {
			txtTransferBeneficiary = new JTextField();
			txtTransferBeneficiary.setEnabled(false);
			txtTransferBeneficiary.setColumns(10);
		}
		return txtTransferBeneficiary;
	}

	private JLabel getLblTransferConcept() {
		if (lblTransferConcept == null) {
			lblTransferConcept = new JLabel("Concepto:");
			lblTransferConcept.setEnabled(false);
		}
		return lblTransferConcept;
	}

	private JTextField getTxtTransferConcept() {
		if (txtTransferConcept == null) {
			txtTransferConcept = new JTextField();
			txtTransferConcept.setEnabled(false);
			txtTransferConcept.setColumns(10);
		}
		return txtTransferConcept;
	}

	private JLabel getLblTransferQuantity() {
		if (lblTransferQuantity == null) {
			lblTransferQuantity = new JLabel("Cantidad:");
			lblTransferQuantity.setEnabled(false);
		}
		return lblTransferQuantity;
	}

	private JTextField getTxtTransferQuantity() {
		if (txtTransferQuantity == null) {
			txtTransferQuantity = new JTextField();
			txtTransferQuantity.setEnabled(false);
			txtTransferQuantity.setColumns(10);
		}
		return txtTransferQuantity;
	}

	private JLabel getLblTransferQuantityCurrency() {
		if (lblTransferQuantityCurrency == null) {
			lblTransferQuantityCurrency = new JLabel("€");
			lblTransferQuantityCurrency.setEnabled(false);
		}
		return lblTransferQuantityCurrency;
	}

	private JLabel getLbTransferlNotations() {
		if (lbTransferlNotations == null) {
			lbTransferlNotations = new JLabel("Anotaciones:");
			lbTransferlNotations.setEnabled(false);
		}
		return lbTransferlNotations;
	}

	private JTextArea getTaTransferNotations() {
		if (taTransferNotations == null) {
			taTransferNotations = new JTextArea();
			taTransferNotations.setWrapStyleWord(true);
			taTransferNotations.setLineWrap(true);
			taTransferNotations.setEnabled(false);
		}
		return taTransferNotations;
	}

	private JRadioButton getRbtnCreditCard() {
		if (rbtnCreditCard == null) {
			rbtnCreditCard = new JRadioButton("Tarjeta de Crédito");
			rbtnCreditCard.setEnabled(false);
			btngrpPaymentMethods.add(rbtnCreditCard);
			rbtnCreditCard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setPanelEnabled(pnCashFields, false);
					setPanelEnabled(pnCreditCardFields, true);
					setPanelEnabled(pnTransferFields, false);
				}

			});
			rbtnCreditCard.setMnemonic('J');
			rbtnCreditCard.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return rbtnCreditCard;
	}

	private JLabel getLblCreditCardName() {
		if (lblCreditCardName == null) {
			lblCreditCardName = new JLabel("Nombre en la Tarjeta:");
			lblCreditCardName.setEnabled(false);
		}
		return lblCreditCardName;
	}

	private JTextField getTxtCreditCardName() {
		if (txtCreditCardName == null) {
			txtCreditCardName = new JTextField();
			txtCreditCardName.setEnabled(false);
			txtCreditCardName.setColumns(10);
		}
		return txtCreditCardName;
	}

	private JLabel getLblCreditCardNumber() {
		if (lblCreditCardNumber == null) {
			lblCreditCardNumber = new JLabel("Numero de Tarjeta:");
			lblCreditCardNumber.setEnabled(false);
		}
		return lblCreditCardNumber;
	}

	private JTextField getTxtCreditCardNumber() {
		if (txtCreditCardNumber == null) {
			txtCreditCardNumber = new JTextField();
			txtCreditCardNumber.setEnabled(false);
			txtCreditCardNumber.setColumns(10);
		}
		return txtCreditCardNumber;
	}

	private JLabel getLblCreditCardDueDate() {
		if (lblCreditCardDueDate == null) {
			lblCreditCardDueDate = new JLabel("Fecha de Caducidad:");
			lblCreditCardDueDate.setEnabled(false);
		}
		return lblCreditCardDueDate;
	}

	private JComboBox<Integer> getCbCreditCardDueDateMonth() {
		if (cbCreditCardDueDateMonth == null) {
			dueDateMonthModel = new DefaultComboBoxModel<Integer>();
			Calendar now = Calendar.getInstance();
			int month = now.get(Calendar.MONTH);
			for (int i = month; i <= 12; i++) {
				dueDateMonthModel.addElement(i);
			}
			cbCreditCardDueDateMonth = new JComboBox<Integer>(
				dueDateMonthModel);
			cbCreditCardDueDateMonth.setEnabled(false);
		}
		return cbCreditCardDueDateMonth;
	}

	private JComboBox<Integer> getCbCreditCardDueDateYear() {
		if (cbCreditCardDueDateYear == null) {
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			dueDateYearModel = new DefaultComboBoxModel<Integer>();
			for (int i = year; i <= year + CREDIT_CARD_DURATION_IN_YEARS; i++) {
				dueDateYearModel.addElement(i);
			}
			cbCreditCardDueDateYear = new JComboBox<Integer>(dueDateYearModel);
			cbCreditCardDueDateYear.setEnabled(false);
			cbCreditCardDueDateYear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resetMonthCb();
				}

			});

		}
		return cbCreditCardDueDateYear;
	}

	private JLabel getLblCreditCardCVV() {
		if (lblCreditCardCVV == null) {
			lblCreditCardCVV = new JLabel("CVV:");
			lblCreditCardCVV.setEnabled(false);
		}
		return lblCreditCardCVV;
	}

	private JTextField getTxtCreditCardCVV() {
		if (txtCreditCardCVV == null) {
			txtCreditCardCVV = new JTextField();
			txtCreditCardCVV.setEnabled(false);
			txtCreditCardCVV.setColumns(10);
		}
		return txtCreditCardCVV;
	}

//---Component fields-----------------------------------------------------------
	private static final long serialVersionUID = 1L;
	private JPanel pnTitle;
	private JLabel lblTitle;
	private JPanel pnButtons;
	private JButton btnConfirmPayment;
	private JPanel pnMain;
	private JLabel lblProducts;
	private JLabel lblSale;
	private JScrollPane spnProducts;
	private JPanel pnOptions;
	private JPanel pnProductOptions;
	private JLabel lblUnits;
	private JSpinner spUnits;
	private JButton btnAdd;
	private JLabel lblRight;
	private JLabel lblLeft;
	private JButton btnDelete;
	private JLabel lblTotal;
	private JTextField txtTotal;
	private JLabel lblTotalCurrency;
	private JButton btnFilter;
	private JScrollPane spnSale;
	private JPanel pnPaymentMethods;
	private JPanel pnCash;
	private JPanel pnTransfer;
	private JPanel pnCreditCard;
	private CustomJTable<StockProductDtoWrapper> tProducts;
	private CustomJTable<DirectSaleProductDtoWrapper> tSale;
	private JPanel pnCashButton;
	private JRadioButton rbtnCash;
	private JPanel pnCashFields;
	private JLabel lblCashPaid;
	private JTextField txtCashPaid;
	private JLabel lblCashCurrency;
	private JLabel lblCashChange;
	private JTextField txtCashChange;
	private JLabel lblCashChangeCurrency;
	private JPanel pnTransferButton;
	private JRadioButton rbtnTransfer;
	private JPanel pnTransferFields;
	private JLabel lblTransferDestinedAccount;
	private JTextField txtTransferDestinedAccount;
	private JLabel lblTransferChargedAccount;
	private JTextField txtTransferChargedAccount;
	private JLabel lblTransferBeneficiary;
	private JTextField txtTransferBeneficiary;
	private JLabel lblTransferConcept;
	private JTextField txtTransferConcept;
	private JLabel lblTransferQuantity;
	private JTextField txtTransferQuantity;
	private JLabel lblTransferQuantityCurrency;
	private JLabel lbTransferlNotations;
	private JTextArea taTransferNotations;
	private JPanel pnCreditCardButton;
	private JRadioButton rbtnCreditCard;
	private JPanel pnCreditCardFields;
	private JLabel lblCreditCardName;
	private JTextField txtCreditCardName;
	private JLabel lblCreditCardNumber;
	private JTextField txtCreditCardNumber;
	private JLabel lblCreditCardDueDate;
	private JComboBox<Integer> cbCreditCardDueDateMonth;
	private JComboBox<Integer> cbCreditCardDueDateYear;
	private JLabel lblCreditCardCVV;
	private JTextField txtCreditCardCVV;
	private final ButtonGroup btngrpPaymentMethods = new ButtonGroup();
	private JPanel pnCreditCardDueDateMonth;
	private JPanel pnCreditCardDueDateYear;

//------Panels------------------------------------------------------------------

	private JPanel getPnMain() {
		if (pnMain == null) {
			pnMain = new JPanel();
			GridBagLayout gbl_pnMain = new GridBagLayout();
			gbl_pnMain.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_pnMain.rowHeights = new int[] { 0, 0, 333, 0, 272, 0, 0 };
			gbl_pnMain.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
					1.0, 0.0, Double.MIN_VALUE };
			gbl_pnMain.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0,
					Double.MIN_VALUE };
			pnMain.setLayout(gbl_pnMain);
			GridBagConstraints gbc_lblProducts = new GridBagConstraints();
			gbc_lblProducts.anchor = GridBagConstraints.WEST;
			gbc_lblProducts.insets = new Insets(0, 0, 5, 5);
			gbc_lblProducts.gridx = 1;
			gbc_lblProducts.gridy = 1;
			pnMain.add(getLblProducts(), gbc_lblProducts);
			GridBagConstraints gbc_lblSale = new GridBagConstraints();
			gbc_lblSale.anchor = GridBagConstraints.WEST;
			gbc_lblSale.insets = new Insets(0, 0, 5, 5);
			gbc_lblSale.gridx = 5;
			gbc_lblSale.gridy = 1;
			pnMain.add(getLblSale(), gbc_lblSale);
			GridBagConstraints gbc_spnProducts = new GridBagConstraints();
			gbc_spnProducts.fill = GridBagConstraints.BOTH;
			gbc_spnProducts.insets = new Insets(0, 0, 5, 5);
			gbc_spnProducts.gridx = 1;
			gbc_spnProducts.gridy = 2;
			pnMain.add(getSpnProducts(), gbc_spnProducts);
			GridBagConstraints gbc_pnOptions = new GridBagConstraints();
			gbc_pnOptions.fill = GridBagConstraints.BOTH;
			gbc_pnOptions.insets = new Insets(0, 0, 5, 5);
			gbc_pnOptions.gridx = 3;
			gbc_pnOptions.gridy = 2;
			pnMain.add(getPnOptions(), gbc_pnOptions);
			GridBagConstraints gbc_spnSale = new GridBagConstraints();
			gbc_spnSale.fill = GridBagConstraints.BOTH;
			gbc_spnSale.insets = new Insets(0, 0, 5, 5);
			gbc_spnSale.gridx = 5;
			gbc_spnSale.gridy = 2;
			pnMain.add(getSpnSale(), gbc_spnSale);
			GridBagConstraints gbc_pnPaymentMethods = new GridBagConstraints();
			gbc_pnPaymentMethods.fill = GridBagConstraints.BOTH;
			gbc_pnPaymentMethods.gridwidth = 5;
			gbc_pnPaymentMethods.insets = new Insets(0, 0, 5, 5);
			gbc_pnPaymentMethods.gridx = 1;
			gbc_pnPaymentMethods.gridy = 4;
			pnMain.add(getPnPaymentMethods(), gbc_pnPaymentMethods);
		}
		return pnMain;
	}

	private JPanel getPnOptions() {
		if (pnOptions == null) {
			pnOptions = new JPanel();
			GridBagLayout gbl_pnOptions = new GridBagLayout();
			gbl_pnOptions.columnWidths = new int[] { 0, 14, 0 };
			gbl_pnOptions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_pnOptions.columnWeights = new double[] { 1.0, 0.0,
					Double.MIN_VALUE };
			gbl_pnOptions.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
					0.0, 0.0, Double.MIN_VALUE };
			pnOptions.setLayout(gbl_pnOptions);
			GridBagConstraints gbc_pnProductOptions = new GridBagConstraints();
			gbc_pnProductOptions.fill = GridBagConstraints.BOTH;
			gbc_pnProductOptions.gridwidth = 2;
			gbc_pnProductOptions.insets = new Insets(0, 0, 5, 0);
			gbc_pnProductOptions.gridx = 0;
			gbc_pnProductOptions.gridy = 1;
			pnOptions.add(getPnProductOptions(), gbc_pnProductOptions);
			GridBagConstraints gbc_lblTotal = new GridBagConstraints();
			gbc_lblTotal.anchor = GridBagConstraints.WEST;
			gbc_lblTotal.insets = new Insets(0, 0, 5, 5);
			gbc_lblTotal.gridx = 0;
			gbc_lblTotal.gridy = 3;
			pnOptions.add(getLblTotal(), gbc_lblTotal);
			GridBagConstraints gbc_txtTotal = new GridBagConstraints();
			gbc_txtTotal.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTotal.anchor = GridBagConstraints.NORTH;
			gbc_txtTotal.insets = new Insets(0, 0, 5, 5);
			gbc_txtTotal.gridx = 0;
			gbc_txtTotal.gridy = 4;
			pnOptions.add(getTxtTotal(), gbc_txtTotal);
			GridBagConstraints gbc_lblTotalCurrency = new GridBagConstraints();
			gbc_lblTotalCurrency.anchor = GridBagConstraints.WEST;
			gbc_lblTotalCurrency.insets = new Insets(0, 0, 5, 0);
			gbc_lblTotalCurrency.gridx = 1;
			gbc_lblTotalCurrency.gridy = 4;
			pnOptions.add(getLblTotalCurrency(), gbc_lblTotalCurrency);
			GridBagConstraints gbc_btnFilter = new GridBagConstraints();
			gbc_btnFilter.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnFilter.gridwidth = 2;
			gbc_btnFilter.gridx = 0;
			gbc_btnFilter.gridy = 6;
			pnOptions.add(getBtnFilter(), gbc_btnFilter);
		}
		return pnOptions;
	}

	private JPanel getPnProductOptions() {
		if (pnProductOptions == null) {
			pnProductOptions = new JPanel();
			pnProductOptions.setBorder(new TitledBorder(null, "Producto",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_pnProductOptions = new GridBagLayout();
			gbl_pnProductOptions.columnWidths = new int[] { 0, 0, 0, 0 };
			gbl_pnProductOptions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
			gbl_pnProductOptions.columnWeights = new double[] { 0.0, 0.0, 0.0,
					Double.MIN_VALUE };
			gbl_pnProductOptions.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
					0.0, Double.MIN_VALUE };
			pnProductOptions.setLayout(gbl_pnProductOptions);
			GridBagConstraints gbc_lblUnits = new GridBagConstraints();
			gbc_lblUnits.insets = new Insets(0, 0, 5, 5);
			gbc_lblUnits.gridx = 1;
			gbc_lblUnits.gridy = 0;
			pnProductOptions.add(getLblUnits(), gbc_lblUnits);
			GridBagConstraints gbc_spUnits = new GridBagConstraints();
			gbc_spUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_spUnits.insets = new Insets(0, 0, 5, 5);
			gbc_spUnits.gridx = 1;
			gbc_spUnits.gridy = 1;
			pnProductOptions.add(getSpUnits(), gbc_spUnits);
			GridBagConstraints gbc_btnAdd = new GridBagConstraints();
			gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
			gbc_btnAdd.gridx = 1;
			gbc_btnAdd.gridy = 3;
			pnProductOptions.add(getBtnAdd(), gbc_btnAdd);
			GridBagConstraints gbc_lblRight = new GridBagConstraints();
			gbc_lblRight.insets = new Insets(0, 0, 5, 0);
			gbc_lblRight.gridx = 2;
			gbc_lblRight.gridy = 3;
			pnProductOptions.add(getLblRight(), gbc_lblRight);
			GridBagConstraints gbc_lblLeft = new GridBagConstraints();
			gbc_lblLeft.insets = new Insets(0, 0, 0, 5);
			gbc_lblLeft.gridx = 0;
			gbc_lblLeft.gridy = 4;
			pnProductOptions.add(getLblLeft(), gbc_lblLeft);
			GridBagConstraints gbc_btnDelete = new GridBagConstraints();
			gbc_btnDelete.fill = GridBagConstraints.BOTH;
			gbc_btnDelete.insets = new Insets(0, 0, 0, 5);
			gbc_btnDelete.gridx = 1;
			gbc_btnDelete.gridy = 4;
			pnProductOptions.add(getBtnDelete(), gbc_btnDelete);
		}
		return pnProductOptions;
	}

	private JPanel getPnCashFields() {
		if (pnCashFields == null) {
			pnCashFields = new JPanel();
			GridBagLayout gbl_pnCashFields = new GridBagLayout();
			gbl_pnCashFields.columnWidths = new int[] { 0, 0, 0, 0, 21, 0, 0 };
			gbl_pnCashFields.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
			gbl_pnCashFields.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
					0.0, 0.0, Double.MIN_VALUE };
			gbl_pnCashFields.rowWeights = new double[] { 0.0, 1.0, 0.0, 1.0,
					0.0, Double.MIN_VALUE };
			pnCashFields.setLayout(gbl_pnCashFields);
			GridBagConstraints gbc_lblCashPaid = new GridBagConstraints();
			gbc_lblCashPaid.insets = new Insets(0, 0, 5, 5);
			gbc_lblCashPaid.gridx = 1;
			gbc_lblCashPaid.gridy = 1;
			pnCashFields.add(getLblCashPaid(), gbc_lblCashPaid);
			GridBagConstraints gbc_txtCashPaid = new GridBagConstraints();
			gbc_txtCashPaid.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCashPaid.insets = new Insets(0, 0, 5, 5);
			gbc_txtCashPaid.gridx = 3;
			gbc_txtCashPaid.gridy = 1;
			pnCashFields.add(getTxtCashPaid(), gbc_txtCashPaid);
			GridBagConstraints gbc_lblCashCurrency = new GridBagConstraints();
			gbc_lblCashCurrency.anchor = GridBagConstraints.WEST;
			gbc_lblCashCurrency.insets = new Insets(0, 0, 5, 5);
			gbc_lblCashCurrency.gridx = 4;
			gbc_lblCashCurrency.gridy = 1;
			pnCashFields.add(getLblCashCurrency(), gbc_lblCashCurrency);
			GridBagConstraints gbc_lblCashChange = new GridBagConstraints();
			gbc_lblCashChange.insets = new Insets(0, 0, 5, 5);
			gbc_lblCashChange.gridx = 1;
			gbc_lblCashChange.gridy = 3;
			pnCashFields.add(getLblCashChange(), gbc_lblCashChange);
			GridBagConstraints gbc_txtCashChange = new GridBagConstraints();
			gbc_txtCashChange.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCashChange.insets = new Insets(0, 0, 5, 5);
			gbc_txtCashChange.gridx = 3;
			gbc_txtCashChange.gridy = 3;
			pnCashFields.add(getTxtCashChange(), gbc_txtCashChange);
			GridBagConstraints gbc_lblCashChangeCurrency = new GridBagConstraints();
			gbc_lblCashChangeCurrency.anchor = GridBagConstraints.WEST;
			gbc_lblCashChangeCurrency.insets = new Insets(0, 0, 5, 5);
			gbc_lblCashChangeCurrency.gridx = 4;
			gbc_lblCashChangeCurrency.gridy = 3;
			pnCashFields.add(getLblCashChangeCurrency(),
				gbc_lblCashChangeCurrency);
		}
		return pnCashFields;
	}

	private JPanel getPnTransferFields() {
		if (pnTransferFields == null) {
			pnTransferFields = new JPanel();
			GridBagLayout gbl_pnTransferFields = new GridBagLayout();
			gbl_pnTransferFields.columnWidths = new int[] { 0, 0, 0, 184, 17,
					0 };
			gbl_pnTransferFields.rowHeights = new int[] { 27, 26, 25, 26, 26, 7,
					54, 0, 0 };
			gbl_pnTransferFields.columnWeights = new double[] { 0.0, 0.0, 0.0,
					1.0, 0.0, Double.MIN_VALUE };
			gbl_pnTransferFields.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0,
					1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
			pnTransferFields.setLayout(gbl_pnTransferFields);
			GridBagConstraints gbc_lblTransferDestinedAccount = new GridBagConstraints();
			gbc_lblTransferDestinedAccount.insets = new Insets(0, 0, 5, 5);
			gbc_lblTransferDestinedAccount.gridx = 1;
			gbc_lblTransferDestinedAccount.gridy = 0;
			pnTransferFields.add(getLblTransferDestinedAccount(),
				gbc_lblTransferDestinedAccount);
			GridBagConstraints gbc_txtTransferDestinedAccount = new GridBagConstraints();
			gbc_txtTransferDestinedAccount.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTransferDestinedAccount.insets = new Insets(0, 0, 5, 5);
			gbc_txtTransferDestinedAccount.gridx = 3;
			gbc_txtTransferDestinedAccount.gridy = 0;
			pnTransferFields.add(getTxtTransferDestinedAccount(),
				gbc_txtTransferDestinedAccount);
			GridBagConstraints gbc_lblTransferChargedAccount = new GridBagConstraints();
			gbc_lblTransferChargedAccount.insets = new Insets(0, 0, 5, 5);
			gbc_lblTransferChargedAccount.gridx = 1;
			gbc_lblTransferChargedAccount.gridy = 1;
			pnTransferFields.add(getLblTransferChargedAccount(),
				gbc_lblTransferChargedAccount);
			GridBagConstraints gbc_txtTransferChargedAccount = new GridBagConstraints();
			gbc_txtTransferChargedAccount.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTransferChargedAccount.insets = new Insets(0, 0, 5, 5);
			gbc_txtTransferChargedAccount.gridx = 3;
			gbc_txtTransferChargedAccount.gridy = 1;
			pnTransferFields.add(getTxtTransferChargedAccount(),
				gbc_txtTransferChargedAccount);
			GridBagConstraints gbc_lblTransferBeneficiary = new GridBagConstraints();
			gbc_lblTransferBeneficiary.insets = new Insets(0, 0, 5, 5);
			gbc_lblTransferBeneficiary.gridx = 1;
			gbc_lblTransferBeneficiary.gridy = 2;
			pnTransferFields.add(getLblTransferBeneficiary(),
				gbc_lblTransferBeneficiary);
			GridBagConstraints gbc_txtTransferBeneficiary = new GridBagConstraints();
			gbc_txtTransferBeneficiary.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTransferBeneficiary.insets = new Insets(0, 0, 5, 5);
			gbc_txtTransferBeneficiary.gridx = 3;
			gbc_txtTransferBeneficiary.gridy = 2;
			pnTransferFields.add(getTxtTransferBeneficiary(),
				gbc_txtTransferBeneficiary);
			GridBagConstraints gbc_lblTransferConcept = new GridBagConstraints();
			gbc_lblTransferConcept.insets = new Insets(0, 0, 5, 5);
			gbc_lblTransferConcept.gridx = 1;
			gbc_lblTransferConcept.gridy = 3;
			pnTransferFields.add(getLblTransferConcept(),
				gbc_lblTransferConcept);
			GridBagConstraints gbc_txtTransferConcept = new GridBagConstraints();
			gbc_txtTransferConcept.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTransferConcept.insets = new Insets(0, 0, 5, 5);
			gbc_txtTransferConcept.gridx = 3;
			gbc_txtTransferConcept.gridy = 3;
			pnTransferFields.add(getTxtTransferConcept(),
				gbc_txtTransferConcept);
			GridBagConstraints gbc_lblTransferQuantity = new GridBagConstraints();
			gbc_lblTransferQuantity.insets = new Insets(0, 0, 5, 5);
			gbc_lblTransferQuantity.gridx = 1;
			gbc_lblTransferQuantity.gridy = 4;
			pnTransferFields.add(getLblTransferQuantity(),
				gbc_lblTransferQuantity);
			GridBagConstraints gbc_txtTransferQuantity = new GridBagConstraints();
			gbc_txtTransferQuantity.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtTransferQuantity.insets = new Insets(0, 0, 5, 5);
			gbc_txtTransferQuantity.gridx = 3;
			gbc_txtTransferQuantity.gridy = 4;
			pnTransferFields.add(getTxtTransferQuantity(),
				gbc_txtTransferQuantity);
			GridBagConstraints gbc_lblTransferQuantityCurrency = new GridBagConstraints();
			gbc_lblTransferQuantityCurrency.anchor = GridBagConstraints.WEST;
			gbc_lblTransferQuantityCurrency.insets = new Insets(0, 0, 5, 0);
			gbc_lblTransferQuantityCurrency.gridx = 4;
			gbc_lblTransferQuantityCurrency.gridy = 4;
			pnTransferFields.add(getLblTransferQuantityCurrency(),
				gbc_lblTransferQuantityCurrency);
			GridBagConstraints gbc_lbTransferlNotations = new GridBagConstraints();
			gbc_lbTransferlNotations.anchor = GridBagConstraints.NORTH;
			gbc_lbTransferlNotations.insets = new Insets(0, 0, 5, 5);
			gbc_lbTransferlNotations.gridx = 1;
			gbc_lbTransferlNotations.gridy = 5;
			pnTransferFields.add(getLbTransferlNotations(),
				gbc_lbTransferlNotations);
			GridBagConstraints gbc_taTransferNotations = new GridBagConstraints();
			gbc_taTransferNotations.fill = GridBagConstraints.BOTH;
			gbc_taTransferNotations.gridheight = 2;
			gbc_taTransferNotations.insets = new Insets(0, 0, 5, 5);
			gbc_taTransferNotations.gridx = 3;
			gbc_taTransferNotations.gridy = 5;
			pnTransferFields.add(getTaTransferNotations(),
				gbc_taTransferNotations);
		}
		return pnTransferFields;
	}

	private JPanel getPnCreditCardFields() {
		if (pnCreditCardFields == null) {
			pnCreditCardFields = new JPanel();
			GridBagLayout gbl_pnCreditCardFields = new GridBagLayout();
			gbl_pnCreditCardFields.columnWidths = new int[] { 0, 0, 0, 82, 145,
					0, 0 };
			gbl_pnCreditCardFields.rowHeights = new int[] { 0, 0, 0, 0, 0, 0,
					0 };
			gbl_pnCreditCardFields.columnWeights = new double[] { 0.0, 0.0, 0.0,
					1.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_pnCreditCardFields.rowWeights = new double[] { 1.0, 1.0, 1.0,
					1.0, 1.0, 1.0, Double.MIN_VALUE };
			pnCreditCardFields.setLayout(gbl_pnCreditCardFields);
			GridBagConstraints gbc_lblCreditCardName = new GridBagConstraints();
			gbc_lblCreditCardName.insets = new Insets(0, 0, 5, 5);
			gbc_lblCreditCardName.gridx = 1;
			gbc_lblCreditCardName.gridy = 1;
			pnCreditCardFields.add(getLblCreditCardName(),
				gbc_lblCreditCardName);
			GridBagConstraints gbc_txtCreditCardName = new GridBagConstraints();
			gbc_txtCreditCardName.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCreditCardName.gridwidth = 2;
			gbc_txtCreditCardName.insets = new Insets(0, 0, 5, 5);
			gbc_txtCreditCardName.gridx = 3;
			gbc_txtCreditCardName.gridy = 1;
			pnCreditCardFields.add(getTxtCreditCardName(),
				gbc_txtCreditCardName);
			GridBagConstraints gbc_lblCreditCardNumber = new GridBagConstraints();
			gbc_lblCreditCardNumber.insets = new Insets(0, 0, 5, 5);
			gbc_lblCreditCardNumber.gridx = 1;
			gbc_lblCreditCardNumber.gridy = 2;
			pnCreditCardFields.add(getLblCreditCardNumber(),
				gbc_lblCreditCardNumber);
			GridBagConstraints gbc_txtCreditCardNumber = new GridBagConstraints();
			gbc_txtCreditCardNumber.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCreditCardNumber.gridwidth = 2;
			gbc_txtCreditCardNumber.insets = new Insets(0, 0, 5, 5);
			gbc_txtCreditCardNumber.gridx = 3;
			gbc_txtCreditCardNumber.gridy = 2;
			pnCreditCardFields.add(getTxtCreditCardNumber(),
				gbc_txtCreditCardNumber);
			GridBagConstraints gbc_lblCreditCardDueDate = new GridBagConstraints();
			gbc_lblCreditCardDueDate.insets = new Insets(0, 0, 5, 5);
			gbc_lblCreditCardDueDate.gridx = 1;
			gbc_lblCreditCardDueDate.gridy = 3;
			pnCreditCardFields.add(getLblCreditCardDueDate(),
				gbc_lblCreditCardDueDate);
			GridBagConstraints gbc_pnCreditCardDueDateMonth = new GridBagConstraints();
			gbc_pnCreditCardDueDateMonth.insets = new Insets(0, 0, 5, 5);
			gbc_pnCreditCardDueDateMonth.fill = GridBagConstraints.HORIZONTAL;
			gbc_pnCreditCardDueDateMonth.gridx = 3;
			gbc_pnCreditCardDueDateMonth.gridy = 3;
			pnCreditCardFields.add(getPnCreditCardDueDateMonth(),
				gbc_pnCreditCardDueDateMonth);
			GridBagConstraints gbc_pnCreditCardDueDateYear = new GridBagConstraints();
			gbc_pnCreditCardDueDateYear.insets = new Insets(0, 0, 5, 5);
			gbc_pnCreditCardDueDateYear.fill = GridBagConstraints.HORIZONTAL;
			gbc_pnCreditCardDueDateYear.gridx = 4;
			gbc_pnCreditCardDueDateYear.gridy = 3;
			pnCreditCardFields.add(getPnCreditCardDueDateYear(),
				gbc_pnCreditCardDueDateYear);
			GridBagConstraints gbc_lblCreditCardCVV = new GridBagConstraints();
			gbc_lblCreditCardCVV.insets = new Insets(0, 0, 5, 5);
			gbc_lblCreditCardCVV.gridx = 1;
			gbc_lblCreditCardCVV.gridy = 4;
			pnCreditCardFields.add(getLblCreditCardCVV(), gbc_lblCreditCardCVV);
			GridBagConstraints gbc_txtCreditCardCVV = new GridBagConstraints();
			gbc_txtCreditCardCVV.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCreditCardCVV.gridwidth = 2;
			gbc_txtCreditCardCVV.insets = new Insets(0, 0, 5, 5);
			gbc_txtCreditCardCVV.gridx = 3;
			gbc_txtCreditCardCVV.gridy = 4;
			pnCreditCardFields.add(getTxtCreditCardCVV(), gbc_txtCreditCardCVV);
		}
		return pnCreditCardFields;
	}

	private JPanel getPnTitle() {
		if (pnTitle == null) {
			pnTitle = new JPanel();
			pnTitle.add(getLblTitle());
		}
		return pnTitle;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.add(getBtnConfirmPayment());
		}
		return pnButtons;
	}

	private JScrollPane getSpnProducts() {
		if (spnProducts == null) {
			spnProducts = new JScrollPane();
			spnProducts.setViewportView(getTProducts());
		}
		return spnProducts;
	}

	private JScrollPane getSpnSale() {
		if (spnSale == null) {
			spnSale = new JScrollPane();
			spnSale.setViewportView(getTSale());
		}
		return spnSale;
	}

	private JPanel getPnPaymentMethods() {
		if (pnPaymentMethods == null) {
			pnPaymentMethods = new JPanel();
			pnPaymentMethods.setBorder(
				new TitledBorder(null, "M\u00E9todos de pago",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnPaymentMethods.setLayout(new GridLayout(1, 0, 0, 0));
			pnPaymentMethods.add(getPnCash());
			pnPaymentMethods.add(getPnTransfer());
			pnPaymentMethods.add(getPnCreditCard());
		}
		return pnPaymentMethods;
	}

	private JPanel getPnCash() {
		if (pnCash == null) {
			pnCash = new JPanel();
			pnCash.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			pnCash.setLayout(new BorderLayout(0, 0));
			pnCash.add(getPnCashButton(), BorderLayout.NORTH);
			pnCash.add(getPnCashFields(), BorderLayout.CENTER);
		}
		return pnCash;
	}

	private JPanel getPnTransfer() {
		if (pnTransfer == null) {
			pnTransfer = new JPanel();
			pnTransfer.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			pnTransfer.setLayout(new BorderLayout(0, 0));
			pnTransfer.add(getPnTransferButton(), BorderLayout.NORTH);
			pnTransfer.add(getPnTransferFields(), BorderLayout.CENTER);
		}
		return pnTransfer;
	}

	private JPanel getPnCreditCard() {
		if (pnCreditCard == null) {
			pnCreditCard = new JPanel();
			pnCreditCard.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			pnCreditCard.setLayout(new BorderLayout(0, 0));
			pnCreditCard.add(getPnCreditCardButton(), BorderLayout.NORTH);
			pnCreditCard.add(getPnCreditCardFields(), BorderLayout.CENTER);
		}
		return pnCreditCard;
	}

	private JPanel getPnCashButton() {
		if (pnCashButton == null) {
			pnCashButton = new JPanel();
			pnCashButton.setBorder(
				new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			pnCashButton.add(getRbtnCash());
		}
		return pnCashButton;
	}

	private JPanel getPnTransferButton() {
		if (pnTransferButton == null) {
			pnTransferButton = new JPanel();
			pnTransferButton.setBorder(
				new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			pnTransferButton.add(getRbtnTransfer());
		}
		return pnTransferButton;
	}

	private JPanel getPnCreditCardButton() {
		if (pnCreditCardButton == null) {
			pnCreditCardButton = new JPanel();
			pnCreditCardButton.setBorder(
				new MatteBorder(0, 0, 1, 0, (Color) new Color(0, 0, 0)));
			pnCreditCardButton.add(getRbtnCreditCard());
		}
		return pnCreditCardButton;
	}

	private JPanel getPnCreditCardDueDateMonth() {
		if (pnCreditCardDueDateMonth == null) {
			pnCreditCardDueDateMonth = new JPanel();
			pnCreditCardDueDateMonth.setBorder(new TitledBorder(null, "Month",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnCreditCardDueDateMonth.setLayout(new BorderLayout(0, 0));
			pnCreditCardDueDateMonth.add(getCbCreditCardDueDateMonth(),
				BorderLayout.CENTER);
		}
		return pnCreditCardDueDateMonth;
	}

	private JPanel getPnCreditCardDueDateYear() {
		if (pnCreditCardDueDateYear == null) {
			pnCreditCardDueDateYear = new JPanel();
			pnCreditCardDueDateYear.setBorder(new TitledBorder(null, "Year",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnCreditCardDueDateYear.setLayout(new BorderLayout(0, 0));
			pnCreditCardDueDateYear.add(getCbCreditCardDueDateYear(),
				BorderLayout.CENTER);
		}
		return pnCreditCardDueDateYear;
	}

}
