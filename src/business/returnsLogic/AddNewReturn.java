package business.returnsLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.ReturnDto;
import factories.PersistenceFactory;

public class AddNewReturn {

	private ReturnDto returnDto;

	public AddNewReturn(ReturnDto returnDto) {
		this.returnDto = returnDto;
	}

	public void execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			PersistenceFactory.createReturnsGateway().add(returnDto);
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
