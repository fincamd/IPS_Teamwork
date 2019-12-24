package business.paymentMethodLogic;

import java.sql.Connection;
import java.sql.SQLException;

import common.BusinessException;
import common.Jdbc;
import dtos.PaymentMethodDto;
import factories.PersistenceFactory;

public class FindPaymentMethodById {

	// Fields
	// ------------------------------------------------------------------------

	private int paymentMethodId;

	// Constructors
	// ------------------------------------------------------------------------

	public FindPaymentMethodById(int paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public PaymentMethodDto execute() throws BusinessException {
		Connection connection = null;
		PaymentMethodDto paymentMethod;

		try {
			connection = Jdbc.createThreadConnection();
			connection.setAutoCommit(false);
			paymentMethod = (PaymentMethodDto) PersistenceFactory
					.createPaymentMethodsGateway().findById(paymentMethodId);
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

		return paymentMethod;
	}

}
