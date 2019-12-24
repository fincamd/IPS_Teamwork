package dtos;

/**
 * Represents a product on a budget on the underlying database. It´s used to
 * transfer data between layers
 * 
 * @author Angel
 *
 */
public class BudgetProductDto {

	public int budgetId;
	public int productId;
	public int amount;
	public double price;

}
