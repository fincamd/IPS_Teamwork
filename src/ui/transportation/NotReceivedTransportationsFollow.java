package ui.transportation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import business.serviceLayer.TransportationService;
import common.Conf;
import common.DtoUtility;
import dtos.TransportDto;
import factories.ServiceFactory;
import ui.customtableUtility.CustomJTableRendererChanged;
import ui.customtableUtility.CustomTableModel;
import wrappers.TransportDtoWrapper;

public class NotReceivedTransportationsFollow extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pnTitle;
	private JPanel pnDay;
	private JPanel pnTransportations;
	private JLabel lblPendingTransportations;
	private JLabel lblDay;

	private Conf parameters = Conf.getInstance("configs/parameters.properties");
	private JScrollPane spNotRecievedTransportations;
	private CustomJTableRendererChanged<TransportDtoWrapper> tblNotRevievedTransportations;
	private JPanel pnChangeDate;
	private JButton btnChangeTransportationDate;

	private DoubleClickMouseListener dcmL = new DoubleClickMouseListener();

	/**
	 * Create the panel.
	 */
	public NotReceivedTransportationsFollow() {
		setLayout(new BorderLayout(0, 0));
		add(getPnTitle(), BorderLayout.NORTH);
		add(getPnTransportations(), BorderLayout.CENTER);
		add(getPnDay(), BorderLayout.SOUTH);

	}

	private JPanel getPnTitle() {
		if (pnTitle == null) {
			pnTitle = new JPanel();
			pnTitle.add(getLblTransportesARealizar());
		}
		return pnTitle;
	}

	private JPanel getPnDay() {
		if (pnDay == null) {
			pnDay = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnDay.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnDay.add(getLblDay());
		}
		return pnDay;
	}

	private JPanel getPnTransportations() {
		if (pnTransportations == null) {
			pnTransportations = new JPanel();
			GridBagLayout gbl_pnTransportations = new GridBagLayout();
			gbl_pnTransportations.columnWidths = new int[] { 0, 0 };
			gbl_pnTransportations.rowHeights = new int[] { 0, 0, 0 };
			gbl_pnTransportations.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_pnTransportations.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
			pnTransportations.setLayout(gbl_pnTransportations);
			GridBagConstraints gbc_spNotRecievedTransportations = new GridBagConstraints();
			gbc_spNotRecievedTransportations.insets = new Insets(0, 0, 5, 0);
			gbc_spNotRecievedTransportations.fill = GridBagConstraints.BOTH;
			gbc_spNotRecievedTransportations.gridx = 0;
			gbc_spNotRecievedTransportations.gridy = 0;
			pnTransportations.add(getSpNotRecievedTransportations(), gbc_spNotRecievedTransportations);
			GridBagConstraints gbc_pnChangeDate = new GridBagConstraints();
			gbc_pnChangeDate.fill = GridBagConstraints.BOTH;
			gbc_pnChangeDate.gridx = 0;
			gbc_pnChangeDate.gridy = 1;
			pnTransportations.add(getPnChangeDate(), gbc_pnChangeDate);
		}
		return pnTransportations;
	}

	private JLabel getLblTransportesARealizar() {
		if (lblPendingTransportations == null) {
			lblPendingTransportations = new JLabel("Transportes a Realizar");
			lblPendingTransportations.setHorizontalAlignment(SwingConstants.CENTER);
			lblPendingTransportations.setFont(new Font("Tahoma", Font.BOLD, 15));
		}
		return lblPendingTransportations;
	}

	private JLabel getLblDay() {
		if (lblDay == null) {
			lblDay = new JLabel("Hoy es: " + getTodayDayAsString());
			lblDay.setFont(new Font("Tahoma", Font.BOLD, 13));
			lblDay.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return lblDay;
	}

	private String getTodayDayAsString() {
		ZoneId zone = ZoneId.of(parameters.getProperty("ES_LOCALE"));
		LocalDate today = LocalDate.now(zone);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE").withZone(zone);

		String possibleRes = today.format(format);
		String res = possibleRes.substring(0, 1).toUpperCase().concat(possibleRes.substring(1, possibleRes.length()));
		return res;
	}

	private JScrollPane getSpNotRecievedTransportations() {
		if (spNotRecievedTransportations == null) {
			spNotRecievedTransportations = new JScrollPane();
			spNotRecievedTransportations.setViewportView(getTblNotRevievedTransportations());
		}
		return spNotRecievedTransportations;
	}

	private CustomJTableRendererChanged<TransportDtoWrapper> getTblNotRevievedTransportations() {
		if (tblNotRevievedTransportations == null) {
			CustomTableModel<TransportDtoWrapper> model = initialJTableTransportList();

			Predicate<String> checker = (string) -> {
				return string.toUpperCase().equals(parameters.getProperty("STATUS_TO_HIGHLIGHT_ON_RED"));
			};
			int index = getIndexToCheck();

			tblNotRevievedTransportations = new CustomJTableRendererChanged<TransportDtoWrapper>(model, checker,
					Color.RED, index);
			tblNotRevievedTransportations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			tblNotRevievedTransportations.setName(parameters.getProperty("ACTION_COMMAND_TBL_PRODUCT_TABLE"));
			tblNotRevievedTransportations.addMouseListener(dcmL);
		}
		return tblNotRevievedTransportations;
	}

	private int getIndexToCheck() {
		try {
			return Integer.parseInt(parameters.getProperty("INDEX_OF_HIGHLIGHT_CHECK"));
		} catch (NumberFormatException e) {
			throw new RuntimeException(
					"Changed the parameters.properties property \"INDEX_OF_HIGHLIGHT_CHECK\" cannot continue with the application initialization");
		}
	}

	private CustomTableModel<TransportDtoWrapper> initialJTableTransportList() {
		TransportationService service = ServiceFactory.createTransportService();
		List<TransportDto> dtos = service.getNotDeliveredTranports();

		List<TransportDtoWrapper> wrappedDtos = new ArrayList<>();
		for (TransportDto dto : dtos) {
			wrappedDtos.add(new TransportDtoWrapper(dto));
		}

		CustomTableModel<TransportDtoWrapper> model = new CustomTableModel<TransportDtoWrapper>(wrappedDtos);

		return model;
	}

	private JPanel getPnChangeDate() {
		if (pnChangeDate == null) {
			pnChangeDate = new JPanel();
			FlowLayout flowLayout = (FlowLayout) pnChangeDate.getLayout();
			flowLayout.setAlignment(FlowLayout.TRAILING);
			pnChangeDate.add(getBtnChangeTransportationDate());
		}
		return pnChangeDate;
	}

	private JButton getBtnChangeTransportationDate() {
		if (btnChangeTransportationDate == null) {
			btnChangeTransportationDate = new JButton("Cambiar Fecha Entrega");
			btnChangeTransportationDate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					changeDateOfSelected();
				}
			});
			btnChangeTransportationDate.setEnabled(false);
		}
		return btnChangeTransportationDate;
	}

	private void changeDateOfSelected() {
		TransportDto dtoToUpdateDate = tblNotRevievedTransportations.getCustomModel()
				.getValueAtRow(tblNotRevievedTransportations.getSelectedRow()).getDto();

		TransportDto dtoCopy = DtoUtility.createTransportDtoCopy(dtoToUpdateDate);

		ChangeDateDialog changeDialog = new ChangeDateDialog(dtoToUpdateDate);
		changeDialog.setModal(true);
		changeDialog.setLocationRelativeTo(this);
		changeDialog.setVisible(true);

		if (dtoCopy.id == dtoToUpdateDate.id && !(dtoToUpdateDate.deliveryDate.equals(dtoCopy.deliveryDate)
				&& dtoToUpdateDate.deliveryTime.equals(dtoCopy.deliveryTime))) {
			TransportationService service = ServiceFactory.createTransportService();
			service.setStatus(dtoToUpdateDate, "SOLICITADO");
			resetWindow();
		}

	}

	public void reset() {
		resetWindow();
	}

	private void resetWindow() {
		initializeJTableValues();
		btnChangeTransportationDate.setEnabled(false);
	}

	private void initializeJTableValues() {
		CustomTableModel<TransportDtoWrapper> model = initialJTableTransportList();
		tblNotRevievedTransportations.setModel(model);
	}

	public class DoubleClickMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (tblNotRevievedTransportations.getSelectedRow() != -1) {
				btnChangeTransportationDate.setEnabled(true);
				if (e.getClickCount() == 2 && btnChangeTransportationDate.isEnabled())
					changeDateOfSelected();
			}
		}
	}
}
