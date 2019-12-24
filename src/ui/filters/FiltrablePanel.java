package ui.filters;

import java.util.List;

import dtos.ProductDto;

public interface FiltrablePanel {

	void setFilteredProductList(List<ProductDto> filteredProducts);

}
