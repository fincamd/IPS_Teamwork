package persistence;

import java.sql.SQLException;
import java.util.List;

/**
 * Represents the interface that any gateway must implement. The methods given
 * represent a canonical gateway, that is, a gateway with only CRUD operations
 * and an extra read operation by id. The gateways must be Table data gateways,
 * that is, each gateway communicates with one and only one table on the
 * underlying database
 * 
 * @author Angel
 *
 */
public interface Gateway {

	/**
	 * Add a given entity to the database
	 * 
	 * @param obj - the entity to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	public void add(Object obj) throws SQLException;

	/**
	 * Removes an entity from the database
	 * 
	 * @param id - identifier of the entity that needs to be deleted from the
	 *           database
	 * @return the number of changed rows on the database deleting
	 * @throws SQLException when some error occurs on the database side
	 */
	public int delete(int id) throws SQLException;

	/**
	 * Updates an entity from the database
	 * 
	 * @param obj - the entity with the new data that will be inserted on the
	 *            database
	 * @param obj - the entity with the new data that will be inserted on the
	 *            database
	 * @return the number of changed rows on the database after updating
	 * @throws SQLException when some error occurs on the database side
	 */
	public int update(Object obj) throws SQLException;

	/**
	 * Finds every entity on the table represented by the gateway
	 * 
	 * @return the list of entities retrieved from the database
	 * @throws SQLException when some error occurs on the database side
	 */
	public List<Object> findAll() throws SQLException;

	/**
	 * Finds one entity with an specific identifier
	 * 
	 * @param id - the identifier on the database that needs to be found
	 * @return the entity whose identifier is the same as the value passed as
	 *         parameter
	 * @throws SQLException when some error occurs on the database side
	 */
	public Object findById(int id) throws SQLException;

}
