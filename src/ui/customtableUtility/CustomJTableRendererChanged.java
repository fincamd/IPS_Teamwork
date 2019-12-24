package ui.customtableUtility;

import java.awt.Color;
import java.awt.Component;
import java.util.function.Predicate;

import javax.swing.table.TableCellRenderer;

import common.ColorUtil;
import wrappers.DtoWrapper;

public class CustomJTableRendererChanged<T extends DtoWrapper> extends CustomJTable<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Predicate<String> toCheck;
	private Color colorToChange;
	private int indexOfColumnToCheck;

	public CustomJTableRendererChanged(CustomTableModel<T> model, Predicate<String> toCheck, Color colorToChange,
			int indexOfColumnToCheck) {
		super(model);
		this.toCheck = toCheck;
		this.colorToChange = colorToChange;

		this.indexOfColumnToCheck = indexOfColumnToCheck;
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);

		if (!isRowSelected(row)) {
			c.setBackground(getBackground());
			c.setForeground(this.getForeground());

			String status = this.getCustomModel().getValueAt(row, indexOfColumnToCheck).toString();
			if (toCheck.test(status)) {
				c.setBackground(colorToChange);
				c.setForeground(ColorUtil.getOpositeColor(colorToChange));
			}
		}

		return c;
	}

}
