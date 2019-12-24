package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.SupplierOrderGateway;

public class MarkSupplierOrderAsReceived {
	// Fields
	// ------------------------------------------------------------------------

	private int supplierOrderId;

	// Constructors
	// ------------------------------------------------------------------------

	public MarkSupplierOrderAsReceived(int supplierOrderId) {
		this.supplierOrderId = supplierOrderId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public void execute() throws BusinessException {
		Connection connection = null;
		boolean couldntMarkAsReceived;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			couldntMarkAsReceived = ((SupplierOrderGateway) PersistenceFactory.createSupplierOrderGateway())
					.markOrderAsReceived(supplierOrderId);
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

		if (!couldntMarkAsReceived) {
			throw new BusinessException("No pudimos marcar esa orden como recibida. Quizá no exista.");
		}
	}
}
