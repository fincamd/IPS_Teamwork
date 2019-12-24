package dtos;

/**
 * Represents a product on the underlying database. It´s used to transfer data
 * between layers
 * 
 * @author Angel
 *
 */
public class ProductDto {
	public int id;
	public String name;
	public String category;
	public double publicPrice, supplierPrice, previousPrice;
	public int stock;

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof ProductDto)
			return this.id == ((ProductDto) o).id;
		return false;
	}
}
