package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.ProductGateway;

public class GetProductTableColumnNames {

	// Basic methods
	// ------------------------------------------------------------------------

	public String[] execute() {
		Connection connection = null;
		String[] columnNames = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			columnNames = ((ProductGateway) PersistenceFactory.createProductGateway()).getColumnNames();
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

		return columnNames;
	}

}
