package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.BusinessException;
import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.implementation.OrderedProductsGateway;
import persistence.implementation.ProductGateway;
import wrappers.ProductDtoWrapper;

public class CountProductsOnSupplierOrder {

	// Fields
	// ------------------------------------------------------------------------

	private int supplierOrderId;

	// Constructors
	// ------------------------------------------------------------------------

	public CountProductsOnSupplierOrder(int supplierOrderId) {
		this.supplierOrderId = supplierOrderId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public List<ProductDtoWrapper> execute() throws BusinessException {
		Connection connection = null;
		List<ProductDtoWrapper> products = null;
		List<ProductDtoWrapper> results = new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			products = ((OrderedProductsGateway) PersistenceFactory.createOrderedProductsGateway())
					.countProductsOnSupplierOrder(supplierOrderId);
			ProductDtoWrapper wrapper;
			for (ProductDtoWrapper each : products) {
				ProductDto productInfo = (ProductDto) ((ProductGateway) PersistenceFactory.createProductGateway())
						.findById(each.getDto().id);
				productInfo.supplierPrice = ((OrderedProductsGateway) PersistenceFactory.createOrderedProductsGateway())
						.findOrderedProductPrice(supplierOrderId, productInfo.id);
				wrapper = new ProductDtoWrapper(productInfo);
				wrapper.quantityOrdered = each.quantityOrdered;
				results.add(wrapper);
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
