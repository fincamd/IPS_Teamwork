package business.clientLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.ClientGateway;

public class GetClientId {

	// Fields
	// ------------------------------------------------------------------------

	private String clientName, clientDni;

	// Constructors
	// ------------------------------------------------------------------------

	public GetClientId(String clientName, String clientDni) {
		this.clientName = clientName;
		this.clientDni = clientDni;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public int execute() throws BusinessException {
		Connection connection = null;
		int clientId = -1;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			clientId = ((ClientGateway) PersistenceFactory.createClientGateway()).findByNameAndDni(clientName,
					clientDni);
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

		return clientId;
	}

}
