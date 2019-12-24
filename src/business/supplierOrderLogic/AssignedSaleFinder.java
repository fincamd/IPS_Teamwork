package business.supplierOrderLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.SupplierOrderGateway;

public class AssignedSaleFinder {

	int orderId;
	int saleId;
	Connection con;
	Gateway supplierGateway = PersistenceFactory.createSupplierOrderGateway();

	public AssignedSaleFinder(int orderId) {
		this.orderId = orderId;
	}

	public int execute() {
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			saleId = ((SupplierOrderGateway) supplierGateway).findAssignedSale(orderId);

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
		return saleId;
	}

}
