package business.serviceLayer.implementation;

import business.paymentMethodLogic.Transfer.AddTransferPayment;
import business.serviceLayer.PaymentMethodsService;
import dtos.PaymentMethodDto;

public class TransferPaymentServiceImplementation
	implements PaymentMethodsService {

	@Override
	public int addPaymentMethod(PaymentMethodDto dto) {
		return new AddTransferPayment(dto).execute();

	}

}
