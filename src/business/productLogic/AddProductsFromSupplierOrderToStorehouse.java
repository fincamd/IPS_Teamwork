package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.ProductGateway;
import wrappers.ProductDtoWrapper;

public class AddProductsFromSupplierOrderToStorehouse {

	private List<ProductDtoWrapper> products;

	public AddProductsFromSupplierOrderToStorehouse(ArrayList<ProductDtoWrapper> productsToAdd) {
		this.products = productsToAdd;
	}

	public void execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			for (ProductDtoWrapper each : products) {
				((ProductGateway) PersistenceFactory.createProductGateway()).addUnitsToStorage(each);
			}
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
	}

}
