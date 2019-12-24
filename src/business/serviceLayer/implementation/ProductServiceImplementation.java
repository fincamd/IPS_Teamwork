package business.serviceLayer.implementation;

import java.util.ArrayList;
import java.util.List;

import business.productLogic.AddProductsFromSupplierOrderToStorehouse;
import business.productLogic.FilteredProductByCategoryFinder;
import business.productLogic.FilteredProductByPriceFinder;
import business.productLogic.GetProductTableColumnNames;
import business.productLogic.ProductFinder;
import business.productLogic.ProductUpdater;
import business.returnsLogic.ReturnProductsToStorehouse;
import business.serviceLayer.ProductService;
import business.supplierOrderLogic.CountProductsOnSupplierOrder;
import common.BusinessException;
import dtos.ProductDto;
import dtos.ReturnDto;
import wrappers.ProductDtoWrapper;

/**
 * This class represents an specific implementation for the ProductService
 * (Façade). This class will delegate all Product related operations to classes
 * on the business layer, it acts as an intermediary between the UI layer and
 * the business layer
 * 
 * @author Angel
 *
 */
public class ProductServiceImplementation implements ProductService {

	/**
	 * Delegates to the business layer class ProductFinder the listing of all
	 * products on the underlying database
	 * 
	 * @return all the products on the database as ProductDtos
	 */
	@Override
	public List<ProductDto> getAllProducts() {
		return new ProductFinder().findAllProducts();
	}

	@Override
	public List<ProductDtoWrapper> findProductsOnSupplierOrder(int supplierOrderId) throws BusinessException {
		return new CountProductsOnSupplierOrder(supplierOrderId).execute();
	}

	@Override
	public String[] getColumnNames() {
		return new GetProductTableColumnNames().execute();
	}

	@Override
	public List<ProductDto> getFilteredProductsByCategory(List<String> attributeValues) {
		return new FilteredProductByCategoryFinder().filterProductByCategory(attributeValues);
	}

	@Override
	public List<ProductDto> getProductsPricedHigherThanReference(double referencePrice) {
		return new FilteredProductByPriceFinder().getProductsPricedHigherThanReference(referencePrice);
	}

	@Override
	public List<ProductDto> getProductsPricedLowerThanOrEqualToReference(double referencePrice) {
		return new FilteredProductByPriceFinder().getProductsPricedLowerThanOrEqualToReference(referencePrice);
	}

	@Override
	public void returnProductsToStorehouse(ReturnDto returnDto) {
		new ReturnProductsToStorehouse(returnDto).execute();
	}

	@Override
	public void addProductsToStorehouse(ArrayList<ProductDtoWrapper> arrayList) {
		new AddProductsFromSupplierOrderToStorehouse(arrayList).execute();
	}

	@Override
	public boolean updatePrices(List<ProductDto> infoToPass) {
		return new ProductUpdater().updatePrices(infoToPass);
	}

}