package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import dtos.BudgetDto;
import factories.PersistenceFactory;

public class GetBudgetById {

	// Fields
	// ------------------------------------------------------------------------

	private int budgetId;

	// Constructors
	// ------------------------------------------------------------------------

	public GetBudgetById(int budgetId) {
		this.budgetId = budgetId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public BudgetDto execute() throws BusinessException {
		Connection connection = null;
		BudgetDto budget = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			budget = (BudgetDto) PersistenceFactory.createBudgetGateway().findById(budgetId);
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		} finally {
			Jdbc.close(connection);
		}

		return budget;
	}

}
