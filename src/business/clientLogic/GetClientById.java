package business.clientLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import dtos.ClientDto;
import factories.PersistenceFactory;

public class GetClientById {

	// Fields
	// ------------------------------------------------------------------------

	private int clientId;

	// Constructors
	// ------------------------------------------------------------------------

	public GetClientById(int clientId) {
		this.clientId = clientId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public ClientDto execute() throws BusinessException {
		Connection connection = null;
		ClientDto client;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			client = (ClientDto) PersistenceFactory.createClientGateway().findById(clientId);
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

		return client;
	}

}
