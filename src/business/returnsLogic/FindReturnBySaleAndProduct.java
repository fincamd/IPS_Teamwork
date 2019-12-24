package business.returnsLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.ReturnDto;
import factories.PersistenceFactory;
import persistence.implementation.ReturnGateway;

public class FindReturnBySaleAndProduct {

	private int saleId, productId;

	public FindReturnBySaleAndProduct(int saleid, int productId) {
		this.saleId = saleid;
		this.productId = productId;
	}

	public ReturnDto execute() {
		Connection connection = null;
		ReturnDto dto = null;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			dto = ((ReturnGateway) PersistenceFactory.createReturnsGateway()).findBySaleAndProductId(saleId, productId);
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

		return dto;
	}

}
