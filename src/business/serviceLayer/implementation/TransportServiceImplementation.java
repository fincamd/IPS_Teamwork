package business.serviceLayer.implementation;

import java.util.List;

import business.serviceLayer.TransportationService;
import business.transport.FindTransportBySaleId;
import business.transport.GetAvailableMinutesForHourInSaturday;
import business.transport.GetAvailableMinutesForHourInWeekDay;
import business.transport.TransportationChanger;
import business.transport.TransportationFinder;
import business.transport.TransportationUnblocker;
import dtos.TransportDto;

public class TransportServiceImplementation implements TransportationService {

	@Override
	public TransportDto findBySaleId(int id) {
		return new FindTransportBySaleId(id).execute();
	}

	public List<String> getAvailableMinutesForHourInSaturday(String day, int hour) {
		return new GetAvailableMinutesForHourInSaturday(day, hour).execute();
	}

	public List<String> GetAvailableMinutesForHourInWeekDay(String day, int hour) {
		return new GetAvailableMinutesForHourInWeekDay(day, hour).execute();
	}

	public List<TransportDto> getNotDeliveredTranports() {
		return new TransportationFinder().findNotDeliveredTransports();
	}

	@Override
	public void setStatus(TransportDto dtoToChange, String status) {
		new TransportationChanger().setStatus(dtoToChange, status);
	}

	@Override
	public void unblockSaleTransportation(int saleId) {
		new TransportationUnblocker(saleId).execute();

	}

}
