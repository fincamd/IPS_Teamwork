package wrappers;

import dtos.ProductDto;

public class ProductDtoWrapperLessInfo implements DtoWrapper {

	private ProductDto dto;
	public double originalPublicPrice;

	public ProductDtoWrapperLessInfo(ProductDto dto) {
		this.dto = dto;
		this.originalPublicPrice = dto.publicPrice;
	}

	@Override
	public String toString() {
		return dto.name + " - " + dto.category + " - " + dto.publicPrice;
	}

	/**
	 * Retrieve the dto that the wrapper wraps.
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
		return new Object[] { dto.name, dto.category, dto.publicPrice };
	}

	/**
	 * Gets the headers to show on the JTable
	 * 
	 * @return array with headers
	 */
	@Override
	public Object[] getHeaders() {
		return new Object[] { "Nombre", "Categoría", "Precio Al Público" };
	}

}
