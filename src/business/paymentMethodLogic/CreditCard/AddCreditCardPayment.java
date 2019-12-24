package business.paymentMethodLogic.CreditCard;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.CreditCardPaymentDto;
import dtos.PaymentMethodDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.PaymentMethodGateway;

public class AddCreditCardPayment {

	Gateway creditCardPaymentGW = PersistenceFactory.createCreditCardPaymentGateway();
	Gateway paymentMethodGW = PersistenceFactory.createPaymentMethodsGateway();
	
	Connection con = null;
	
	CreditCardPaymentDto creditCardDto;
	public AddCreditCardPayment(PaymentMethodDto dto) {
		creditCardDto=(CreditCardPaymentDto)dto;
	}
	
	
	public int execute() {
		int paymentMethodId=-1;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			PaymentMethodDto payment = new PaymentMethodDto();
			
			paymentMethodId = ((PaymentMethodGateway)paymentMethodGW).findBiggestId()+1;
			payment.id=paymentMethodId;;
			creditCardDto.id=paymentMethodId;;
			
			paymentMethodGW.add(payment);
			creditCardPaymentGW.add(creditCardDto);
			
			con.commit();
		} catch (SQLException e) {
			try {
				e.printStackTrace();
				System.out.println("\n");
				con.rollback();
			} catch (SQLException ex) {
				throw new RuntimeException();
			}
		} finally {
			Jdbc.close(con);
		}
		return paymentMethodId;
	}

}
