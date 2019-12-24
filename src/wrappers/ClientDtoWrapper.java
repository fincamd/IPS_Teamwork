package wrappers;

import dtos.ClientDto;

/**
 * Wraps the ClientDto in order to give it some functionality. A DTO by
 * definition is only used for data transfer, it has no functionality nor
 * responsibilities so that why the Wrapper is needed
 * 
 * @author Angel
 *
 */
public class ClientDtoWrapper {

	private ClientDto dto;

	public ClientDtoWrapper(ClientDto dto) {
		this.dto = dto;
	}

	@Override
	public String toString() {
		return dto.dni + " - " + dto.name + " - " + dto.street + " - " + dto.phoneNumber + " - " + dto.postCode;
	}

	public ClientDto getDto() {
		return dto;
	}
}