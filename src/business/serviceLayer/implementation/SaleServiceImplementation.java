package business.serviceLayer.implementation;

import java.util.ArrayList;
import java.util.List;

import business.clientLogic.GetClientById;
import business.paymentMethodLogic.GetTypeOfPaymentMeanById;
import business.productLogic.FindProductsInSale;
import business.saleLogic.DirectSaleItemChecker;
import business.saleLogic.DirectSalesAdder;
import business.saleLogic.FindAllSales;
import business.saleLogic.FindSaleById;
import business.saleLogic.SalesAdder;
import business.serviceLayer.SaleService;
import common.BusinessException;
import dtos.ClientDto;
import dtos.ProductSaleDto;
import dtos.SaleDto;
import dtos.SaleHistoryEntryDto;
import dtos.SupplierOrderedProductDto;
import dtos.TransportDto;
import wrappers.ProductDtoWrapper;

public class SaleServiceImplementation implements SaleService {

	public int addSale(SaleDto sdto, TransportDto tdto)
			throws BusinessException {
		return new SalesAdder(sdto, tdto).execute();
	}

	@Override
	public List<SaleHistoryEntryDto> generateSalesHistoryEntries()
			throws BusinessException {
		List<SaleHistoryEntryDto> salesEntries =
				new ArrayList<SaleHistoryEntryDto>();
		List<SaleDto> sales = new FindAllSales().execute();

		for (SaleDto sale : sales) {
			SaleHistoryEntryDto entry = new SaleHistoryEntryDto();
			entry.saleId = sale.id;
			SaleDto dto = new FindSaleById(sale.id).execute();

			if (dto.clientId != Integer.MIN_VALUE) {
				ClientDto tempClient =
						new GetClientById(dto.clientId).execute();
				entry.clientDni = tempClient.dni;
				entry.clientName = tempClient.name;
			} else {
				entry.clientName = "No hay cliente asignado";
				entry.clientDni = "Venta directa";
			}

			if (dto.paymentMeanId != Integer.MIN_VALUE) {
				entry.paymentMethod =
						new GetTypeOfPaymentMeanById(dto.paymentMeanId)
								.execute();
			} else {
				entry.paymentMethod = "No consta";
			}

			entry.saleDate = sale.date;
			salesEntries.add(entry);
		}

		return salesEntries;
	}

	@Override
	public List<ProductDtoWrapper> findProductsOnSale(int selectedSaleId) {
		return new FindProductsInSale(selectedSaleId).execute();
	}

	@Override
	public SaleDto findById(int saleToInvoiceId) {
		return new FindSaleById(saleToInvoiceId).execute();
	}

	@Override
	public List<SupplierOrderedProductDto>
			checkDirectSaleItemsInStock(List<ProductSaleDto> orderedProducts) {
		return new DirectSaleItemChecker(orderedProducts).execute();

	}

	public int addDirectSale(SaleDto sdto, TransportDto tdto,
			List<ProductSaleDto> fullOrder) throws BusinessException {
		return new DirectSalesAdder(sdto, tdto, fullOrder).execute();
	}

}
