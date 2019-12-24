package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.BusinessException;
import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import wrappers.ProductDtoWrapper;

public class GetProductInformation {

	// Fields
	// ------------------------------------------------------------------------

	private List<ProductDtoWrapper> products;

	// Constructors
	// ------------------------------------------------------------------------

	public GetProductInformation(List<ProductDtoWrapper> products) {
		this.products = new ArrayList<ProductDtoWrapper>(products);
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public List<ProductDtoWrapper> execute() throws BusinessException {
		Connection connection = null;
		List<ProductDtoWrapper> updatedProducts = new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			ProductDto temp = null;
			ProductDtoWrapper newWrapper = null;
			for (ProductDtoWrapper each : products) {
				temp = (ProductDto) PersistenceFactory.createProductGateway().findById(each.getDto().id);
				newWrapper = new ProductDtoWrapper(temp);
				newWrapper.quantityOrdered = each.quantityOrdered;
				updatedProducts.add(newWrapper);
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

		return updatedProducts;
	}

}
