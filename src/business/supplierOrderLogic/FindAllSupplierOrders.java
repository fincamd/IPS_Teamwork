package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.BusinessException;
import common.Jdbc;
import dtos.SupplierOrderDto;
import factories.PersistenceFactory;

public class FindAllSupplierOrders {

	// Basic methods
	// ------------------------------------------------------------------------

	public List<SupplierOrderDto> execute() throws BusinessException {
		Connection connection = null;
		List<SupplierOrderDto> supplierOrders = new ArrayList<SupplierOrderDto>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			List<Object> uncastedSupplierOrders = PersistenceFactory.createSupplierOrderGateway().findAll();
			uncastedSupplierOrders.forEach(order -> supplierOrders.add((SupplierOrderDto) order));
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

		return supplierOrders;
	}

}
