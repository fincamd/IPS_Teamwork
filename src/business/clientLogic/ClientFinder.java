package business.clientLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ClientDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.ClientGateway;

/**
 * This class communicates with the data access layer in order to execute select
 * related queries
 * 
 * @author Angel
 *
 */
public class ClientFinder {

	/**
	 * Finds all the products on the database converting the raw received data into
	 * a list of ClientDtos that will be sent upward to the UI Layer
	 * 
	 * @return List of all the products on the database converted to ClientDtos
	 */
	public List<ClientDto> findAllClients() {
		List<ClientDto> res = new ArrayList<ClientDto>();
		Connection con = null;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			Gateway clientGateway = PersistenceFactory.createClientGateway();
			List<Object> dtos = clientGateway.findAll();

			for (Object dto : dtos) {
				res.add((ClientDto) dto);
			}

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

		return res;
	}

	public int computeBiggestId() {
		Connection con = null;

		int newId = 1;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			ClientGateway clientGateway = (ClientGateway) PersistenceFactory.createClientGateway();

			newId += clientGateway.findBiggestId();

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
		return newId;
	}

	public ClientDto findById(int clientId) {
		Connection con = null;
		ClientDto client = null;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			client = (ClientDto) PersistenceFactory.createClientGateway().findById(clientId);
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
		return client;
	}

}
