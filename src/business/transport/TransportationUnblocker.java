package business.transport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import common.Dates;
import common.Jdbc;
import dtos.TransportDto;
import factories.PersistenceFactory;
import persistence.implementation.TransportationGateway;

public class TransportationUnblocker {

	int saleId;
	TransportDto transportToUnblock;
	TransportationGateway transportGateway = (TransportationGateway) PersistenceFactory.createTransportationGateway();
	Connection con;

	public TransportationUnblocker(int saleId) {
		this.saleId = saleId;
	}

	public void execute() {

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			transportToUnblock = transportGateway.findBySaleId(saleId);

			if (transportToUnblock != null) {

				unblockTransportDependingOnDay();
				transportGateway.update(transportToUnblock);
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
	}

	private void unblockTransportDependingOnDay() {
		Date today = Dates.today();
		Date transportDate = Dates.fromString(transportToUnblock.deliveryDate);
		// Adding the hours and minutes to the delivery date
		String[] time = transportToUnblock.deliveryTime.split("");
		Dates.addHours(transportDate, Integer.parseInt(time[0]));
		Dates.addMinutes(transportDate, Integer.parseInt(time[1]));

		if (Dates.isBefore(transportDate, today)) {
			transportToUnblock.status = "CANCELADO";
		} else {
			transportToUnblock.status = "SOLICITADO";
		}

	}

}
