package wrappers;

import dtos.ProductDto;

/**
 * Wraps the ProductDto in order to give it some functionality and ease the task
 * of adding its information to the Sale table in DirectSales
 * 
 * @author Daniel Adrian Mare
 *
 */
public class DirectSaleProductDtoWrapper implements DtoWrapper {

	private ProductDto dto;
	public int quantityOrdered;

	public DirectSaleProductDtoWrapper(ProductDto dto) {
		this.dto = dto;
	}

	/**
	 * Retrieves the wrapped dto
	 * 
	 * @return the wrapped ProductDto
	 */
	public ProductDto getDto() {
		return dto;
	}

	/**
	 * Gets data needed to show it on the JTable
	 * 
	 * @return array with the data
	 */
	@Override
	public Object[] getData() {
		return new Object[] { dto.id, dto.name, dto.category,
				String.valueOf(quantityOrdered),
				String.valueOf(dto.publicPrice) };
	}

	/**
	 * Gets the headers to show on the JTable
	 * 
	 * @return array with headers
	 */
	@Override
	public Object[] getHeaders() {
		return new Object[] { "ID", "Nombre", "Categoría", "Unidades",
				"Precio" };
	}

}
