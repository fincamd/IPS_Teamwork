package business.transport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.TransportationGateway;

public class GetAvailableMinutesForHourInWeekDay {

	private String day;
	private int hour;

	public GetAvailableMinutesForHourInWeekDay(String day, int hour) {
		this.day = day;
		this.hour = hour;
	}

	public List<String> execute() {
		Connection connection = null;
		List<String> list = new ArrayList<String>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);

			List<String> l = ((TransportationGateway) PersistenceFactory.createTransportationGateway())
					.getHoursForDay(day);

			if (hour == 20) {
				if (!minIsSelected(l, 0)) {
					list.add(String.valueOf("00"));
				}
			} else {
				for (int min = 0; min < 60; min++) {
					if (hour > 9 && previousIsSelected(l, min)) {
						list.clear();
					}
					if (afterIsSelected(l, min)) {
						if (min < 10)
							list.add("0" + String.valueOf(min));
						else
							list.add(String.valueOf(min));
						break;
					}
					if (minIsSelected(l, min)) {
						list.clear();
						break;
					}
					if (min < 10)
						list.add("0" + String.valueOf(min));
					else
						list.add(String.valueOf(min));
				}
			}

			connection.commit();
			return list;
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

	private boolean minIsSelected(List<String> l, int min) {
		String h;
		String m;
		String hm;
		if (min < 10)
			m = "0" + String.valueOf(min);
		else
			m = String.valueOf(min);
		if (hour < 10)
			h = "0" + String.valueOf(hour);
		else
			h = String.valueOf(hour);
		hm = h + ":" + m;
		return l.contains(hm);
	}

	private boolean previousIsSelected(List<String> l, int min) {
		String h;
		String m;
		String hm;
		if (min < 10)
			m = "0" + String.valueOf(min);
		else
			m = String.valueOf(min);
		if (hour < 11)
			h = "0" + String.valueOf(hour - 1);
		else
			h = String.valueOf(hour - 1);
		hm = h + ":" + m;
		return l.contains(hm);
	}

	private boolean afterIsSelected(List<String> l, int min) {
		String h;
		String m;
		String hm;
		if (min < 10)
			m = "0" + String.valueOf(min);
		else
			m = String.valueOf(min);
		if (hour < 11)
			h = "0" + String.valueOf(hour + 1);
		else
			h = String.valueOf(hour + 1);
		hm = h + ":" + m;
		return l.contains(hm);
	}

}
