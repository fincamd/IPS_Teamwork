package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Conf;
import common.Jdbc;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;

public class SupplierProductsDiscountedPriceComputer {
	int productId;
	private int units;
	private Conf conf = Conf.getInstance("configs/parameters.properties");
	private Gateway pg = PersistenceFactory.createProductGateway();

	public SupplierProductsDiscountedPriceComputer(int productId, int units) {
		this.productId = productId;
		this.units = units;
	}

	public double execute() {
		Connection connection = null;
		double price;
		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			ProductDto product = (ProductDto) pg.findById(productId);
			price = calculatePrice(product);
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
		return price;
	}

	private double calculatePrice(ProductDto product) {
		double discount = 0.0;
		double price;
		if (units > 50)
			discount = Double.parseDouble(conf.getProperty("DISCOUNT_OVER_50_ITEMS"));

		else if (units > 20)
			discount = Double.parseDouble(conf.getProperty("DISCOUNT_OVER_20_ITEMS"));

		else if (units > 10)
			discount = Double.parseDouble(conf.getProperty("DISCOUNT_OVER_10_ITEMS"));

		price = product.supplierPrice * (1 - discount);
		return price;
	}

}
