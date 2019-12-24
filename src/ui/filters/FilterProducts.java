package ui.filters;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import business.serviceLayer.ProductService;
import common.Conf;
import dtos.ProductDto;
import factories.ServiceFactory;

/**
 * Dialog used for filterig the products available for sale. They can be
 * filtered by their price (Greater or lower than a specific quantity) and by
 * their categories. Products will not be filtered unless the Filter button is
 * pressed.
 * 
 * @author Daniel Adrian Mare
 *
 */
public class FilterProducts extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel pnBottomBar;
	private JPanel pnEmpty;
	private JPanel pnButtons;
	private JButton btnCancel;
	private JButton btnFilter;
	private JPanel pnCancel;
	private JPanel pnFilter;
	private JPanel pnTitle;
	private JLabel lblMuebleriasPepin;
	private JPanel pnFilters;
	private JPanel pnPriceFilter;
	private JTextField txtPriceFilter;
	private JPanel pnTypeOfPriceFilter;
	private JRadioButton rdbtnPriceGreaterThan;
	private JRadioButton rdbtnPriceLowerThan;
	private JLabel lblMoney;
	private JPanel pnGreaterThan;
	private JPanel pnLowerThan;
	private final ButtonGroup btnGroupPriceFilter = new ButtonGroup();
	private JPanel pnIntroducePrice;
	private JPanel pnTypeProductFilter;
	private JPanel pnCheckBoxes;

	// CATEGORY FILTERS
	private JCheckBox chckbxArmario;
	private JCheckBox chckbxSilla;
	private JCheckBox chckbxCamas;
	private JCheckBox chckbxSofas;
	private JCheckBox chckbxSillones;
	private JCheckBox chckbxEscritorios;
	private JCheckBox chckbxMesas;

	private CheckCategoriesSelectedListener checkCategoriesSelected = new CheckCategoriesSelectedListener();

	private FiltrablePanel parent = null;

	private Conf productCategories = Conf.getInstance("configs/productFilters.properties");
	ProductService service = ServiceFactory.createProductService();

	private List<String> categoryFilters = new ArrayList<String>();
	private List<ProductDto> filteredProductsByCategories = new ArrayList<ProductDto>();
	private List<ProductDto> filteredProductsByPrice = new ArrayList<ProductDto>();

	private CloseWindowListener closeWindow = new CloseWindowListener();

	double priceForFiltering = 0;
	private List<ProductDto> filteredProducts = new ArrayList<ProductDto>();
	private JCheckBox chckbxOrganizator;
	private JCheckBox chckbxShelves;
	private JCheckBox chckbxDecorations;
	private JCheckBox chckbxCommode;

	public FilterProducts(FiltrablePanel parent) {
		this.parent = parent;
		setTitle("Mueblerias Pepin: Filtrar productos");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		getContentPane().add(getPnBottomBar(), BorderLayout.SOUTH);
		getContentPane().add(getPnTitle(), BorderLayout.NORTH);
		getContentPane().add(getPnFilters(), BorderLayout.CENTER);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setSize(500, 300);

		this.addWindowListener(closeWindow);
		getContentPane().setVisible(true);
	}

	private class CloseWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
		}
	}

	private class CheckCategoriesSelectedListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {

			JCheckBox ch = (JCheckBox) e.getSource();
			String category = ch.getName();

			if (e.getStateChange() == ItemEvent.SELECTED)
				categoryFilters.add(category);
			else if (e.getStateChange() == ItemEvent.DESELECTED)
				categoryFilters.remove(category);
		}

	}

	private JPanel getPnBottomBar() {
		if (pnBottomBar == null) {
			pnBottomBar = new JPanel();
			pnBottomBar.setLayout(new GridLayout(1, 0, 0, 0));
			pnBottomBar.add(getPnEmpty());
			pnBottomBar.add(getPnButtons());
		}
		return pnBottomBar;
	}

	private JPanel getPnEmpty() {
		if (pnEmpty == null) {
			pnEmpty = new JPanel();
			pnEmpty.setBorder(null);
		}
		return pnEmpty;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.setLayout(new BoxLayout(pnButtons, BoxLayout.X_AXIS));
			pnButtons.add(getPnCancel());
			pnButtons.add(getPnFilter());
		}
		return pnButtons;
	}

	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton("Cancelar");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
			});
			btnCancel.setVerticalAlignment(SwingConstants.BOTTOM);
		}
		return btnCancel;
	}

	private JButton getBtnFilter() {
		if (btnFilter == null) {
			btnFilter = new JButton("Filtrar");
			btnFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!categoryFilters.isEmpty()) {
						filteredProductsByCategories = service.getFilteredProductsByCategory(categoryFilters);
					}

					if (rdbtnPriceGreaterThan.isSelected()) {
						filteredProductsByPrice = service
								.getProductsPricedHigherThanReference(Double.parseDouble(txtPriceFilter.getText()));
					} else if (rdbtnPriceLowerThan.isSelected()) {
						filteredProductsByPrice = service.getProductsPricedLowerThanOrEqualToReference(
								Double.parseDouble(txtPriceFilter.getText()));
					}
					if (!filteredProductsByCategories.isEmpty()) {
						for (int i = 0; i < filteredProductsByCategories.size(); i++) {
							for (int j = 0; j < filteredProductsByPrice.size(); j++) {
								if (filteredProductsByCategories.get(i).equals(filteredProductsByPrice.get(j)))
									filteredProducts.add(filteredProductsByCategories.get(i));
							}
						}
					} else {
						filteredProducts = filteredProductsByPrice;
					}
					parent.setFilteredProductList(filteredProducts);
					dispose();

				}
			});
		}
		return btnFilter;
	}

	private JPanel getPnCancel() {
		if (pnCancel == null) {
			pnCancel = new JPanel();
			pnCancel.setBorder(null);
			pnCancel.add(getBtnCancel());
		}
		return pnCancel;
	}

	private JPanel getPnFilter() {
		if (pnFilter == null) {
			pnFilter = new JPanel();
			pnFilter.add(getBtnFilter());
		}
		return pnFilter;
	}

	private JPanel getPnTitle() {
		if (pnTitle == null) {
			pnTitle = new JPanel();
			pnTitle.add(getLblMuebleriasPepin());
		}
		return pnTitle;
	}

	private JLabel getLblMuebleriasPepin() {
		if (lblMuebleriasPepin == null) {
			lblMuebleriasPepin = new JLabel("Filtro de productos");
		}
		return lblMuebleriasPepin;
	}

	private JPanel getPnFilters() {
		if (pnFilters == null) {
			pnFilters = new JPanel();
			pnFilters.setLayout(new GridLayout(2, 1, 0, 0));
			pnFilters.add(getPnPriceFilter());
			pnFilters.add(getPnTypeProductFilter());
		}
		return pnFilters;
	}

	private JPanel getPnPriceFilter() {
		if (pnPriceFilter == null) {
			pnPriceFilter = new JPanel();
			pnPriceFilter.setBorder(
					new TitledBorder(null, "Filtrar por precio", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnPriceFilter.setLayout(new BoxLayout(pnPriceFilter, BoxLayout.X_AXIS));
			pnPriceFilter.add(getPnTypeOfPriceFilter());
			pnPriceFilter.add(getPnIntroducePrice());
		}
		return pnPriceFilter;
	}

	private JTextField getTxtPriceFilter() {
		if (txtPriceFilter == null) {
			txtPriceFilter = new JTextField();
			txtPriceFilter.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					double newPriceForFiltering;
					try {
						newPriceForFiltering = Double.parseDouble(txtPriceFilter.getText());
						if (newPriceForFiltering < 0) {
							JOptionPane.showMessageDialog(rootPane,
									"The introduced price for filtering must be greater or equal to 0");
						} else {
							priceForFiltering = newPriceForFiltering;
						}
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(rootPane,
								"The introduced price for filtering must be a number and greater or equal to 0");
						txtPriceFilter.setText("0");
					}
				}
			});
			txtPriceFilter.setHorizontalAlignment(SwingConstants.CENTER);
			txtPriceFilter.setText("0");
			txtPriceFilter.setColumns(10);
		}
		return txtPriceFilter;
	}

	private JPanel getPnTypeOfPriceFilter() {
		if (pnTypeOfPriceFilter == null) {
			pnTypeOfPriceFilter = new JPanel();
			pnTypeOfPriceFilter.setLayout(new GridLayout(0, 1, 0, 0));
			pnTypeOfPriceFilter.add(getPnGreaterThan());
			pnTypeOfPriceFilter.add(getPnLowerThan());
		}
		return pnTypeOfPriceFilter;
	}

	private JRadioButton getRdbtnPriceGreaterThan() {
		if (rdbtnPriceGreaterThan == null) {
			rdbtnPriceGreaterThan = new JRadioButton("Mayor que:");
			rdbtnPriceGreaterThan.setSelected(true);
			btnGroupPriceFilter.add(rdbtnPriceGreaterThan);
		}
		return rdbtnPriceGreaterThan;
	}

	private JRadioButton getRdbtnPriceLowerThan() {
		if (rdbtnPriceLowerThan == null) {
			rdbtnPriceLowerThan = new JRadioButton("Menor o igual que:");
			btnGroupPriceFilter.add(rdbtnPriceLowerThan);
		}
		return rdbtnPriceLowerThan;
	}

	private JLabel getLblMoney() {
		if (lblMoney == null) {
			lblMoney = new JLabel("€");
		}
		return lblMoney;
	}

	private JPanel getPnGreaterThan() {
		if (pnGreaterThan == null) {
			pnGreaterThan = new JPanel();
			pnGreaterThan.setLayout(new BorderLayout(0, 0));
			pnGreaterThan.add(getRdbtnPriceGreaterThan());
		}
		return pnGreaterThan;
	}

	private JPanel getPnLowerThan() {
		if (pnLowerThan == null) {
			pnLowerThan = new JPanel();
			pnLowerThan.setLayout(new BorderLayout(0, 0));
			pnLowerThan.add(getRdbtnPriceLowerThan());
		}
		return pnLowerThan;
	}

	private JPanel getPnIntroducePrice() {
		if (pnIntroducePrice == null) {
			pnIntroducePrice = new JPanel();
			pnIntroducePrice.setBorder(null);
			pnIntroducePrice.setLayout(new BorderLayout(0, 0));
			pnIntroducePrice.add(getTxtPriceFilter(), BorderLayout.CENTER);
			pnIntroducePrice.add(getLblMoney(), BorderLayout.EAST);
		}
		return pnIntroducePrice;
	}

	private JPanel getPnTypeProductFilter() {
		if (pnTypeProductFilter == null) {
			pnTypeProductFilter = new JPanel();
			pnTypeProductFilter.setBorder(new TitledBorder(null, "Filtrar por tipo de producto", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			pnTypeProductFilter.setLayout(new BorderLayout(0, 0));
			pnTypeProductFilter.add(getPnCheckBoxes());
		}
		return pnTypeProductFilter;
	}

	private JPanel getPnCheckBoxes() {
		if (pnCheckBoxes == null) {
			pnCheckBoxes = new JPanel();
			pnCheckBoxes.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pnCheckBoxes.add(getChckbxArmario());
			pnCheckBoxes.add(getChckbxSilla());
			pnCheckBoxes.add(getChckbxCamas());
			pnCheckBoxes.add(getChckbxSofas());
			pnCheckBoxes.add(getChckbxSillones());
			pnCheckBoxes.add(getChckbxEscritorios());
			pnCheckBoxes.add(getChckbxMesas());
			pnCheckBoxes.add(getChckbxOrganizator());
			pnCheckBoxes.add(getChckbxShelves());
			pnCheckBoxes.add(getChckbxDecorations());
			pnCheckBoxes.add(getChckbxCommode());
		}
		return pnCheckBoxes;
	}

	private JCheckBox getChckbxArmario() {
		if (chckbxArmario == null) {
			chckbxArmario = new JCheckBox("Armarios");
			chckbxArmario.setHorizontalAlignment(SwingConstants.LEFT);
			chckbxArmario.setName(productCategories.getProperty("WARDROBE"));
			chckbxArmario.addItemListener(checkCategoriesSelected);
		}
		return chckbxArmario;
	}

	private JCheckBox getChckbxSilla() {
		if (chckbxSilla == null) {
			chckbxSilla = new JCheckBox("Sillas");
			chckbxSilla.setName(productCategories.getProperty("CHAIR"));
			chckbxSilla.addItemListener(checkCategoriesSelected);
		}
		return chckbxSilla;
	}

	private JCheckBox getChckbxCamas() {
		if (chckbxCamas == null) {
			chckbxCamas = new JCheckBox("Camas");
			chckbxCamas.setName(productCategories.getProperty("BED"));
			chckbxCamas.addItemListener(checkCategoriesSelected);
		}
		return chckbxCamas;
	}

	private JCheckBox getChckbxSofas() {
		if (chckbxSofas == null) {
			chckbxSofas = new JCheckBox("Sofás");
			chckbxSofas.setName(productCategories.getProperty("SOFA"));
			chckbxSofas.addItemListener(checkCategoriesSelected);
		}
		return chckbxSofas;
	}

	private JCheckBox getChckbxSillones() {
		if (chckbxSillones == null) {
			chckbxSillones = new JCheckBox("Sillones");
			chckbxSillones.setName(productCategories.getProperty("ARMCHAIR"));
			chckbxSillones.addItemListener(checkCategoriesSelected);
		}
		return chckbxSillones;
	}

	private JCheckBox getChckbxEscritorios() {
		if (chckbxEscritorios == null) {
			chckbxEscritorios = new JCheckBox("Escritorios");
			chckbxEscritorios.setName(productCategories.getProperty("DESK"));
			chckbxEscritorios.addItemListener(checkCategoriesSelected);
		}
		return chckbxEscritorios;
	}

	private JCheckBox getChckbxMesas() {
		if (chckbxMesas == null) {
			chckbxMesas = new JCheckBox("Mesas");
			chckbxMesas.setName(productCategories.getProperty("TABLE"));
			chckbxMesas.addItemListener(checkCategoriesSelected);
		}
		return chckbxMesas;
	}

	private JCheckBox getChckbxOrganizator() {
		if (chckbxOrganizator == null) {
			chckbxOrganizator = new JCheckBox("Organizadores");
			chckbxOrganizator.setName(productCategories.getProperty("ORGANIZATOR"));
			chckbxOrganizator.addItemListener(checkCategoriesSelected);
		}
		return chckbxOrganizator;
	}

	private JCheckBox getChckbxShelves() {
		if (chckbxShelves == null) {
			chckbxShelves = new JCheckBox("Estanterias");
			chckbxShelves.setName(productCategories.getProperty("SHELF"));
			chckbxShelves.addItemListener(checkCategoriesSelected);
		}
		return chckbxShelves;
	}

	private JCheckBox getChckbxDecorations() {
		if (chckbxDecorations == null) {
			chckbxDecorations = new JCheckBox("Decoraciones");
			chckbxDecorations.setName(productCategories.getProperty("DECORATION"));
			chckbxDecorations.addItemListener(checkCategoriesSelected);
		}
		return chckbxDecorations;
	}

	private JCheckBox getChckbxCommode() {
		if (chckbxCommode == null) {
			chckbxCommode = new JCheckBox("Cómodas");
			chckbxCommode.setName(productCategories.getProperty("COMMODE"));
			chckbxCommode.addItemListener(checkCategoriesSelected);
		}
		return chckbxCommode;
	}
}
