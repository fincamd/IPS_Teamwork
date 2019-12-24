package business.serviceLayer;

import java.util.List;

import dtos.ReturnDto;

public interface ReturnService {

	List<ReturnDto> findReturnedProductsForSale(int selectedSaleId);

	ReturnDto findBySaleAndProductId(int parseInt, int id);

	void addNewReturn(ReturnDto returnDto);

	void updateReturn(ReturnDto returnDto);

}
