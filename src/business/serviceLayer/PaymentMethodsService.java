package business.serviceLayer;

import dtos.PaymentMethodDto;

public interface PaymentMethodsService {

	/**
	 * Adds a new payment method to the database
	 * 
	 * @param PaymentMethodDto - object containing all the information related
	 *                         to the payment method
	 * @return TODO
	 */
	public int addPaymentMethod(PaymentMethodDto dto);

}
