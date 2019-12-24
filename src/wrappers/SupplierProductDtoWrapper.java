package wrappers;

import common.Conf;
import dtos.ProductDto;

/**
 * Wraps the ProductDto in order to give it some functionality. A DTO by
 * definition is only used for data transfer, it has no functionality nor
 * responsibilities so that why the Wrapper is needed
 * 
 * @author Daniel Adrian Mare
 *
 */
public class SupplierProductDtoWrapper implements DtoWrapper {

	private ProductDto dto;
	public boolean isOrdered = false;
	private Conf conf = Conf.getInstance("configs/tableHeaders.properties");

	/**
	 * Constructor that creates the wrapper with a given ProductDto
	 * 
	 * 
	 * /** Constructor that creates the wrapper with a given ProductDto
	 * 
	 * @param dto - the dto to wrap
	 */
	public SupplierProductDtoWrapper(ProductDto dto) {
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
		return new Object[] { dto.id, dto.name, dto.category, String.valueOf(dto.supplierPrice) };
	}

	/**
	 * Gets the headers to show on the JTable
	 * 
	 * @return array with headers
	 */
	@Override
	public Object[] getHeaders() {
		String header1 = conf.getProperty("ID");
		String header2 = conf.getProperty("NAME");
		String header3 = conf.getProperty("CATEGORY");
		String header4 = conf.getProperty("PRICE");
		return new Object[] { header1, header2, header3, header4 };

	}

}