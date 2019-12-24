package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Dates;
import common.Jdbc;
import dtos.SupplierOrderDto;
import persistence.Gateway;

public class SupplierOrderGateway implements Gateway {
	private Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	@Override
	public void add(Object obj) throws SQLException {
		SupplierOrderDto dto = (SupplierOrderDto) obj;
		Connection con = Jdbc.getCurrentConnection();
		String selectedSql;
		if (dto.saleId == -1) {
			selectedSql = "SQL_CREATE_NEW_DEFAULT_STOCK_ORDER";
		} else {
			selectedSql = "SQL_CREATE_NEW_ORDER_FOR_SALE_STOCK";
		}
		String sqlSentence = sqlLoader.getProperty(selectedSql);
		PreparedStatement pst = con.prepareStatement(sqlSentence);
		try {
			int id = dto.id;
			String status = dto.status;
			String date = Dates.toString(dto.date);
			int saleId = dto.saleId;
			pst.setInt(1, id);
			pst.setString(2, status);
			pst.setString(3, date);
			if (saleId != -1)
				pst.setInt(4, saleId);

			pst.executeUpdate();

			// log
			DBLogger.getLogger().log(Level.INFO,
					"pedido al proveedor " + dto.id + " para la venta " + dto.saleId
							+ " añadido a tabla tpedidos_proveedor con datos: " + "[estado:" + dto.status + "],[fecha:"
							+ date + "]");

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
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<Object> supplierOrders = new ArrayList<Object>();

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(sqlLoader.getProperty("SQL_FIND_ALL_SUPPLIER_ORDERS"));
			resultSet = preparedStatement.executeQuery();
			SupplierOrderDto order;
			while (resultSet.next()) {
				order = new SupplierOrderDto();
				order.id = resultSet.getInt("id");
				order.status = resultSet.getString("estado");
				order.saleId = resultSet.getInt("id_venta") == 0 ? -1 : resultSet.getInt("id_venta");
				try {
					SimpleDateFormat format = new SimpleDateFormat("dd/mm/YYYY");
					order.date = format.parse(resultSet.getString("fecha"));
				} catch (ParseException e) {
					order.date = null;
				}
				supplierOrders.add(order);
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return supplierOrders;
	}

	@Override
	public Object findById(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		SupplierOrderDto supplierOrder = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(sqlLoader.getProperty("SQL_FIND_SUPPLIER_ORDER_BY_ID"));
			preparedStatement.setLong(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			supplierOrder = new SupplierOrderDto();
			supplierOrder.id = resultSet.getInt("id");
			supplierOrder.status = resultSet.getString("estado");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return supplierOrder;
	}

	public boolean markOrderAsReceived(int orderId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(sqlLoader.getProperty("SQL_MARK_ORDER_AS_RECEIVED"));
			preparedStatement.setLong(1, orderId);
			// log
			DBLogger.getLogger().log(Level.INFO, "pedido al proveedor " + orderId + " ha sido marcado como recibido");
			return preparedStatement.executeUpdate() != 0;
		} finally {
			Jdbc.close(preparedStatement);
		}
	}

	/**
	 * Finds the biggest identifier on the represented table
	 * 
	 * @return the biggest identifier
	 * @throws SQLException when some error occurs on the database side
	 * 
	 */
	public int findBiggestId() throws SQLException {
		String sqlSentence = sqlLoader.getProperty("SQL_GET_BIGGEST_ID_SUPPLIER_ORDERS");
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();

		ResultSet rs = st.executeQuery(sqlSentence);
		int id = 0;

		if (rs.next()) {
			id = rs.getInt(1);
		}
		Jdbc.close(rs, st);

		return id;
	}

	public int findAssignedSale(int orderId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int saleId;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(sqlLoader.getProperty("SQL_FIND_ASSIGNED_SALE_ID"));
			preparedStatement.setLong(1, orderId);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			saleId = resultSet.getInt("id_venta");

		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}

		return saleId;

	}

	public double getTotalSpend(String month, String year) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String fecha = "%/" + month + "/" + year;
		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_GET_TOTAL_SPEND_FOR_MONTH_YEAR"));
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
}
