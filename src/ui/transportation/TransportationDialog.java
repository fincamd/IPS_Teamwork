package ui.transportation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import business.serviceLayer.BudgetService;
import business.serviceLayer.SupplierOrderService;
import business.serviceLayer.implementation.SaleServiceImplementation;
import common.BusinessException;
import dtos.SaleDto;
import dtos.SupplierOrderedProductDto;
import dtos.TransportDto;
import factories.ServiceFactory;
import ui.budget.ShowPendingBudgetsPanel;
import ui.directSales.DirectSales;

public class TransportationDialog extends JDialog {

	private static final long serialVersionUID = 3204757633392075139L;

	private final JPanel contentPanel = new JPanel();
	private JLabel lblMedioDeTransporte;
	private JComboBox<String> comboBoxTransporte;
	private JCheckBox chckbxMontaje;
	private JLabel lblFechaDeEntrega;
	private JButton btnFechaDeEntrega;
	private JLabel lblEleccinDeTransporte;
	private JButton btnCancelar;
	private JButton btnAceptar;

	private SaleDto sdto = null;
	private TransportDto tdto;
	private List<SupplierOrderedProductDto> missingProducts;
	private JLabel lblFecha;

	private TransportationDialog me = this;
	ShowPendingBudgetsPanel pendingBudgetsPanel;
	DirectSales directSales;

	/**
	 * Constructor for creation from Pending Budgets Panel
	 * 
	 * @param parent
	 */
	public TransportationDialog(SaleDto dto,
		ShowPendingBudgetsPanel pendingBudgetsPanel) {
		this.pendingBudgetsPanel = pendingBudgetsPanel;
		setResizable(false);
		setModal(true);
		setTitle("Transporte");
		setBounds(100, 100, 457, 275);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getChckbxMontaje());
		contentPanel.add(getLblFechaDeEntrega());
		contentPanel.add(getBtnFechaDeEntrega());
		contentPanel.add(getLblFecha());
		contentPanel.add(getLblEleccinDeTransporte());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.add(getBtnAceptar());
			buttonPane.add(getBtnCancelar());
		}
		contentPanel.add(getComboBoxTransporte());
		contentPanel.add(getLblMedioDeTransporte());
		this.sdto = dto;
		tdto = new TransportDto();
	}

	public TransportationDialog(DirectSales directSales) {
		this.directSales = directSales;
		setResizable(false);
		setModal(true);
		setTitle("Transporte");
		setBounds(100, 100, 457, 275);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		contentPanel.add(getChckbxMontaje());
		contentPanel.add(getLblFechaDeEntrega());
		contentPanel.add(getBtnFechaDeEntrega());
		contentPanel.add(getLblFecha());
		contentPanel.add(getLblEleccinDeTransporte());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.add(getBtnAceptar());
			buttonPane.add(getBtnCancelar());
		}
		contentPanel.add(getComboBoxTransporte());
		contentPanel.add(getLblMedioDeTransporte());
		tdto = new TransportDto();
	}

	private JLabel getLblMedioDeTransporte() {
		if (lblMedioDeTransporte == null) {
			lblMedioDeTransporte = new JLabel("Medio de transporte:");
			lblMedioDeTransporte.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblMedioDeTransporte.setLabelFor(getComboBoxTransporte());
			lblMedioDeTransporte.setBounds(38, 53, 130, 24);
		}
		return lblMedioDeTransporte;
	}

	private JComboBox<String> getComboBoxTransporte() {
		if (comboBoxTransporte == null) {
			comboBoxTransporte = new JComboBox<String>();
			comboBoxTransporte.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (comboBoxTransporte.getSelectedIndex() == 0) {
						getChckbxMontaje().setEnabled(false);
						getBtnFechaDeEntrega().setEnabled(false);
						lblFecha.setEnabled(false);
					} else {
						getChckbxMontaje().setEnabled(true);
						getBtnFechaDeEntrega().setEnabled(true);
						lblFecha.setEnabled(true);
					}
				}

			});
			// in this sprint there's just two transporsts
			ArrayList<String> transports = new ArrayList<String>();
			transports.add("Ninguno");
			transports.add("Estándar");
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(
				new Vector<String>(transports));
			//
			comboBoxTransporte.setModel(model);
			comboBoxTransporte.setSelectedIndex(0);
			comboBoxTransporte.setFont(new Font("Tahoma", Font.PLAIN, 12));
			comboBoxTransporte.setBounds(178, 53, 230, 24);
		}
		return comboBoxTransporte;
	}

	private JCheckBox getChckbxMontaje() {
		if (chckbxMontaje == null) {
			chckbxMontaje = new JCheckBox("Montaje");
			chckbxMontaje.setEnabled(false);
			chckbxMontaje.setMnemonic('m');
			chckbxMontaje.setFont(new Font("Tahoma", Font.PLAIN, 12));
			chckbxMontaje.setBounds(38, 102, 97, 23);

		}
		return chckbxMontaje;
	}

	private JLabel getLblFechaDeEntrega() {
		if (lblFechaDeEntrega == null) {
			lblFechaDeEntrega = new JLabel("Fecha de entrega:");
			lblFechaDeEntrega.setLabelFor(getBtnFechaDeEntrega());
			lblFechaDeEntrega.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblFechaDeEntrega.setBounds(38, 153, 130, 24);
		}
		return lblFechaDeEntrega;
	}

	private JButton getBtnFechaDeEntrega() {
		if (btnFechaDeEntrega == null) {
			btnFechaDeEntrega = new JButton("Fecha de entrega");
			btnFechaDeEntrega.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					crearDialogoDeCalendario();
				}

			});
			btnFechaDeEntrega.setEnabled(false);
			btnFechaDeEntrega.setMnemonic('f');
			btnFechaDeEntrega.setFont(new Font("Tahoma", Font.PLAIN, 12));
			btnFechaDeEntrega.setBounds(178, 153, 130, 24);
		}
		return btnFechaDeEntrega;
	}

	private void crearDialogoDeCalendario() {
		CalendarDialog dialog = new CalendarDialog(tdto);
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
		if (tdto.deliveryDate == null) {
			lblFecha.setText("Ninguna");
		} else
			lblFecha.setText(tdto.deliveryDate + " - " + tdto.deliveryTime);
	}

	private JLabel getLblEleccinDeTransporte() {
		if (lblEleccinDeTransporte == null) {
			lblEleccinDeTransporte = new JLabel("Elecci\u00F3n de transporte");
			lblEleccinDeTransporte.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblEleccinDeTransporte.setBounds(150, 11, 147, 24);
		}
		return lblEleccinDeTransporte;
	}

	private JButton getBtnCancelar() {
		if (btnCancelar == null) {
			btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}

			});
			btnCancelar.setMnemonic('c');
		}
		return btnCancelar;
	}

	private JButton getBtnAceptar() {
		if (btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (sdto == null) {
						transportForDirectSale();
					} else {
						if (!comboBoxTransporte	.getSelectedItem()
												.equals("Ninguno")) {
							if (tdto.deliveryDate != null
								&& tdto.deliveryTime != null) {
								// transportation
								tdto.id = 1; // since there is no transport
												// 1 is added as an id
								tdto.status = "SOLICITADO"; // Parcheao

								// TODO
								/// QUITAR
								tdto.saleId = sdto.id;
								tdto.idTransportista = 1; // falta referencia
								///

								// montaje
								if (getChckbxMontaje().isSelected())
									tdto.requiresAssembly = "SI";
								else
									tdto.requiresAssembly = "NO";
							} else {
								JOptionPane.showMessageDialog(me,
									"Tiene que elegir una fecha y hora para el transporte.");
								return;
							}
						} else {
							tdto.id = 0;
							tdto.requiresAssembly = "NO";
							tdto.deliveryDate = null;
							tdto.deliveryTime = null;
						}

						checkAllBudgetItemsInStock();
						if (!missingProducts.isEmpty()) {
							// createOrderForMissingProducts(sdto.id);
							tdto.status = "CANCELADO";
						}
						SaleServiceImplementation s = (SaleServiceImplementation) ServiceFactory.createSaleService();
						try {
							int saleId = s.addSale(sdto, tdto);

							if (!missingProducts.isEmpty())
								createOrderForMissingProducts(saleId);

						} catch (BusinessException e1) {
							e1.printStackTrace();
						}
						pendingBudgetsPanel.reloadPendingBudgetsTable();
						dispose();
					}
				}

			});
			btnAceptar.setMnemonic('a');

		}
		return btnAceptar;
	}

	private void transportForDirectSale() {
		if (!comboBoxTransporte.getSelectedItem().equals("Ninguno")) {
			if (tdto.deliveryDate != null && tdto.deliveryTime != null) {
				// transportation
				tdto.id = 1; // since there is no transport
								// 1 is added as an id
				tdto.status = "SOLICITADO"; // Parcheao

				tdto.idTransportista = 1; // falta referencia

				// montaje
				if (getChckbxMontaje().isSelected())
					tdto.requiresAssembly = "SI";
				else
					tdto.requiresAssembly = "NO";
			} else {
				JOptionPane.showMessageDialog(me,
					"Tiene que elegir una fecha y hora para el transporte.");
				return;
			}
		} else {
			tdto.id = 0;
			tdto.requiresAssembly = "NO";
			tdto.deliveryDate = null;
			tdto.deliveryTime = null;
		}

		directSales.confirmSale(tdto);
		directSales.reloadWindow();
		dispose();

	}

	protected void createOrderForMissingProducts(int saleId) {
		SupplierOrderService service = ServiceFactory.createSupplierOrderService();
		service.createSupplierOrderToSale(missingProducts, saleId);

	}

	protected void checkAllBudgetItemsInStock() {
		BudgetService service = ServiceFactory.createBudgetService();
		missingProducts = service.areBudgetItemsInStock(sdto.budgetId);
	}

	private JLabel getLblFecha() {
		if (lblFecha == null) {
			lblFecha = new JLabel("Ninguna");
			lblFecha.setEnabled(false);
			lblFecha.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblFecha.setBounds(318, 153, 123, 24);
		}
		return lblFecha;
	}

}
