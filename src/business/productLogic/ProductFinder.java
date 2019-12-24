package business.productLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;

/**
 * This class communicates with the data access layer in order to execute select
 * related queries
 * 
 * @author Angel
 *
 */
public class ProductFinder {

	/**
	 * Finds all the products on the database converting the raw received data into
	 * a list of ProductDtos that will be sent upward to the UI Layer
	 * 
	 * @return List of all the products on the database converted to ProductDtos
	 */
	public List<ProductDto> findAllProducts() {
		List<ProductDto> res = new ArrayList<>();
		Gateway gatewatProduct = PersistenceFactory.createProductGateway();
		Connection con = null;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			List<Object> auxRes = gatewatProduct.findAll();

			for (int i = 0; i < auxRes.size(); i++) {
				res.add((ProductDto) auxRes.get(i));
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

}
