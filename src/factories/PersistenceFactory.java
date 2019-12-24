package factories;

import persistence.Gateway;
import persistence.implementation.BudgetGateway;
import persistence.implementation.BudgetProductGateway;
import persistence.implementation.BudgetedProductGateway;
import persistence.implementation.CashPaymentGateway;
import persistence.implementation.ClientGateway;
import persistence.implementation.CreditCardPaymentGateway;
import persistence.implementation.OrderedProductsGateway;
import persistence.implementation.PaymentMethodGateway;
import persistence.implementation.ProductGateway;
import persistence.implementation.ProductSaleGateway;
import persistence.implementation.ReturnGateway;
import persistence.implementation.SaleGateway;
import persistence.implementation.SupplierOrderGateway;
import persistence.implementation.TransferPaymentGateway;
import persistence.implementation.TransportationGateway;
import persistence.implementation.UsersGateway;

/**
 * This class is responsible of creating specific implementations of the Façades
 * in order to be used by the UI Layer and reduce coupling between the UI
 * classes and the specific Façades implementations so that new implementations
 * can be added and the UI will not change
 * 
 * @author Angel
 *
 */
public class PersistenceFactory {

	/**
	 * Gives an implementation of the ProductGateway as a Gateway
	 * 
	 * @return a Gateway interface reference that points to a specific
	 *         implementation
	 */
	public static Gateway createProductGateway() {
		return new ProductGateway();
	}

	/**
	 * Gives an implementation of the BudgetGateway as a Gateway
	 * 
	 * @return a Gateway interface reference that points to a specific
	 *         implementation
	 */
	public static Gateway createBudgetGateway() {
		return new BudgetGateway();
	}

	/**
	 * Gives an implementation of the ClientGateway as a Gateway
	 * 
	 * @return a Gateway interface reference that points to a specific
	 *         implementation
	 */
	public static Gateway createClientGateway() {
		return new ClientGateway();
	}

	/**
	 * Gives an implementation of the BudgetProductGateway as a Gateway
	 * 
	 * @return a Gateway interface reference that points to a specific
	 *         implementation
	 */
	public static Gateway createBudgetProductGateway() {
		return new BudgetProductGateway();
	}

	public static Gateway createSupplierOrderGateway() {
		return new SupplierOrderGateway();
	}

	public static Gateway createOrderedProductsGateway() {
		return new OrderedProductsGateway();
	}

	public static Gateway createTransportationGateway() {
		return new TransportationGateway();
	}

	public static Gateway createSaleGateway() {
		return new SaleGateway();
	}

	public static Gateway createProductSaleGateway() {
		return new ProductSaleGateway();
	}

	public static Gateway createBudgetedProductGateway() {
		return new BudgetedProductGateway();
	}

	public static Gateway createReturnsGateway() {
		return new ReturnGateway();
	}

	public static Gateway createUsersGateway() {
		return new UsersGateway();
	}
	
	public static Gateway createPaymentMethodsGateway() {
		return new PaymentMethodGateway();
	}
	
	public static Gateway createCashPaymentGateway() {
		return new CashPaymentGateway();
	}
	public static Gateway createCreditCardPaymentGateway() {
		return new CreditCardPaymentGateway();
	}
	
	public static Gateway createTransferPaymentGateway() {
		return new TransferPaymentGateway();
	}

}
