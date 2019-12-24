package business.serviceLayer.implementation;

import java.util.List;

import business.returnsLogic.AddNewReturn;
import business.returnsLogic.FindReturnBySaleAndProduct;
import business.returnsLogic.FindReturnedProductsInSale;
import business.returnsLogic.UpdateReturn;
import business.serviceLayer.ReturnService;
import dtos.ReturnDto;

public class ReturnServiceImplementation implements ReturnService {

	@Override
	public List<ReturnDto> findReturnedProductsForSale(int selectedSaleId) {
		return new FindReturnedProductsInSale(selectedSaleId).execute();
	}

	@Override
	public ReturnDto findBySaleAndProductId(int saleid, int productId) {
		return new FindReturnBySaleAndProduct(saleid, productId).execute();
	}

	@Override
	public void addNewReturn(ReturnDto returnDto) {
		new AddNewReturn(returnDto).execute();
	}

	@Override
	public void updateReturn(ReturnDto returnDto) {
		new UpdateReturn(returnDto).execute();
	}

}
