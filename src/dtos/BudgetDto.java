package dtos;

/**
 * Represents a budget on the underlying database. It´s used to transfer data
 * between layers
 * 
 * @author Angel
 *
 */
public class BudgetDto {

	public int id;
	public String status;
	public String creationDate;
	public String expirationDate;
	public Integer clientId;
	public String clientName;

}
