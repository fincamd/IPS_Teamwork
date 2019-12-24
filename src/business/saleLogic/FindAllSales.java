package business.saleLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.BusinessException;
import common.Jdbc;
import dtos.SaleDto;
import factories.PersistenceFactory;

public class FindAllSales {

	// Basic methods
	// ------------------------------------------------------------------------

	public List<SaleDto> execute() throws BusinessException {
		Connection connection = null;
		List<SaleDto> sales = new ArrayList<SaleDto>();

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			List<Object> tempSales = PersistenceFactory.createSaleGateway().findAll();
			tempSales.forEach(sale -> sales.add((SaleDto) sale));
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

		return sales;
	}
}
