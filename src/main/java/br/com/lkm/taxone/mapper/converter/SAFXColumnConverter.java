package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.SAFXColumnDTO;
import br.com.lkm.taxone.mapper.dto.SAFXTableDTO;
import br.com.lkm.taxone.mapper.entity.DSColumn;
import br.com.lkm.taxone.mapper.entity.SAFXColumn;
import br.com.lkm.taxone.mapper.entity.SAFXTable;

public class SAFXColumnConverter {

	public static SAFXColumnDTO convert(SAFXColumn safxcolumn) {
		SAFXColumnDTO cDTO = new SAFXColumnDTO();
		cDTO.setId(safxcolumn.getId());
		cDTO.setName(safxcolumn.getName());
		cDTO.setColumnType(safxcolumn.getColumnType());
		cDTO.setRequired(safxcolumn.getRequired());
		cDTO.setSize(safxcolumn.getSize());
		cDTO.setPosition(safxcolumn.getPosition());
		
		DSColumn dsColumn = safxcolumn.getDsColumn();
		if (dsColumn != null) {
			cDTO.setDsColumnId(dsColumn.getId());
			cDTO.setDsColumnName(dsColumn.getName());
		}
		return cDTO;
	}

	public static SAFXColumnDTO convertCriteria(SAFXColumn safxcolumn) {
		SAFXColumnDTO cDTO = new SAFXColumnDTO();
		cDTO.setId(safxcolumn.getId());
		cDTO.setName(safxcolumn.getName());
		SAFXTableDTO safxTableDTO = new SAFXTableDTO();
		SAFXTable safxTable = safxcolumn.getSafxTable();
		safxTableDTO.setId(safxTable.getId());
		safxTableDTO.setName(safxTable.getName());
		cDTO.setSafxTable(safxTableDTO);
		return cDTO;
	}
	
	public static SAFXColumn convert(SAFXColumnDTO safxcolumn) {
		SAFXColumn c = new SAFXColumn();
		c.setId(safxcolumn.getId());
		return c; 
	}

}
