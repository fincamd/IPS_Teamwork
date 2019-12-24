package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.implementation.ProductSaleGateway;
import wrappers.ProductDtoWrapper;

public class FindProductsInSale {

	private int idSale;

	public FindProductsInSale(int selectedSaleId) {
		this.idSale = selectedSaleId;
	}

	public List<ProductDtoWrapper> execute() {
		Connection connection = null;
		List<ProductDtoWrapper> rawProducts, results;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			rawProducts = ((ProductSaleGateway) PersistenceFactory
					.createProductSaleGateway()).findBySaleId(idSale);
			results = new ArrayList<ProductDtoWrapper>();
			ProductDtoWrapper temp;
			ProductDto productFound;
			for (ProductDtoWrapper each : rawProducts) {
				productFound = (ProductDto) PersistenceFactory
						.createProductGateway().findById(each.getDto().id);
				temp = new ProductDtoWrapper(productFound);
				temp.quantityOrdered = each.quantityOrdered;
				temp.calculatedPrice = ((ProductSaleGateway) PersistenceFactory
						.createProductSaleGateway()).findPriceForProductSale(
								idSale, productFound.id);
				results.add(temp);
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

		return results;
	}

}
