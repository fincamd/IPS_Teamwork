package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.BudgetDto;
import persistence.Gateway;

/**
 * Represents a Gateway between the business layer class using this and the
 * specific table on the database. This gateway is a Table Data Gateway, so it
 * represents one and only one table on the database. It represents the
 * TPresupuesto table on the database
 * 
 * @author Angel Olmedo García, Daniel Adrian Mare
 *
 */
public class BudgetGateway implements Gateway {

	private Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	/**
	 * Adds a given entity to the specific table
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		String sqlSentence = sqlLoader.getProperty("SQL_INSERT_BUDGET_DEFAULT_STATE");
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		BudgetDto dto = (BudgetDto) obj;
		int id = dto.id;
		Integer client_id = dto.clientId;
		String creationDate = dto.creationDate;
		String expirationDate = dto.expirationDate;

		if (client_id == null) {
			pst.setInt(1, id);
			pst.setString(2, dto.status);
			pst.setString(3, creationDate);
			pst.setString(4, expirationDate);
			pst.setNull(5, java.sql.Types.INTEGER);
		} else {
			pst.setInt(1, id);
			pst.setString(2, dto.status);
			pst.setString(3, creationDate);
			pst.setString(4, expirationDate);
			pst.setInt(5, client_id);
		}

		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
				"presupuesto " + dto.id + " añadido a tabla tpresupuestos con datos: [cliente:" + dto.clientId
				+ "],[fecha de creacion:" + creationDate + "],[fecha de expiracion:" + expirationDate + 
				"],[estado" + dto.status + "]");

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
		List<Object> res = new ArrayList<Object>();
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();
		String sqlStatement = sqlLoader.getProperty("SQL_FIND_ALL_BUDGETS");

		ResultSet rs = st.executeQuery(sqlStatement);
		while (rs.next()) {
			BudgetDto dto = new BudgetDto();

			Integer cId = new Integer(rs.getInt("ID_CLIENTE"));
			if (rs.wasNull())
				dto.clientId = null;
			else
				dto.clientId = cId;
			dto.creationDate = rs.getString("FECHA_CREACION");
			dto.expirationDate = rs.getString("FECHA_CADUCIDAD");
			dto.id = rs.getInt("ID");
			dto.status = rs.getString("ESTADO");

			res.add(dto);
		}

		Jdbc.close(rs, st);
		return res;
	}

	/**
	 * Finds the object represented by a given id
	 * 
	 * @param id - the identifier of the object on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public Object findById(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_BUDGET_BY_ID"));
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			BudgetDto budget = new BudgetDto();
			budget.id = resultSet.getInt("id");
			budget.clientId = resultSet.getInt("id_cliente");
			budget.creationDate = resultSet.getString("fecha_creacion");
			budget.expirationDate = resultSet.getString("fecha_caducidad");
			budget.status = resultSet.getString("estado");
			return budget;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	/**
	 * Finds the biggest identifier on the represented table table
	 * 
	 * @return the biggest identifier
	 * @throws SQLException when some error occurs on the database side
	 * 
	 */
	public int findBiggestId() throws SQLException {
		String sqlSentence = sqlLoader.getProperty("SQL_GET_BIGGEST_ID_BUDGETS");
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();

		ResultSet rs = st.executeQuery(sqlSentence);
		int id = 0;

		if (rs.next()) {
			id = rs.getInt(1);
		}
		Jdbc.close(rs, st);

		return id;
	}

	/**
	 * Finds all the unaccepted budgets that have a client assigned which have not
	 * expired yet and stores them into DTOs that are returned in a list.
	 * 
	 * @return List of Objects that contains the DTOs of all the pending budgets
	 *         retrieved from the database.
	 */
	public List<Object> findAllPendingBudgets() {
		try {
			String sqlSentence = sqlLoader.getProperty("SQL_GET_PENDING_BUDGETS");
			List<Object> budgetList = new ArrayList<Object>();

			Connection c = Jdbc.getCurrentConnection();
			Statement stat = c.createStatement();

			ResultSet rs = stat.executeQuery(sqlSentence);

			while (rs.next()) {
				BudgetDto budget = new BudgetDto();
				budget.id = rs.getInt(1);
				budget.creationDate = rs.getString(2);
				budget.expirationDate = rs.getString(3);
				budget.clientName = rs.getString(4);
				budgetList.add(budget);
			}

			Jdbc.close(rs, stat);
			return budgetList;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void changeToAcepted(int id) throws SQLException {
		Connection conn = Jdbc.getCurrentConnection();
		PreparedStatement pst = conn.prepareStatement(
				Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_SET_STATE_ACCEPTED"));
		pst.setInt(1, id);
		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"el estado del presupuesto " + id + " ha sido cambiado a aceptado");
		
		Jdbc.close(pst);
	}

}
