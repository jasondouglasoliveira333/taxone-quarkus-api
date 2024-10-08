package br.com.lkm.taxone.mapper.converter;

import java.util.stream.Collectors;

import br.com.lkm.taxone.mapper.dto.SAFXTableDTO;
import br.com.lkm.taxone.mapper.dto.SAFXTableDetailtDTO;
import br.com.lkm.taxone.mapper.entity.DSTable;
import br.com.lkm.taxone.mapper.entity.SAFXTable;

public class SAFXTableConverter {

	public static SAFXTableDTO convert(SAFXTable safxTable) {
		SAFXTableDTO tDTO = new SAFXTableDTO();
		tDTO.setId(safxTable.getId());
		tDTO.setName(safxTable.getName());
		tDTO.setDescription(safxTable.getDescription());
		DSTable dsTable = safxTable.getDsTable(); 
		if (dsTable != null) {
			tDTO.setDsTableId(dsTable.getId());
			tDTO.setDsTableName(dsTable.getName());
		}
		return tDTO;
	}

	public static SAFXTableDetailtDTO convertWithColumns(SAFXTable safxTable) {
		SAFXTableDetailtDTO tDTO = new SAFXTableDetailtDTO();
		tDTO.setId(safxTable.getId());
		tDTO.setName(safxTable.getName());
		tDTO.setSafxColumns(safxTable.getSafxColumns().stream().map(SAFXColumnConverter::convert).collect(Collectors.toList()));
		return tDTO;
	}

	public static SAFXTableDTO convertIdName(SAFXTable safxTable) {
		SAFXTableDTO tDTO = new SAFXTableDTO();
		tDTO.setId(safxTable.getId());
		tDTO.setName(safxTable.getName());
		return tDTO;
	}

	public static SAFXTable convertIdName(SAFXTableDTO safxTable) {
		SAFXTable tDTO = new SAFXTable();
		tDTO.setId(safxTable.getId());
		tDTO.setName(safxTable.getName());
		return tDTO;
	}

}
