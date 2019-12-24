package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.BudgetDto;
import factories.PersistenceFactory;
import persistence.Gateway;

/**
 * Logic class that communicates with the data access layer in order to create a
 * new Budget on the database
 * 
 * @author Angel
 *
 */
public class BudgetAdder {

	private BudgetDto dto;

	public BudgetAdder(BudgetDto dto) {
		this.dto = dto;
	}

	/**
	 * Inserts a budget to the database
	 * 
	 * @return true if everything went fine, false if some problem happened
	 */
	public boolean addBudget() {
		Connection con = null;
		boolean valid = false;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
			budgetGateway.add(dto);
			con.commit();
			valid = true;
		} catch (SQLException e) {
			try {
				con.rollback();
				return valid;
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}

		return valid;
	}

}
