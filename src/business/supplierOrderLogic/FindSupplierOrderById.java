package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import dtos.SupplierOrderDto;
import factories.PersistenceFactory;

public class FindSupplierOrderById {

	// Fields
	// ------------------------------------------------------------------------

	private int supplierOrderId;

	// Constructors
	// ------------------------------------------------------------------------

	public FindSupplierOrderById(int supplierOrderId) {
		this.supplierOrderId = supplierOrderId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public SupplierOrderDto execute() throws BusinessException {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			SupplierOrderDto supplierOrder = (SupplierOrderDto) PersistenceFactory.createSupplierOrderGateway()
					.findById(supplierOrderId);
			connection.commit();
			return supplierOrder;
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
	}

}
