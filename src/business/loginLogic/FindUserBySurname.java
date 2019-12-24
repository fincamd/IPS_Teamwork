package business.loginLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.UserDto;
import factories.PersistenceFactory;
import persistence.implementation.UsersGateway;

public class FindUserBySurname {

	// Fields
	// ------------------------------------------------------------------------

	private String username;

	// Constructors
	// ------------------------------------------------------------------------

	public FindUserBySurname(String username) {
		this.username = username;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public UserDto execute() {
		Connection connection = null;
		UserDto user = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			user = ((UsersGateway) PersistenceFactory.createUsersGateway()).findUserByUsername(username);
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		} finally {
			Jdbc.close(connection);
		}

		return user;
	}

}
