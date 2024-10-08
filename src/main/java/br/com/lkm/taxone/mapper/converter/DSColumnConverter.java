package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.DSColumnDTO;
import br.com.lkm.taxone.mapper.entity.DSColumn;

public class DSColumnConverter {
	
	public static DSColumnDTO convert(DSColumn dsColumn) {
		DSColumnDTO dDTO = new DSColumnDTO();
		dDTO.setId(dsColumn.getId());
		dDTO.setName(dsColumn.getName());
		dDTO.setColumnType(dsColumn.getColumnType());
		dDTO.setSize(dsColumn.getSize());
		dDTO.setDsTable(DSTableConverter.convert(dsColumn.getDsTable()));
		return dDTO;
	}

	public static DSColumn converter(DSColumnDTO dsd) {
		DSColumn dsc = new DSColumn();
		dsc.setName(dsd.getName());
		dsc.setColumnType(dsd.getColumnType());
		dsc.setSize(dsd.getSize());
		return dsc;
	}

	public static void marge(DSColumnDTO dsd, DSColumn dsc) {
		dsc.setColumnType(dsd.getColumnType());
		dsc.setSize(dsd.getSize());
	}

}
