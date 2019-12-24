package persistence.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import common.Conf;
import common.DBLogger;
import common.Jdbc;
import dtos.TransferPaymentDto;
import persistence.Gateway;

public class TransferPaymentGateway implements Gateway {

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
		String sqlSentence = sqlLoader.getProperty("SQL_ADD_TRANSFER_PAYMENT_METHOD");
		PreparedStatement pst = con.prepareStatement(sqlSentence);

		TransferPaymentDto dto = (TransferPaymentDto) obj;
		int id = dto.id;
		String chargedAccount= dto.chargedAccount;
		String destinationAccount= dto.destinationAccount;
		String beneficiary= dto.beneficiary;
		String concept = dto.concept;
		String notations = dto.notations;
		
		pst.setInt(1, id);
		pst.setString(2, chargedAccount);
		pst.setString(3, destinationAccount);
		pst.setString(4, beneficiary);
		pst.setString(5, concept);
		pst.setString(6, notations);
		
		pst.executeUpdate();
		
		// log
		DBLogger.getLogger().log(Level.INFO,
			"metodo de pago por transferencia " + dto.id + " añadido a ttransferencias_bancarias con datos: "+
			"[cuenta de cargo:"+chargedAccount+"],["+"[cuenta de destino:"+destinationAccount+"],["+
			"[beneficiario:"+beneficiary+"],["+"[concepto:"+concept+"],[anotaciones:"+notations+"]");
		
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
