package ui.salesHistory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dtos.ReturnDto;
import factories.ServiceFactory;
import ui.extraClasses.NotEditableTableModel;
import ui.main.MainWindow;
import wrappers.ProductDtoWrapper;

public class ProductReturnsDialog extends JDialog {

	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 7413373927485257809L;

	// Fields
	// ------------------------------------------------------------------------

	private SalesHistoryPanel invoker;
	private ProductReturnPanel productReturnsPanel;
	private NotEditableTableModel saleProductsModel, returnedProductsModel;

	private Object[] columnNames;

	// Constructors
	// ------------------------------------------------------------------------

	public ProductReturnsDialog(SalesHistoryPanel invoker) {
		this.invoker = invoker;
		productReturnsPanel = new ProductReturnPanel();
		setModal(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/resources/appicon.png")));
		setTitle("Mueblerías Pepín: Devolución de productos de una venta");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		setContentPane(productReturnsPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnCancelReturn = new JButton("Cancelar");
				btnCancelReturn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showConfirmationDialogForCancelReturn();
					}
				});
				btnCancelReturn.setActionCommand("Cancel");
				buttonPane.add(btnCancelReturn);
			}
			{
				JButton btnAcceptReturn = new JButton("Aceptar devolución");
				btnAcceptReturn.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						showConfirmationDialogForAcceptReturns();
					}

				});
				btnAcceptReturn.setActionCommand("OK");
				buttonPane.add(btnAcceptReturn);
				getRootPane().setDefaultButton(btnAcceptReturn);
			}
		}

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				showConfirmationDialogForCancelReturn();
			}

		});

		columnNames = getProductsColumnNames();
		returnedProductsModel = new NotEditableTableModel(columnNames, 0);
		productReturnsPanel.tProductsToReturn.setModel(returnedProductsModel);

		populateTableWithSaleProducts();
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private Object[] getProductsColumnNames() {
		String[] columnNames = ServiceFactory.createProductService().getColumnNames();

		List<String> extendedColumnNames = new ArrayList<String>();
		for (String each : columnNames) {
			extendedColumnNames.add(each);
		}
		extendedColumnNames.set(1, "PRECIO");
		extendedColumnNames.add("CANTIDAD");

		return extendedColumnNames.toArray();
	}

	private void populateTableWithSaleProducts() {
		saleProductsModel = new NotEditableTableModel(columnNames, 0);
		Vector<String> data;
		if (invoker.getReturnedProductsForSale().isEmpty()) {
			for (ProductDtoWrapper each : invoker.getProductsInSelectedSale()) {
				data = new Vector<String>();
				data.add(each.getDto().name);
				data.add(String.valueOf(each.getDto().publicPrice));
				data.add(each.getDto().category);
				data.add(String.valueOf(each.quantityOrdered));
				saleProductsModel.addRow(data);
			}
		} else {
			for (ProductDtoWrapper productWrapperDto : invoker.getProductsInSelectedSale()) {
				if (thisProductIsReturned(productWrapperDto)) {
					ReturnDto returnDto = getReturnedProduct(productWrapperDto);
					data = new Vector<String>();
					data.add(productWrapperDto.getDto().name);
					data.add(String.valueOf(productWrapperDto.getDto().publicPrice));
					data.add(productWrapperDto.getDto().category);
					data.add(String.valueOf(productWrapperDto.quantityOrdered - returnDto.quantity));
					if (productWrapperDto.quantityOrdered - returnDto.quantity > 0) {
						saleProductsModel.addRow(data);
					}
				} else {
					data = new Vector<String>();
					data.add(productWrapperDto.getDto().name);
					data.add(String.valueOf(productWrapperDto.getDto().publicPrice));
					data.add(productWrapperDto.getDto().category);
					data.add(String.valueOf(productWrapperDto.quantityOrdered));
					saleProductsModel.addRow(data);
				}
			}
		}
		productReturnsPanel.tProductsInSale.setModel(saleProductsModel);
	}

	private ReturnDto getReturnedProduct(ProductDtoWrapper productWrapperDto) {
		for (ReturnDto each : invoker.getReturnedProductsForSale()) {
			if (each.productId == productWrapperDto.getDto().id) {
				return each;
			}
		}
		return null;
	}

	private boolean thisProductIsReturned(ProductDtoWrapper productWrapperDto) {
		for (ReturnDto each : invoker.getReturnedProductsForSale()) {
			if (each.productId == productWrapperDto.getDto().id) {
				return true;
			}
		}
		return false;
	}

	private void showConfirmationDialogForAcceptReturns() {
		if (returnedProductsModel.getDataVector().isEmpty()) {
			JOptionPane.showMessageDialog(this, "No hay productos seleccionados para devolver",
					"Error: No se han indicado los productos a devolver", JOptionPane.WARNING_MESSAGE);
		} else {
			if (String.valueOf(productReturnsPanel.cbReasons.getSelectedItem()).equalsIgnoreCase("Otra")
					&& (productReturnsPanel.txtAreaOtherReason.getText().isEmpty()
							|| productReturnsPanel.txtAreaOtherReason.getText()
									.equalsIgnoreCase("Escriba aqui su razon"))) {
				JOptionPane.showMessageDialog(this, "Especifica el motivo de la devolucion");
			} else {
				int option = JOptionPane.showConfirmDialog(this,
						"Se devolverán los productos indicados.\n¿Quieres continuar?", "Aceptar devolución",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					String reason = "";
					if (String.valueOf(productReturnsPanel.cbReasons.getSelectedItem()).equalsIgnoreCase("Otra")) {
						reason = productReturnsPanel.txtAreaOtherReason.getText();
					} else {
						reason = String.valueOf(productReturnsPanel.cbReasons.getSelectedItem());
					}
					invoker.registerProductReturns(returnedProductsModel, reason);
					dispose();
				}
			}
		}
	}

	private void showConfirmationDialogForCancelReturn() {
		if (returnedProductsModel.getDataVector().isEmpty()) {
			dispose();
		} else {
			int option = JOptionPane.showConfirmDialog(this,
					"No se guardarán los progresos de la devolución.\n¿Quieres continuar?", "Cancelar devolución",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dispose();
			}
		}
	}

	// Content pane
	// ------------------------------------------------------------------------

	private class ProductReturnPanel extends JPanel {

		// Constants
		// ------------------------------------------------------------------------

		private static final long serialVersionUID = -3322816513674850545L;

		// Fields for the components of the panel
		// ------------------------------------------------------------------------

		private JPanel pnReturnsForSale;
		private JPanel pnProductsInSale;
		private JPanel pnProductsToReturn;
		private JPanel pnTitle;
		private JLabel lblReturnsPanelTitle;
		private JScrollPane spProductsInSale;
		private JScrollPane spProductsToReturn;
		private JTable tProductsInSale;
		private JTable tProductsToReturn;
		private JButton btnMoveProduct;
		private JButton btnMoveUnits;
		private JPanel pnActionButtons;
		private JButton btnMoveAllSaleToReturn;
		private JButton btnMoveAllReturnToSale;
		private JComboBox<String> cbReasons;
		private JScrollPane spOtherReason;
		private JTextArea txtAreaOtherReason;
		private JLabel lblOtherReason;

		// Constructor for the panel
		// ------------------------------------------------------------------------

		private ProductReturnPanel() {
			setLayout(new BorderLayout(0, 0));
			add(getPnTitle(), BorderLayout.NORTH);
			add(getPnReturnsForSale());
		}

		// Getters for the components of the panel
		// ------------------------------------------------------------------------

		private JPanel getPnReturnsForSale() {
			if (pnReturnsForSale == null) {
				pnReturnsForSale = new JPanel();
				GridBagLayout gbl_pnReturnsForSale = new GridBagLayout();
				gbl_pnReturnsForSale.columnWidths = new int[] { 278, 156, 296, 0 };
				gbl_pnReturnsForSale.rowHeights = new int[] { 550, 0, 0, 120, 0, 0 };
				gbl_pnReturnsForSale.columnWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
				gbl_pnReturnsForSale.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
				pnReturnsForSale.setLayout(gbl_pnReturnsForSale);
				GridBagConstraints gbc_pnProductsInSale = new GridBagConstraints();
				gbc_pnProductsInSale.gridheight = 5;
				gbc_pnProductsInSale.weighty = 0.1;
				gbc_pnProductsInSale.fill = GridBagConstraints.BOTH;
				gbc_pnProductsInSale.insets = new Insets(0, 0, 0, 5);
				gbc_pnProductsInSale.gridx = 0;
				gbc_pnProductsInSale.gridy = 0;
				pnReturnsForSale.add(getPnProductsInSale(), gbc_pnProductsInSale);
				GridBagConstraints gbc_pnActionButtons = new GridBagConstraints();
				gbc_pnActionButtons.insets = new Insets(0, 0, 5, 5);
				gbc_pnActionButtons.fill = GridBagConstraints.HORIZONTAL;
				gbc_pnActionButtons.gridx = 1;
				gbc_pnActionButtons.gridy = 0;
				pnReturnsForSale.add(getPnActionButtons(), gbc_pnActionButtons);
				GridBagConstraints gbc_pnProductsToReturn = new GridBagConstraints();
				gbc_pnProductsToReturn.gridheight = 5;
				gbc_pnProductsToReturn.weighty = 0.1;
				gbc_pnProductsToReturn.anchor = GridBagConstraints.WEST;
				gbc_pnProductsToReturn.fill = GridBagConstraints.BOTH;
				gbc_pnProductsToReturn.gridx = 2;
				gbc_pnProductsToReturn.gridy = 0;
				pnReturnsForSale.add(getPnProductsToReturn(), gbc_pnProductsToReturn);
				GridBagConstraints gbc_lblEscojaLaRazn = new GridBagConstraints();
				gbc_lblEscojaLaRazn.insets = new Insets(0, 0, 5, 5);
				gbc_lblEscojaLaRazn.gridx = 1;
				gbc_lblEscojaLaRazn.gridy = 1;
				pnReturnsForSale.add(getLblOtherReason(), gbc_lblEscojaLaRazn);
				GridBagConstraints gbc_cbReasons = new GridBagConstraints();
				gbc_cbReasons.insets = new Insets(0, 0, 5, 5);
				gbc_cbReasons.fill = GridBagConstraints.HORIZONTAL;
				gbc_cbReasons.gridx = 1;
				gbc_cbReasons.gridy = 2;
				pnReturnsForSale.add(getCbReasons(), gbc_cbReasons);
				GridBagConstraints gbc_spOtherReason = new GridBagConstraints();
				gbc_spOtherReason.insets = new Insets(0, 0, 5, 5);
				gbc_spOtherReason.fill = GridBagConstraints.BOTH;
				gbc_spOtherReason.gridx = 1;
				gbc_spOtherReason.gridy = 3;
				pnReturnsForSale.add(getSpOtherReason(), gbc_spOtherReason);
			}
			return pnReturnsForSale;
		}

		private JPanel getPnProductsInSale() {
			if (pnProductsInSale == null) {
				pnProductsInSale = new JPanel();
				pnProductsInSale.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
						"Productos en la venta", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
				pnProductsInSale.setLayout(new BorderLayout(0, 0));
				pnProductsInSale.add(getSpProductsInSale(), BorderLayout.CENTER);
				pnProductsInSale.add(getBtnMoveAllSaleToReturn(), BorderLayout.SOUTH);
			}
			return pnProductsInSale;
		}

		private JPanel getPnProductsToReturn() {
			if (pnProductsToReturn == null) {
				pnProductsToReturn = new JPanel();
				pnProductsToReturn.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
						"Productos a devolver", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
				pnProductsToReturn.setLayout(new BorderLayout(0, 0));
				pnProductsToReturn.add(getSpProductsToReturn(), BorderLayout.CENTER);
				pnProductsToReturn.add(getBtnMoveAllReturnToSale(), BorderLayout.SOUTH);
			}
			return pnProductsToReturn;
		}

		private JPanel getPnTitle() {
			if (pnTitle == null) {
				pnTitle = new JPanel();
				pnTitle.add(getLblReturnsPanelTitle());
			}
			return pnTitle;
		}

		private JLabel getLblReturnsPanelTitle() {
			if (lblReturnsPanelTitle == null) {
				lblReturnsPanelTitle = new JLabel("Tramitar devoluciones");
				lblReturnsPanelTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
			}
			return lblReturnsPanelTitle;
		}

		private JScrollPane getSpProductsInSale() {
			if (spProductsInSale == null) {
				spProductsInSale = new JScrollPane();
				spProductsInSale.setViewportView(getTProductsInSale());
			}
			return spProductsInSale;
		}

		private JScrollPane getSpProductsToReturn() {
			if (spProductsToReturn == null) {
				spProductsToReturn = new JScrollPane();
				spProductsToReturn.setViewportView(getTProductsToReturn());
			}
			return spProductsToReturn;
		}

		private JTable getTProductsInSale() {
			if (tProductsInSale == null) {
				tProductsInSale = new JTable();
				tProductsInSale.getTableHeader().setReorderingAllowed(false);
				tProductsInSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				tProductsInSale.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = getSelectedRowFromSaleProducts();
						if (selectedRow >= 0) {
							tProductsToReturn.clearSelection();
							btnMoveProduct.setEnabled(true);
							btnMoveUnits.setEnabled(true);
						}
					}
				});
				tProductsInSale.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() >= 2) {
							int selectedRow = getSelectedRowFromSaleProducts();
							if (selectedRow >= 0) {
								moveAllUnitsOfSelectedProductToReturn();
								resetTableViews();
							}
						}
					}
				});
			}
			return tProductsInSale;
		}

		private JTable getTProductsToReturn() {
			if (tProductsToReturn == null) {
				tProductsToReturn = new JTable();
				tProductsToReturn.getTableHeader().setReorderingAllowed(false);
				tProductsToReturn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				tProductsToReturn.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int selectedRow = getSelectedRowFromReturnProducts();
						if (selectedRow >= 0) {
							tProductsInSale.clearSelection();
							btnMoveProduct.setEnabled(true);
							btnMoveUnits.setEnabled(true);
						}
					}
				});
				tProductsToReturn.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() >= 2) {
							int selectedRow = getSelectedRowFromReturnProducts();
							if (selectedRow >= 0) {
								moveAllUnitsOfSelectedProductToSale();
								resetTableViews();
							}
						}
					}
				});
			}
			return tProductsToReturn;
		}

		private JButton getBtnMoveProduct() {
			if (btnMoveProduct == null) {
				btnMoveProduct = new JButton("Mover todas las unidades");
				btnMoveProduct.setEnabled(false);
				btnMoveProduct.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (getSelectedRowFromSaleProducts() != -1) {
							moveAllUnitsOfSelectedProductToReturn();
							if (saleProductsModel.getDataVector().isEmpty()) {
								btnMoveAllSaleToReturn.setEnabled(false);
							}
							btnMoveAllReturnToSale.setEnabled(true);
						} else {
							moveAllUnitsOfSelectedProductToSale();
							if (returnedProductsModel.getDataVector().isEmpty()) {
								btnMoveAllReturnToSale.setEnabled(false);
							}
							btnMoveAllSaleToReturn.setEnabled(true);
						}
						resetTableViews();
					}
				});
			}
			return btnMoveProduct;
		}

		private JButton getBtnMoveUnits() {
			if (btnMoveUnits == null) {
				btnMoveUnits = new JButton("Mover unidades...");
				btnMoveUnits.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int totalUnits = askForUnitsToMove();
						if (totalUnits != 0) {
							if (getSelectedRowFromSaleProducts() != -1) {
								moveUnitsOfSelectedProductToReturn(totalUnits);
								if (saleProductsModel.getDataVector().isEmpty()) {
									btnMoveAllSaleToReturn.setEnabled(false);
								}
								btnMoveAllReturnToSale.setEnabled(true);
							} else {
								moveUnitsOfSelectedProductToSale(totalUnits);
								if (returnedProductsModel.getDataVector().isEmpty()) {
									btnMoveAllReturnToSale.setEnabled(false);
								}
								btnMoveAllSaleToReturn.setEnabled(true);
							}
							resetTableViews();
						}
					}
				});
				btnMoveUnits.setEnabled(false);
			}
			return btnMoveUnits;
		}

		private JPanel getPnActionButtons() {
			if (pnActionButtons == null) {
				pnActionButtons = new JPanel();
				pnActionButtons.setLayout(new GridLayout(0, 1, 0, 0));
				pnActionButtons.add(getBtnMoveProduct());
				pnActionButtons.add(getBtnMoveUnits());
			}
			return pnActionButtons;
		}

		private JButton getBtnMoveAllSaleToReturn() {
			if (btnMoveAllSaleToReturn == null) {
				btnMoveAllSaleToReturn = new JButton("Mover todo a la lista de devolucion");
				btnMoveAllSaleToReturn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						moveAllProductsFromSaleToReturn();
						resetTableViews();
					}
				});
			}
			return btnMoveAllSaleToReturn;
		}

		private JButton getBtnMoveAllReturnToSale() {
			if (btnMoveAllReturnToSale == null) {
				btnMoveAllReturnToSale = new JButton("Mover todo a la lista de la venta");
				btnMoveAllReturnToSale.setEnabled(false);
				btnMoveAllReturnToSale.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						moveAllProductsFromReturnToSale();
						resetTableViews();
					}
				});
			}
			return btnMoveAllReturnToSale;
		}

		private JComboBox<String> getCbReasons() {
			if (cbReasons == null) {
				cbReasons = new JComboBox<String>();
				cbReasons.addItem("Pedido por error");
				cbReasons.addItem("Ya no lo necesito");
				cbReasons.addItem("He cambiado de opinion");
				cbReasons.addItem("Otra");
				cbReasons.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (String.valueOf(cbReasons.getSelectedItem()).equalsIgnoreCase("Otra")) {
							txtAreaOtherReason.setEnabled(true);
							txtAreaOtherReason.requestFocus();
						} else {
							if (txtAreaOtherReason != null) {
								txtAreaOtherReason.setText("Escriba aqui su razon");
								txtAreaOtherReason.setEnabled(false);
							}
						}
					}
				});
			}
			return cbReasons;
		}

		private JScrollPane getSpOtherReason() {
			if (spOtherReason == null) {
				spOtherReason = new JScrollPane();
				spOtherReason.setViewportView(getTxtAreaOtherReason());
			}
			return spOtherReason;
		}

		private JTextArea getTxtAreaOtherReason() {
			if (txtAreaOtherReason == null) {
				txtAreaOtherReason = new JTextArea();
				txtAreaOtherReason.setEnabled(false);
				txtAreaOtherReason.setText("Escriba aqui su razon");
				txtAreaOtherReason.addFocusListener(new FocusAdapter() {

					@Override
					public void focusGained(FocusEvent e) {
						JTextArea temp = (JTextArea) e.getSource();
						temp.selectAll();
					}

				});
			}
			return txtAreaOtherReason;
		}

		private JLabel getLblOtherReason() {
			if (lblOtherReason == null) {
				lblOtherReason = new JLabel("Escoja la razón de la devolución:");
			}
			return lblOtherReason;
		}

		// Auxiliary methods
		// --------------------------------------------------------------------

		private void moveAllProductsFromSaleToReturn() {
			if (saleProductsModel.getDataVector().isEmpty() && returnedProductsModel.getDataVector().isEmpty()) {
				JOptionPane.showMessageDialog(this, "No hay ningun producto que mover", "Error: No hay productos",
						JOptionPane.PLAIN_MESSAGE);
			} else if (!saleProductsModel.getDataVector().isEmpty()) {
				while (!saleProductsModel.getDataVector().isEmpty()) {
					tProductsInSale.getSelectionModel().setSelectionInterval(0, 0);
					@SuppressWarnings("unchecked")
					int unitsToMove = Integer
							.parseInt(((Vector<String>) saleProductsModel.getDataVector().get(0)).get(3));
					moveUnitsOfSelectedProductToReturn(unitsToMove);
				}
			}

			btnMoveAllReturnToSale.setEnabled(true);
			btnMoveAllSaleToReturn.setEnabled(false);
		}

		private void moveAllProductsFromReturnToSale() {
			if (returnedProductsModel.getDataVector().isEmpty() && saleProductsModel.getDataVector().isEmpty()) {
				JOptionPane.showMessageDialog(this, "No hay ningun producto que mover", "Error: No hay productos",
						JOptionPane.PLAIN_MESSAGE);
			} else if (!returnedProductsModel.getDataVector().isEmpty()) {
				while (!returnedProductsModel.getDataVector().isEmpty()) {
					tProductsToReturn.getSelectionModel().setSelectionInterval(0, 0);
					@SuppressWarnings("unchecked")
					int unitsToMove = Integer
							.parseInt(((Vector<String>) returnedProductsModel.getDataVector().get(0)).get(3));
					moveUnitsOfSelectedProductToSale(unitsToMove);
				}
			}

			btnMoveAllReturnToSale.setEnabled(false);
			btnMoveAllSaleToReturn.setEnabled(true);
		}

		@SuppressWarnings("unchecked")
		private void moveAllUnitsOfSelectedProductToSale() {
			int totalUnits = Integer.parseInt(
					((Vector<String>) returnedProductsModel.getDataVector().get(getSelectedRowFromReturnProducts()))
							.get(3));
			moveUnitsOfSelectedProductToSale(totalUnits);
			btnMoveAllSaleToReturn.setEnabled(true);
			if (returnedProductsModel.getDataVector().isEmpty()) {
				btnMoveAllReturnToSale.setEnabled(false);
			}
		}

		@SuppressWarnings("unchecked")
		private void moveAllUnitsOfSelectedProductToReturn() {
			int totalUnits = Integer.parseInt(
					((Vector<String>) saleProductsModel.getDataVector().get(getSelectedRowFromSaleProducts())).get(3));
			moveUnitsOfSelectedProductToReturn(totalUnits);
			btnMoveAllReturnToSale.setEnabled(true);
			if (saleProductsModel.getDataVector().isEmpty()) {
				btnMoveAllSaleToReturn.setEnabled(false);
			}
		}

		@SuppressWarnings("unchecked")
		private void moveUnitsOfSelectedProductToSale(int unitsToMove) {
			int selectedRow = getSelectedRowFromReturnProducts();
			Vector<String> productOnSale = (Vector<String>) returnedProductsModel.getDataVector().get(selectedRow);
			int productOnSaleRow = returnedProductsModel.getDataVector().indexOf(productOnSale);
			int unitsAvailable = Integer.parseInt(productOnSale.get(3));

			if (unitsToMove > unitsAvailable) {
				JOptionPane.showMessageDialog(this, "No hay suficientes unidades que mover",
						"Error: demasiadas unidades", JOptionPane.WARNING_MESSAGE);
			} else if (unitsToMove == unitsAvailable) {
				returnedProductsModel.getDataVector().remove(productOnSaleRow);
				Vector<String> productOnReturn = null;
				for (Object each : saleProductsModel.getDataVector().toArray()) {
					Vector<String> temp = (Vector<String>) each;
					if (temp.get(0).equalsIgnoreCase(productOnSale.get(0))) {
						productOnReturn = temp;
					}
				}

				if (productOnReturn == null) {
					saleProductsModel.addRow(productOnSale);
				} else {
					productOnReturn.set(3, String.valueOf(Integer.parseInt(productOnReturn.get(3)) + unitsAvailable));
				}
			} else {
				Vector<String> productOnReturn = null;
				for (Object each : saleProductsModel.getDataVector().toArray()) {
					Vector<String> temp = (Vector<String>) each;
					if (temp.get(0).equalsIgnoreCase(productOnSale.get(0))) {
						productOnReturn = temp;
					}
				}

				if (productOnReturn == null) {
					productOnReturn = new Vector<String>();
					productOnReturn.add(productOnSale.get(0));
					productOnReturn.add(productOnSale.get(1));
					productOnReturn.add(productOnSale.get(2));
					productOnReturn.add(String.valueOf(unitsToMove));
					saleProductsModel.addRow(productOnReturn);
					productOnSale.set(3, String.valueOf(unitsAvailable - unitsToMove));
				} else {
					saleProductsModel.getDataVector().remove(productOnReturn);
					productOnReturn.set(3, String.valueOf(Integer.parseInt(productOnReturn.get(3)) + unitsToMove));
					saleProductsModel.addRow(productOnReturn);
					productOnSale.set(3, String.valueOf(unitsAvailable - unitsToMove));
				}
			}
		}

		@SuppressWarnings("unchecked")
		private void moveUnitsOfSelectedProductToReturn(int unitsToMove) {
			int selectedRow = getSelectedRowFromSaleProducts();
			Vector<String> productOnSale = (Vector<String>) saleProductsModel.getDataVector().get(selectedRow);
			int productOnSaleRow = saleProductsModel.getDataVector().indexOf(productOnSale);
			int unitsAvailable = Integer.parseInt(productOnSale.get(3));

			if (unitsToMove > unitsAvailable) {
				JOptionPane.showMessageDialog(this, "No hay suficientes unidades que mover",
						"Error: demasiadas unidades", JOptionPane.WARNING_MESSAGE);
			} else if (unitsToMove == unitsAvailable) {
				saleProductsModel.getDataVector().remove(productOnSaleRow);
				Vector<String> productOnReturn = null;
				for (Object each : returnedProductsModel.getDataVector().toArray()) {
					Vector<String> temp = (Vector<String>) each;
					if (temp.get(0).equalsIgnoreCase(productOnSale.get(0))) {
						productOnReturn = temp;
					}
				}

				if (productOnReturn == null) {
					returnedProductsModel.addRow(productOnSale);
				} else {
					productOnReturn.set(3, String.valueOf(Integer.parseInt(productOnReturn.get(3)) + unitsAvailable));
				}
			} else {
				Vector<String> productOnReturn = null;
				for (Object each : returnedProductsModel.getDataVector().toArray()) {
					Vector<String> temp = (Vector<String>) each;
					if (temp.get(0).equalsIgnoreCase(productOnSale.get(0))) {
						productOnReturn = temp;
					}
				}

				if (productOnReturn == null) {
					productOnReturn = new Vector<String>();
					productOnReturn.add(productOnSale.get(0));
					productOnReturn.add(productOnSale.get(1));
					productOnReturn.add(productOnSale.get(2));
					productOnReturn.add(String.valueOf(unitsToMove));
					returnedProductsModel.addRow(productOnReturn);
					productOnSale.set(3, String.valueOf(unitsAvailable - unitsToMove));
				} else {
					returnedProductsModel.getDataVector().remove(productOnReturn);
					productOnReturn.set(3, String.valueOf(Integer.parseInt(productOnReturn.get(3)) + unitsToMove));
					returnedProductsModel.addRow(productOnReturn);
					productOnSale.set(3, String.valueOf(unitsAvailable - unitsToMove));
				}
			}
		}

		private int getSelectedRowFromSaleProducts() {
			return tProductsInSale.getSelectedRow();
		}

		private int getSelectedRowFromReturnProducts() {
			return tProductsToReturn.getSelectedRow();
		}

		private int askForUnitsToMove() {
			boolean done = false;
			int units = 0;

			while (!done) {
				String input = JOptionPane.showInputDialog(this,
						"¿Cuantas unidades del producto seleccionado quieres mover?");

				if (input == null || (input != null && ("".equals(input)))) {
					done = false;
					units = 0;
					return units;
				}

				try {
					units = Integer.parseInt(input);
					done = true;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(this, "Por favor, introduce un numero valido");
				}

				if (units < 0) {
					done = false;
					JOptionPane.showMessageDialog(this, "No se pueden mover cantidades negativas");
				}
			}

			return units;
		}

		private void resetTableViews() {
			tProductsInSale.revalidate();
			tProductsToReturn.revalidate();

			for (int i = 0; i < returnedProductsModel.getDataVector().size(); i++) {
				tProductsToReturn.getSelectionModel().setSelectionInterval(i, i);
			}

			for (int i = 0; i < saleProductsModel.getDataVector().size(); i++) {
				tProductsInSale.getSelectionModel().setSelectionInterval(i, i);
			}

			btnMoveProduct.setEnabled(false);
			btnMoveUnits.setEnabled(false);

			tProductsInSale.clearSelection();
			tProductsToReturn.clearSelection();
		}

	}

}
