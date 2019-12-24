package business.returnsLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.ReturnDto;
import factories.PersistenceFactory;
import persistence.implementation.ProductGateway;

public class ReturnProductsToStorehouse {

	private ReturnDto dto;

	public ReturnProductsToStorehouse(ReturnDto returnDto) {
		this.dto = returnDto;
	}

	public void execute() {
		Connection connection = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			((ProductGateway) PersistenceFactory.createProductGateway()).returnProductsToStoreHouse(dto);
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
