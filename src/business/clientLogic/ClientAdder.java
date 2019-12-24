package business.clientLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.ClientDto;
import factories.PersistenceFactory;
import persistence.Gateway;

/**
 * Logic class that communicates with the data access layer in order to create a
 * new Client on the database
 * 
 * @author Angel
 *
 */
public class ClientAdder {

	private ClientDto dto;

	public ClientAdder(ClientDto dto) {
		this.dto = dto;
	}

	/**
	 * Inserts a client to the database
	 * 
	 * @return true if everything went fine, false if some problem happened
	 */
	public boolean addClient() {
		Connection con = null;
		boolean valid = false;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			Gateway clientGateway = PersistenceFactory.createClientGateway();
			clientGateway.add(dto);
			con.commit();
			valid = true;
		} catch (SQLException e) {
			try {
				con.rollback();
				return valid;
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}

		return valid;
	}
}
