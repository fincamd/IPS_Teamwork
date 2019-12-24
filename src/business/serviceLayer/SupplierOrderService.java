package business.serviceLayer;

import java.util.List;

import common.BusinessException;
import dtos.SupplierOrderDto;
import dtos.SupplierOrderedProductDto;

public interface SupplierOrderService {

	public SupplierOrderDto findSupplierOrderById(int supplierOrderId) throws BusinessException;

	public List<SupplierOrderDto> findAllSupplierOrders() throws BusinessException;

	public void markSupplierOrderAsReceived(int supplierOrderId) throws BusinessException;

	public void createSupplierOrderToStock(List<SupplierOrderedProductDto> products);// throws BusinessException;

	public void createSupplierOrderToSale(List<SupplierOrderedProductDto> productsToOrder, int saleId);

	public int assignedSaleFinder(int orderId);

	public double computeDiscountedProductPrice(int productId, int units);
}
