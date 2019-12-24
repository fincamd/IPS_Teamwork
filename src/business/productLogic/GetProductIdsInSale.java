package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.ProductSaleGateway;
import wrappers.ProductDtoWrapper;

public class GetProductIdsInSale {

	private int saleId;

	public GetProductIdsInSale(int saleId) {
		this.saleId = saleId;
	}

	public List<ProductDtoWrapper> execute() {
		Connection connection = null;
		List<ProductDtoWrapper> productsInSale = new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			productsInSale = ((ProductSaleGateway) PersistenceFactory.createProductSaleGateway()).findBySaleId(saleId);
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

		return productsInSale;
	}

}
