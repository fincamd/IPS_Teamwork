package business.transport;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.TransportationGateway;

public class ChangeTime {
	private int saleId;
	private String time;

	public ChangeTime(int saleId, String time) {
		this.time = time;
		this.saleId = saleId;
	}

	public void execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			((TransportationGateway) PersistenceFactory.createTransportationGateway()).updateTime(saleId, time);

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
	}
}
