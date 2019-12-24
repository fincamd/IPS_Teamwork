package business.budgetLogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Jdbc;
import dtos.BudgetDto;
import dtos.BudgetProductDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.BudgetGateway;

/**
 * This class communicates with the data access layer and executes select
 * related queries
 * 
 * @author Angel
 *
 */
public class BudgetFinder {

	/**
	 * Finds the biggest id of all the budgets in the database and computes the next
	 * id to be used
	 * 
	 * @return the new id to be used
	 */
	public int computeBudgetId() {
		BudgetGateway gatewayBudget = (BudgetGateway) PersistenceFactory.createBudgetGateway();
		Connection con = null;

		int newId = 1;

		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			newId += gatewayBudget.findBiggestId();

			con.commit();
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException(ex);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			Jdbc.close(con);
		}

		return newId;
	}

	public List<BudgetDto> getModelBudgets() {
		Gateway budgetGateway = PersistenceFactory.createBudgetGateway();
		Connection con = null;
		
		List<BudgetDto> res = new ArrayList<>();
		
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			
			List<Object> uncasted = budgetGateway.findAll();
			List<BudgetDto> allBudgets = new ArrayList<>();
			for(Object obj : uncasted) {
				allBudgets.add((BudgetDto) obj);
			}
			
			res = allBudgets.stream().filter(dto -> dto.status.equals("MODELO")).collect(Collectors.toList());
			
			con.commit();
		} catch(SQLException e) {
			try {
				con.rollback();
			} catch(SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}
		return res;
	}

	public int getOrderedQuantityForProductOnBudget(int budgetId, int productId) {
		int res = 0;
		Connection con = null;
		Gateway budgetedProductsGateway = PersistenceFactory.createBudgetedProductGateway();
		
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);
			List<BudgetProductDto> allBudgetedProducts = getAllBudgetedProducts(budgetedProductsGateway);
			
			for(BudgetProductDto dto : allBudgetedProducts) {
				if(dto.budgetId == budgetId && dto.productId == productId) {
					res = dto.amount;
					break;
				}
			}
			
			con.commit();
		} catch(SQLException e) {
			try {
				con.rollback();
			} catch(SQLException ex) {
				throw new RuntimeException(ex);
			}
		} finally {
			Jdbc.close(con);
		}
		return res;
	}

	private List<BudgetProductDto> getAllBudgetedProducts(Gateway budgetedProductsGateway) throws SQLException {
		List<BudgetProductDto> res = new ArrayList<>();
		List<Object> uncasted = budgetedProductsGateway.findAll();
		
		for(Object obj : uncasted) {
			res.add((BudgetProductDto) obj);
		}
		return res;
	}

}
