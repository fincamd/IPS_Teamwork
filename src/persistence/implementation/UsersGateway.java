package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import common.Conf;
import common.Jdbc;
import dtos.UserDto;
import persistence.Gateway;

public class UsersGateway implements Gateway {

	@Override
	public void add(Object obj) throws SQLException {
		// TODO Auto-generated method stub

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

	public UserDto findUserByUsername(String username) throws SQLException {
		Connection connection = null;
		PreparedStatement psUserRetrieval = null, psUserTypeRetrieval = null;
		ResultSet rsUserRetrieval = null, rsUserTypeRetrieval = null;

		try {
			connection = Jdbc.getCurrentConnection();
			psUserRetrieval = connection.prepareStatement(
					Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_USER_BY_USERNAME"));
			psUserRetrieval.setString(1, username);
			rsUserRetrieval = psUserRetrieval.executeQuery();
			UserDto user = null;
			if (rsUserRetrieval.next()) {
				psUserTypeRetrieval = connection.prepareStatement(
						Conf.getInstance("configs/sqlstatements.properties").getProperty("SQL_FIND_USER_TYPE_BY_ID"));
				user = new UserDto();
				user.id = rsUserRetrieval.getInt("id");
				user.username = rsUserRetrieval.getString("username");
				user.shaHash = rsUserRetrieval.getString("HASHSHA512");
				user.md5Hash = rsUserRetrieval.getString("HASHMD5");
				psUserTypeRetrieval.setInt(1, user.id);
				rsUserTypeRetrieval = psUserTypeRetrieval.executeQuery();
				if (rsUserTypeRetrieval.next()) {
					user.type = "Transportista";
				} else {
					user.type = "Vendedor";
				}
				return user;
			} else {
				return null;
			}
		} finally {
			Jdbc.close(rsUserRetrieval, psUserRetrieval);
			Jdbc.close(rsUserTypeRetrieval, psUserTypeRetrieval);
		}
	}

}
