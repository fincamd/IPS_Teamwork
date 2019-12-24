package wrappers;

import dtos.TransportDto;

public class TransportDtoWrapper implements DtoWrapper {

	private TransportDto dto;

	public TransportDtoWrapper(TransportDto dto) {
		this.dto = dto;
	}

	@Override
	public Object[] getData() {
		return new Object[] { String.valueOf(dto.id), String.valueOf(dto.saleId), dto.deliveryDate, dto.deliveryTime,
				dto.status };
	}

	@Override
	public Object[] getHeaders() {
		return new Object[] { "ID_Transporte", "ID_Venta", "Fecha_entrega", "Hora_entrega", "Estado" };
	}

	public TransportDto getDto() {
		return dto;
	}

}
