package ui.customtableUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import wrappers.DtoWrapper;

/**
 * This model allows users access and store references of objects. This model is
 * generic, the generic type parameter is bounded, every class that is going to
 * be stored on this model must implement the DtoWrapper interface (see
 * documentation of DtoWrapper). If the no parameterized constructor is used, no
 * reference will be stored so, if the programmer´s purpose is to use this
 * constructor, the usage of the Swing JTable is recommended
 * 
 * @author Angel
 *
 * @param <T> bounded generic that assures that it extends the DtoWrapper
 *            interface that defines the contract that every generic must
 *            fulfill
 */
public class CustomTableModel<T extends DtoWrapper> extends DefaultTableModel {
	private static final long serialVersionUID = 1L;

	private List<T> dataReferences = new ArrayList<>();

	/**
	 * Copies all the objects inside the parameter and initializes the JTable data
	 * and header vectors.
	 * 
	 * @param dataReferences - references to be stored on the Model
	 */
	public CustomTableModel(List<T> dataReferences) {

		if (dataReferences != null) {
			for (T reference : dataReferences) {
				this.dataReferences.add(reference);
			}

			if (dataReferences.size() == 0) {
				setDataVector(new Vector<Vector<Object>>(), new Vector<>());
			} else {
				T sampleWrapper = this.dataReferences.get(0);
				if (sampleWrapper.getHeaders().length != sampleWrapper.getData().length)
					throw new IllegalArgumentException(
							"The class implementing the DtoWrapper interface returns data and headers of different size");

				Vector<Object> columnIdentifiers = new Vector<Object>(Arrays.asList(sampleWrapper.getHeaders()));

				Vector<Vector<Object>> dataAsVector = new Vector<Vector<Object>>();
				for (T reference : this.dataReferences) {
					List<Object> dataForRow = Arrays.asList(reference.getData());
					Vector<Object> row = new Vector<>(dataForRow);
					dataAsVector.add(row);
				}

				setDataVector(dataAsVector, columnIdentifiers);
			}

		}

	}

	public CustomTableModel(Object[] headers) {
		super(headers, 0);
	}

	public CustomTableModel() {
		super();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public List<T> getDataReferences() {
		return dataReferences;
	}

	/**
	 * Returns the reference that is stored on a given row
	 * 
	 * @param row - row where the reference data is
	 * @return the reference on the given row
	 */
	public T getValueAtRow(int row) {
		if (row < dataReferences.size() && row >= 0)
			return dataReferences.get(row);
		else
			throw new IllegalArgumentException(
					"The row is not valid, no reference found for the given row. row out of bounds");
	}

}