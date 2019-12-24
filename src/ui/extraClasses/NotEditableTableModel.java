package ui.extraClasses;

import javax.swing.table.DefaultTableModel;

public class NotEditableTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public NotEditableTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public NotEditableTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

}