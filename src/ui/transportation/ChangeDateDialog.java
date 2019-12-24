package ui.transportation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.toedter.calendar.JCalendar;

import business.serviceLayer.implementation.TransportServiceImplementation;
import business.transport.ChangeDate;
import business.transport.ChangeTime;
import common.Dates;
import dtos.TransportDto;
import factories.ServiceFactory;

public class ChangeDateDialog extends JDialog {

	private static final long serialVersionUID = 8811890385122717857L;

	private JPanel pnCalendar;
	private JPanel pnButtons;
	private JButton btnCancelar;
	private JButton btnAceptar;
	private JCalendar calendar;

	private TransportDto dto;
	private JLabel lblHora;
	private JLabel lblMinutos;
	private JComboBox<String> cbHora;
	private JComboBox<String> cbMinutos;

	public static void main(String[] args) {
		try {
			TransportDto t = new TransportDto();
			t.deliveryDate = "23/11/2019";
			ChangeDateDialog dialog = new ChangeDateDialog(t);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChangeDateDialog(TransportDto dto) {
		setResizable(false);
		setTitle("Fecha de entrega");
		setModal(true);
		setBounds(100, 100, 464, 310);
		this.dto = dto;
		getContentPane().setLayout(null);
		getContentPane().add(getPnCalendar());
		getContentPane().add(getPnButtons());
		getContentPane().add(getLblHora());
		getContentPane().add(getLblMinutos());
		getContentPane().add(getCbHora());
		getContentPane().add(getCbMinutos());
	}

	private JPanel getPnCalendar() {
		if (pnCalendar == null) {
			pnCalendar = new JPanel();
			pnCalendar.setBounds(0, 0, 311, 228);
			pnCalendar.setLayout(new BorderLayout(0, 0));
			pnCalendar.add(getCalendar(), BorderLayout.CENTER);
		}
		return pnCalendar;
	}

	private JPanel getPnButtons() {
		if (pnButtons == null) {
			pnButtons = new JPanel();
			pnButtons.setBounds(0, 230, 458, 51);
			pnButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			pnButtons.add(getBtnAceptar());
			pnButtons.add(getBtnCancelar());
		}
		return pnButtons;
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
					if (cbHora.getSelectedIndex() >= 0 && cbMinutos.getSelectedIndex() >= 0 && cbMinutos.isEnabled()) {
						dto.deliveryDate = Dates.toString(calendar.getDate());
						if (cbHora.getSelectedItem().toString().length() < 2) {
							dto.deliveryTime = "0" + cbHora.getSelectedItem().toString() + ":"
									+ cbMinutos.getSelectedItem().toString();
						} else {
							dto.deliveryTime = cbHora.getSelectedItem().toString() + ":"
									+ cbMinutos.getSelectedItem().toString();
						}

						ChangeDate ct = new ChangeDate(dto.saleId, dto.deliveryDate);
						ct.execute();

						ChangeTime cd = new ChangeTime(dto.saleId, dto.deliveryTime);
						cd.execute();

						dispose();
					} else {
						JOptionPane.showMessageDialog(rootPane, "Selecciona una hora válida.");
					}
				}
			});
			btnAceptar.setMnemonic('a');
		}
		return btnAceptar;
	}

	private JCalendar getCalendar() {
		if (calendar == null) {
			calendar = new JCalendar();
			calendar.getDayChooser().addDateEvaluator(new DateEspecificator());
			calendar.setMinSelectableDate(new Date());
			try {
				calendar.setDate(Dates.fromString(dto.deliveryDate));
			} catch (NullPointerException e) {
				calendar.setDate(new Date());
				if (calendar.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					calendar.setDate(Dates.addDays(calendar.getDate(), 1));
				}
			}
			for (Component c : calendar.getDayChooser().getDayPanel().getComponents()) {
				c.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (cbHora.getSelectedIndex() < 0) {
							return;
						}
						resetcbHora();
					}
				});
			}
		}
		return calendar;
	}

	private JLabel getLblHora() {
		if (lblHora == null) {
			lblHora = new JLabel("Hora:");
			lblHora.setDisplayedMnemonic('h');
			lblHora.setLabelFor(getCbHora());
			lblHora.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblHora.setBounds(332, 50, 48, 14);
		}
		return lblHora;
	}

	private JLabel getLblMinutos() {
		if (lblMinutos == null) {
			lblMinutos = new JLabel("Minutos:");
			lblMinutos.setLabelFor(getCbMinutos());
			lblMinutos.setDisplayedMnemonic('m');
			lblMinutos.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lblMinutos.setBounds(332, 132, 48, 14);
		}
		return lblMinutos;
	}

	private JComboBox<String> getCbHora() {
		if (cbHora == null) {
			cbHora = new JComboBox<String>();
			cbHora.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					resetcbHora();
				}
			});
			if (calendar.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				for (int i = 10; i <= 14; i++) {
					cbHora.addItem(String.valueOf(i));
				}
			} else {
				for (int i = 9; i <= 20; i++) {
					cbHora.addItem(String.valueOf(i));
				}
			}
			cbHora.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (cbHora.getSelectedIndex() < 0) {
						return;
					}
					resetcbMinutos();
				}
			});

			cbHora.setFont(new Font("Tahoma", Font.PLAIN, 12));
			cbHora.setBounds(329, 75, 105, 22);
		}
		return cbHora;
	}

	private JComboBox<String> getCbMinutos() {
		if (cbMinutos == null) {
			cbMinutos = new JComboBox<String>();
			cbMinutos.setEnabled(false);
			cbMinutos.setFont(new Font("Tahoma", Font.PLAIN, 12));
			cbMinutos.setBounds(332, 157, 105, 22);
		}
		return cbMinutos;
	}

	private void resetcbMinutos() {
		ArrayList<String> min;
		TransportServiceImplementation t = (TransportServiceImplementation) ServiceFactory.createTransportService();

		if (calendar.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			min = (ArrayList<String>) t.getAvailableMinutesForHourInSaturday(Dates.toString(calendar.getDate()),
					Integer.parseInt((String) cbHora.getSelectedItem()));
		} else {
			min = (ArrayList<String>) t.GetAvailableMinutesForHourInWeekDay(Dates.toString(calendar.getDate()),
					Integer.parseInt((String) cbHora.getSelectedItem()));
		}

		cbMinutos.removeAllItems();
		if (min.isEmpty()) {
			cbMinutos.addItem("Sin disponivilidad");
			cbMinutos.setSelectedIndex(0);
			cbMinutos.setEnabled(false);
		} else {
			for (int i = 0; i < min.size(); i++) {
				cbMinutos.addItem(min.get(i));
			}
			cbMinutos.setSelectedIndex(0);
			cbMinutos.setEnabled(true);
		}
	}

	private void resetcbHora() {
		cbHora.removeAllItems();
		if (calendar.getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			for (int i = 10; i <= 14; i++) {
				cbHora.addItem(String.valueOf(i));
			}
		} else {
			for (int i = 9; i <= 20; i++) {
				cbHora.addItem(String.valueOf(i));
			}
		}
		cbHora.setSelectedIndex(0);
		cbMinutos.setSelectedIndex(-1);
		cbMinutos.setEnabled(false);
	}
}
