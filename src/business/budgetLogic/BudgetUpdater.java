package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Jdbc;
import dtos.BudgetDto;
import dtos.BudgetProductDto;
import dtos.ProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;

public class BudgetUpdater {

	public boolean updateNotAcceptedBudgetPricesForProducts(List<ProductDto> infoToUpdate) {
		boolean success = false;
		Connection con = null;
		Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
		Gateway budgetProductGateway = PersistenceFactory.createBudgetedProductGateway();
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			List<BudgetDto> notAcceptedBudgets = getAllBudgets(budgetGateway).stream()
					.filter(element -> element.status.equals("NO ACEPTADO")).collect(Collectors.toList());
			List<BudgetProductDto> allBudgetProduct = getAllBudgetProducts(budgetProductGateway);

			List<BudgetProductDto> budgetedNotAccepted = new ArrayList<>();
			for (BudgetProductDto budgeted : allBudgetProduct) {
				boolean found = false;
				for (BudgetDto budget : notAcceptedBudgets) {
					if (budgeted.budgetId == budget.id) {
						found = true;
						break;
					}
				}
				if (found)
					budgetedNotAccepted.add(budgeted);
			}

			for (ProductDto productToUpdate : infoToUpdate) {
				for (BudgetProductDto possibleBudgetToUpdate : budgetedNotAccepted) {
					if (possibleBudgetToUpdate.productId == productToUpdate.id) {
						if (possibleBudgetToUpdate.price > productToUpdate.publicPrice)
							possibleBudgetToUpdate.price = productToUpdate.publicPrice;
					}
				}
			}

			for (BudgetProductDto toUpdate : budgetedNotAccepted)
				budgetProductGateway.update(toUpdate);

			con.commit();
			success = true;
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		}

		return success;
	}

	private List<BudgetProductDto> getAllBudgetProducts(Gateway budgetProductGateway) throws SQLException {
		List<BudgetProductDto> res = new ArrayList<>();
		List<Object> uncasted = budgetProductGateway.findAll();
		for (Object obj : uncasted) {
			res.add((BudgetProductDto) obj);
		}
		return res;
	}

	private List<BudgetDto> getAllBudgets(Gateway budgetGateway) throws SQLException {
		List<BudgetDto> res = new ArrayList<>();
		List<Object> uncasted = budgetGateway.findAll();
		for (Object obj : uncasted) {
			res.add((BudgetDto) obj);
		}
		return res;
	}

}
