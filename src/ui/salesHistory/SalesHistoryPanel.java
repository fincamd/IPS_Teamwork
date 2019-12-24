package ui.salesHistory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
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

import com.toedter.calendar.JDateChooser;

import business.saleLogic.InvoicePdfGenerator;
import common.BusinessException;
import common.Conf;
import common.Round;
import dtos.ReturnDto;
import dtos.SaleDto;
import dtos.SaleHistoryEntryDto;
import dtos.TransportDto;
import factories.ServiceFactory;
import ui.extraClasses.NotEditableTableModel;
import wrappers.ProductDtoWrapper;

public class SalesHistoryPanel extends JPanel {

	// Constants
	// ------------------------------------------------------------------------

	private static final long serialVersionUID = 8149545031086652283L;

	// Fields for the components of the frame
	// ------------------------------------------------------------------------

	private JPanel pnFilteringControls;
	private JButton btnFilterHistory;
	private JButton btnRemoveFilter;
	private JLabel lblFrom;
	private JLabel lblTo;
	private JDateChooser fromDate, toDate;
	private JSplitPane spltPnSalesHistory;
	private JScrollPane spSalesHistoryEntries;
	private JPanel pnSaleEntryData;
	private JTable tSalesHistoryEntries;
	private JScrollPane spSaleEntryData;
	private JTable tSaleEntryData;
	private JPanel pnSalesHistoryEntries;
	private JPanel pnProducts;
	private JPanel pnReturns;
	private JScrollPane spReturns;
	private JTable tReturns;
	private JPanel pnSaleDetails;
	private JLabel lblBuildUp;
	private JTextField txtBuildUp;
	private JLabel lblTransport;
	private JTextField txtTransport;
	private JLabel lblTotalSale;
	private JTextField txtTotalSale;
	private JPanel pnReturnDetails;
	private JLabel lblPreviousTotal;
	private JTextField txtPreviousTotal;
	private JLabel lblReturnsTotal;
	private JTextField txtReturnsTotal;
	private JLabel lblNewtotal;
	private JTextField txtNewTotal;
	private JPanel pnExtraInfoAndReturnsButton;
	private JButton btnOpenReturnsWindow;
	private JPanel pnExtraInformation;
	private JPanel pnEmpty;
	private JPanel pnOpenReturnsWindowButton;
	private JPanel pnFiltersAndTitle;
	private JLabel lblWindowTitle;
	private JButton btnGenerateInvoice;

	// Fields
	// ------------------------------------------------------------------------

	private NotEditableTableModel salesHistoryModel, saleProductsModel,
			returnsModel;
	private List<ProductDtoWrapper> productsInSelectedSale;
	private List<ReturnDto> returnedProductsForSelectedSale;

	// Constructor for the panel
	// ------------------------------------------------------------------------

	public SalesHistoryPanel() {
		setLayout(new BorderLayout(0, 0));
		add(getPnFiltersAndTitle(), BorderLayout.NORTH);
		add(getSpltPnSalesHistory(), BorderLayout.CENTER);

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				reset();
			}
		});

		populateTableWithSalesHistory(getSaleHistoryEntries());

		productsInSelectedSale = new ArrayList<ProductDtoWrapper>();
		returnedProductsForSelectedSale = new ArrayList<ReturnDto>();

		saleProductsModel = new NotEditableTableModel(null, 0);
		salesHistoryModel = new NotEditableTableModel(null, 0);
		returnsModel = new NotEditableTableModel(null, 0);
	}

	// Getters for the components of the panel
	// ------------------------------------------------------------------------

	private JPanel getPnFilteringControls() {
		if (pnFilteringControls == null) {
			pnFilteringControls = new JPanel(new FlowLayout());
			pnFilteringControls.add(getLblFrom());
			pnFilteringControls.add(getDtChFromDate());
			pnFilteringControls.add(getLblTo());
			pnFilteringControls.add(getDtChToDate());
			pnFilteringControls.add(getBtnFilterHistory());
			pnFilteringControls.add(getBtnRemoveFilter());
		}
		return pnFilteringControls;
	}

	private JButton getBtnFilterHistory() {
		if (btnFilterHistory == null) {
			btnFilterHistory = new JButton("Filtrar historial");
			btnFilterHistory.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (areDatesValid()) {
						filterSaleHistory();
					}
				}
			});
		}
		return btnFilterHistory;
	}

	private JButton getBtnRemoveFilter() {
		if (btnRemoveFilter == null) {
			btnRemoveFilter = new JButton("Eliminar filtro");
			btnRemoveFilter.setEnabled(false);
			btnRemoveFilter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fromDate.setDate(null);
					toDate.setDate(null);
					populateTableWithSalesHistory(getSaleHistoryEntries());
					btnRemoveFilter.setEnabled(false);
					resetProductsInSalePanel();
				}
			});
		}
		return btnRemoveFilter;
	}

	private JLabel getLblFrom() {
		if (lblFrom == null) {
			lblFrom = new JLabel("Desde:");
		}
		return lblFrom;
	}

	private JLabel getLblTo() {
		if (lblTo == null) {
			lblTo = new JLabel("Hasta:");
		}
		return lblTo;
	}

	private JDateChooser getDtChFromDate() {
		if (fromDate == null) {
			fromDate = new JDateChooser();
		}
		return fromDate;
	}

	private JDateChooser getDtChToDate() {
		if (toDate == null) {
			toDate = new JDateChooser();
		}
		return toDate;
	}

	private JSplitPane getSpltPnSalesHistory() {
		if (spltPnSalesHistory == null) {
			spltPnSalesHistory = new JSplitPane();
			spltPnSalesHistory.setLeftComponent(getPnSalesHistoryEntries());
			spltPnSalesHistory.setRightComponent(getPnSaleEntryData());
			spltPnSalesHistory.setResizeWeight(.5d);
		}
		return spltPnSalesHistory;
	}

	private JScrollPane getSpSalesHistoryEntries() {
		if (spSalesHistoryEntries == null) {
			spSalesHistoryEntries = new JScrollPane();
			spSalesHistoryEntries.setViewportView(getTSalesHistoryEntries());
		}
		return spSalesHistoryEntries;
	}

	private JPanel getPnSaleEntryData() {
		if (pnSaleEntryData == null) {
			pnSaleEntryData = new JPanel();
			pnSaleEntryData.setLayout(new GridLayout(0, 1, 0, 0));
			pnSaleEntryData.add(getPnProducts());
			pnSaleEntryData.add(getPnReturns());
		}
		return pnSaleEntryData;
	}

	private JTable getTSalesHistoryEntries() {
		if (tSalesHistoryEntries == null) {
			tSalesHistoryEntries = new JTable();
			tSalesHistoryEntries
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tSalesHistoryEntries.getSelectionModel()
					.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							int selectedRow = getSelectedRowFromSalesHistory();
							if (selectedRow >= 0) {
								populateSaleHistoryEntry();
							}
						}
					});
			tSalesHistoryEntries.getTableHeader().setReorderingAllowed(false);
		}
		return tSalesHistoryEntries;
	}

	private JScrollPane getSpSaleEntryData() {
		if (spSaleEntryData == null) {
			spSaleEntryData = new JScrollPane();
			spSaleEntryData.setViewportView(getTSaleEntryData());
		}
		return spSaleEntryData;
	}

	private JTable getTSaleEntryData() {
		if (tSaleEntryData == null) {
			tSaleEntryData = new JTable();
			tSalesHistoryEntries
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tSaleEntryData.getTableHeader().setReorderingAllowed(false);
		}
		return tSaleEntryData;
	}

	private JPanel getPnSalesHistoryEntries() {
		if (pnSalesHistoryEntries == null) {
			pnSalesHistoryEntries = new JPanel();
			pnSalesHistoryEntries.setBorder(new TitledBorder(null,
					"Historial de ventas", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));
			pnSalesHistoryEntries.setLayout(new BorderLayout(0, 0));
			pnSalesHistoryEntries.add(getSpSalesHistoryEntries(),
					BorderLayout.CENTER);
		}
		return pnSalesHistoryEntries;
	}

	private JPanel getPnProducts() {
		if (pnProducts == null) {
			pnProducts = new JPanel();
			pnProducts.setBorder(new TitledBorder(null, "Productos en la venta",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnProducts.setLayout(new BorderLayout(0, 0));
			pnProducts.add(getSpSaleEntryData(), BorderLayout.CENTER);
			pnProducts.add(getPnExtraInfoAndReturnsButton(),
					BorderLayout.SOUTH);
		}
		return pnProducts;
	}

	private JPanel getPnReturns() {
		if (pnReturns == null) {
			pnReturns = new JPanel();
			pnReturns.setBorder(new TitledBorder(null,
					"Devoluciones realizadas para la venta",
					TitledBorder.LEADING, TitledBorder.TOP, null, null));
			pnReturns.setLayout(new BorderLayout(0, 0));
			pnReturns.add(getSpReturns(), BorderLayout.CENTER);
			pnReturns.add(getPnExtraInformation(), BorderLayout.SOUTH);
		}
		return pnReturns;
	}

	private JScrollPane getSpReturns() {
		if (spReturns == null) {
			spReturns = new JScrollPane();
			spReturns.setViewportView(getTReturns());
		}
		return spReturns;
	}

	private JTable getTReturns() {
		if (tReturns == null) {
			tReturns = new JTable();
			tReturns.getTableHeader().setReorderingAllowed(false);
			tReturns.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return tReturns;
	}

	private JPanel getPnSaleDetails() {
		if (pnSaleDetails == null) {
			pnSaleDetails = new JPanel();
			pnSaleDetails.setLayout(new GridLayout(0, 2, 0, 0));
			pnSaleDetails.add(getLblBuildUp());
			pnSaleDetails.add(getTxtBuildUp());
			pnSaleDetails.add(getLblTransport());
			pnSaleDetails.add(getTxtTransport());
			pnSaleDetails.add(getLblTotalSale());
			pnSaleDetails.add(getTxtTotalSale());
		}
		return pnSaleDetails;
	}

	private JLabel getLblBuildUp() {
		if (lblBuildUp == null) {
			lblBuildUp = new JLabel("Solicita montaje: ");
			lblBuildUp.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblBuildUp;
	}

	private JTextField getTxtBuildUp() {
		if (txtBuildUp == null) {
			txtBuildUp = new JTextField();
			txtBuildUp.setHorizontalAlignment(SwingConstants.CENTER);
			txtBuildUp.setEditable(false);
			txtBuildUp.setColumns(10);
		}
		return txtBuildUp;
	}

	private JLabel getLblTransport() {
		if (lblTransport == null) {
			lblTransport = new JLabel("Solicita transporte: ");
			lblTransport.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblTransport;
	}

	private JTextField getTxtTransport() {
		if (txtTransport == null) {
			txtTransport = new JTextField();
			txtTransport.setHorizontalAlignment(SwingConstants.CENTER);
			txtTransport.setEditable(false);
			txtTransport.setColumns(10);
		}
		return txtTransport;
	}

	private JLabel getLblTotalSale() {
		if (lblTotalSale == null) {
			lblTotalSale = new JLabel("Total: ");
			lblTotalSale.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblTotalSale;
	}

	private JTextField getTxtTotalSale() {
		if (txtTotalSale == null) {
			txtTotalSale = new JTextField();
			txtTotalSale.setHorizontalAlignment(SwingConstants.CENTER);
			txtTotalSale.setEditable(false);
			txtTotalSale.setColumns(10);
		}
		return txtTotalSale;
	}

	private JPanel getPnReturnDetails() {
		if (pnReturnDetails == null) {
			pnReturnDetails = new JPanel();
			pnReturnDetails.setLayout(new GridLayout(0, 2, 0, 0));
			pnReturnDetails.add(getLblPreviousTotal());
			pnReturnDetails.add(getTxtPreviousTotal());
			pnReturnDetails.add(getLblReturnsTotal());
			pnReturnDetails.add(getTxtReturnsTotal());
			pnReturnDetails.add(getLblNewtotal());
			pnReturnDetails.add(getTxtNewTotal());
		}
		return pnReturnDetails;
	}

	private JLabel getLblPreviousTotal() {
		if (lblPreviousTotal == null) {
			lblPreviousTotal = new JLabel("Total anterior: ");
			lblPreviousTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblPreviousTotal;
	}

	private JTextField getTxtPreviousTotal() {
		if (txtPreviousTotal == null) {
			txtPreviousTotal = new JTextField();
			txtPreviousTotal.setHorizontalAlignment(SwingConstants.CENTER);
			txtPreviousTotal.setEditable(false);
			txtPreviousTotal.setColumns(10);
		}
		return txtPreviousTotal;
	}

	private JLabel getLblReturnsTotal() {
		if (lblReturnsTotal == null) {
			lblReturnsTotal = new JLabel("Devoluciones: ");
			lblReturnsTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblReturnsTotal;
	}

	private JTextField getTxtReturnsTotal() {
		if (txtReturnsTotal == null) {
			txtReturnsTotal = new JTextField();
			txtReturnsTotal.setHorizontalAlignment(SwingConstants.CENTER);
			txtReturnsTotal.setEditable(false);
			txtReturnsTotal.setColumns(10);
		}
		return txtReturnsTotal;
	}

	private JLabel getLblNewtotal() {
		if (lblNewtotal == null) {
			lblNewtotal = new JLabel("Total final: ");
			lblNewtotal.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return lblNewtotal;
	}

	private JTextField getTxtNewTotal() {
		if (txtNewTotal == null) {
			txtNewTotal = new JTextField();
			txtNewTotal.setHorizontalAlignment(SwingConstants.CENTER);
			txtNewTotal.setEditable(false);
			txtNewTotal.setColumns(10);
		}
		return txtNewTotal;
	}

	private JPanel getPnExtraInfoAndReturnsButton() {
		if (pnExtraInfoAndReturnsButton == null) {
			pnExtraInfoAndReturnsButton = new JPanel();
			pnExtraInfoAndReturnsButton.setLayout(new GridLayout(0, 2, 0, 0));
			pnExtraInfoAndReturnsButton.add(getPnOpenReturnsWindowButton());
			pnExtraInfoAndReturnsButton.add(getPnSaleDetails());
		}
		return pnExtraInfoAndReturnsButton;
	}

	private JButton getBtnOpenReturnsWindow() {
		if (btnOpenReturnsWindow == null) {
			btnOpenReturnsWindow =
					new JButton("Tramitar devoluciones para esta venta");
			btnOpenReturnsWindow.setEnabled(false);
			btnOpenReturnsWindow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					openReturnsDialog();
				}

			});
		}
		return btnOpenReturnsWindow;
	}

	private JPanel getPnExtraInformation() {
		if (pnExtraInformation == null) {
			pnExtraInformation = new JPanel();
			pnExtraInformation.setLayout(new GridLayout(0, 2, 0, 0));
			pnExtraInformation.add(getPnEmpty());
			pnExtraInformation.add(getPnReturnDetails());
		}
		return pnExtraInformation;
	}

	private JPanel getPnEmpty() {
		if (pnEmpty == null) {
			pnEmpty = new JPanel();
		}
		return pnEmpty;
	}

	private JPanel getPnOpenReturnsWindowButton() {
		if (pnOpenReturnsWindowButton == null) {
			pnOpenReturnsWindowButton = new JPanel();
			pnOpenReturnsWindowButton.setLayout(new GridLayout(0, 1, 0, 0));
			pnOpenReturnsWindowButton.add(getBtnOpenReturnsWindow());
			pnOpenReturnsWindowButton.add(getBtnGenerateInvoice());
		}
		return pnOpenReturnsWindowButton;
	}

	private JPanel getPnFiltersAndTitle() {
		if (pnFiltersAndTitle == null) {
			pnFiltersAndTitle = new JPanel();
			pnFiltersAndTitle.setLayout(new GridLayout(0, 1, 0, 0));
			pnFiltersAndTitle.add(getLblWindowTitle());
			pnFiltersAndTitle.add(getPnFilteringControls());
		}
		return pnFiltersAndTitle;
	}

	private JLabel getLblWindowTitle() {
		if (lblWindowTitle == null) {
			lblWindowTitle = new JLabel("Historial de ventas");
			lblWindowTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
			lblWindowTitle.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblWindowTitle;
	}

	private JButton getBtnGenerateInvoice() {
		if (btnGenerateInvoice == null) {
			btnGenerateInvoice = new JButton("Generar factura para esta venta");
			btnGenerateInvoice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					generateInvoiceForSelectedSale();
				}
			});
			btnGenerateInvoice.setEnabled(false);
		}
		return btnGenerateInvoice;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public void reset() {
		populateTableWithSalesHistory(getSaleHistoryEntries());
		resetProductsInSalePanel();
		resetReturnedProductsInSalePanel();
		resetFilters();
	}

	public List<ProductDtoWrapper> getProductsInSelectedSale() {
		return new ArrayList<ProductDtoWrapper>(productsInSelectedSale);
	}

	public List<ReturnDto> getReturnedProductsForSale() {
		return new ArrayList<ReturnDto>(returnedProductsForSelectedSale);
	}

	@SuppressWarnings("unchecked")
	public void registerProductReturns(NotEditableTableModel productsToReturn,
			String returnReason) {
		Vector<String> product;
		for (Object each : productsToReturn.getDataVector().toArray()) {
			product = (Vector<String>) each;
			ProductDtoWrapper productDto = null;
			for (ProductDtoWrapper dto : productsInSelectedSale) {
				if (dto.getDto().name.equalsIgnoreCase(product.get(0))) {
					productDto = dto;
					break;
				}
			}
			ReturnDto returnDto = ServiceFactory.createReturnsService()
					.findBySaleAndProductId(Integer.parseInt(
							String.valueOf(tSalesHistoryEntries.getValueAt(
									getSelectedRowFromSalesHistory(), 2))),
							productDto.getDto().id);
			if (returnDto != null) {
				returnDto.quantity += Integer.parseInt(product.get(3));
				ServiceFactory.createReturnsService().updateReturn(returnDto);
				ServiceFactory.createProductService()
						.returnProductsToStorehouse(returnDto);
			} else {
				returnDto = new ReturnDto();
				returnDto.productId = productDto.getDto().id;
				returnDto.saleId = Integer.parseInt(
						String.valueOf(tSalesHistoryEntries.getValueAt(
								getSelectedRowFromSalesHistory(), 2)));
				returnDto.quantity = Integer.parseInt(product.get(3));
				returnDto.reason = returnReason;
				ServiceFactory.createReturnsService().addNewReturn(returnDto);
				ServiceFactory.createProductService()
						.returnProductsToStorehouse(returnDto);
			}
		}

		populateSaleHistoryEntry();
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private boolean areDatesValid() {
		Date from = fromDate.getDate();
		Date to = toDate.getDate();

		if (from == null || to == null) {
			JOptionPane.showMessageDialog(this,
					"Por favor, comprueba que ambos selectores tengan una fecha "
							+ "asignada para poder filtrar el historial");
			return false;
		}

		if (from.equals(to)) {
			return true;
		}

		if (from.after(to)) {
			JOptionPane.showMessageDialog(this,
					"Por favor, comprueba que la fecha 'Desde' sea anterior a la fecha 'Hasta'");
			fromDate.setDate(null);
			toDate.setDate(null);
			return false;
		}

		return true;
	}

	private void filterSaleHistory() {
		List<SaleHistoryEntryDto> saleHistoryEntries = getSaleHistoryEntries();
		List<SaleHistoryEntryDto> filteredSaleHistoryEntries =
				new ArrayList<SaleHistoryEntryDto>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		Date from = fromDate.getDate();
		Date to = toDate.getDate();

		for (SaleHistoryEntryDto each : saleHistoryEntries) {
			String rawDate = each.saleDate.replaceAll("/", "-");
			Date entryDate = null;

			try {
				entryDate = format.parse(rawDate);
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(this,
						"No se pudo filtrar las ventas. Lo sentimos");
			}

			if ((entryDate.equals(from))
					|| ((entryDate.after(from) && entryDate.before(to)))) {
				filteredSaleHistoryEntries.add(each);
			}
		}

		if (filteredSaleHistoryEntries.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"No hay ninguna venta hecha durante ese intervalo",
					"Filtrado sin resultados", JOptionPane.INFORMATION_MESSAGE);
		}

		populateTableWithSalesHistory(filteredSaleHistoryEntries);

		btnRemoveFilter.setEnabled(true);

		resetProductsInSalePanel();
		resetReturnedProductsInSalePanel();
	}

	private List<SaleHistoryEntryDto> getSaleHistoryEntries() {
		List<SaleHistoryEntryDto> saleHistoryEntries =
				new ArrayList<SaleHistoryEntryDto>();

		try {
			saleHistoryEntries = ServiceFactory.createSaleService()
					.generateSalesHistoryEntries();
		} catch (BusinessException ex) {
			JOptionPane.showMessageDialog(this,
					"Algo salió mal conectando con la base de datos para conseguir el historial de ventas. Lo sentimos.",
					"Error: No se pudo obtener el historial de ventas",
					JOptionPane.ERROR_MESSAGE);
		}

		sortHistoryEntries(saleHistoryEntries);

		return saleHistoryEntries;
	}

	private void
			sortHistoryEntries(List<SaleHistoryEntryDto> saleHistoryEntries) {
		saleHistoryEntries.sort(new Comparator<SaleHistoryEntryDto>() {
			@Override
			public int compare(SaleHistoryEntryDto o1, SaleHistoryEntryDto o2) {
				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				String rawDate1 = o1.saleDate.replaceAll("/", "-");
				String rawDate2 = o2.saleDate.replaceAll("/", "-");
				Date date1 = null;
				Date date2 = null;

				try {
					date1 = format.parse(rawDate1);
					date2 = format.parse(rawDate2);
				} catch (ParseException ex) {
					JOptionPane.showMessageDialog(null,
							"Hubo un error mientras se intentaban ordenar las fechas",
							"Error: fallo en ordenación de fechas",
							JOptionPane.ERROR_MESSAGE);
				}

				return date1.compareTo(date2);
			}
		});
	}

	private void populateTableWithSalesHistory(
			List<SaleHistoryEntryDto> saleHistoryEntries) {
		String[] columnNames = new String[] { "Nombre del cliente",
				"Dni del cliente", "Identificador de la venta",
				"Fecha de venta", "Método de pago" };
		salesHistoryModel = new NotEditableTableModel(columnNames, 0);

		Vector<String> data;
		for (SaleHistoryEntryDto each : saleHistoryEntries) {
			data = new Vector<String>();
			data.add(each.clientName);
			data.add(each.clientDni);
			data.add(String.valueOf(each.saleId));
			data.add(each.saleDate);
			data.add(each.paymentMethod);
			salesHistoryModel.addRow(data);
		}

		tSalesHistoryEntries.setModel(salesHistoryModel);
		tSalesHistoryEntries.revalidate();
	}

	private void populateSaleHistoryEntry() {
		fillProductsInSale();
		fillReturnsInSale();

		tReturns.setModel(returnsModel);
		tReturns.revalidate();

		tSaleEntryData.setModel(saleProductsModel);
		tSaleEntryData.revalidate();

		txtBuildUp.setText("");
		txtTransport.setText("");
		txtTotalSale.setText("");

		txtPreviousTotal.setText("");
		txtNewTotal.setText("");
		txtReturnsTotal.setText("");

		calculateSaleInfo();
		calculateReturnsInfo();

	}

	private void calculateReturnsInfo() {
		float accumulated = 0f;

		for (ReturnDto returnDto : returnedProductsForSelectedSale) {
			for (ProductDtoWrapper productWrapperDto : productsInSelectedSale) {
				if (productWrapperDto.getDto().id == returnDto.productId) {
					accumulated += productWrapperDto.getDto().publicPrice
							* returnDto.quantity;
					break;
				}
			}
		}

		txtPreviousTotal.setText(txtTotalSale.getText());
		txtReturnsTotal.setText(
				'-' + String.valueOf(((accumulated * 100f) / 100f)) + " €");
		double newTotal =
				Float.parseFloat(txtPreviousTotal.getText().split(" ")[0])
						- accumulated;
		newTotal = Round.twoCents(newTotal);
		txtNewTotal.setText(newTotal + " €");
	}

	private void fillReturnsInSale() {
		returnedProductsForSelectedSale = ServiceFactory.createReturnsService()
				.findReturnedProductsForSale(getSelectedSaleId());
		String[] tableColumnNames = getProductsColumnNames();

		List<String> extendedColumnNames = new ArrayList<String>();
		for (String each : tableColumnNames) {
			extendedColumnNames.add(each);
		}
		extendedColumnNames.set(1, "PRECIO");
		extendedColumnNames.add("CANTIDAD");

		returnsModel =
				new NotEditableTableModel(extendedColumnNames.toArray(), 0);

		Vector<String> data;
		for (ReturnDto returnDto : returnedProductsForSelectedSale) {
			for (ProductDtoWrapper productWrapperDto : productsInSelectedSale) {
				if (productWrapperDto.getDto().id == returnDto.productId) {
					data = new Vector<String>();
					data.add(productWrapperDto.getDto().name);
					data.add(String
							.valueOf(productWrapperDto.getDto().publicPrice));
					data.add(productWrapperDto.getDto().category);
					data.add(String.valueOf(returnDto.quantity));
					returnsModel.addRow(data);
					break;
				}
			}
		}

		btnOpenReturnsWindow.setEnabled(true);
		btnGenerateInvoice.setEnabled(true);
	}

	private void fillProductsInSale() {
		productsInSelectedSale = new ArrayList<ProductDtoWrapper>();
		String[] tableColumnNames = new String[] {};

		productsInSelectedSale = findProductsOfSelectedSale();
		tableColumnNames = getProductsColumnNames();

		List<String> extendedColumnNames = new ArrayList<String>();
		for (String each : tableColumnNames) {
			extendedColumnNames.add(each);
		}
		extendedColumnNames.set(1, "PRECIO");
		extendedColumnNames.add("CANTIDAD");

		saleProductsModel =
				new NotEditableTableModel(extendedColumnNames.toArray(), 0);

		Vector<String> data;
		for (ProductDtoWrapper each : productsInSelectedSale) {
			data = new Vector<String>();
			data.add(each.getDto().name);
			data.add(String.valueOf(each.calculatedPrice));
			data.add(each.getDto().category);
			data.add(String.valueOf(each.quantityOrdered));
			saleProductsModel.addRow(data);
		}
	}

	private void resetProductsInSalePanel() {
		txtBuildUp.setText("");
		txtTransport.setText("");
		txtTotalSale.setText("");
		productsInSelectedSale.clear();
		saleProductsModel.getDataVector().clear();
		tSaleEntryData.revalidate();
	}

	private void resetReturnedProductsInSalePanel() {
		txtPreviousTotal.setText("");
		txtNewTotal.setText("");
		txtReturnsTotal.setText("");
		btnOpenReturnsWindow.setEnabled(false);
		btnGenerateInvoice.setEnabled(false);
		returnedProductsForSelectedSale.clear();
		returnsModel.getDataVector().clear();
		tReturns.revalidate();
	}

	private void resetFilters() {
		fromDate.setDate(null);
		toDate.setDate(null);
		btnRemoveFilter.setEnabled(false);
	}

	private String[] getProductsColumnNames() {
		return ServiceFactory.createProductService().getColumnNames();
	}

	private List<ProductDtoWrapper> findProductsOfSelectedSale() {
		return ServiceFactory.createSaleService()
				.findProductsOnSale(getSelectedSaleId());
	}

	private int getSelectedSaleId() {
		return Integer.parseInt(String.valueOf(tSalesHistoryEntries.getModel()
				.getValueAt(getSelectedRowFromSalesHistory(), 2)));
	}

	private int getSelectedRowFromSalesHistory() {
		return tSalesHistoryEntries.getSelectedRow();
	}

	private void calculateSaleInfo() {
		float accumulated = 0;
		TransportDto transport = ServiceFactory.createTransportService()
				.findBySaleId(getSelectedSaleId());
		if (transport != null) {
			txtTransport.setText("SI");
			if (transport.requiresAssembly.equalsIgnoreCase("si")) {
				double assemblyCharge = Double.parseDouble(
						Conf.getInstance("configs/parameters.properties")
								.getProperty("MONTAJE_PRICE"));
				txtBuildUp.setText("SI (+" + assemblyCharge + " €)");
				accumulated += assemblyCharge;
			} else {
				txtBuildUp.setText("NO");
			}
		} else {
			txtTransport.setText("NO");
			txtBuildUp.setText("NO");
		}
		accumulated += fillTxtTotalSale();
		accumulated = (accumulated * 100f) / 100f;
		txtTotalSale.setText(accumulated + " €");
	}

	private double fillTxtTotalSale() {
		float totalAccumulated = 0;

		for (ProductDtoWrapper each : productsInSelectedSale) {
			totalAccumulated +=
					each.quantityOrdered * each.getDto().publicPrice;
		}

		return totalAccumulated;
	}

	private void openReturnsDialog() {
		ProductReturnsDialog dialog = new ProductReturnsDialog(this);
		dialog.setSize(1000, 600);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setVisible(true);
	}

	private void generateInvoiceForSelectedSale() {
		SaleDto saleToInvoice = ServiceFactory.createSaleService()
				.findById(getSelectedSaleId());
		InvoicePdfGenerator generator = new InvoicePdfGenerator(saleToInvoice,
				getProductsInSelectedSale());
		String creationMessage = generator.generate();
		if (!creationMessage.equalsIgnoreCase(
				"Algo salió mal en la creación de la factura")) {
			JOptionPane.showMessageDialog(this,
					"Factura creada correctamente con nombre: "
							+ creationMessage,
					"Exito", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Error creando la factura",
					"Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}
