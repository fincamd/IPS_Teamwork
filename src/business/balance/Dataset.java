package business.balance;

import java.sql.Connection;
import java.sql.SQLException;

import org.jfree.data.category.DefaultCategoryDataset;

import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.SaleGateway;
import persistence.implementation.SupplierOrderGateway;

public class Dataset {
	Connection con;
	SaleGateway saleGateway;
	SupplierOrderGateway supplierGateway;

	public DefaultCategoryDataset addBalance(DefaultCategoryDataset dataset, String year) {
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			saleGateway = (SaleGateway) PersistenceFactory.createSaleGateway();
			supplierGateway = (SupplierOrderGateway) PersistenceFactory.createSupplierOrderGateway();

			dataset.setValue(getBalance("01", year), "Balance", "Enero");
			dataset.setValue(getBalance("02", year), "Balance", "Febrero");
			dataset.setValue(getBalance("03", year), "Balance", "Marzo");
			dataset.setValue(getBalance("04", year), "Balance", "Abril");
			dataset.setValue(getBalance("05", year), "Balance", "Mayo");
			dataset.setValue(getBalance("06", year), "Balance", "Junio");
			dataset.setValue(getBalance("07", year), "Balance", "Julio");
			dataset.setValue(getBalance("08", year), "Balance", "Agosto");
			dataset.setValue(getBalance("09", year), "Balance", "Septiembre");
			dataset.setValue(getBalance("10", year), "Balance", "Octubre");
			dataset.setValue(getBalance("11", year), "Balance", "Noviembre");
			dataset.setValue(getBalance("12", year), "Balance", "Diciembre");

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
		return dataset;
	}

	public DefaultCategoryDataset addEarn(DefaultCategoryDataset dataset, String year) {
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			saleGateway = (SaleGateway) PersistenceFactory.createSaleGateway();

			dataset.setValue(saleGateway.getTotalEarn("01", year), "Ganancias", "Enero");
			dataset.setValue(saleGateway.getTotalEarn("02", year), "Ganancias", "Febrero");
			dataset.setValue(saleGateway.getTotalEarn("03", year), "Ganancias", "Marzo");
			dataset.setValue(saleGateway.getTotalEarn("04", year), "Ganancias", "Abril");
			dataset.setValue(saleGateway.getTotalEarn("05", year), "Ganancias", "Mayo");
			dataset.setValue(saleGateway.getTotalEarn("06", year), "Ganancias", "Junio");
			dataset.setValue(saleGateway.getTotalEarn("07", year), "Ganancias", "Julio");
			dataset.setValue(saleGateway.getTotalEarn("08", year), "Ganancias", "Agosto");
			dataset.setValue(saleGateway.getTotalEarn("09", year), "Ganancias", "Septiembre");
			dataset.setValue(saleGateway.getTotalEarn("10", year), "Ganancias", "Octubre");
			dataset.setValue(saleGateway.getTotalEarn("11", year), "Ganancias", "Noviembre");
			dataset.setValue(saleGateway.getTotalEarn("12", year), "Ganancias", "Diciembre");

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
		return dataset;
	}

	public DefaultCategoryDataset addSpend(DefaultCategoryDataset dataset, String year) {
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			supplierGateway = (SupplierOrderGateway) PersistenceFactory.createSupplierOrderGateway();

			dataset.setValue(supplierGateway.getTotalSpend("01", year), "Gastos", "Enero");
			dataset.setValue(supplierGateway.getTotalSpend("02", year), "Gastos", "Febrero");
			dataset.setValue(supplierGateway.getTotalSpend("03", year), "Gastos", "Marzo");
			dataset.setValue(supplierGateway.getTotalSpend("04", year), "Gastos", "Abril");
			dataset.setValue(supplierGateway.getTotalSpend("05", year), "Gastos", "Mayo");
			dataset.setValue(supplierGateway.getTotalSpend("06", year), "Gastos", "Junio");
			dataset.setValue(supplierGateway.getTotalSpend("07", year), "Gastos", "Julio");
			dataset.setValue(supplierGateway.getTotalSpend("08", year), "Gastos", "Agosto");
			dataset.setValue(supplierGateway.getTotalSpend("09", year), "Gastos", "Septiembre");
			dataset.setValue(supplierGateway.getTotalSpend("10", year), "Gastos", "Octubre");
			dataset.setValue(supplierGateway.getTotalSpend("11", year), "Gastos", "Noviembre");
			dataset.setValue(supplierGateway.getTotalSpend("12", year), "Gastos", "Diciembre");

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
		return dataset;
	}

	private double getBalance(String month, String year) throws SQLException {
		double earn;
		double spend;
		double balance = 0.0;
		earn = saleGateway.getTotalEarn(month, year);
		spend = supplierGateway.getTotalSpend(month, year);
		balance = earn - spend;
		return balance;
	}

}
