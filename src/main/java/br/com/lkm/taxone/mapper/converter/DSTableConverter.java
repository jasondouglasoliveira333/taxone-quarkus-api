package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.DSTableDTO;
import br.com.lkm.taxone.mapper.entity.DSTable;

public class DSTableConverter {

	public static DSTableDTO convert(DSTable dsTable) {
		DSTableDTO dDTO = new DSTableDTO();
		dDTO.setId(dsTable.getId());
		dDTO.setName(dsTable.getName());
		return dDTO;
	}
	
	
}
