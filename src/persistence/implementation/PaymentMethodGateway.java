package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.PaymentMethodDto;
import persistence.Gateway;

public class PaymentMethodGateway implements Gateway {
	private Conf sqlLoader =
			Conf.getInstance("configs/sqlstatements.properties");
	Connection con;

	/**
	 * Adds a given entity to the specific table
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		con = Jdbc.getCurrentConnection();
		String sqlSentence = sqlLoader.getProperty("SQL_ADD_PAYMENT_METHOD");
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		PaymentMethodDto dto = (PaymentMethodDto) obj;
		int id = dto.id;
		pst.setInt(1, id);
		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"metodo de pago " + dto.id + " añadido a tmetodos_de_pago ");
		
		Jdbc.close(pst);
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

	/**
	 * Finds the biggest identifier on the represented table
	 * 
	 * @return the biggest identifier
	 * @throws SQLException when some error occurs on the database side
	 * 
	 */
	public int findBiggestId() throws SQLException {
		String sqlSentence =
				sqlLoader.getProperty("SQL_GET_BIGGEST_ID_PAYMENT_METHODS");
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();

		ResultSet rs = st.executeQuery(sqlSentence);
		int id = 1;

		if (rs.next()) {
			id = rs.getInt(1);
		}
		Jdbc.close(rs, st);

		return id;
	}

	public boolean checkIfCreditCard(int paymentMethodId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_CREDIT_CARD_BY_ID"));
			preparedStatement.setInt(1, paymentMethodId);
			resultSet = preparedStatement.executeQuery();
			return resultSet.next();
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public boolean checkIfTransference(int paymentMethodId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_TRANSFERENCE_BY_ID"));
			preparedStatement.setInt(1, paymentMethodId);
			resultSet = preparedStatement.executeQuery();
			return resultSet.next();
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public boolean checkIfCash(int paymentMethodId) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties")
							.getProperty("SQL_FIND_CASH_BY_ID"));
			preparedStatement.setInt(1, paymentMethodId);
			resultSet = preparedStatement.executeQuery();
			return resultSet.next();
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

}
