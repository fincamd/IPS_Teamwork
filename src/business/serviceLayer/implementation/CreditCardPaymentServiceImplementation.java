package business.serviceLayer.implementation;

import business.paymentMethodLogic.CreditCard.AddCreditCardPayment;
import business.serviceLayer.PaymentMethodsService;
import dtos.PaymentMethodDto;

public class CreditCardPaymentServiceImplementation
	implements PaymentMethodsService {

	@Override
	public int addPaymentMethod(PaymentMethodDto dto) {
		return new AddCreditCardPayment(dto).execute();

	}

}
