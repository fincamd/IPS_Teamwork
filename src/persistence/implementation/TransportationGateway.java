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
import dtos.TransportDto;
import persistence.Gateway;

public class TransportationGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {
		TransportDto dto = (TransportDto) obj;
		Connection conn = null;
		PreparedStatement pst = null;
		String sqlSentence = Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_ADD_TRANSPORTATION");
		if (dto.status.equals("BLOQUEADO"))
			sqlSentence = Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_ADD_BLOCKED_TRANSPORTATION");
		try {
			conn = Jdbc.getCurrentConnection();
			int id;
			id = newTransportationId(conn);
			dto.id = id;
			pst = conn.prepareStatement(sqlSentence);
			pst.setInt(1, dto.id);
			pst.setInt(2, dto.saleId);
			pst.setString(3, dto.deliveryDate);
			pst.setString(4, dto.deliveryTime);
			if (dto.status.equals("BLOQUEADO"))
				pst.setString(5, dto.status);
			pst.executeUpdate();
			// log
			DBLogger.getLogger().log(Level.INFO, "transporte " + dto.id + " for sale " + dto.saleId
					+ " añadido a tabla ttransportes con datos: [id_venta:" + dto.saleId + "],[fecha:"
					+ dto.deliveryDate + "],[hora:" + dto.deliveryTime + "],[id_transportista" + dto.idTransportista
					+ "],[montaje:" + dto.requiresAssembly + "],[estado:" + dto.status + "]");

		} finally {
			Jdbc.close(pst);
		}
	}

	private int newTransportationId(Connection conn) throws SQLException {
		int id = 1;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(
				Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_GET_NEW_TRANSPORTATION_ID"));
		if (rs.next())
			id = rs.getInt(1) + 1;
		return id;
	}

	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Object obj) throws SQLException {
		TransportDto dto = (TransportDto) obj;
		Connection con = Jdbc.getCurrentConnection();
		String sqlQuery = Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_UPDATE_TRANSPORTATION");
		PreparedStatement pst = con.prepareStatement(sqlQuery);
		pst.setInt(1, dto.saleId);
		pst.setString(2, dto.requiresAssembly);
		pst.setString(3, dto.status);
		pst.setString(4, dto.deliveryDate);
		pst.setString(5, dto.deliveryTime);
		pst.setInt(6, dto.id);

		int res = pst.executeUpdate();

		// log
		DBLogger.getLogger().log(Level.INFO,
				"transporte " + dto.id + "se ha modificado con datos: [id_venta:" + dto.saleId + "],[fecha:"
						+ dto.deliveryDate + "],[hora:" + dto.deliveryTime + "],[id_transportista" + dto.idTransportista
						+ "],[montaje:" + dto.requiresAssembly + "],[estado:" + dto.status + "]");

		Jdbc.close(pst);
		return res;
	}

	@Override
	public List<Object> findAll() throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		String sqlQuery = Conf.getInstance("configs/sqlstatements.properties")
				.getProperty("SQL_FIND_ALL_TRANSPORTATION");
		List<Object> res = new ArrayList<>();

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sqlQuery);
		while (rs.next()) {
			TransportDto dto = new TransportDto();
			dto.id = rs.getInt("ID");
			dto.saleId = rs.getInt("ID_VENTA");
			dto.requiresAssembly = rs.getString("SOLICITA_MONTAJE");
			dto.status = rs.getString("ESTADO");
			dto.deliveryDate = rs.getString("FECHA_ENTREGA");
			dto.deliveryTime = rs.getString("HORA_ENTREGA");

			res.add(dto);
		}

		Jdbc.close(rs, st);
		return res;
	}

	@Override
	public Object findById(int id) throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		String sqlQuery = Conf.getInstance("configs/sqlstatements.properties")
				.getProperty("SQL_FIND_BY_ID_TRANSPORTATION");
		PreparedStatement pst = con.prepareStatement(sqlQuery);
		pst.setInt(1, id);

		ResultSet rs = pst.executeQuery();

		TransportDto res = null;
		if (rs.next()) {
			res = new TransportDto();
			res.id = rs.getInt("ID");
			res.saleId = rs.getInt("ID_VENTA");
			res.requiresAssembly = rs.getString("SOLICITA_MONTAJE");
			res.status = rs.getString("ESTADO");
			res.deliveryDate = rs.getString("FECHA_ENTREGA");
			res.deliveryTime = rs.getString("HORA_ENTREGA");
		}

		Jdbc.close(rs, pst);

		return res;
	}

	public void updateMontaje(int id, boolean bool) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = Jdbc.getCurrentConnection();
			if (bool) {
				pst = conn.prepareStatement(
						Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_ADD_MONTAJE"));
				pst.setInt(1, id);
				pst.executeUpdate();
				// log
				DBLogger.getLogger().log(Level.INFO, "a transporte " + id + " se le ha añadido montaje");
			}
		} finally {
			Jdbc.close(pst);
		}
	}

	public void updateDate(int idVenta, String date) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = Jdbc.getCurrentConnection();
			pst = conn.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_SET_TRANSPORTATION_DATE"));
			pst.setString(1, date);
			pst.setInt(2, idVenta);
			pst.executeUpdate();
			// log
			DBLogger.getLogger().log(Level.INFO,
					"a transporte con id_venta " + idVenta + " se le ha cambiado la fecha a " + date);
		} finally {
			Jdbc.close(pst);
		}
	}

	public TransportDto findBySaleId(int idVenta) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_TRANSPORTATION_BY_SALE_ID"));
			preparedStatement.setInt(1, idVenta);
			resultSet = preparedStatement.executeQuery();

			TransportDto transport;
			if (resultSet.next()) {
				transport = new TransportDto();
				transport.id = resultSet.getInt("id");
				transport.saleId = resultSet.getInt("id_venta");
				transport.requiresAssembly = resultSet.getString("solicita_montaje");
				transport.status = resultSet.getString("estado");
				transport.deliveryDate = resultSet.getString("fecha_entrega");
				if (resultSet.wasNull()) {
					transport.deliveryDate = "";
				}
				transport.deliveryTime = resultSet.getString("hora_entrega");
				if (resultSet.wasNull()) {
					transport.deliveryTime = "";
				}
				return transport;
			} else {
				return null;
			}
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public List<String> getHoursForDay(String day) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		List<String> list = new ArrayList<String>();

		try {
			connection = Jdbc.getCurrentConnection();

			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_HOURS_FOR_A_DAY"));
			preparedStatement.setString(1, day);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
			return list;

		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public void updateTime(int idVenta, String deliveryTime) throws SQLException {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = Jdbc.getCurrentConnection();
			pst = conn.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_SET_TRANSPORTATION_TIME"));
			pst.setString(1, deliveryTime);
			pst.setInt(2, idVenta);
			pst.executeUpdate();
			// log
			DBLogger.getLogger().log(Level.INFO,
					"a transporte con id_venta " + idVenta + " se le ha cambiado la hora a " + deliveryTime);
		} finally {
			Jdbc.close(pst);
		}
	}

}
