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
import dtos.ReturnDto;
import persistence.Gateway;

public class ReturnGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ReturnDto dto = (ReturnDto) obj;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_ADD_NEW_RETURN"));
			preparedStatement.setInt(1, dto.saleId);
			preparedStatement.setInt(2, dto.productId);
			preparedStatement.setString(3, dto.reason);
			preparedStatement.setInt(4, dto.quantity);
			preparedStatement.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO,
					"la devolucion " + dto.id + " para la venta " + dto.saleId
							+ " añadido a tabla tdevoluciones con datos: " + "[id_producto:" + dto.productId
							+ "],[razon:" + dto.reason + "],[cantidad:" + dto.quantity + "]");

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
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ReturnDto dto = (ReturnDto) obj;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_UPDATE_RETURN"));
			preparedStatement.setInt(1, dto.quantity);
			preparedStatement.setString(2, dto.reason);
			preparedStatement.setInt(3, dto.id);

			int retorno = preparedStatement.executeUpdate();

			if (retorno > 0) {
				// log
				DBLogger.getLogger().log(Level.INFO, "la devolucion " + dto.id + " ha sido modificado con datos: "
						+ "[razon:" + dto.reason + "],[cantidad:" + dto.quantity + "]");
			}

			return retorno;
		} finally {
			Jdbc.close(preparedStatement);
		}
	}

	@Override
	public List<Object> findAll() throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Object> results = new ArrayList<Object>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_ALL_RETURNS"));
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				results.add(rsToDto(resultSet));
			}
			return results;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	@Override
	public Object findById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> findBySaleId(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Object> results = new ArrayList<Object>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_ALL_RETURNS_BY_SALE_ID"));
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				results.add(rsToDto(resultSet));
			}
			return results;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public ReturnDto findBySaleAndProductId(int saleId, int productId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_RETURN_BY_SALE_AND_PRODUCT_IDS"));
			preparedStatement.setInt(1, saleId);
			preparedStatement.setInt(2, productId);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return rsToDto(resultSet);
			} else {
				return null;
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private ReturnDto rsToDto(ResultSet resultSet) throws SQLException {
		ReturnDto productReturn = new ReturnDto();
		productReturn.id = resultSet.getInt("id");
		productReturn.saleId = resultSet.getInt("id_venta");
		productReturn.productId = resultSet.getInt("id_producto");
		productReturn.reason = resultSet.getString("motivo");
		productReturn.quantity = resultSet.getInt("cantidad_devuelta");
		return productReturn;
	}

}
