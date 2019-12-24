package business.paymentMethodLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import factories.PersistenceFactory;
import persistence.implementation.PaymentMethodGateway;

public class GetTypeOfPaymentMeanById {

	// Fields
	// ------------------------------------------------------------------------

	private int paymentMethodId;

	// Constructors
	// ------------------------------------------------------------------------

	public GetTypeOfPaymentMeanById(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public String execute() throws BusinessException {
		Connection connection = null;
		String paymentMethodType = "None";

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			boolean found = false;
			found = ((PaymentMethodGateway) PersistenceFactory
					.createPaymentMethodsGateway())
							.checkIfCreditCard(paymentMethodId);
			if (found) {
				return "Tarjeta de crédito";
			}
			found = ((PaymentMethodGateway) PersistenceFactory
					.createPaymentMethodsGateway())
							.checkIfTransference(paymentMethodId);
			if (found) {
				return "Transferencia bancaria";
			}
			found = ((PaymentMethodGateway) PersistenceFactory
					.createPaymentMethodsGateway())
							.checkIfCash(paymentMethodId);
			if (found) {
				return "Efectivo";
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		} finally {
			Jdbc.close(connection);
		}

		return paymentMethodType;
	}
}
