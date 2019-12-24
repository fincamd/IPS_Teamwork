package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;

public class ProductUpdater {

	public boolean updatePrices(List<ProductDto> infoToPass) {
		boolean succesful = false;
		Connection con = null;
		Gateway productGateway = PersistenceFactory.createProductGateway();
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			boolean noProblem = true;
			for (ProductDto dto : infoToPass) {
				if (!existsOnDatabase(dto)) {
					noProblem = false;
					break;
				}
				productGateway.update(dto);
			}
			con.commit();
			succesful = true && noProblem;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}
		return succesful;
	}

	private boolean existsOnDatabase(ProductDto dto) throws SQLException {
		Gateway productGateway = PersistenceFactory.createProductGateway();
		Object found = productGateway.findById(dto.id);
		return found != null;
	}

}
