package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.BudgetProductDto;
import persistence.Gateway;

/**
 * Represents a Gateway between the business layer class using this and the
 * specific table on the database. This gateway is a Table Data Gateway, so it
 * represents one and only one table on the database. It represents the
 * TPresupuestados table on the database
 * 
 * @author Angel
 *
 */
public class BudgetProductGateway implements Gateway {

	Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	/**
	 * Adds a given entity to the specific table
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		BudgetProductDto dto = (BudgetProductDto) obj;
		String sqlStatement = sqlLoader.getProperty("SQL_ASSIGN_PRODUCT_TO_BUDGET");
		Connection con = Jdbc.getCurrentConnection();
		PreparedStatement pst = con.prepareStatement(sqlStatement);

		pst.setInt(1, dto.budgetId);
		pst.setInt(2, dto.productId);
		pst.setInt(3, dto.amount);
		pst.setDouble(4, dto.price);

		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"producto " + dto.productId + " añadido para el presupuesto "+dto.budgetId+
			" con datos: [cantidad:" + dto.amount + "],[precio:" + dto.price + "]");

		Jdbc.close(pst);
	}

	/**
	 * Deletes an entity from the specific table on the database
	 * 
	 * @param id - the identifier of the entity
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Updates the information of a given entity on the specific database
	 * 
	 * @param obj - the entity that encapsulates the new values that will be
	 *            represented on the database
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int update(Object obj) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Finds all the entities on the represented table on the database
	 * 
	 * @return a collection of the found entities
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public List<Object> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Finds the object represented by a given id
	 * 
	 * @param id - the identifier of the object on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public Object findById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
