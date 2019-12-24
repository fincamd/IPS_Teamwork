package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Dates;
import common.Jdbc;
import dtos.CreditCardPaymentDto;
import persistence.Gateway;

public class CreditCardPaymentGateway implements Gateway {

	private Conf sqlLoader = Conf.getInstance("configs/sqlstatements.properties");
	Connection con;
	
	/**
	 * Adds a given entity to the specific table
	 * 
	 * @param obj - the entity that is going to be added
	 * @throws SQLException when some error occurs on the database side
	 */
	@Override
	public void add(Object obj) throws SQLException {
		con = Jdbc.getCurrentConnection();
		String sqlSentence = sqlLoader.getProperty("SQL_ADD_CREDIT_CARD_PAYMENT_METHOD");
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		CreditCardPaymentDto dto = (CreditCardPaymentDto) obj;
		int id = dto.id;
		String ownerName= dto.ownerName;
		int cvv = dto.cvv;
		String dueDate = Dates.toStringDueDate(dto.dueDate);
		String cardNumber = dto.cardNumber;
		
		pst.setInt(1, id);
		pst.setString(2, ownerName);
		pst.setInt(3, cvv);
		pst.setString(4, dueDate);
		pst.setString(5, cardNumber);
		
		pst.executeUpdate();
		
		// log
			DBLogger.getLogger().log(Level.INFO,
			"metodo de pago con tarjeta " + dto.id + " añadido a ttargejas_de_credito con datos: "+
			"[nombre de cliente:"+ownerName+"],["+"[cvv:"+cvv+"],["+
			"[fecha de caducidad:"+dueDate+"],["+"[numero de tarjeta:"+cardNumber+"]");
		
		Jdbc.close(pst);
	}

	@Override
	public int delete(int id) throws SQLException {
		// TODO Auto-generated method stub
		return 0;

	}

	@Override
	public int update(Object obj) throws SQLException {
		// TODO Auto-generated method stub
		return 0;

	}

	@Override
	public List<Object> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public Object findById(int id) throws SQLException {
		// TODO Auto-generated method stub
		return null;

	}

}
