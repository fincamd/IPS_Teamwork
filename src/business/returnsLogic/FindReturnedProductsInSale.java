package business.returnsLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ReturnDto;
import factories.PersistenceFactory;
import persistence.implementation.ReturnGateway;

public class FindReturnedProductsInSale {

	private int id;

	public FindReturnedProductsInSale(int selectedSaleId) {
		this.id = selectedSaleId;
	}

	public List<ReturnDto> execute() {
		Connection connection = null;
		List<ReturnDto> returns = new ArrayList<ReturnDto>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			List<Object> tempReturns = ((ReturnGateway) PersistenceFactory.createReturnsGateway()).findBySaleId(id);
			tempReturns.forEach(sale -> returns.add((ReturnDto) sale));
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

		return returns;
	}

}
