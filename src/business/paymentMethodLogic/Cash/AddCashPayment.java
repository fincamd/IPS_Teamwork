package business.paymentMethodLogic.Cash;

import java.sql.Connection;
import java.sql.SQLException;

import common.Jdbc;
import dtos.CashPaymentDto;
import dtos.PaymentMethodDto;
import factories.PersistenceFactory;
import persistence.Gateway;
import persistence.implementation.PaymentMethodGateway;

public class AddCashPayment {

	Gateway cashPaymentGW = PersistenceFactory.createCashPaymentGateway();
	Gateway paymentMethodGW = PersistenceFactory.createPaymentMethodsGateway();
	
	Connection con = null;
	
	CashPaymentDto cashDto;
	public AddCashPayment(PaymentMethodDto dto) {
		cashDto= (CashPaymentDto)dto;
	}
	
	
	public int execute() {
		int paymentMethodId=-1;
		try {
			con = Jdbc.createThreadConnection();
			con.setAutoCommit(false);

			PaymentMethodDto payment = new PaymentMethodDto();
			
			paymentMethodId = (((PaymentMethodGateway)paymentMethodGW).findBiggestId())+1;
			payment.id=paymentMethodId;
			cashDto.id=paymentMethodId;
			
			paymentMethodGW.add(payment);
			cashPaymentGW.add(cashDto);
			
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
