package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.BusinessException;
import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.BudgetedProductGateway;
import wrappers.ProductDtoWrapper;

public class FindProductsInBudget {

	// Fields
	// ------------------------------------------------------------------------

	private int budgetId;

	// Constructors
	// ------------------------------------------------------------------------

	public FindProductsInBudget(int budgetId) {
		this.budgetId = budgetId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public List<ProductDtoWrapper> execute() throws BusinessException {
		Connection connection = null;
		List<ProductDtoWrapper> budgetedProducts = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			budgetedProducts = ((BudgetedProductGateway) PersistenceFactory.createBudgetedProductGateway())
					.findProductsInBudget(budgetId);
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

		return budgetedProducts;
	}

}
