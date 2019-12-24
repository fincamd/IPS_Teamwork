package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.ProductDto;
import dtos.SaleDto;
import persistence.Gateway;
import wrappers.ProductDtoWrapper;

public class SaleGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {
		SaleDto dto = (SaleDto) obj;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = Jdbc.getCurrentConnection();
			int id;
			id = newVentaId(connection);
			dto.id = id;
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_ADD_SALE"));
			preparedStatement.setInt(1, dto.id);
			preparedStatement.setInt(2, dto.budgetId);
			preparedStatement.setString(3, dto.date);
			preparedStatement.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO, "venta " + dto.id
					+ " añadido a tabla tventas con datos: [id_presupuesto:"
					+ dto.budgetId + "],[id_cliente:" + dto.clientId
					+ "],[fecha:" + dto.date + "],[metodo de pago"
					+ dto.paymentMeanId + "]");

			dto.id = id;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public int addSale(Object obj) throws SQLException {
		SaleDto dto = (SaleDto) obj;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = Jdbc.getCurrentConnection();
			int id;
			id = newVentaId(connection);
			dto.id = id;
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_ADD_SALE"));
			preparedStatement.setInt(1, dto.id);
			preparedStatement.setInt(2, dto.budgetId);
			preparedStatement.setString(3, dto.date);
			preparedStatement.setInt(4, dto.clientId);
			preparedStatement.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO, "venta " + dto.id
					+ " añadido a tabla tventas con datos: [id_presupuesto:"
					+ dto.budgetId + "],[id_cliente:" + dto.clientId
					+ "],[fecha:" + dto.date + "],[metodo de pago"
					+ dto.paymentMeanId + "]");

			dto.id = id;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
		return dto.id;
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
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Object> sales = new ArrayList<Object>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_ALL_SALES"));
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				sales.add(rsToDto(resultSet));
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return sales;
	}

	@Override
	public Object findById(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_SALE_BY_ID"));
			preparedStatement.setInt(1, id);
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

	public int findByClientAndBudget(int clientId, int budgetId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf
					.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_SALE_BY_CLIENT_AND_BUDGET_IDS"));
			preparedStatement.setInt(1, clientId);
			preparedStatement.setInt(2, budgetId);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			return resultSet.getInt("id");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	private static int newVentaId(Connection conn) throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		try {
			int id = 1;
			st = conn.createStatement();
			rs = st.executeQuery(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_GET_NEW_ID"));
			if (rs.next())
				id = rs.getInt(1) + 1;
			return id;
		} finally {
			Jdbc.close(rs, st);
		}
	}

	@Deprecated
	public void addMontagePrice() throws SQLException {
		double extra = Double
				.parseDouble(Conf.getInstance("configs/parameters.properties")
						.getProperty("MONTAJE_PRICE"));
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = Jdbc.getCurrentConnection();
			pst = conn.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_ADD_TRANSPORTATION"));
			pst.setFloat(1, (float) extra);
			pst.executeQuery();
		} finally {
			Jdbc.close(pst);
		}
	}

	public List<ProductDtoWrapper> findProductsToAdd(int budgetId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<ProductDtoWrapper> list = new ArrayList<ProductDtoWrapper>();
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_PRODUCTS_TO_ADD"));
			preparedStatement.setInt(1, budgetId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				ProductDtoWrapper dto = new ProductDtoWrapper(new ProductDto());
				dto.getDto().id = resultSet.getInt("id_producto");
				dto.quantityOrdered = resultSet.getInt("cantidad");
				list.add(dto);
			}
			return list;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public double getTotalEarn(String month, String year) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String fecha = "%/" + month + "/" + year;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")

							.getProperty("SQL_GET_TOTAL_EARN_FOR_MONTH_YEAR"));
			preparedStatement.setString(1, fecha);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getDouble(1);
			}
			return 0.0;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public int addDirectSale(Object obj) throws SQLException {
		SaleDto dto = (SaleDto) obj;
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sqlSentence =
				Conf.getInstance("configs/sqlstatements.properties")
						.getProperty("SQL_ADD_DIRECT_SALE");
		try {
			connection = Jdbc.getCurrentConnection();
			int id;
			id = newVentaId(connection);
			dto.id = id;
			preparedStatement = connection.prepareStatement(sqlSentence);
			preparedStatement.setInt(1, dto.id);
			preparedStatement.setString(2, dto.date);
			preparedStatement.setInt(3, dto.paymentMeanId);
			preparedStatement.executeUpdate();
			// log
			DBLogger.getLogger().log(Level.INFO,
					"venta directa" + dto.id + " añadido a tabla tventas con datos: [id_cliente:" + dto.clientId + 
					"],[fecha:" + dto.date + "],[metodo de pago"+ dto.paymentMeanId + "]");
			dto.id = id;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
		return dto.id;
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private SaleDto rsToDto(ResultSet resultSet) throws SQLException {
		SaleDto sale;
		sale = new SaleDto();
		sale.id = resultSet.getInt("id");
		sale.date = resultSet.getString("fecha");
		sale.budgetId = resultSet.getInt("id_presupuesto");
		if (resultSet.wasNull()) {
			sale.budgetId = Integer.MIN_VALUE;
		}
		sale.clientId = resultSet.getInt("id_cliente");
		if (resultSet.wasNull()) {
			sale.clientId = Integer.MIN_VALUE;
		}
		sale.paymentMeanId = resultSet.getInt("ID_METODO_DE_PAGO");
		if (resultSet.wasNull()) {
			sale.paymentMeanId = Integer.MIN_VALUE;
		}
		return sale;
	}

}
