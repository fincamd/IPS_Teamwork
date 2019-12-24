package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;

public class FilteredProductByPriceFinder {

	public List<ProductDto> getProductsPricedHigherThanReference(double referencePrice) {
		List<ProductDto> filteredList = new ArrayList<>();
		Gateway gatewayProduct = PersistenceFactory.createProductGateway();
		Connection con = null;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			List<Object> allProducts = gatewayProduct.findAll();

			for (int i = 0; i < allProducts.size(); i++) {
				ProductDto product = (ProductDto) allProducts.get(i);
				if (product.publicPrice > referencePrice)
					filteredList.add(product);
			}

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}

		return filteredList;
	}

	public List<ProductDto> getProductsPricedLowerThanOrEqualToReference(double referencePrice) {
		List<ProductDto> filteredList = new ArrayList<>();
		Gateway gatewayProduct = PersistenceFactory.createProductGateway();
		Connection con = null;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			List<Object> allProducts = gatewayProduct.findAll();

			for (int i = 0; i < allProducts.size(); i++) {
				ProductDto product = (ProductDto) allProducts.get(i);
				if (product.publicPrice <= referencePrice)
					filteredList.add(product);
			}

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}

		return filteredList;
	}
}
