package ui.priceChange;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import business.serviceLayer.BudgetService;
import business.serviceLayer.ProductService;
import dtos.ProductDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTable;
import ui.customtableUtility.CustomTableModel;
import wrappers.ProductDtoWrapperLessInfo;

public class PublicPriceChange extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblTitle;
	private JSplitPane spltMain;
	private JPanel pnProductList;
	private JSplitPane splitPane;
	private JPanel pnSetNewPrice;
	private JPanel pnListOfPoductPriceToAccept;
	private JScrollPane spProductTable;
	private CustomJTable<ProductDtoWrapperLessInfo> tblAllProducts;
	private JLabel lblAllProducts;
	private JLabel lblChangeProductPrice;
	private JPanel pnNewPrice;
	private JLabel lblActualPrice;
	private JTextField txtName;
	private JTextField txtActualPrice;
	private JTextField txtNewPrice;
	private JLabel lblName;
	private JScrollPane spProductsToUpdateTable;
	private CustomJTable<ProductDtoWrapperLessInfo> tblProductsToUpdate;
	private JPanel pnAddToBatch;
	private JButton btnIntroduceToChangeList;
	private JLabel lblProductosDespusDe;
	private JPanel pnBtnMenuNewProducts;
	private JButton btnAcceptNewPrices;
	private JButton btnDeleteFromChangeList;
	private JLabel lblNewPrice;

	private ProductDtoWrapperLessInfo currentPossibleNewPrice;
	private JPanel pnListProductToAcceptWithChange;
	private JPanel pnChangePrice;
	private JTextField txtNewPriceUpdate;
	private JButton btnNewPriceUpdate;

	/**
	 * Create the panel.
	 */
	public PublicPriceChange() {
		setLayout(new BorderLayout(0, 0));
		add(getLblTitle(), BorderLayout.NORTH);
		add(getSpltMain(), BorderLayout.CENTER);

	}

	private JLabel getLblTitle() {
		if (lblTitle == null) {
			lblTitle = new JLabel("Cambio del precio de los productos");
			lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblTitle;
	}

	private JSplitPane getSpltMain() {
		if (spltMain == null) {
			spltMain = new JSplitPane();
			spltMain.setLeftComponent(getPnProductList());
			spltMain.setRightComponent(getSplitPane());
		}
		return spltMain;
	}

	private JPanel getPnProductList() {
		if (pnProductList == null) {
			pnProductList = new JPanel();
			pnProductList.setLayout(new BorderLayout(0, 0));
			pnProductList.add(getSpProductTable(), BorderLayout.CENTER);
			pnProductList.add(getLblAllProducts(), BorderLayout.NORTH);
		}
		return pnProductList;
	}

	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane();
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(getPnSetNewPrice());
			splitPane.setRightComponent(getPnListOfPoductPriceToAccept());
		}
		return splitPane;
	}

	private JPanel getPnSetNewPrice() {
		if (pnSetNewPrice == null) {
			pnSetNewPrice = new JPanel();
			pnSetNewPrice.setLayout(new BorderLayout(0, 0));
			pnSetNewPrice.add(getLblChangeProductPrice(), BorderLayout.NORTH);
			pnSetNewPrice.add(getPnNewPrice(), BorderLayout.CENTER);
			pnSetNewPrice.add(getPnAddToBatch(), BorderLayout.SOUTH);
		}
		return pnSetNewPrice;
	}

	private JPanel getPnListOfPoductPriceToAccept() {
		if (pnListOfPoductPriceToAccept == null) {
			pnListOfPoductPriceToAccept = new JPanel();
			pnListOfPoductPriceToAccept.setLayout(new BorderLayout(0, 0));
			pnListOfPoductPriceToAccept.add(getLblProductosDespusDe(), BorderLayout.NORTH);
			pnListOfPoductPriceToAccept.add(getPnBtnMenuNewProducts(), BorderLayout.SOUTH);
			pnListOfPoductPriceToAccept.add(getPnListProductToAcceptWithChange(), BorderLayout.CENTER);
		}
		return pnListOfPoductPriceToAccept;
	}

	public void reset() {
		resetWindow();
	}

	private void resetWindow() {
		// Disable buttons
		btnAcceptNewPrices.setEnabled(false);
		btnDeleteFromChangeList.setEnabled(false);
		btnIntroduceToChangeList.setEnabled(false);
		btnNewPriceUpdate.setEnabled(false);

		// Set text to empty
		txtActualPrice.setText("");
		txtName.setText("");
		txtNewPrice.setText("");
		txtNewPriceUpdate.setText("");

		// Empty the new price table
		tblProductsToUpdate.setModel(new CustomTableModel<>());

		// Reset original price table
		CustomTableModel<ProductDtoWrapperLessInfo> model = initialJTableProductList();
		tblAllProducts.setModel(model);
	}

	private JScrollPane getSpProductTable() {
		if (spProductTable == null) {
			spProductTable = new JScrollPane();
			spProductTable.setViewportView(getTblAllProducts());
		}
		return spProductTable;
	}

	private JTable getTblAllProducts() {
		if (tblAllProducts == null) {
			CustomTableModel<ProductDtoWrapperLessInfo> model = initialJTableProductList();
			tblAllProducts = new CustomJTable<ProductDtoWrapperLessInfo>(model);
			tblAllProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			tblAllProducts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					manageMouseClickOnInitalTable();
				}
			});
		}
		return tblAllProducts;
	}

	private void manageMouseClickOnInitalTable() {
		int row = tblAllProducts.getSelectedRow();
		if (row >= 0) {
			txtName.setText(tblAllProducts.getCustomModel().getValueAtRow(row).getDto().name);
			txtActualPrice
					.setText(String.valueOf(tblAllProducts.getCustomModel().getValueAtRow(row).getDto().publicPrice));
			txtNewPrice.setText("");

			currentPossibleNewPrice = tblAllProducts.getCustomModel().getValueAtRow(row);
		}
	}

	private CustomTableModel<ProductDtoWrapperLessInfo> initialJTableProductList() {
		ProductService service = ServiceFactory.createProductService();
		List<ProductDto> dtos = service.getAllProducts();

		List<ProductDtoWrapperLessInfo> wrappedDtos = new ArrayList<ProductDtoWrapperLessInfo>();
		for (ProductDto dto : dtos) {
			wrappedDtos.add(new ProductDtoWrapperLessInfo(dto));
		}

		CustomTableModel<ProductDtoWrapperLessInfo> model = new CustomTableModel<ProductDtoWrapperLessInfo>(
				wrappedDtos);

		return model;
	}

	private JLabel getLblAllProducts() {
		if (lblAllProducts == null) {
			lblAllProducts = new JLabel("Productos En El Almacén");
			lblAllProducts.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblAllProducts;
	}

	private JLabel getLblChangeProductPrice() {
		if (lblChangeProductPrice == null) {
			lblChangeProductPrice = new JLabel("Cambiar Precio de Producto Seleccionado");
			lblChangeProductPrice.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblChangeProductPrice;
	}

	private JPanel getPnNewPrice() {
		if (pnNewPrice == null) {
			pnNewPrice = new JPanel();
			GridBagLayout gbl_pnNewPrice = new GridBagLayout();
			gbl_pnNewPrice.columnWidths = new int[] { 0, 0, 0 };
			gbl_pnNewPrice.rowHeights = new int[] { 0, 0, 0, 0 };
			gbl_pnNewPrice.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
			gbl_pnNewPrice.rowWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
			pnNewPrice.setLayout(gbl_pnNewPrice);
			GridBagConstraints gbc_lblName = new GridBagConstraints();
			gbc_lblName.insets = new Insets(0, 0, 5, 5);
			gbc_lblName.anchor = GridBagConstraints.EAST;
			gbc_lblName.gridx = 0;
			gbc_lblName.gridy = 0;
			pnNewPrice.add(getLblName(), gbc_lblName);
			GridBagConstraints gbc_txtName = new GridBagConstraints();
			gbc_txtName.insets = new Insets(0, 0, 5, 0);
			gbc_txtName.fill = GridBagConstraints.BOTH;
			gbc_txtName.gridx = 1;
			gbc_txtName.gridy = 0;
			pnNewPrice.add(getTxtName(), gbc_txtName);
			GridBagConstraints gbc_lblActualPrice = new GridBagConstraints();
			gbc_lblActualPrice.anchor = GridBagConstraints.EAST;
			gbc_lblActualPrice.insets = new Insets(0, 0, 5, 5);
			gbc_lblActualPrice.gridx = 0;
			gbc_lblActualPrice.gridy = 1;
			pnNewPrice.add(getLblActualPrice(), gbc_lblActualPrice);
			GridBagConstraints gbc_txtActualPrice = new GridBagConstraints();
			gbc_txtActualPrice.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtActualPrice.insets = new Insets(0, 0, 5, 0);
			gbc_txtActualPrice.gridx = 1;
			gbc_txtActualPrice.gridy = 1;
			pnNewPrice.add(getTxtActualPrice(), gbc_txtActualPrice);
			GridBagConstraints gbc_lblNewPrice = new GridBagConstraints();
			gbc_lblNewPrice.insets = new Insets(0, 0, 0, 5);
			gbc_lblNewPrice.anchor = GridBagConstraints.EAST;
			gbc_lblNewPrice.gridx = 0;
			gbc_lblNewPrice.gridy = 2;
			pnNewPrice.add(getLblNewPrice(), gbc_lblNewPrice);
			GridBagConstraints gbc_txtNewPrice = new GridBagConstraints();
			gbc_txtNewPrice.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtNewPrice.anchor = GridBagConstraints.NORTH;
			gbc_txtNewPrice.gridx = 1;
			gbc_txtNewPrice.gridy = 2;
			pnNewPrice.add(getTxtNewPrice(), gbc_txtNewPrice);
		}
		return pnNewPrice;
	}

	private JLabel getLblActualPrice() {
		if (lblActualPrice == null) {
			lblActualPrice = new JLabel("Precio Publico Actual:");
			lblActualPrice.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return lblActualPrice;
	}

	private JTextField getTxtName() {
		if (txtName == null) {
			txtName = new JTextField();
			txtName.setFont(new Font("Tahoma", Font.PLAIN, 14));
			txtName.setEditable(false);
			txtName.setColumns(10);
		}
		return txtName;
	}

	private JTextField getTxtActualPrice() {
		if (txtActualPrice == null) {
			txtActualPrice = new JTextField();
			txtActualPrice.setFont(new Font("Tahoma", Font.PLAIN, 14));
			txtActualPrice.setEditable(false);
			txtActualPrice.setColumns(10);
		}
		return txtActualPrice;
	}

	private JTextField getTxtNewPrice() {
		if (txtNewPrice == null) {
			txtNewPrice = new JTextField();
			txtNewPrice.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					if (txtNewPrice.getText().length() == 0)
						btnIntroduceToChangeList.setEnabled(false);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					btnIntroduceToChangeList.setEnabled(true);

				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub

				}
			});
			txtNewPrice.setFont(new Font("Tahoma", Font.PLAIN, 14));
			txtNewPrice.setColumns(10);
		}
		return txtNewPrice;
	}

	private JLabel getLblName() {
		if (lblName == null) {
			lblName = new JLabel("Nombre:");
			lblName.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return lblName;
	}

	private JScrollPane getSpProductsToUpdateTable() {
		if (spProductsToUpdateTable == null) {
			spProductsToUpdateTable = new JScrollPane();
			spProductsToUpdateTable.setViewportView(getTblProductsToUpdate());
		}
		return spProductsToUpdateTable;
	}

	private JTable getTblProductsToUpdate() {
		if (tblProductsToUpdate == null) {
			CustomTableModel<ProductDtoWrapperLessInfo> model = new CustomTableModel<>();
			tblProductsToUpdate = new CustomJTable<>(model);
			tblProductsToUpdate.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (txtNewPriceUpdate.getText().length() == 0 || tblProductsToUpdate.getSelectedRow() < 0)
						btnNewPriceUpdate.setEnabled(false);
					else
						btnNewPriceUpdate.setEnabled(true);

					if (tblProductsToUpdate.getSelectedRow() >= 0) {
						btnDeleteFromChangeList.setEnabled(true);
					}

					if (arg0.getClickCount() == 2 && tblProductsToUpdate.getSelectedRow() != -1) {
						deleteSelectedRowAndAddToOriginalTable();
					}
				}
			});
			tblProductsToUpdate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return tblProductsToUpdate;
	}

	private JPanel getPnAddToBatch() {
		if (pnAddToBatch == null) {
			pnAddToBatch = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnAddToBatch.getLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			pnAddToBatch.add(getBtnIntroduceToChangeList());
		}
		return pnAddToBatch;
	}

	private JButton getBtnIntroduceToChangeList() {
		if (btnIntroduceToChangeList == null) {
			btnIntroduceToChangeList = new JButton("Introduccir a Lista de Cambios");
			btnIntroduceToChangeList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addToNewPriceTable();
				}
			});
			btnIntroduceToChangeList.setEnabled(false);
		}
		return btnIntroduceToChangeList;
	}

	private void addToNewPriceTable() {
		try {
			double price = Double.parseDouble(txtNewPrice.getText());

			if (price > 0) {
				// Delete row from original table
				CustomTableModel<ProductDtoWrapperLessInfo> oldModelOriginalTable = tblAllProducts.getCustomModel();
				List<ProductDtoWrapperLessInfo> newInfoOriginalTable = new ArrayList<>();
				for (int i = 0; i < oldModelOriginalTable.getRowCount(); i++) {
					if (i == tblAllProducts.getSelectedRow())
						continue;
					newInfoOriginalTable.add(oldModelOriginalTable.getValueAtRow(i));
				}

				CustomTableModel<ProductDtoWrapperLessInfo> newModelOriginalTable = new CustomTableModel<>(
						newInfoOriginalTable);
				tblAllProducts.setModel(newModelOriginalTable);

				// Update new product table
				CustomTableModel<ProductDtoWrapperLessInfo> oldModel = tblProductsToUpdate.getCustomModel();
				List<ProductDtoWrapperLessInfo> newListForModel = new ArrayList<>();

				for (int i = 0; i < oldModel.getRowCount(); i++) {
					newListForModel.add(oldModel.getValueAtRow(i));
				}
				currentPossibleNewPrice.getDto().publicPrice = price;
				newListForModel.add(currentPossibleNewPrice);
				CustomTableModel<ProductDtoWrapperLessInfo> newModel = new CustomTableModel<>(newListForModel);
				tblProductsToUpdate.setModel(newModel);

				btnAcceptNewPrices.setEnabled(true);
				btnIntroduceToChangeList.setEnabled(false);
				txtActualPrice.setText("");
				txtName.setText("");
				txtNewPrice.setText("");
			} else {
				txtNewPrice.setText("");
				showError("El precio no puede ser negativo", "Error precio negativo");
			}
		} catch (NumberFormatException e) {
			txtNewPrice.setText("");
			showError("El formato para el nuevo precio no es valido", "Error Nuevo Precio");
		}
	}

	private void showError(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}

	private JLabel getLblProductosDespusDe() {
		if (lblProductosDespusDe == null) {
			lblProductosDespusDe = new JLabel("Productos Después de Actualizar Precio");
			lblProductosDespusDe.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblProductosDespusDe;
	}

	private JPanel getPnBtnMenuNewProducts() {
		if (pnBtnMenuNewProducts == null) {
			pnBtnMenuNewProducts = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnBtnMenuNewProducts.getLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			pnBtnMenuNewProducts.add(getBtnDeleteFromChangeList());
			pnBtnMenuNewProducts.add(getBtnAcceptNewPrices());
		}
		return pnBtnMenuNewProducts;
	}

	private JButton getBtnAcceptNewPrices() {
		if (btnAcceptNewPrices == null) {
			btnAcceptNewPrices = new JButton("Aceptar Nuevos Precios");
			btnAcceptNewPrices.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					updatePricesAndReset();
				}
			});
			btnAcceptNewPrices.setEnabled(false);
		}
		return btnAcceptNewPrices;
	}

	/**
	 * 
	 */
	private void updatePricesAndReset() {
		if (JOptionPane.showConfirmDialog(this, "¿Está seguro que quiere cambiar los precios de los productos?",
				"Confirmar cambio de precios", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			List<ProductDtoWrapperLessInfo> infoToUpdateWrapped = tblProductsToUpdate.getCustomModel()
					.getDataReferences();
			ProductService productService = ServiceFactory.createProductService();
			BudgetService budgetService = ServiceFactory.createBudgetService();
			List<ProductDto> infoToPass = infoToUpdateWrapped.stream().map(element -> element.getDto())
					.collect(Collectors.toList());
			boolean sucessfulPriceUpdate = productService.updatePrices(infoToPass);
			boolean sucessfulBudgetUpdate = budgetService.updateNotAcceptedBudgetPricesForProducts(infoToPass);

			if (sucessfulBudgetUpdate && sucessfulPriceUpdate) {
				showMessage("Los precios se han actualizado correctamente", "Precios actualizados");
			} else {
				showError("Los precios no se han podido actualizar", "Error actualizacion de precios");
			}

			resetWindow();
		}
	}

	private void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private JButton getBtnDeleteFromChangeList() {
		if (btnDeleteFromChangeList == null) {
			btnDeleteFromChangeList = new JButton("Borrar De la Lista de Cambios");
			btnDeleteFromChangeList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					deleteSelectedRowAndAddToOriginalTable();
				}
			});
			btnDeleteFromChangeList.setEnabled(false);
		}
		return btnDeleteFromChangeList;
	}

	private void deleteSelectedRowAndAddToOriginalTable() {
		int row = tblProductsToUpdate.getSelectedRow();
		if (row >= 0) {
			ProductDtoWrapperLessInfo selected = tblProductsToUpdate.getCustomModel().getValueAtRow(row);
			selected.getDto().publicPrice = selected.originalPublicPrice;

			CustomTableModel<ProductDtoWrapperLessInfo> oldModel = tblProductsToUpdate.getCustomModel();
			List<ProductDtoWrapperLessInfo> newList = new ArrayList<>();
			for (int i = 0; i < oldModel.getRowCount(); i++) {
				if (i == row)
					continue;
				newList.add(oldModel.getValueAtRow(i));
			}

			CustomTableModel<ProductDtoWrapperLessInfo> newModel = new CustomTableModel<>(newList);
			tblProductsToUpdate.setModel(newModel);

			CustomTableModel<ProductDtoWrapperLessInfo> oldOriginalTableModel = tblAllProducts.getCustomModel();
			List<ProductDtoWrapperLessInfo> newListOriginalTable = new ArrayList<>();
			for (int i = 0; i < oldOriginalTableModel.getRowCount(); i++)
				newListOriginalTable.add(oldOriginalTableModel.getValueAtRow(i));
			newListOriginalTable.add(selected);

			CustomTableModel<ProductDtoWrapperLessInfo> newOriginalTableModel = new CustomTableModel<>(
					newListOriginalTable);
			tblAllProducts.setModel(newOriginalTableModel);

			btnDeleteFromChangeList.setEnabled(false);
			btnAcceptNewPrices.setEnabled(tblProductsToUpdate.getRowCount() != 0);
			btnNewPriceUpdate.setEnabled(false);
			txtNewPriceUpdate.setText("");
		}
	}

	private JLabel getLblNewPrice() {
		if (lblNewPrice == null) {
			lblNewPrice = new JLabel("Precio Publico Nuevo:");
			lblNewPrice.setFont(new Font("Tahoma", Font.BOLD, 14));
		}
		return lblNewPrice;
	}

	private JPanel getPnListProductToAcceptWithChange() {
		if (pnListProductToAcceptWithChange == null) {
			pnListProductToAcceptWithChange = new JPanel();
			pnListProductToAcceptWithChange.setLayout(new BorderLayout(0, 0));
			pnListProductToAcceptWithChange.add(getSpProductsToUpdateTable(), BorderLayout.CENTER);
			pnListProductToAcceptWithChange.add(getPnChangePrice(), BorderLayout.SOUTH);
		}
		return pnListProductToAcceptWithChange;
	}

	private JPanel getPnChangePrice() {
		if (pnChangePrice == null) {
			pnChangePrice = new JPanel();
			pnChangePrice.setBorder(new TitledBorder(null, "Cambiar Precio Del Producto Seleccionado",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			GridBagLayout gbl_pnChangePrice = new GridBagLayout();
			gbl_pnChangePrice.columnWidths = new int[] { 86, 233, 0 };
			gbl_pnChangePrice.rowHeights = new int[] { 23, 0 };
			gbl_pnChangePrice.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
			gbl_pnChangePrice.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			pnChangePrice.setLayout(gbl_pnChangePrice);
			GridBagConstraints gbc_txtNewPriceUpdate = new GridBagConstraints();
			gbc_txtNewPriceUpdate.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtNewPriceUpdate.insets = new Insets(0, 0, 0, 5);
			gbc_txtNewPriceUpdate.gridx = 0;
			gbc_txtNewPriceUpdate.gridy = 0;
			pnChangePrice.add(getTxtNewPriceUpdate(), gbc_txtNewPriceUpdate);
			GridBagConstraints gbc_btnNewPriceUpdate = new GridBagConstraints();
			gbc_btnNewPriceUpdate.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnNewPriceUpdate.anchor = GridBagConstraints.NORTH;
			gbc_btnNewPriceUpdate.gridx = 1;
			gbc_btnNewPriceUpdate.gridy = 0;
			pnChangePrice.add(getBtnNewPriceUpdate(), gbc_btnNewPriceUpdate);
		}
		return pnChangePrice;
	}

	private JTextField getTxtNewPriceUpdate() {
		if (txtNewPriceUpdate == null) {
			txtNewPriceUpdate = new JTextField();
			txtNewPriceUpdate.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					if (txtNewPriceUpdate.getText().length() == 0 || tblProductsToUpdate.getSelectedRow() < 0)
						btnNewPriceUpdate.setEnabled(false);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					if (tblProductsToUpdate.getSelectedRow() >= 0)
						btnNewPriceUpdate.setEnabled(true);
				}

				@Override
				public void changedUpdate(DocumentEvent e) {

				}
			});
			txtNewPriceUpdate.setColumns(10);
		}
		return txtNewPriceUpdate;
	}

	private JButton getBtnNewPriceUpdate() {
		if (btnNewPriceUpdate == null) {
			btnNewPriceUpdate = new JButton("Actualizar Precio");
			btnNewPriceUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					changePrice();
				}
			});
			btnNewPriceUpdate.setEnabled(false);
		}
		return btnNewPriceUpdate;
	}

	private void changePrice() {
		try {
			double price = Double.parseDouble(txtNewPriceUpdate.getText());
			if (price > 0) {
				ProductDtoWrapperLessInfo selected = tblProductsToUpdate.getCustomModel()
						.getValueAtRow(tblProductsToUpdate.getSelectedRow());
				selected.getDto().publicPrice = price;
				btnNewPriceUpdate.setEnabled(false);
				txtNewPriceUpdate.setText("");

				tblProductsToUpdate.setValueAt(selected.getDto().publicPrice, tblProductsToUpdate.getSelectedRow(),
						tblProductsToUpdate.getColumnCount() - 1);
			} else {
				txtNewPriceUpdate.setText("");
				showError("El precio no puede ser negativo", "Error precio negativo");
			}
		} catch (NumberFormatException e) {
			txtNewPriceUpdate.setText("");
			showError("El formato para el precio actualizado no es valido", "Error Nuevo Precio");
		}
	}
}