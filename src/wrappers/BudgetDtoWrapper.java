package wrappers;

import common.Conf;
import dtos.BudgetDto;

/**
 * Wraps the BudgetDto in order to give it some functionality. A DTO by
 * definition is only used for data transfer, it has no functionality nor
 * responsibilities so that why the Wrapper is needed
 * 
 * @author Daniel Adrian Mare
 *
 */
public class BudgetDtoWrapper implements DtoWrapper {

	private BudgetDto budgetDto;
	private Conf conf = Conf.getInstance("configs/tableHeaders.properties");

	/**
	 * Constructor that creates the wrapper with a given BudgetDto
	 * 
	 * @param dto - The DTO to wrap
	 */
	public BudgetDtoWrapper(BudgetDto budgetDto) {
		this.budgetDto = budgetDto;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ID: " + budgetDto.id);
		builder.append(" - Estado: " + budgetDto.status);
		builder.append(" - Fecha de creación: " + budgetDto.creationDate);
		builder.append(" - Fecha de caducidad: " + budgetDto.expirationDate);
		if (budgetDto.clientId != null)
			builder.append(" - ID del cliente: " + budgetDto.clientId);
		return builder.toString();
	}

	/**
	 * Retrieve the wrapped DTO.
	 * 
	 * @return the wrapped BudgetDTO
	 */
	public BudgetDto getDto() {
		return budgetDto;
	}

	@Override
	public Object[] getData() {
		return new Object[] { String.valueOf(budgetDto.id), budgetDto.creationDate, budgetDto.expirationDate,
				budgetDto.clientName };
	}

	@Override
	public Object[] getHeaders() {
		String header1 = conf.getProperty("BUDGET_ID");
		String header2 = conf.getProperty("CREATION_DATE");
		String header3 = conf.getProperty("EXPIRATION_DATE");
		String header4 = conf.getProperty("CLIENT_NAME");
		return new Object[] { header1, header2, header3, header4 };
	}
}