package business.serviceLayer.implementation;

import java.util.List;

import business.budgetLogic.BudgetAdder;
import business.budgetLogic.BudgetCreator;
import business.budgetLogic.BudgetFinder;
import business.budgetLogic.BudgetUpdater;
import business.budgetLogic.FindProductsInBudget;
import business.budgetLogic.BudgetItemStockChecker;
import business.budgetLogic.PendingBudgetsLister;
import business.productLogic.GetProductInformation;
import business.serviceLayer.BudgetService;
import common.BusinessException;
import dtos.BudgetDto;
import dtos.ClientDto;
import dtos.ProductDto;
import dtos.SupplierOrderedProductDto;
import wrappers.ProductDtoWrapper;

/**
 * This class represents an specific implementation for the BudgetService
 * (Façade). This class will delegate all Budget related operations to classes
 * on the business layer, it acts as an intermediary between the UI layer and
 * the business layer
 * 
 * @author Angel, Daniel Adrian Mare
 *
 */
public class BudgetServiceImplementation implements BudgetService {

	/**
	 * Delegates to the business layer class BudgetFinder the id computation for a
	 * new budget
	 * 
	 * @return the ID to be used in order to create a new budget
	 */
	@Override
	public int computeBudgetId() {
		return new BudgetFinder().computeBudgetId();
	}

	/**
	 * Delegates to the business layer class BudgetAdder the insertion of a new
	 * 
	 * @param dto - DTO representing all the data of a budget
	 */
	@Override
	public boolean insertBudget(BudgetDto dto) {
		return new BudgetAdder(dto).addBudget();
	}

	/**
	 * Delegates to the business layer class BudgetCreator the whole transactional
	 * creation process of a new budget, without client or existing client, on the
	 * underlying database
	 * 
	 * @param dtoBudget   - Dto representing the new Budget that is going to be
	 *                    added to the database
	 * @param dtosProduct - List of ProductDto representing all the products on the
	 *                    Budget
	 * @return true if everything went fine, false if some problem happened
	 */
	@Override
	public boolean createBudgetWithoutClientOrExistingClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct) {
		return new BudgetCreator().createBudgetWithoutClientOrExistingClient(dtoBudget, dtosProduct);
	}

	/**
	 * Delegates to the business layer class BudgetCreator the whole transactional
	 * creation process of a new budget, with a new client, on the underlying
	 * database
	 * 
	 * @param dtoBudget   - Dto representing the new Budget that is going to be
	 *                    added to the database
	 * @param dtosProduct - List of ProductDto representing all the products on the
	 *                    Budget
	 * @param dtoClient   - Dto representing the new Client that is going to be
	 *                    added to the database
	 * @return true if everything went fine, false if some problem happened
	 */
	@Override
	public boolean createBudgetWithNewClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct,
			ClientDto dtoClient) {
		return new BudgetCreator().createBudgetWithNewClient(dtoBudget, dtosProduct, dtoClient);
	}

	/**
	 * Delegates to the business layer class PendingBudgetsLister the retrieval from
	 * the database of all the budgets that have not been accepted as a sale, have a
	 * client assigned and have not expired yet.
	 * 
	 * @return List that contains the information of each pending budget.
	 */
	@Override
	public List<BudgetDto> listPendingBudgets() {
		return new PendingBudgetsLister().listAllPendingBudgets();
	}

	@Override
	public List<ProductDtoWrapper> getProductsInBudget(int budgetId) throws BusinessException {
		List<ProductDtoWrapper> productIds = new FindProductsInBudget(budgetId).execute();
		return new GetProductInformation(productIds).execute();
	}

	@Override
	public void createBudget(BudgetDto dto) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SupplierOrderedProductDto> areBudgetItemsInStock(int budgetId) {
		return new BudgetItemStockChecker(budgetId).execute();
	}

	@Override
	public boolean updateNotAcceptedBudgetPricesForProducts(List<ProductDto> infoToUpdate) {
		return new BudgetUpdater().updateNotAcceptedBudgetPricesForProducts(infoToUpdate);
	}

	@Override
	public List<BudgetDto> getModelBudgets() {
		return new BudgetFinder().getModelBudgets();
	}

	@Override
	public int getOrderedQuantityForProductOnBudget(int budgetId, int productId) {
		return new BudgetFinder().getOrderedQuantityForProductOnBudget(budgetId, productId);
	}

}
