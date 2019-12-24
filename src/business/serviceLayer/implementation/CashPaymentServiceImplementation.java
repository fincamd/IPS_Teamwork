package business.serviceLayer.implementation;

import business.paymentMethodLogic.Cash.AddCashPayment;
import business.serviceLayer.PaymentMethodsService;
import dtos.PaymentMethodDto;

public class CashPaymentServiceImplementation implements PaymentMethodsService {

	@Override
	public int addPaymentMethod(PaymentMethodDto dto) {
		return new AddCashPayment(dto).execute();
	}

}
