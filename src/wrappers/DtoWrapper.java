package wrappers;

/**
 * This interface defines the contract that every class whose objects will be
 * stored on the CustomTableModel must follow. Every class that implements this
 * interface must provide how the data will be represented and what headers will
 * be used on the JTable. The arrays returned by both methods must be of the
 * same size. Order insertion of the data and headers on the returned arrays
 * matters if the programmer wants its data to be coherent with the headers, if
 * you want, for example, to show the name and age of a Person, the headers
 * array must be something like: {"Name", "Age"} and the data array must be
 * {"Mark", "18"}, if it is {"18", "Mark"} the headers and data will be
 * inconsistent (on the "Name" column the age "18" will be shown and on the
 * "Age" column the name "Mark" will be show)
 * 
 * @author Angel
 *
 */
public interface DtoWrapper {

	/**
	 * What data will be stored on each cell of the row where the DtoWrapper
	 * reference is stored
	 * 
	 * @return an array with the data
	 */
	public Object[] getData();

	/**
	 * Name of the headers of the JTable where the reference is stored
	 * 
	 * @return an array with the headers
	 */
	public Object[] getHeaders();
}