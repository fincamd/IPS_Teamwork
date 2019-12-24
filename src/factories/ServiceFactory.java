package factories;

import business.serviceLayer.BudgetService;
import business.serviceLayer.ClientService;
import business.serviceLayer.PaymentMethodsService;
import business.serviceLayer.ProductService;
import business.serviceLayer.ReturnService;
import business.serviceLayer.SaleService;
import business.serviceLayer.SupplierOrderService;
import business.serviceLayer.TransportationService;
import business.serviceLayer.UserService;
import business.serviceLayer.implementation.BudgetServiceImplementation;
import business.serviceLayer.implementation.CashPaymentServiceImplementation;
import business.serviceLayer.implementation.ClientServiceImplementation;
import business.serviceLayer.implementation.CreditCardPaymentServiceImplementation;
import business.serviceLayer.implementation.ProductServiceImplementation;
import business.serviceLayer.implementation.ReturnServiceImplementation;
import business.serviceLayer.implementation.SaleServiceImplementation;
import business.serviceLayer.implementation.SupplierOrderServiceImplementation;
import business.serviceLayer.implementation.TransferPaymentServiceImplementation;
import business.serviceLayer.implementation.TransportServiceImplementation;
import business.serviceLayer.implementation.UserServiceImplementation;

/**
 * This class is responsible of creating specific implementations of the Façades
 * in order to be used by the UI Layer and reduce coupling between the UI
 * classes and the specific Façades implementations so that new implementations
 * can be added and the UI will not change
 * 
 * @author Angel
 *
 */
public class ServiceFactory {

	/**
	 * Gives an implementation of the ProductService
	 * 
	 * @return a ProductService interface reference that points to a specific
	 *         implementation
	 */
	public static ProductService createProductService() {
		return new ProductServiceImplementation();
	}

	/**
	 * Gives an implementation of the BudgetService
	 * 
	 * @return a BudgetService interface reference that points to a specific
	 *         implementation
	 */
	public static BudgetService createBudgetService() {
		return new BudgetServiceImplementation();
	}

	/**
	 * Gives an implementation of the SaleService
	 * 
	 * @return a SaleService interface reference that points to a specific
	 *         implementation
	 */
	public static SaleService createSaleService() {
		return new SaleServiceImplementation();
	}

	public static SupplierOrderService createSupplierOrderService() {
		return new SupplierOrderServiceImplementation();
	}

	/**
	 * Gives an implementation of the ClientService
	 * 
	 * @return a ClientService interface reference that points to a specific
	 *         implementation;
	 */
	public static ClientService createClientService() {
		return new ClientServiceImplementation();
	}

	public static TransportationService createTransportService() {
		return new TransportServiceImplementation();
	}

	public static ReturnService createReturnsService() {
		return new ReturnServiceImplementation();
	}

	public static UserService createUserService() {
		return new UserServiceImplementation();
	}
	
	public static PaymentMethodsService createCashPaymentService() {
		return new CashPaymentServiceImplementation();
	}
	
	public static PaymentMethodsService createCreditCardPaymentService() {
		return new CreditCardPaymentServiceImplementation();
	}
	
	public static PaymentMethodsService createTransferPaymentService() {
		return new TransferPaymentServiceImplementation();
	}

}
