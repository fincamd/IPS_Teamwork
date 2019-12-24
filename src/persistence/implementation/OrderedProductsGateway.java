package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.ProductDto;
import dtos.SupplierOrderedProductDto;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

public class OrderedProductsGateway implements Gateway {
	private Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	@Override
	public void add(Object obj) throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		String sqlSentence = sqlLoader.getProperty("SQL_ADD_PRODUCTS_TO_ORDER");
		PreparedStatement pst = con.prepareStatement(sqlSentence);
		try {
			SupplierOrderedProductDto dto = (SupplierOrderedProductDto) obj;
			int productId = dto.productId;
			int supplierOrderId = dto.supplierOrderId;
			int quantity = dto.quantity;
			double price = dto.price;

			pst.setInt(1, productId);
			pst.setInt(2, supplierOrderId);
			pst.setInt(3, quantity);
			pst.setDouble(4, price);
			pst.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO,
					"producto " + dto.productId + " para el pedido al proveedor " + dto.supplierOrderId
							+ " añadido a tabla tpedidos_proveedor con datos: " + "[cantidad:" + dto.quantity
							+ "],[precio:" + dto.price + "]");

		} finally {
			Jdbc.close(pst);
		}

	}

	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Object obj) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Object> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object findById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ProductDtoWrapper> countProductsOnSupplierOrder(int supplierOrderId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ProductDtoWrapper> products = new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_COUNT_PRODUCTS_IN_SUPPLIER_ORDER"));
			preparedStatement.setInt(1, supplierOrderId);
			resultSet = preparedStatement.executeQuery();

			ProductDto product;
			ProductDtoWrapper wrapper;
			while (resultSet.next()) {
				product = new ProductDto();
				product.id = resultSet.getInt("id_producto");
				wrapper = new ProductDtoWrapper(product);
				wrapper.quantityOrdered = resultSet.getInt(2);
				products.add(wrapper);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return products;
	}

	public double findOrderedProductPrice(int supplierOrderId, int productId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		double productPrice;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_GET_ORDERED_PRODUCT_PRICE"));
			preparedStatement.setInt(1, supplierOrderId);
			preparedStatement.setInt(2, productId);
			resultSet = preparedStatement.executeQuery();

			resultSet.next();
			productPrice = resultSet.getDouble("CUANTIA");

		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return productPrice;
	}

}
