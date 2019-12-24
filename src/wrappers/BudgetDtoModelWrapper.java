package wrappers;

import dtos.BudgetDto;

public class BudgetDtoModelWrapper implements DtoWrapper {

	public BudgetDto dto;

	public BudgetDtoModelWrapper(BudgetDto dto) {
		this.dto = dto;
	}
	
	@Override
	public Object[] getData() {
		return new Object[] {dto.id, dto.status};
	}

	@Override
	public Object[] getHeaders() {
		return new Object[] {"ID", "Estado"};
	}
	
	public BudgetDto getDto() {
		return dto;
	}
	
	@Override
	public String toString() {
		return dto.id + " - " + dto.status;
	}
}
