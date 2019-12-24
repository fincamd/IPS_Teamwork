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
import dtos.ProductSaleDto;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

public class ProductSaleGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {
		ProductSaleDto dto = (ProductSaleDto) obj;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_ADD_VENTA_PRODUCTO"));
			preparedStatement.setInt(1, dto.saleId);
			preparedStatement.setInt(2, dto.productId);
			preparedStatement.setInt(3, dto.quantity);
			preparedStatement.setDouble(4, dto.price);
			preparedStatement.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO,
					"producto " + dto.productId + " añadido para la venta "
							+ dto.saleId + " con datos: [cantidad:"
							+ dto.quantity + "],[precio:" + dto.price + "]");
		} finally {
			Jdbc.close(preparedStatement);
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

	public List<ProductDtoWrapper> findBySaleId(int saleId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ProductDtoWrapper> productIdsFound =
				new ArrayList<ProductDtoWrapper>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_PRODUCTS_BY_SALE_ID"));
			preparedStatement.setInt(1, saleId);
			resultSet = preparedStatement.executeQuery();

			ProductDtoWrapper tempProduct;
			while (resultSet.next()) {
				tempProduct = new ProductDtoWrapper(new ProductDto());
				tempProduct.getDto().id = resultSet.getInt("id_producto");
				tempProduct.quantityOrdered = resultSet.getInt("cantidad");
				productIdsFound.add(tempProduct);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return productIdsFound;
	}

	public double findPriceForProductSale(int idSale, int idProd)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		double total = -1;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf
					.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_PRICE_FOR_PRODUCT_AND_SALE"));
			preparedStatement.setInt(1, idSale);
			preparedStatement.setInt(2, idProd);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				total = resultSet.getDouble(1);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
		return total;
	}

}
