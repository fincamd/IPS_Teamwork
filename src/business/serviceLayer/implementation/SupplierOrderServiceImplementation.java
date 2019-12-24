package business.serviceLayer.implementation;

import java.util.List;

import business.serviceLayer.SupplierOrderService;
import business.supplierOrderLogic.AssignedSaleFinder;
import business.supplierOrderLogic.CreateSupplierOrderToSale;
import business.supplierOrderLogic.CreateSupplierOrderToStock;
import business.supplierOrderLogic.FindAllSupplierOrders;
import business.supplierOrderLogic.FindSupplierOrderById;
import business.supplierOrderLogic.MarkSupplierOrderAsReceived;
import business.supplierOrderLogic.SupplierProductsDiscountedPriceComputer;
import common.BusinessException;
import dtos.SupplierOrderDto;
import dtos.SupplierOrderedProductDto;

public class SupplierOrderServiceImplementation implements SupplierOrderService {

	@Override
	public SupplierOrderDto findSupplierOrderById(int supplierOrderId) throws BusinessException {
		return new FindSupplierOrderById(supplierOrderId).execute();
	}

	@Override
	public List<SupplierOrderDto> findAllSupplierOrders() throws BusinessException {
		return new FindAllSupplierOrders().execute();
	}

	@Override
	public void markSupplierOrderAsReceived(int supplierOrderId) throws BusinessException {
		new MarkSupplierOrderAsReceived(supplierOrderId).execute();
	}

	@Override
	public void createSupplierOrderToStock(List<SupplierOrderedProductDto> products) {
		new CreateSupplierOrderToStock(products).execute();

	}

	@Override
	public void createSupplierOrderToSale(List<SupplierOrderedProductDto> productsToOrder, int saleId) {
		new CreateSupplierOrderToSale(productsToOrder, saleId).execute();

	}

	@Override
	public int assignedSaleFinder(int orderId) {
		return new AssignedSaleFinder(orderId).execute();
	}

	@Override
	public double computeDiscountedProductPrice(int productId, int units) {
		return new SupplierProductsDiscountedPriceComputer(productId, units).execute();
	}
}
