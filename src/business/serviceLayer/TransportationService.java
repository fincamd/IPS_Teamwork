package business.serviceLayer;

import java.util.List;

import dtos.TransportDto;

public interface TransportationService {

	TransportDto findBySaleId(int i);

	public List<TransportDto> getNotDeliveredTranports();

	public void setStatus(TransportDto dtoToChange, String status);

	void unblockSaleTransportation(int saleId);

}
