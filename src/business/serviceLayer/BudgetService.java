package business.serviceLayer;

import java.util.List;

import common.BusinessException;
import dtos.BudgetDto;
import dtos.ClientDto;
import dtos.ProductDto;
import dtos.SupplierOrderedProductDto;
import wrappers.ProductDtoWrapper;

/**
 * Interface representing the contract that every BudgetService (Façade) must
 * follow. This interface will be used by the UI layer only and acts as an
 * intermediary between this layer and the business layer, isolating all the
 * functionality of the business layer so the UI layer works as an independent
 * module. In order to create new functionalities must be added that the UI
 * needs to execute, a new implementation for the interface must be given or
 * extending an existing one
 * 
 * @author Angel, Daniel Adrian Mare
 */
public interface BudgetService {

	/**
	 * Compute a new id for a budget
	 * 
	 * @return the new id for the budget
	 */
	public int computeBudgetId();

	/**
	 * Insert a new budget on the database
	 * 
	 * @param dto - BudgetDto encapsulating all the info a Budget has
	 */
	public void createBudget(BudgetDto dto);

	/**
	 * Retrieves all the budgets that have not been accepted as a sale, have a
	 * client assigned and have not expired yet.
	 * 
	 * @return List that contains the information of each valid and unaccepted
	 *         budget.
	 */
	public List<BudgetDto> listPendingBudgets();

	public List<ProductDtoWrapper> getProductsInBudget(int parseInt) throws BusinessException;

	/**
	 * Insert a new budget on the database
	 * 
	 * @param dto - BudgetDto encapsulating all the info a Budget has
	 */
	public boolean insertBudget(BudgetDto dto);

	/**
	 * Creates a budget without client on the database, the process consist on 1.
	 * Compute a new ID for the budget without setting the client 2. Inserting the
	 * budget on the database 3. Insert the Products of the given budget on the
	 * TPresupuestados table
	 * 
	 * This process must be done in a transactional way.
	 * 
	 * @param dtoBudget   - DTO representing a given budget
	 * @param dtosProduct - a list containing all the Products that are going to be
	 *                    added to the budget
	 * @return true if everything was successful, false if some problem happened
	 */
	public boolean createBudgetWithoutClientOrExistingClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct);

	/**
	 * Creates a budget without client on the database, the process consist on 1.
	 * Compute a new ID for the budget 2. Inserting the budget on the database 3.
	 * Compute a new ID for the client 4. Insert the new client on the database 5.
	 * Insert the Products of the given budget on the TPresupuestados table
	 * 
	 * This process must be done in a transactional way
	 * 
	 * @param dtoBudget   - DTO representing a given budget
	 * @param dtosProduct - a list containing all the Products that are going to be
	 *                    added to the budget
	 * @param dtoClient   - DTO representing the client to be added to the database
	 * @return true if everything was successful, false if some problem happened
	 */
	public boolean createBudgetWithNewClient(BudgetDto dtoBudget, List<ProductDtoWrapper> dtosProduct,
			ClientDto dtoClient);

	/**
	 * Checks the stock of the items belonging to the specified budget.
	 * 
	 * @param budgetId - identifier of the budget whose items must be checked
	 * @return list - containing all the missing products for the sale. Empty if no
	 *         products are needed
	 */
	public List<SupplierOrderedProductDto> areBudgetItemsInStock(int budgetId);

	public boolean updateNotAcceptedBudgetPricesForProducts(List<ProductDto> infoToUpdate);

	public List<BudgetDto> getModelBudgets();

	public int getOrderedQuantityForProductOnBudget(int budgetId, int productId);
}
