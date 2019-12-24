package business.serviceLayer;

import java.util.ArrayList;
import java.util.List;

import common.BusinessException;
import dtos.ProductDto;
import dtos.ReturnDto;
import wrappers.ProductDtoWrapper;

/**
 * Interface representing the contract that every ProductService (Façade) must
 * follow. This interface will be used by the UI layer only and acts as an
 * intermediary between this layer and the business layer, isolating all the
 * functionality of the business layer so the UI layer works as an independent
 * module. In order to create new functionalities that the UI needs to execute,
 * a new implementation for the interface must be given or extending an existing
 * one
 * 
 * @author Angel
 *
 */
public interface ProductService {

	/**
	 * Finds all products on the underlying database
	 * 
	 * @return the list of products on the underlying database encapsulated on
	 *         ProductDtos
	 */
	public List<ProductDto> getAllProducts();

	public List<ProductDtoWrapper> findProductsOnSupplierOrder(int supplierOrderId) throws BusinessException;

	public String[] getColumnNames();

	/**
	 * Retrieves all the Products from the database whose specified attribute
	 * matches one of the values given in the list.
	 * 
	 * @param attributeValues List that contains the accepted values by the filter.
	 * @return List of filtered ProductDTOs
	 */
	public List<ProductDto> getFilteredProductsByCategory(List<String> attributeValues);

	/**
	 * Retrieves all the Products from the database whose price is greater than the
	 * specified reference price.
	 * 
	 * @param referencePrice price used as a reference for comparing the price of
	 *                       each product to be filtered
	 * @return List of filtered ProductDTOs
	 */
	public List<ProductDto> getProductsPricedHigherThanReference(double referencePrice);

	/**
	 * Retrieves all the Products from the database whose price is lower than or
	 * equal to the specified reference price.
	 * 
	 * @param referencePrice price used as a reference for comparing the price of
	 *                       each product to be filtered
	 * @return List of filtered ProductDTOs
	 */
	public List<ProductDto> getProductsPricedLowerThanOrEqualToReference(double referencePrice);

	public void returnProductsToStorehouse(ReturnDto returnDto);

	public void addProductsToStorehouse(ArrayList<ProductDtoWrapper> arrayList);

	public boolean updatePrices(List<ProductDto> infoToPass);

}