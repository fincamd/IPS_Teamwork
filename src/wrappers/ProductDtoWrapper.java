package wrappers;

import dtos.ProductDto;

/**
 * Wraps the ProductDto in order to give it some functionality. A DTO by
 * definition is only used for data transfer, it has no functionality nor
 * responsibilities so that why the Wrapper is needed
 * 
 * @author Angel
 *
 */
public class ProductDtoWrapper implements DtoWrapper {

	private ProductDto dto;
	public int quantityOrdered;
	public double calculatedPrice;
	public boolean isOrdered = false;

	/**
	 * Constructor that creates the wrapper with a given ProductDto
	 * 
	 * 
	 * /** Constructor that creates the wrapper with a given ProductDto
	 * 
	 * @param dto - the dto to wrap
	 */
	public ProductDtoWrapper(ProductDto dto) {
		this.dto = dto;
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
		if (!isOrdered)
			return new Object[] { dto.name, dto.category,
					String.valueOf(dto.publicPrice),
					String.valueOf(dto.stock) };
		else
			return new Object[] { dto.name, dto.category,
					String.valueOf(dto.publicPrice),
					String.valueOf(quantityOrdered) };
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