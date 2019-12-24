package common;

import dtos.TransportDto;

public class DtoUtility {

	public static TransportDto createTransportDtoCopy(TransportDto baseDto) {
		TransportDto res = new TransportDto();
		res.id = baseDto.id;
		res.deliveryDate = baseDto.deliveryDate;
		res.deliveryTime = baseDto.deliveryTime;
		res.requiresAssembly = baseDto.requiresAssembly;
		res.saleId = baseDto.saleId;
		res.status = baseDto.status;
		return res;
	}

}
