package ui.budget;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import business.serviceLayer.BudgetService;
import business.serviceLayer.ClientService;
import business.serviceLayer.ProductService;
import common.Conf;
import dtos.BudgetDto;
import dtos.ClientDto;
import dtos.ProductDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import ui.filters.FilterProducts;
import ui.filters.FiltrablePanel;
import wrappers.ClientDtoWrapper;
import wrappers.ProductDtoWrapper;

/**
 * This panel serves as a container to show any user the information related
 * with the budget creation and interact with the user retrieving what he inputs
 * and managing it.
 * 
 * @author Angel
 *
 */
public class CreateBudgetFirstPanel extends JPanel implements FiltrablePanel {
	private static final long serialVersionUID = 1L;

	private JPanel pnBudgetList;
	private JPanel pnButtons;
	private JButton btnAddProduct;
	private JPanel pnProductList;
	private JPanel pnBudgetProductList;
	private JScrollPane spProductListTable;
	private JScrollPane spBudgetProductList;
	private JLabel lblProductListTable;
	private JLabel lblBudgetProductList;
	private JButton btnDeleteProduct;
	private JPanel pnCreateBudget;
	private JButton btnCreateBudget;
	private JPanel pnClients;
	private JCheckBox chbxUseExistingClient;
	private JPanel pnNewClient;
	private JPanel pnExistingClients;
	private JList<ClientDtoWrapper> listExistingClients;
	private JScrollPane spListExistingClients;
	private JPanel pnNewClientDni;
	private JPanel pnNewClientName;
	private JPanel pnNewClientAddress;
	private JPanel pnNewClientPhoneNumber;
	private JPanel pnNewClientPostalCode;
	private JLabel lblClientDni;
	private JLabel lblClientName;
	private JLabel lblClientAddress;
	private JLabel lblClientPhone;
	private JLabel lblPostalCode;
	private JTextField txtClientDni;
	private JTextField txtClientName;
	private JTextField txtClientAddress;
	private JTextField txtClientPhone;
	private JTextField txtClientPostalCode;
	private JPanel pnExistingClientsTitle;
	private JLabel lblExistingClientsTitle;
	private JCheckBox chbxUseClient;
	private JPanel pnChbxExistingClient;
	private CustomJTable<ProductDtoWrapper> tblProductList;
	private CustomJTable<ProductDtoWrapper> tblBudgetProductList;
	private JPanel pnBudgetButtons;
	private JLabel lblAddProductButton;
	private JPanel pnLblDeleteProductButton;
	private JPanel pnLblAddProductButton;
	private JLabel lblDeleteProductButton;
	private JButton btnFiltrarAlmacen;

	private FilterProducts filterDialog = null;

	private Conf parameters = Conf.getInstance("configs/parameters.properties");
	private ButtonConstrainListener bcL = new ButtonConstrainListener();
	private CheckRadioButtonsChecked crbtncL = new CheckRadioButtonsChecked();
	private DoubleClickOnTablelListener dcotL = new DoubleClickOnTablelListener();

	private List<ProductDto> filteredProductList;
	private JPanel pnSpinnerForProductQuantity;
	private JSpinner spinnerProductQuantity;
	private JLabel lblSpinnerProductQuantity;

	private static final int DEFAULT_QUANTITY_SPINNER_VALUE = 1;

	private boolean wasCheckedUseClient = false;
	private boolean wasCheckedUseExistingClient = false;

	/**
	 * Create the panel.
	 * 
	 * @param mainWindow
	 */
	public CreateBudgetFirstPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getPnBudgetList());

		add(getPnCreateBudget(), BorderLayout.SOUTH);

	}

	private JPanel getPnBudgetList() {
		if (pnBudgetList == null) {
			pnBudgetList = new JPanel();
			GridBagLayout gbl_pnBudgetList = new GridBagLayout();
			gbl_pnBudgetList.columnWidths = new int[] { 452, 226, 452, 0 };
			gbl_pnBudgetList.rowHeights = new int[] { 287, 216, 0 };
			gbl_pnBudgetList.columnWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnBudgetList.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			pnBudgetList.setLayout(gbl_pnBudgetList);
			GridBagConstraints gbc_pnProductList = new GridBagConstraints();
			gbc_pnProductList.weighty = 0.1;
			gbc_pnProductList.fill = GridBagConstraints.BOTH;
			gbc_pnProductList.insets = new Insets(0, 0, 5, 5);
			gbc_pnProductList.gridx = 0;
			gbc_pnProductList.gridy = 0;
			pnBudgetList.add(getPnProductList(), gbc_pnProductList);
			GridBagConstraints gbc_pnBudgetButtons = new GridBagConstraints();
			gbc_pnBudgetButtons.fill = GridBagConstraints.HORIZONTAL;
			gbc_pnBudgetButtons.insets = new Insets(0, 0, 5, 5);
			gbc_pnBudgetButtons.gridx = 1;
			gbc_pnBudgetButtons.gridy = 0;
			pnBudgetList.add(getPnBudgetButtons(), gbc_pnBudgetButtons);
			GridBagConstraints gbc_pnBudgetProductList = new GridBagConstraints();
			gbc_pnBudgetProductList.weighty = 0.1;
			gbc_pnBudgetProductList.anchor = GridBagConstraints.WEST;
			gbc_pnBudgetProductList.fill = GridBagConstraints.BOTH;
			gbc_pnBudgetProductList.insets = new Insets(0, 0, 5, 0);
			gbc_pnBudgetProductList.gridx = 2;
			gbc_pnBudgetProductList.gridy = 0;
			pnBudgetList.add(getPnBudgetProductList(), gbc_pnBudgetProductList);
			GridBagConstraints gbc_pnClients = new GridBagConstraints();
			gbc_pnClients.anchor = GridBagConstraints.NORTH;
			gbc_pnClients.fill = GridBagConstraints.HORIZONTAL;
			gbc_pnClients.gridwidth = 3;
			gbc_pnClients.gridx = 0;
			gbc_pnClients.gridy = 1;
			pnBudgetList.add(getPnClients(), gbc_pnClients);
		}
		return pnBudgetList;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.setLayout(new GridLayout(0, 1, 0, 20));
			pnButtons.add(getBtnAddProduct());
			pnButtons.add(getPnSpinnerForProductQuantity());
			pnButtons.add(getBtnDeleteProduct());
		}
		return pnButtons;
	}

	private JButton getBtnAddProduct() {
		if (btnAddProduct == null) {
			btnAddProduct = new JButton("Agregar Producto");
			btnAddProduct.addActionListener(new AddButtonProductListener());
		}
		return btnAddProduct;
	}

	private JPanel getPnProductList() {
		if (pnProductList == null) {
			pnProductList = new JPanel();
			pnProductList.setLayout(new BorderLayout(0, 0));
			pnProductList.add(getSpProductListTable());
			pnProductList.add(getLblProductListTable(), BorderLayout.NORTH);
		}
		return pnProductList;
	}

	private JPanel getPnBudgetProductList() {
		if (pnBudgetProductList == null) {
			pnBudgetProductList = new JPanel();
			pnBudgetProductList.setLayout(new BorderLayout(0, 0));
			pnBudgetProductList.add(getSpBudgetProductList());
			pnBudgetProductList.add(getLblBudgetProductList(), BorderLayout.NORTH);
		}
		return pnBudgetProductList;
	}

	private JScrollPane getSpProductListTable() {
		if (spProductListTable == null) {
			spProductListTable = new JScrollPane();
			spProductListTable.setViewportView(getTblProductList());
		}
		return spProductListTable;
	}

	private JScrollPane getSpBudgetProductList() {
		if (spBudgetProductList == null) {
			spBudgetProductList = new JScrollPane();
			spBudgetProductList.setViewportView(getTblBudgetProductList());
		}
		return spBudgetProductList;
	}

	private JLabel getLblProductListTable() {
		if (lblProductListTable == null) {
			lblProductListTable = new JLabel("Productos En Almacén");
			lblProductListTable.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblProductListTable;
	}

	private JLabel getLblBudgetProductList() {
		if (lblBudgetProductList == null) {
			lblBudgetProductList = new JLabel("Productos del Presupuesto");
			lblBudgetProductList.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblBudgetProductList;
	}

	private JButton getBtnDeleteProduct() {
		if (btnDeleteProduct == null) {
			btnDeleteProduct = new JButton("Borrar Producto");

			btnDeleteProduct.addActionListener(new DeleteButtonProductListener());
		}
		return btnDeleteProduct;
	}

	private JPanel getPnCreateBudget() {
		if (pnCreateBudget == null) {
			pnCreateBudget = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnCreateBudget.getLayout();
			flowLayout.setAlignment(FlowLayout.CENTER);
			pnCreateBudget.add(getBtnFiltrarAlmacen());
			pnCreateBudget.add(getBtnCreateBudget());
		}
		return pnCreateBudget;
	}

	private JButton getBtnCreateBudget() {
		if (btnCreateBudget == null) {
			btnCreateBudget = new JButton("Crear Presupuesto");
			btnCreateBudget.addActionListener(new CreateBudgetActionListener());
			btnCreateBudget.setEnabled(false);
		}
		return btnCreateBudget;
	}

	public void updateCreateButton(int size) {
		if (size > 0)
			btnCreateBudget.setEnabled(true);
		else
			btnCreateBudget.setEnabled(false);
	}

	private JPanel getPnClients() {
		if (pnClients == null) {
			pnClients = new JPanel();
			GridBagLayout gbl_pnClients = new GridBagLayout();
			gbl_pnClients.columnWidths = new int[] { 236, 258, 0 };
			gbl_pnClients.rowHeights = new int[] { 0, 154, 0, 0 };
			gbl_pnClients.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnClients.rowWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
			pnClients.setLayout(gbl_pnClients);
			GridBagConstraints gbc_pnChbxExistingClient = new GridBagConstraints();
			gbc_pnChbxExistingClient.gridwidth = 2;
			gbc_pnChbxExistingClient.insets = new Insets(0, 0, 5, 5);
			gbc_pnChbxExistingClient.fill = GridBagConstraints.BOTH;
			gbc_pnChbxExistingClient.gridx = 0;
			gbc_pnChbxExistingClient.gridy = 0;
			pnClients.add(getPnChbxExistingClient(), gbc_pnChbxExistingClient);
			GridBagConstraints gbc_pnNewClient = new GridBagConstraints();
			gbc_pnNewClient.anchor = GridBagConstraints.WEST;
			gbc_pnNewClient.insets = new Insets(0, 0, 5, 5);
			gbc_pnNewClient.gridx = 0;
			gbc_pnNewClient.gridy = 1;
			pnClients.add(getPnNewClient(), gbc_pnNewClient);
			GridBagConstraints gbc_pnExistingClients = new GridBagConstraints();
			gbc_pnExistingClients.insets = new Insets(0, 0, 5, 0);
			gbc_pnExistingClients.weightx = 0.1;
			gbc_pnExistingClients.fill = GridBagConstraints.HORIZONTAL;
			gbc_pnExistingClients.anchor = GridBagConstraints.EAST;
			gbc_pnExistingClients.gridx = 1;
			gbc_pnExistingClients.gridy = 1;
			pnClients.add(getPnExistingClients(), gbc_pnExistingClients);
		}
		return pnClients;
	}

	private JCheckBox getChbxUseExistingClient() {
		if (chbxUseExistingClient == null) {
			chbxUseExistingClient = new JCheckBox("Usar cliente existente");
			chbxUseExistingClient.addActionListener(crbtncL);
		}
		return chbxUseExistingClient;
	}

	private JPanel getPnNewClient() {
		if (pnNewClient == null) {
			pnNewClient = new JPanel();
			pnNewClient.setBorder(new TitledBorder(null, "Informaci\u00F3n de nuevo cliente", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			pnNewClient.setLayout(new GridLayout(0, 1, 0, 0));
			pnNewClient.add(getPnNewClientDni());
			pnNewClient.add(getPnNewClientName());
			pnNewClient.add(getPnNewClientAddress());
			pnNewClient.add(getPnNewClientPhoneNumber());
			pnNewClient.add(getPnNewClientPostalCode());
		}
		return pnNewClient;
	}

	private JPanel getPnExistingClients() {
		if (pnExistingClients == null) {
			pnExistingClients = new JPanel();
			pnExistingClients.setLayout(new BorderLayout(0, 0));
			pnExistingClients.add(getSpListExistingClients());
			pnExistingClients.add(getPnExistingClientsTitle(), BorderLayout.NORTH);
		}
		return pnExistingClients;
	}

	private JList<ClientDtoWrapper> getListExistingClients() {
		if (listExistingClients == null) {
			DefaultListModel<ClientDtoWrapper> existingClientsModel = initialClientList();

			listExistingClients = new JList<ClientDtoWrapper>(existingClientsModel);
			listExistingClients.setEnabled(false);
		}
		return listExistingClients;
	}

	private JScrollPane getSpListExistingClients() {
		if (spListExistingClients == null) {
			spListExistingClients = new JScrollPane();
			spListExistingClients.setViewportView(getListExistingClients());
		}
		return spListExistingClients;
	}

	private JPanel getPnNewClientDni() {
		if (pnNewClientDni == null) {
			pnNewClientDni = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnNewClientDni.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnNewClientDni.add(getLblClientDni());
			pnNewClientDni.add(getTxtClientDni());
		}
		return pnNewClientDni;
	}

	private JPanel getPnNewClientName() {
		if (pnNewClientName == null) {
			pnNewClientName = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnNewClientName.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnNewClientName.add(getLblClientName());
			pnNewClientName.add(getTxtClientName());
		}
		return pnNewClientName;
	}

	private JPanel getPnNewClientAddress() {
		if (pnNewClientAddress == null) {
			pnNewClientAddress = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnNewClientAddress.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnNewClientAddress.add(getLblClientAddress());
			pnNewClientAddress.add(getTxtClientAddress());
		}
		return pnNewClientAddress;
	}

	private JPanel getPnNewClientPhoneNumber() {
		if (pnNewClientPhoneNumber == null) {
			pnNewClientPhoneNumber = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnNewClientPhoneNumber.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnNewClientPhoneNumber.add(getLblClientPhone());
			pnNewClientPhoneNumber.add(getTxtClientPhone());
		}
		return pnNewClientPhoneNumber;
	}

	private JPanel getPnNewClientPostalCode() {
		if (pnNewClientPostalCode == null) {
			pnNewClientPostalCode = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnNewClientPostalCode.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnNewClientPostalCode.add(getLblPostalCode());
			pnNewClientPostalCode.add(getTxtClientPostalCode());
		}
		return pnNewClientPostalCode;
	}

	private JLabel getLblClientDni() {
		if (lblClientDni == null) {
			lblClientDni = new JLabel("Dni Cliente:");
			lblClientDni.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblClientDni;
	}

	private JLabel getLblClientName() {
		if (lblClientName == null) {
			lblClientName = new JLabel("Nombre Cliente:");
			lblClientName.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblClientName;
	}

	private JLabel getLblClientAddress() {
		if (lblClientAddress == null) {
			lblClientAddress = new JLabel("Dirección Cliente:");
			lblClientAddress.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblClientAddress;
	}

	private JLabel getLblClientPhone() {
		if (lblClientPhone == null) {
			lblClientPhone = new JLabel("Numero de teléfono Cliente:");
			lblClientPhone.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblClientPhone;
	}

	private JLabel getLblPostalCode() {
		if (lblPostalCode == null) {
			lblPostalCode = new JLabel("Codigo Postal Cliente:");
			lblPostalCode.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblPostalCode;
	}

	private JTextField getTxtClientDni() {
		if (txtClientDni == null) {
			txtClientDni = new JTextField();
			txtClientDni.setEditable(false);
			txtClientDni.setName(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_DNI_BUDGET_FIRST"));

			txtClientDni.setHorizontalAlignment(SwingConstants.LEFT);
			txtClientDni.setColumns(10);

			txtClientDni.addFocusListener(bcL);
		}
		return txtClientDni;
	}

	private JTextField getTxtClientName() {
		if (txtClientName == null) {
			txtClientName = new JTextField();
			txtClientName.setEditable(false);
			txtClientName.setName(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_NAME_BUDGET_FIRST"));

			txtClientName.setHorizontalAlignment(SwingConstants.LEFT);
			txtClientName.setColumns(10);

			txtClientName.addFocusListener(bcL);
		}
		return txtClientName;
	}

	private JTextField getTxtClientAddress() {
		if (txtClientAddress == null) {
			txtClientAddress = new JTextField();
			txtClientAddress.setEditable(false);
			txtClientAddress.setName(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_ADDRESS_BUDGET_FIRST"));

			txtClientAddress.setHorizontalAlignment(SwingConstants.LEFT);
			txtClientAddress.setColumns(10);

			txtClientAddress.addFocusListener(bcL);
		}
		return txtClientAddress;
	}

	private JTextField getTxtClientPhone() {
		if (txtClientPhone == null) {
			txtClientPhone = new JTextField();
			txtClientPhone.setEditable(false);
			txtClientPhone.setName(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_PHONE_BUDGET_FIRST"));

			txtClientPhone.setHorizontalAlignment(SwingConstants.LEFT);
			txtClientPhone.setColumns(10);

			txtClientPhone.addFocusListener(bcL);
		}
		return txtClientPhone;
	}

	private JTextField getTxtClientPostalCode() {
		if (txtClientPostalCode == null) {
			txtClientPostalCode = new JTextField();
			txtClientPostalCode.setEditable(false);
			txtClientPostalCode.setName(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_CP_BUDGET_FIRST"));

			txtClientPostalCode.setHorizontalAlignment(SwingConstants.LEFT);
			txtClientPostalCode.setColumns(10);

			txtClientPostalCode.addFocusListener(bcL);
		}
		return txtClientPostalCode;
	}

	private JPanel getPnExistingClientsTitle() {
		if (pnExistingClientsTitle == null) {
			pnExistingClientsTitle = new JPanel();
			pnExistingClientsTitle.add(getLblExistingClientsTitle());
		}
		return pnExistingClientsTitle;
	}

	private JLabel getLblExistingClientsTitle() {
		if (lblExistingClientsTitle == null) {
			lblExistingClientsTitle = new JLabel("Clientes Existentes:");
			lblExistingClientsTitle.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblExistingClientsTitle;
	}

	private boolean checkConstrainsOfButton(String name) {
		if (name.equals(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_ADDRESS_BUDGET_FIRST"))) {
			if (txtClientAddress.getText().isEmpty())
				return false;

		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_DNI_BUDGET_FIRST"))) {
			if (txtClientDni.getText().isEmpty())
				return false;

			String dni = txtClientDni.getText();
			String number = dni.substring(0, dni.length() - 1);
			char letter = dni.substring(dni.length() - 1, dni.length()).toCharArray()[0];

			try {
				Integer.parseInt(number);
				if (!Character.isLetter(letter)) {
					txtClientDni.setText("");
					return false;
				}
			} catch (NumberFormatException e) {
				txtClientDni.setText("");
				return false;
			}
		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_NAME_BUDGET_FIRST"))) {
			if (txtClientName.getText().isEmpty())
				return false;

			for (char letter : txtClientName.getText().toCharArray()) {
				if (Character.isDigit(letter)) {
					txtClientName.setText("");
					return false;
				}
			}
		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_PHONE_BUDGET_FIRST"))) {
			if (txtClientPhone.getText().isEmpty())
				return false;

			String possibleNumber = txtClientPhone.getText();
			try {
				Integer.parseInt(possibleNumber);
			} catch (NumberFormatException e) {
				txtClientPhone.setText("");
				return false;
			}
		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TXT_CLIENT_CP_BUDGET_FIRST"))) {
			if (txtClientPostalCode.getText().isEmpty())
				return false;

			String possibleCp = txtClientPostalCode.getText();
			int maxCp = Integer.parseInt(parameters.getProperty("MAXIMUM_CP"));

			try {
				int cp = Integer.parseInt(possibleCp);
				if (cp >= maxCp) {
					txtClientPostalCode.setText("");
					return false;
				}
			} catch (NumberFormatException e) {
				txtClientPostalCode.setText("");
				return false;
			}
		}

		return true;
	}

	private JPanel getPnChbxExistingClient() {
		if (pnChbxExistingClient == null) {
			pnChbxExistingClient = new JPanel();
			pnChbxExistingClient.add(getChbxUseClient());
			pnChbxExistingClient.add(getChbxUseExistingClient());
		}
		return pnChbxExistingClient;
	}

	private class DeleteButtonProductListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int row = tblBudgetProductList.getSelectedRow();

			deleteEntryFromTable(row, false);
		}
	}

	/**
	 * 
	 * @param row
	 * @param requestFromTable true if this method was called from the JTable
	 */
	private void deleteEntryFromTable(int row, boolean requestFromTable) {
		if (row >= 0) {

			List<ProductDtoWrapper> newModelBudgetValues = new ArrayList<>();

			ProductDtoWrapper selected = tblBudgetProductList.getCustomModel().getValueAtRow(row);
			Integer toDeleteQuantity = (Integer) spinnerProductQuantity.getValue();

			selected.quantityOrdered -= toDeleteQuantity;
			if (selected.quantityOrdered <= 0 || requestFromTable) {
				selected.quantityOrdered = 0;
				selected.isOrdered = false;
				for (int i = 0; i < tblBudgetProductList.getModel().getRowCount(); i++) {
					if (i == row)
						continue;
					newModelBudgetValues.add(tblBudgetProductList.getCustomModel().getValueAtRow(i));
				}

				CustomTableModel<ProductDtoWrapper> newModelBudget = new CustomTableModel<ProductDtoWrapper>(
						newModelBudgetValues);

				tblBudgetProductList.setModel(newModelBudget);

				updateCreateButton(newModelBudget.getRowCount());

			} else {
				tblBudgetProductList.getModel().setValueAt(selected.quantityOrdered, row,
						tblBudgetProductList.getModel().getColumnCount() - 1);
			}
		}
		
		spinnerProductQuantity.setValue(DEFAULT_QUANTITY_SPINNER_VALUE);
	}

	private class AddButtonProductListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int row = tblProductList.getSelectedRow();

			addEntryToTable(row);
		}

	}

	private void addEntryToTable(int row) {
		if (row >= 0) {

			ProductDtoWrapper dto = tblProductList.getCustomModel().getValueAtRow(row);
			if (dto.isOrdered) {
				dto.quantityOrdered += (Integer) spinnerProductQuantity.getValue();
				int rowToUpdate = lookForDtoOnBudgetProduct(dto);
				tblBudgetProductList.getModel().setValueAt(dto.quantityOrdered, rowToUpdate,
						tblBudgetProductList.getModel().getColumnCount() - 1);
			} else {
				dto.quantityOrdered = (Integer) spinnerProductQuantity.getValue();
				dto.isOrdered = true;

//				List<ProductDtoWrapper> newModelProductValues = new ArrayList<>();
//				for (int i = 0; i < tblProductList.getCustomModel().getRowCount(); i++) {
//					if (i == row)
//						continue;
//					newModelProductValues.add(tblProductList.getCustomModel().getValueAtRow(i));
//				}
//
//				CustomTableModel<ProductDtoWrapper> newModelProduct = new CustomTableModel<ProductDtoWrapper>(
//						newModelProductValues);
//				tblProductList.setModel(newModelProduct);

				List<ProductDtoWrapper> newModelBudgetValues = new ArrayList<>();
				for (int i = 0; i < tblBudgetProductList.getModel().getRowCount(); i++) {
					newModelBudgetValues.add(tblBudgetProductList.getCustomModel().getValueAtRow(i));
				}

				newModelBudgetValues.add(dto);

				CustomTableModel<ProductDtoWrapper> newModelBudget = new CustomTableModel<ProductDtoWrapper>(
						newModelBudgetValues);
				tblBudgetProductList.setModel(newModelBudget);

				updateCreateButton(newModelBudget.getRowCount());
			}

			if (dto.quantityOrdered > dto.getDto().stock)
				showWarning(
						"Esta añadiendo mas productos de los que hay en el almacen al presupuesto, el productos se añadira de cualquier forma",
						"Advertencia añadir producto a presupuesto");
		}

		spinnerProductQuantity.setValue(DEFAULT_QUANTITY_SPINNER_VALUE);
	}

	private int lookForDtoOnBudgetProduct(ProductDtoWrapper dto) {
		for (int i = 0; i < tblBudgetProductList.getModel().getRowCount(); i++) {
			if (dto.getDto().id == tblBudgetProductList.getCustomModel().getValueAtRow(i).getDto().id)
				return i;
		}
		return -1;
	}

	private void showWarning(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
	}

	private class ButtonConstrainListener extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			checkConstrainsOfButton(e.getComponent().getName());
		}
	}

	private class CreateBudgetActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (askForPermission("¿Está seguro que quiere crear el presupuesto?",
					"Confirmación creacion de presupuesto") == JOptionPane.YES_OPTION) {
				boolean succesfulCreation = false;
				BudgetService budgetService = ServiceFactory.createBudgetService();

				BudgetDto dtoBudget = new BudgetDto();
				dtoBudget.id = 0;

				ZoneId zone = ZoneId.of(parameters.getProperty("ES_LOCALE"));
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(parameters.getProperty("DATE_FORMAT"));
				LocalDate nowDate = LocalDate.now(zone);
				String creation_date = nowDate.format(dateFormat);
				dtoBudget.creationDate = creation_date;

				LocalDate nextDate = nowDate
						.plusDays(Integer.parseInt(parameters.getProperty("DAYS_TO_EXPIRE_BUDGET")));
				String expiration_date = nextDate.format(dateFormat);
				dtoBudget.expirationDate = expiration_date;

				List<ProductDtoWrapper> dtosProduct = tblBudgetProductList.getCustomModel().getDataReferences();

				if (!chbxUseClient.isSelected()) {
					dtoBudget.clientId = null;
					succesfulCreation = budgetService.createBudgetWithoutClientOrExistingClient(dtoBudget, dtosProduct);
				} else {
					if (chbxUseExistingClient.isSelected()) {
						if (listExistingClients.getSelectedIndex() != -1) {
							int client_id = listExistingClients.getModel()
									.getElementAt(listExistingClients.getSelectedIndex()).getDto().id;
							dtoBudget.clientId = client_id;
							succesfulCreation = budgetService.createBudgetWithoutClientOrExistingClient(dtoBudget,
									dtosProduct);
						} else {
							showError(
									"No se pudo crear el presupuesto, un cliente en la lista debe ser seleccionado segun los parametros actuales",
									"Error creación de presupuesto");
						}
					} else {
						boolean isValidPostalCode = checkConstrainsOfButton(txtClientPostalCode.getName());
						boolean isValidPhone = checkConstrainsOfButton(txtClientPhone.getName());
						boolean isValidClientName = checkConstrainsOfButton(txtClientName.getName());
						boolean isValidClientDni = checkConstrainsOfButton(txtClientDni.getName());
						boolean isValidAddress = checkConstrainsOfButton(txtClientAddress.getName());

						if (isValidPostalCode && isValidPhone && isValidClientName && isValidClientDni
								&& isValidAddress) {
							ClientDto dtoClient = new ClientDto();
							dtoClient.id = 0;
							dtoClient.street = txtClientAddress.getText();
							dtoClient.dni = txtClientDni.getText();
							dtoClient.name = txtClientName.getText();
							dtoClient.phoneNumber = Integer.parseInt(txtClientPhone.getText());
							dtoClient.postCode = txtClientPostalCode.getText();

							dtoBudget.clientId = dtoClient.id;
							succesfulCreation = budgetService.createBudgetWithNewClient(dtoBudget, dtosProduct,
									dtoClient);

						} else {
							showErrorForAllInvalidFields();
						}

					}
				}
				if (succesfulCreation) {
					showEverythingOk("El presupuesto se ha creado sin problemas", "Exito creacion de presupuesto");
					resetToCreateNewBudget();
				} else {
					showError("Ha ocurrido un error al crear el presupuesto", "Error al crear presupuesto");
				}
			}

		}

	}

	private JCheckBox getChbxUseClient() {
		if (chbxUseClient == null) {
			chbxUseClient = new JCheckBox("Usar cliente");
			chbxUseClient.addActionListener(crbtncL);
		}
		return chbxUseClient;
	}

	private void resetToCreateNewBudget() {
		initialTxtClientInfo();
		initialJTablesListing();
		initialRdbtnStates();

		// Client List Initialization
		listExistingClients.setModel(initialClientList());
		listExistingClients.setEnabled(false);

		// Budget creation button
		btnCreateBudget.setEnabled(false);
	}

	private DefaultListModel<ClientDtoWrapper> initialClientList() {
		DefaultListModel<ClientDtoWrapper> existingClientsModel = new DefaultListModel<>();
		ClientService clientService = ServiceFactory.createClientService();

		List<ClientDto> clients = clientService.findAllClients();
		for (ClientDto dto : clients) {
			existingClientsModel.addElement(new ClientDtoWrapper(dto));
		}

		return existingClientsModel;
	}

	private void initialRdbtnStates() {
		chbxUseClient.setSelected(false);
		chbxUseExistingClient.setSelected(false);
	}

	private void initialTxtClientInfo() {
		txtClientAddress.setText("");
		txtClientDni.setText("");
		txtClientName.setText("");
		txtClientPhone.setText("");
		txtClientPostalCode.setText("");

		txtClientAddress.setEditable(false);
		txtClientDni.setEditable(false);
		txtClientName.setEditable(false);
		txtClientPhone.setEditable(false);
		txtClientPostalCode.setEditable(false);

	}

	private void initialJTablesListing() {
		// JTable Productos En Almacen
		tblProductList.setModel(initialJTableProductList());

		// JTable Productos del Presupuestoç
		tblBudgetProductList.setModel(initialJTableBudgetProductList());
	}

	private CustomTableModel<ProductDtoWrapper> initialJTableBudgetProductList() {
		CustomTableModel<ProductDtoWrapper> model = new CustomTableModel<>();
		return model;
	}

	private CustomTableModel<ProductDtoWrapper> filteredJTableProductList() {

		List<ProductDtoWrapper> wrappedDtos = new ArrayList<ProductDtoWrapper>();
		for (ProductDto dto : filteredProductList) {
			wrappedDtos.add(new ProductDtoWrapper(dto));
		}

		CustomTableModel<ProductDtoWrapper> model = new CustomTableModel<ProductDtoWrapper>(wrappedDtos);

		return model;
	}

	private CustomTableModel<ProductDtoWrapper> initialJTableProductList() {
		ProductService service = ServiceFactory.createProductService();
		List<ProductDto> dtos = service.getAllProducts();

		List<ProductDtoWrapper> wrappedDtos = new ArrayList<ProductDtoWrapper>();
		for (ProductDto dto : dtos) {
			wrappedDtos.add(new ProductDtoWrapper(dto));
		}

		CustomTableModel<ProductDtoWrapper> model = new CustomTableModel<ProductDtoWrapper>(wrappedDtos);

		return model;
	}

	private void showError(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private void showErrorForAllInvalidFields() {
		boolean isValidPostalCode = checkConstrainsOfButton(txtClientPostalCode.getName());
		boolean isValidPhone = checkConstrainsOfButton(txtClientPhone.getName());
		boolean isValidClientName = checkConstrainsOfButton(txtClientName.getName());
		boolean isValidClientDni = checkConstrainsOfButton(txtClientDni.getName());
		boolean isValidAddress = checkConstrainsOfButton(txtClientAddress.getName());

		if (!isValidPostalCode)
			JOptionPane.showMessageDialog(this,
					"El código postal para el nuevo cliente no es válido, el numero máximo es 99999",
					"Error creación presupuesto", JOptionPane.ERROR_MESSAGE);
		if (!isValidPhone)
			JOptionPane.showMessageDialog(this,
					"El teléfono para el nuevo cliente no es válido, el teléfono debe ser un numero sin letras",
					"Error creación presupuesto", JOptionPane.ERROR_MESSAGE);
		if (!isValidClientName)
			JOptionPane.showMessageDialog(this,
					"El nombre para el nuevo cliente no es válido, el nombre no puede estar vacio ni puede contener dígitos",
					"Error creación presupuesto", JOptionPane.ERROR_MESSAGE);
		if (!isValidClientDni)
			JOptionPane.showMessageDialog(this,
					"El DNI para el nuevo cliente no es válido, el DNI debe seguir la estructura \"NumeroNumero...Letra",
					"Error creación presupuesto", JOptionPane.ERROR_MESSAGE);
		if (!isValidAddress)
			JOptionPane.showMessageDialog(this,
					"La dirreción para el nuevo cliente no es válida, la dirección no puede estar vacía",
					"Error creación presupuesto", JOptionPane.ERROR_MESSAGE);

	}

	private void showEverythingOk(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);

	}

	private int askForPermission(String message, String title) {
		return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	private class CheckRadioButtonsChecked implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isCheckedUseClient = chbxUseClient.isSelected();
			boolean isCheckedUseExistingClient = chbxUseExistingClient.isSelected();

			if (isCheckedUseExistingClient && !wasCheckedUseExistingClient) {
				isCheckedUseClient = true;
				chbxUseClient.setSelected(isCheckedUseClient);
			} else if (!isCheckedUseClient && wasCheckedUseClient) {
				isCheckedUseExistingClient = false;
				chbxUseExistingClient.setSelected(isCheckedUseExistingClient);
			}

			txtClientPostalCode.setEditable(isCheckedUseClient && !isCheckedUseExistingClient);
			txtClientPhone.setEditable(isCheckedUseClient && !isCheckedUseExistingClient);
			txtClientName.setEditable(isCheckedUseClient && !isCheckedUseExistingClient);
			txtClientDni.setEditable(isCheckedUseClient && !isCheckedUseExistingClient);
			txtClientAddress.setEditable(isCheckedUseClient && !isCheckedUseExistingClient);
			listExistingClients.setEnabled(isCheckedUseClient && isCheckedUseExistingClient);

			if (!isCheckedUseClient) {
				txtClientAddress.setText("");
				txtClientDni.setText("");
				txtClientName.setText("");
				txtClientPhone.setText("");
				txtClientPostalCode.setText("");

				chbxUseExistingClient.setSelected(false);
			}

			listExistingClients.clearSelection();

			wasCheckedUseClient = isCheckedUseClient;
			wasCheckedUseExistingClient = isCheckedUseExistingClient;
		}

	}

	private JTable getTblProductList() {
		if (tblProductList == null) {
			CustomTableModel<ProductDtoWrapper> model = initialJTableProductList();
			tblProductList = new CustomJTable<ProductDtoWrapper>(model);
			tblProductList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			tblProductList.setName(parameters.getProperty("ACTION_COMMAND_TBL_PRODUCT_TABLE"));
			tblProductList.addMouseListener(dcotL);
		}
		return tblProductList;
	}

	private JTable getTblBudgetProductList() {
		if (tblBudgetProductList == null) {
			CustomTableModel<ProductDtoWrapper> model = initialJTableBudgetProductList();
			tblBudgetProductList = new CustomJTable<ProductDtoWrapper>(model);
			tblBudgetProductList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tblBudgetProductList.setName(parameters.getProperty("ACTION_COMMAND_TBL_PRODUCTS_ON_BUDGET_TABLE"));

			tblBudgetProductList.addMouseListener(dcotL);
		}
		return tblBudgetProductList;
	}

	private JPanel getPnBudgetButtons() {
		if (pnBudgetButtons == null) {
			pnBudgetButtons = new JPanel();
			GridBagLayout gbl_pnBudgetButtons = new GridBagLayout();
			gbl_pnBudgetButtons.columnWidths = new int[] { 0, 0, 0, 0 };
			gbl_pnBudgetButtons.rowHeights = new int[] { 46, 0 };
			gbl_pnBudgetButtons.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
			gbl_pnBudgetButtons.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			pnBudgetButtons.setLayout(gbl_pnBudgetButtons);
			GridBagConstraints gbc_pnLblDeleteProductButton = new GridBagConstraints();
			gbc_pnLblDeleteProductButton.anchor = GridBagConstraints.SOUTHEAST;
			gbc_pnLblDeleteProductButton.insets = new Insets(0, 0, 0, 5);
			gbc_pnLblDeleteProductButton.gridx = 0;
			gbc_pnLblDeleteProductButton.gridy = 0;
			pnBudgetButtons.add(getPnLblDeleteProductButton(), gbc_pnLblDeleteProductButton);
			GridBagConstraints gbc_pnButtons = new GridBagConstraints();
			gbc_pnButtons.insets = new Insets(0, 0, 0, 5);
			gbc_pnButtons.fill = GridBagConstraints.BOTH;
			gbc_pnButtons.gridx = 1;
			gbc_pnButtons.gridy = 0;
			pnBudgetButtons.add(getPnButtons(), gbc_pnButtons);
			GridBagConstraints gbc_pnLblAddProductButton = new GridBagConstraints();
			gbc_pnLblAddProductButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_pnLblAddProductButton.gridx = 2;
			gbc_pnLblAddProductButton.gridy = 0;
			pnBudgetButtons.add(getPnLblAddProductButton(), gbc_pnLblAddProductButton);
		}
		return pnBudgetButtons;
	}

	private JLabel getLblAddProductButton() {
		if (lblAddProductButton == null) {
			lblAddProductButton = new JLabel("=>");
		}
		return lblAddProductButton;
	}

	private JPanel getPnLblDeleteProductButton() {
		if (pnLblDeleteProductButton == null) {
			pnLblDeleteProductButton = new JPanel();
			pnLblDeleteProductButton.add(getLblDeleteProductButton());
		}
		return pnLblDeleteProductButton;
	}

	private JPanel getPnLblAddProductButton() {
		if (pnLblAddProductButton == null) {
			pnLblAddProductButton = new JPanel();
			pnLblAddProductButton.add(getLblAddProductButton());
		}
		return pnLblAddProductButton;
	}

	private JLabel getLblDeleteProductButton() {
		if (lblDeleteProductButton == null) {
			lblDeleteProductButton = new JLabel("<=");
		}
		return lblDeleteProductButton;
	}

	private class DoubleClickOnTablelListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			checkForDoubleClickOnTables(e.getComponent().getName(), e);
		}

	}

	private void checkForDoubleClickOnTables(String name, MouseEvent e) {
		spinnerProductQuantity.setEnabled(true);
		if (name.equals(parameters.getProperty("ACTION_COMMAND_TBL_PRODUCT_TABLE")) && e.getClickCount() == 2
				&& tblProductList.getSelectedRow() != -1) {
			addEntryToTable(tblProductList.getSelectedRow());
		} else if (name.equals(parameters.getProperty("ACTION_COMMAND_TBL_PRODUCTS_ON_BUDGET_TABLE"))
				&& e.getClickCount() == 2 && tblBudgetProductList.getSelectedRow() != -1) {
			deleteEntryFromTable(tblBudgetProductList.getSelectedRow(), true);
		}

	}

	public void setFilteredProductList(List<ProductDto> filteredProductList) {
		this.filteredProductList = filteredProductList;
		CustomTableModel<ProductDtoWrapper> filteredModel = filteredJTableProductList();
		tblProductList.setModel(filteredModel);

	}

	private JButton getBtnFiltrarAlmacen() {
		if (btnFiltrarAlmacen == null) {
			btnFiltrarAlmacen = new JButton("Filtrar Almacén");
			btnFiltrarAlmacen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createFilterWindow();
				}
			});
		}
		return btnFiltrarAlmacen;
	}

	private void createFilterWindow() {
		filterDialog = new FilterProducts(this);
		filterDialog.setLocationRelativeTo(getParent());
		filterDialog.setVisible(true);
	}

	private JPanel getPnSpinnerForProductQuantity() {
		if (pnSpinnerForProductQuantity == null) {
			pnSpinnerForProductQuantity = new JPanel();
			pnSpinnerForProductQuantity.setBorder(null);
			pnSpinnerForProductQuantity.setLayout(new GridLayout(0, 1, 0, 0));
			pnSpinnerForProductQuantity.add(getLblSpinnerProductQuantity());
			pnSpinnerForProductQuantity.add(getSpinnerProductQuantity());
		}
		return pnSpinnerForProductQuantity;
	}

	private JSpinner getSpinnerProductQuantity() {
		if (spinnerProductQuantity == null) {
			spinnerProductQuantity = new JSpinner();
			spinnerProductQuantity.setModel(
					new SpinnerNumberModel(DEFAULT_QUANTITY_SPINNER_VALUE, new Integer(1), null, new Integer(1)));
			spinnerProductQuantity.setEnabled(false);
		}
		return spinnerProductQuantity;
	}

	private JLabel getLblSpinnerProductQuantity() {
		if (lblSpinnerProductQuantity == null) {
			lblSpinnerProductQuantity = new JLabel(
					"Cantidad del producto seleccionado a añadir o unidades a borrar del presupuesto:");
		}
		return lblSpinnerProductQuantity;
	}

	public void reset() {
		resetToCreateNewBudget();
	}
}
