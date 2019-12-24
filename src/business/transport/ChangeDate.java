package business.transport;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.TransportationGateway;

public class ChangeDate {
	private int saleId;
	private String date;

	public ChangeDate(int saleId, String date) {
		this.date = date;
		this.saleId = saleId;
	}

	public void execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			((TransportationGateway) PersistenceFactory.createTransportationGateway()).updateDate(saleId, date);

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
