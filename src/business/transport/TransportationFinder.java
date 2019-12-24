package business.transport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.TransportDto;
import factories.PersistenceFactory;
import persistence.Gateway;

/**
 * This class communicates with the data access layer and executes select
 * related queries
 * 
 * @author Angel
 *
 */
public class TransportationFinder {

	/**
	 * Asks the Data access layer to find all transports and then filters the
	 * results so that the only transports returned are those whose state is not
	 * delivered
	 * 
	 * @return The list of not delivered Transports encapsulated on TransportDtos
	 */
	public List<TransportDto> findNotDeliveredTransports() {
		Connection con = null;
		Gateway transportGateway = PersistenceFactory.createTransportationGateway();

		List<TransportDto> res = new ArrayList<>();
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			List<TransportDto> unfilteredTransports = getAllTransports(transportGateway);

			unfilteredTransports.stream().forEach(element -> {
				if (!element.status.toUpperCase().equals("RECIBIDO")
						&& !element.status.toUpperCase().equals("BLOQUEADO"))
					res.add(element);
			});
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

	public TransportDto findById(int id) {
		Connection con = null;
		Gateway transportGateway = PersistenceFactory.createTransportationGateway();

		TransportDto res = new TransportDto();
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			res = (TransportDto) transportGateway.findById(id);
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

	private List<TransportDto> getAllTransports(Gateway transportGateway) throws SQLException {
		List<Object> uncastedTransports = transportGateway.findAll();
		List<TransportDto> res = new ArrayList<>();

		for (Object o : uncastedTransports) {
			res.add((TransportDto) o);
		}

		return res;
	}

}
