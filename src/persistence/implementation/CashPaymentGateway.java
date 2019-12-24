package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.CashPaymentDto;
import persistence.Gateway;

public class CashPaymentGateway implements Gateway {

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
		String sqlSentence = sqlLoader.getProperty("SQL_ADD_CASH_PAYMENT_METHOD");
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		CashPaymentDto dto = (CashPaymentDto) obj;
		int id = dto.id;
		pst.setInt(1, id);
		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"metodo de pago en efectivo " + dto.id + " añadido a tefectivos ");
		
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
