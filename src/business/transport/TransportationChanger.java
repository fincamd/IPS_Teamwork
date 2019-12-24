package business.transport;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.TransportDto;
import factories.PersistenceFactory;
import persistence.Gateway;

public class TransportationChanger {

	public void setStatus(TransportDto dtoToChange, String status) {
		Connection con = null;
		Gateway transportGateway = PersistenceFactory.createTransportationGateway();

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			dtoToChange.status = status;
			transportGateway.update(dtoToChange);

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}

	}

}
