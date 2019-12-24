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
import dtos.ClientDto;
import persistence.Gateway;

/**
 * Represents a Gateway between the business layer class using this and the
 * specific table on the database. This gateway is a Table Data Gateway, so it
 * represents one and only one table on the database. It represents the
 * TClientes table on the database
 * 
 * @author Angel
 *
 */
public class ClientGateway implements Gateway {

	Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");

	/**
	 * Adds a given entity to the specific table
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		ClientDto dto = (ClientDto) obj;
		String sqlSentence = sqlLoader.getProperty("SQL_INSERT_CLIENT");

		Connection con = Jdbc.getCurrentConnection();
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		pst.setInt(1, dto.id);
		pst.setString(2, dto.dni);
		pst.setString(3, dto.name);
		pst.setString(4, dto.street);
		pst.setString(5, dto.postCode);
		pst.setInt(6, dto.phoneNumber);

		pst.executeUpdate();

		// log
		DBLogger.getLogger().log(Level.INFO,
				"cliente " + dto.id + " añadido a tabla tclientes con datos: [dni:" + dto.dni + "],[name:" + dto.name
						+ "],[street:" + dto.street + "],[postCode:" + dto.postCode + "],[phoneNumber:"
						+ dto.phoneNumber);

		Jdbc.close(pst);
	}

	/**
	 * Deletes an entity from the specific table on the database
	 * 
	 * @param id - the identifier of the entity
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Updates the information of a given entity on the specific database
	 * 
	 * @param obj - the entity that encapsulates the new values that will be
	 *            represented on the database
	 * @return the number of changed rows on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public int update(Object obj) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Finds all the entities on the represented table on the database
	 * 
	 * @return a collection of the found entities
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public List<Object> findAll() throws SQLException {
		List<Object> res = new ArrayList<Object>();
		String sqlSentence = sqlLoader.getProperty("SQL_FIND_ALL_CLIENTS");

		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(sqlSentence);

		while (rs.next()) {
			ClientDto dto = new ClientDto();
			dto.id = rs.getInt("ID");
			dto.street = rs.getString("DIRECCION");
			dto.dni = rs.getString("DNI");
			dto.name = rs.getString("NOMBRE");
			dto.phoneNumber = rs.getInt("TELEFONO");
			dto.postCode = rs.getString("CP");

			res.add(dto);
		}

		Jdbc.close(rs, st);
		return res;
	}

	/**
	 * Finds the object represented by a given id
	 * 
	 * @param id - the identifier of the object on the database
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public Object findById(int id) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_CLIENT_BY_ID"));
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			ClientDto client = new ClientDto();
			client.id = resultSet.getInt("id");
			client.name = resultSet.getString("nombre");
			client.dni = resultSet.getString("dni");
			client.street = resultSet.getString("direccion");
			client.postCode = resultSet.getString("cp");
			client.phoneNumber = resultSet.getInt("telefono");
			return client;
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	public int findByNameAndDni(String clientName, String clientDni) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Jdbc.getCurrentConnection();
			preparedStatement = connection.prepareStatement(Conf.getInstance("configs/sqlstatements.properties")
					.getProperty("SQL_FIND_CLIENT_BY_NAME_AND_DNI"));
			preparedStatement.setString(1, clientName);
			preparedStatement.setString(2, clientDni);
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			return resultSet.getInt("id");
		} finally {
			Jdbc.close(resultSet, preparedStatement);
		}
	}

	/**
	 * Finds the biggest identifier on the represented table table
	 * 
	 * @return the biggest identifier
	 * @throws SQLException when some error occurs on the database side
	 */
	public int findBiggestId() throws SQLException {
		Connection con = Jdbc.getCurrentConnection();
		Statement st = con.createStatement();
		String sqlSentence = sqlLoader.getProperty("SQL_GET_BIGGEST_ID_CLIENTS");

		ResultSet rs = st.executeQuery(sqlSentence);

		int biggest = 0;
		if (rs.next()) {
			biggest = rs.getInt(1);
		}
		Jdbc.close(rs, st);
		return biggest;
	}

}
