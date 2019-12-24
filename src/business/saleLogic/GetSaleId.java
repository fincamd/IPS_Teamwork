package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.SaleGateway;

public class GetSaleId {

	// Fields
	// ------------------------------------------------------------------------

	private int clientId, budgetId;

	// Constructors
	// ------------------------------------------------------------------------

	public GetSaleId(int clientId, int budgetId) {
		this.clientId = clientId;
		this.budgetId = budgetId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public int execute() throws BusinessException {
		Connection connection = null;
		int saleId = -1;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			saleId = ((SaleGateway) PersistenceFactory.createSaleGateway()).findByClientAndBudget(clientId, budgetId);
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

		return saleId;
	}

}
