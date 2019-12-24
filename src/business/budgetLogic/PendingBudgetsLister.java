package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.Dates;
import common.Jdbc;
import dtos.BudgetDto;
import factories.PersistenceFactory;
import persistence.implementation.BudgetGateway;

/**
 * This class lists all pending budgets from the database. Considering as
 * pending budgets those that have a client assigned, have not expired and have
 * not been accepted yet.
 * 
 * @author Daniel Adrian Mare
 *
 */

public class PendingBudgetsLister {
	private BudgetGateway mg = (BudgetGateway) PersistenceFactory.createBudgetGateway();

	private Connection c = null;
	private List<BudgetDto> budgetList;

	/**
	 * Lists all the budgets that have not been accepted yet as a sale, that have
	 * not expired yet and have a client assigned.
	 * 
	 * @return List that contains all pending budgets.
	 */
	public List<BudgetDto> listAllPendingBudgets() {
		try {
			budgetList = new ArrayList<BudgetDto>();
			List<Object> temp;

			c = Jdbc.createThreadConnection();
			c.setAutoCommit(false);

			temp = mg.findAllPendingBudgets();

			for (Object each : temp) {
				BudgetDto budget = (BudgetDto) each;
				if (isNotExpired(budget))
					budgetList.add(budget);

			}

			c.commit();
		} catch (SQLException e) {
			try {
				c.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(c);
		}
		return budgetList;
	}

	private boolean isNotExpired(BudgetDto budget) {
		Date today = Dates.today();
		Date budgetExpirationDate = Dates.fromString(budget.expirationDate);
		return Dates.isBefore(today, budgetExpirationDate);
	}
}
