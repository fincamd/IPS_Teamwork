package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Jdbc;
import dtos.BudgetProductDto;
import dtos.SupplierOrderedProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.BudgetedProductGateway;
import persistence.implementation.ProductGateway;

public class BudgetItemStockChecker {

	FindProductsInBudget productFinder;
	int budgetId;
	Connection con = null;
	Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
	Gateway productGateway = PersistenceFactory.createProductGateway();

	public BudgetItemStockChecker(int budgetId) {
		this.budgetId = budgetId;
		productFinder = new FindProductsInBudget(budgetId);
	}

	public List<SupplierOrderedProductDto> execute() {
		List<SupplierOrderedProductDto> missingProducts = new ArrayList<SupplierOrderedProductDto>();
		List<BudgetProductDto> budgetProducts;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			budgetProducts = ((BudgetedProductGateway) PersistenceFactory.createBudgetedProductGateway())
					.findProductsByBudgetId(budgetId);

			SupplierOrderedProductDto productWithMissingStock;
			for (BudgetProductDto productInBudget : budgetProducts) {
				int stock = ((ProductGateway) productGateway).findProductStockById(productInBudget.productId);
				if (stock < productInBudget.amount) {
					int missingStock = productInBudget.amount - stock;
					productWithMissingStock = new SupplierOrderedProductDto();
					productWithMissingStock.productId = productInBudget.productId;
					productWithMissingStock.quantity = missingStock;
					productWithMissingStock.price = ((ProductGateway) productGateway).findProductSupplierPriceById(productInBudget.productId);
					missingProducts.add(productWithMissingStock);
				}
			}

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
		return missingProducts;
	}

}
