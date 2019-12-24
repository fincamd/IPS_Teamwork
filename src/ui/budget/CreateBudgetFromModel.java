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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import business.serviceLayer.BudgetService;
import business.serviceLayer.ClientService;
import common.BusinessException;
import common.Conf;
import dtos.BudgetDto;
import dtos.ClientDto;
import dtos.ProductDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import wrappers.BudgetDtoModelWrapper;
import wrappers.ClientDtoWrapper;
import wrappers.ProductDtoInBudgetWrapper;
import wrappers.ProductDtoWrapper;

public class CreateBudgetFromModel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pnBudgetClient;
	private JPanel pnButtons;
	private JSplitPane spModelBudgets;
	private JPanel pnModelBudgets;
	private JPanel pnModelBudgetProducts;
	private JLabel lblModelBudgets;
	private JScrollPane spTblModelBudgets;
	private CustomJTable<BudgetDtoModelWrapper> tblModelBudget;
	private JLabel lblProductosDelPresupuesto;
	private JScrollPane spModelBudgetProducts;
	private CustomJTable<ProductDtoInBudgetWrapper> tblModelBudgetProducts;
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

	private Conf parameters = Conf.getInstance("configs/parameters.properties");
	private JTextField txtClientPhone;
	private JTextField txtClientPostalCode;
	private JPanel pnExistingClientsTitle;
	private JLabel lblExistingClientsTitle;
	private JPanel pnChbxExistingClient;

	private ButtonConstrainListener bcL = new ButtonConstrainListener();
	private JButton btnCreateBudget;

	/**
	 * Create the panel.
	 */
	public CreateBudgetFromModel() {
		setLayout(new BorderLayout(0, 0));
		add(getPnBudgetClient(), BorderLayout.CENTER);
		add(getPnButtons(), BorderLayout.SOUTH);

	}

	public void reset() {
		resetWindow();
	}

	private void resetWindow() {
		chbxUseExistingClient.setSelected(false);
		txtClientAddress.setText("");
		txtClientDni.setText("");
		txtClientName.setText("");
		txtClientPhone.setText("");
		txtClientPostalCode.setText("");
		
		txtClientAddress.setEditable(true);
		txtClientDni.setEditable(true);
		txtClientName.setEditable(true);
		txtClientPhone.setEditable(true);
		txtClientPostalCode.setEditable(true);
		
		listExistingClients.clearSelection();
		btnCreateBudget.setEnabled(false);
		
		tblModelBudget.setModel(initializeModelBudgetModel());
		tblModelBudgetProducts.setModel(initializeModelBudgetProductsModel());
		
		listExistingClients.setModel(initialClientList());
		
		listExistingClients.setEnabled(false);
	}

	private JPanel getPnBudgetClient() {
		if (pnBudgetClient == null) {
			pnBudgetClient = new JPanel();
			GridBagLayout gbl_pnBudgetClient = new GridBagLayout();
			gbl_pnBudgetClient.columnWidths = new int[] { 0, 0 };
			gbl_pnBudgetClient.rowHeights = new int[] { 0, 0, 0 };
			gbl_pnBudgetClient.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_pnBudgetClient.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
			pnBudgetClient.setLayout(gbl_pnBudgetClient);
			GridBagConstraints gbc_spModelBudgets = new GridBagConstraints();
			gbc_spModelBudgets.insets = new Insets(0, 0, 5, 0);
			gbc_spModelBudgets.fill = GridBagConstraints.BOTH;
			gbc_spModelBudgets.gridx = 0;
			gbc_spModelBudgets.gridy = 0;
			pnBudgetClient.add(getSpModelBudgets(), gbc_spModelBudgets);
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 1;
			pnBudgetClient.add(getPnClients(), gbc_panel);
		}
		return pnBudgetClient;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.add(getBtnCreateBudget());
		}
		return pnButtons;
	}

	private JSplitPane getSpModelBudgets() {
		if (spModelBudgets == null) {
			spModelBudgets = new JSplitPane();
			spModelBudgets.setLeftComponent(getPnModelBudgets());
			spModelBudgets.setRightComponent(getPnModelBudgetProducts());
		}
		return spModelBudgets;
	}

	private JPanel getPnModelBudgets() {
		if (pnModelBudgets == null) {
			pnModelBudgets = new JPanel();
			pnModelBudgets.setLayout(new BorderLayout(0, 0));
			pnModelBudgets.add(getLblModelBudgets(), BorderLayout.NORTH);
			pnModelBudgets.add(getSpTblModelBudgets(), BorderLayout.CENTER);
		}
		return pnModelBudgets;
	}

	private JPanel getPnModelBudgetProducts() {
		if (pnModelBudgetProducts == null) {
			pnModelBudgetProducts = new JPanel();
			pnModelBudgetProducts.setLayout(new BorderLayout(0, 0));
			pnModelBudgetProducts.add(getLblProductosDelPresupuesto(), BorderLayout.NORTH);
			pnModelBudgetProducts.add(getSpModelBudgetProducts(), BorderLayout.CENTER);
		}
		return pnModelBudgetProducts;
	}

	private JLabel getLblModelBudgets() {
		if (lblModelBudgets == null) {
			lblModelBudgets = new JLabel("Presupuestos Modelo");
			lblModelBudgets.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblModelBudgets;
	}

	private JScrollPane getSpTblModelBudgets() {
		if (spTblModelBudgets == null) {
			spTblModelBudgets = new JScrollPane();
			spTblModelBudgets.setViewportView(getTblModelBudget());
		}
		return spTblModelBudgets;
	}

	private JTable getTblModelBudget() {
		if (tblModelBudget == null) {
			CustomTableModel<BudgetDtoModelWrapper> model = initializeModelBudgetModel();
			tblModelBudget = new CustomJTable<>(model);
			tblModelBudget.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			tblModelBudget.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					manageMouseClickOnInitalTable();
					updateAcceptButton();
				}
			});
		}
		return tblModelBudget;
	}

	private void manageMouseClickOnInitalTable() {
		int row = tblModelBudget.getSelectedRow();
		if (row >= 0) {
			BudgetService service = ServiceFactory.createBudgetService();
			try {
				int budgetId = tblModelBudget.getCustomModel().getValueAtRow(row).getDto().id;
				List<ProductDtoWrapper> productsInBudget = service.getProductsInBudget(budgetId);
				List<ProductDtoInBudgetWrapper> modelData = new ArrayList<>();

				for (ProductDtoWrapper wrapped : productsInBudget) {
					ProductDto dto = wrapped.getDto();
					int quantityOnBudget = service.getOrderedQuantityForProductOnBudget(budgetId, dto.id);
					modelData.add(new ProductDtoInBudgetWrapper(dto, quantityOnBudget, budgetId));
				}
				CustomTableModel<ProductDtoInBudgetWrapper> newModel = new CustomTableModel<>(modelData);
				tblModelBudgetProducts.setModel(newModel);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}

	private CustomTableModel<BudgetDtoModelWrapper> initializeModelBudgetModel() {
		List<BudgetDtoModelWrapper> toIntroduce = getAllModelBudgets();
		return new CustomTableModel<>(toIntroduce);
	}

	private List<BudgetDtoModelWrapper> getAllModelBudgets() {
		BudgetService service = ServiceFactory.createBudgetService();
		List<BudgetDto> dtos = service.getModelBudgets();

		List<BudgetDtoModelWrapper> res = new ArrayList<>();
		for (BudgetDto dto : dtos) {
			res.add(new BudgetDtoModelWrapper(dto));
		}
		return res;
	}

	private JLabel getLblProductosDelPresupuesto() {
		if (lblProductosDelPresupuesto == null) {
			lblProductosDelPresupuesto = new JLabel("Productos del Presupuesto Modelo Seleccionado");
			lblProductosDelPresupuesto.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblProductosDelPresupuesto;
	}

	private JScrollPane getSpModelBudgetProducts() {
		if (spModelBudgetProducts == null) {
			spModelBudgetProducts = new JScrollPane();
			spModelBudgetProducts.setViewportView(getTblModelBudgetProducts());
		}
		return spModelBudgetProducts;
	}

	private JTable getTblModelBudgetProducts() {
		if (tblModelBudgetProducts == null) {
			CustomTableModel<ProductDtoInBudgetWrapper> model = initializeModelBudgetProductsModel();
			tblModelBudgetProducts = new CustomJTable<>(model);

			tblModelBudgetProducts.setRowSelectionAllowed(false);
		}
		return tblModelBudgetProducts;
	}

	private CustomTableModel<ProductDtoInBudgetWrapper> initializeModelBudgetProductsModel() {
		CustomTableModel<ProductDtoInBudgetWrapper> model = new CustomTableModel<>();
		return model;
	}

	private JPanel getPnClients() {
		if (pnClients == null) {
			pnClients = new JPanel();
			GridBagLayout gbl_pnClients = new GridBagLayout();
			gbl_pnClients.columnWidths = new int[] { 236, 258, 0 };
			gbl_pnClients.rowHeights = new int[] { 0, 154, 0 };
			gbl_pnClients.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnClients.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
			pnClients.setLayout(gbl_pnClients);
			GridBagConstraints gbc_pnChbxExistingClient = new GridBagConstraints();
			gbc_pnChbxExistingClient.gridwidth = 2;
			gbc_pnChbxExistingClient.insets = new Insets(0, 0, 5, 0);
			gbc_pnChbxExistingClient.fill = GridBagConstraints.BOTH;
			gbc_pnChbxExistingClient.gridx = 0;
			gbc_pnChbxExistingClient.gridy = 0;
			pnClients.add(getPnChbxExistingClient(), gbc_pnChbxExistingClient);
			GridBagConstraints gbc_pnNewClient = new GridBagConstraints();
			gbc_pnNewClient.anchor = GridBagConstraints.WEST;
			gbc_pnNewClient.insets = new Insets(0, 0, 0, 5);
			gbc_pnNewClient.gridx = 0;
			gbc_pnNewClient.gridy = 1;
			pnClients.add(getPnNewClient(), gbc_pnNewClient);
			GridBagConstraints gbc_pnExistingClients = new GridBagConstraints();
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
			chbxUseExistingClient.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					boolean isCheckedUseExistingClient = chbxUseExistingClient.isSelected();

					listExistingClients.setEnabled(isCheckedUseExistingClient);

					txtClientDni.setEditable(!isCheckedUseExistingClient);
					txtClientName.setEditable(!isCheckedUseExistingClient);
					txtClientPhone.setEditable(!isCheckedUseExistingClient);
					txtClientPostalCode.setEditable(!isCheckedUseExistingClient);
					txtClientAddress.setEditable(!isCheckedUseExistingClient);

					txtClientDni.setText("");
					txtClientName.setText("");
					txtClientPhone.setText("");
					txtClientPostalCode.setText("");
					txtClientAddress.setText("");

					listExistingClients.clearSelection();

					updateAcceptButton();
				}
			});
		}
		return chbxUseExistingClient;
	}

	private void updateAcceptButton() {
		boolean isCheckedUseExistingClient = chbxUseExistingClient.isSelected();

		if (!isCheckedUseExistingClient) {
			if (tblModelBudget.getSelectedRow() >= 0) {
				btnCreateBudget.setEnabled(true);
			} else {
				btnCreateBudget.setEnabled(false);
			}
		} else {
			if (tblModelBudget.getSelectedRow() >= 0 && listExistingClients.getSelectedIndex() >= 0) {
				btnCreateBudget.setEnabled(true);
			} else {
				btnCreateBudget.setEnabled(false);
			}
		}
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
			listExistingClients.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					updateAcceptButton();
				}
			});
			listExistingClients.setEnabled(false);
		}
		return listExistingClients;
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
			pnChbxExistingClient.add(getChbxUseExistingClient());
		}
		return pnChbxExistingClient;
	}

	private class ButtonConstrainListener extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			checkConstrainsOfButton(e.getComponent().getName());
		}
	}

	private JButton getBtnCreateBudget() {
		if (btnCreateBudget == null) {
			btnCreateBudget = new JButton("Crear Presupuesto A Partir De Modelo Seleccionado");
			btnCreateBudget.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					createBudget();
				}
			});
			btnCreateBudget.setEnabled(false);
		}
		return btnCreateBudget;
	}

	private void createBudget() {
		if (JOptionPane.showConfirmDialog(this, "¿Está seguro que quiere crear un nuevo presupuesto?",
				"Crear presupuesto", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			boolean succesfulCreation = false;
			BudgetService budgetService = ServiceFactory.createBudgetService();

			BudgetDto dtoBudget = new BudgetDto();
			dtoBudget.id = 0;

			ZoneId zone = ZoneId.of(parameters.getProperty("ES_LOCALE"));
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(parameters.getProperty("DATE_FORMAT"));
			LocalDate nowDate = LocalDate.now(zone);
			String creation_date = nowDate.format(dateFormat);
			dtoBudget.creationDate = creation_date;

			LocalDate nextDate = nowDate.plusDays(Integer.parseInt(parameters.getProperty("DAYS_TO_EXPIRE_BUDGET")));
			String expiration_date = nextDate.format(dateFormat);
			dtoBudget.expirationDate = expiration_date;
			
			dtoBudget.status = "NO ACEPTADO";

			List<ProductDtoInBudgetWrapper> productsOfBudget = tblModelBudgetProducts.getCustomModel()
					.getDataReferences();
			
			List<ProductDtoWrapper> dtosProduct = new ArrayList<>();

			for(ProductDtoInBudgetWrapper wrapped : productsOfBudget) {
				ProductDtoWrapper newWrapped = new ProductDtoWrapper(wrapped.getDto());
				newWrapped.quantityOrdered = wrapped.getQuantityOnBudget();
				dtosProduct.add(newWrapped);
				
			}
			if (chbxUseExistingClient.isSelected()) {
				dtoBudget.clientId = listExistingClients.getModel().getElementAt(listExistingClients.getSelectedIndex())
						.getDto().id;

				succesfulCreation = budgetService.createBudgetWithoutClientOrExistingClient(dtoBudget, dtosProduct);
			} else {
				boolean isValidPostalCode = checkConstrainsOfButton(txtClientPostalCode.getName());
				boolean isValidPhone = checkConstrainsOfButton(txtClientPhone.getName());
				boolean isValidClientName = checkConstrainsOfButton(txtClientName.getName());
				boolean isValidClientDni = checkConstrainsOfButton(txtClientDni.getName());
				boolean isValidAddress = checkConstrainsOfButton(txtClientAddress.getName());

				if (isValidPostalCode && isValidPhone && isValidClientName && isValidClientDni && isValidAddress) {
					ClientDto dtoClient = new ClientDto();
					dtoClient.id = 0;
					dtoClient.street = txtClientAddress.getText();
					dtoClient.dni = txtClientDni.getText();
					dtoClient.name = txtClientName.getText();
					dtoClient.phoneNumber = Integer.parseInt(txtClientPhone.getText());
					dtoClient.postCode = txtClientPostalCode.getText();

					dtoBudget.clientId = dtoClient.id;
					succesfulCreation = budgetService.createBudgetWithNewClient(dtoBudget, dtosProduct, dtoClient);
				} else {
					showErrorForAllInvalidFields();
				}
			}
			
			if(succesfulCreation) {
				showEverythingOk("El presupuesto se ha creado sin problemas", "Exito creacion de presupuesto");
				resetWindow();
			} else {
				showError(
						"Ha ocurrido un error al crear el presupuesto",
						"Error al crear presupuesto");
			}
		}
	}
	
	private void showEverythingOk(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);

	}
}
