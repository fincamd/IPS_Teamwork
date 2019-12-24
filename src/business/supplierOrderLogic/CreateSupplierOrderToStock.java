package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.Conf;
import common.Dates;
import common.Jdbc;
import dtos.SupplierOrderDto;
import dtos.SupplierOrderedProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.SupplierOrderGateway;

public class CreateSupplierOrderToStock {
	List<SupplierOrderedProductDto> products;
	SupplierOrderGateway supplierGateway = (SupplierOrderGateway) PersistenceFactory.createSupplierOrderGateway();
	Gateway orderedProductsGateway = PersistenceFactory.createOrderedProductsGateway();
	Connection con = null;

	public CreateSupplierOrderToStock(List<SupplierOrderedProductDto> products) {
		this.products = products;
	}

	public void execute() {
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			// CREATING A NEW ORDER
			int newOrderId = createNewDefaultOrder();

			// ADDING PRODUCTS TO THE ORDER
			addProductsToOrder(newOrderId);

			con.commit();
		} catch (SQLException e) {
			try {
				e.printStackTrace();
				System.out.println("\n");
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException();
			}
		} finally {
			Jdbc.close(con);
		}
	}

	/**
	 * Adds the products to the newly created order, specified by its ID.
	 * 
	 * @param newOrderId the id of the new order to which the products belong
	 * @throws SQLException if the access to the data base goes wrong
	 */
	private void addProductsToOrder(int newOrderId) throws SQLException {
		for (SupplierOrderedProductDto each : products) {
			each.supplierOrderId = newOrderId;
			orderedProductsGateway.add(each);
		}
	}

	/**
	 * Creates a new default order to which items will be added. This order is
	 * manually created by the salesman in order for re-stocking purposes.
	 * 
	 * @return the id of the newly created order.
	 * @throws SQLException if the access to the data base goes wrong
	 */
	private int createNewDefaultOrder() throws SQLException {
		int newOrderId = supplierGateway.findBiggestId();
		SupplierOrderDto newOrder = new SupplierOrderDto();
		newOrder.id = ++newOrderId;
		newOrder.status = Conf.getInstance("configs/parameters.properties")
				.getProperty("DEFAULT_SUPPLIER_ORDER_STATUS");
		newOrder.date = Dates.now();
		supplierGateway.add(newOrder);
		return newOrderId;
	}
}
