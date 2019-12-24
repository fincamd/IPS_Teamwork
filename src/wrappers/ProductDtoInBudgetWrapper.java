package wrappers;

import dtos.ProductDto;

public class ProductDtoInBudgetWrapper implements DtoWrapper {

	private ProductDto dto;
	private int quantityOnBudget;
	private int budgetId;
	
	public ProductDtoInBudgetWrapper(ProductDto dto, int quantityOnBudget, int budgetId) {
		this.dto = dto;
		this.quantityOnBudget = quantityOnBudget;
		this.budgetId = budgetId;
	}
	
	public int getQuantityOnBudget() {
		return quantityOnBudget;
	}
	
	public int getBudgetId() {
		return budgetId;
	}

	public ProductDto getDto() {
		return dto;
	}

	@Override
	public Object[] getData() {
		return new Object[] { dto.name, dto.category, String.valueOf(dto.publicPrice), String.valueOf(quantityOnBudget) };
	}

	/**
	 * Gets the headers to show on the JTable
	 * 
	 * @return array with headers
	 */
	@Override
	public Object[] getHeaders() {
		return new Object[] { "Nombre", "Categoría", "Precio", "Cantidad" };
	}
}
