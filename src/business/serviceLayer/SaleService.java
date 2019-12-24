package business.serviceLayer;

import java.util.List;

import common.BusinessException;
import dtos.ProductSaleDto;
import dtos.SaleDto;
import dtos.SaleHistoryEntryDto;
import dtos.SupplierOrderedProductDto;
import wrappers.ProductDtoWrapper;

public interface SaleService {

	public List<SaleHistoryEntryDto> generateSalesHistoryEntries() throws BusinessException;

	public List<ProductDtoWrapper> findProductsOnSale(int i);

	public SaleDto findById(int saleToInvoiceId);
	
	public List<SupplierOrderedProductDto> checkDirectSaleItemsInStock(List<ProductSaleDto> orderedProducts);

}
