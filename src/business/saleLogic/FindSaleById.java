package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.SaleDto;
import factories.PersistenceFactory;

public class FindSaleById {

	// Fields
	// ------------------------------------------------------------------------

	private int saleId;

	// Constructors
	// ------------------------------------------------------------------------

	public FindSaleById(int saleId) {
		this.saleId = saleId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public SaleDto execute() {
		Connection connection = null;
		SaleDto sale = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			sale = (SaleDto) PersistenceFactory.createSaleGateway().findById(saleId);
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

		return sale;
	}

}
