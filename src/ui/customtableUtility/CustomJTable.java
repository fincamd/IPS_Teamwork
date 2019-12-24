package ui.customtableUtility;

import javax.swing.JTable;

import wrappers.DtoWrapper;

/**
 * Customized JTable that works like a normal JTable but stores the
 * CustomJTableModel since users of this CustomJTable will use it to obtain this
 * specific Table Model.
 * 
 * @author Angel
 * @param <T> bounded generic that assures that it extends the DtoWrapper
 *            interface that defines the contract that every generic must
 *            fulfill
 */
public class CustomJTable<T extends DtoWrapper> extends JTable {

	private static final long serialVersionUID = 1L;
	private CustomTableModel<T> model;

	public CustomJTable(CustomTableModel<T> model) {
		super(model);
		this.model = model;
	}

	/**
	 * Returns the specific TableModel CustomTableModel. It is not possible to
	 * override the getModel() method of the JTable because we need the specific
	 * CustomTableModel. Also, the CustomJTable will not work as intended if the
	 * getModel() method is overridden
	 * 
	 * @return the CustomTableModel of this CustomJTable
	 */
	public CustomTableModel<T> getCustomModel() {
		return model;
	}

	/**
	 * Updates the CustomTableModel of the JTable
	 * 
	 * @param model - the new CustomTableModel to be set as the model of the JTable
	 */
	public void setModel(CustomTableModel<T> model) {
		super.setModel(model);
		this.model = model;
	}

}
