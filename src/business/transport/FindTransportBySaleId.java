package business.transport;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.TransportDto;
import factories.PersistenceFactory;
import persistence.implementation.TransportationGateway;

public class FindTransportBySaleId {

	private int id;

	public FindTransportBySaleId(int id) {
		this.id = id;
	}

	public TransportDto execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			TransportDto transport = ((TransportationGateway) PersistenceFactory.createTransportationGateway())
					.findBySaleId(id);
			connection.commit();
			return transport;
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
	}

}
