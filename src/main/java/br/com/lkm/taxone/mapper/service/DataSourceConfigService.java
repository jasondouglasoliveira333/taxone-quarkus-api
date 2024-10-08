package br.com.lkm.taxone.mapper.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.lkm.taxone.mapper.converter.DSColumnConverter;
import br.com.lkm.taxone.mapper.converter.DSTableConverter;
import br.com.lkm.taxone.mapper.converter.DataSourceConfigConverter;
import br.com.lkm.taxone.mapper.dto.DSColumnDTO;
import br.com.lkm.taxone.mapper.dto.DSTableDTO;
import br.com.lkm.taxone.mapper.dto.DataSourceDTO;
import br.com.lkm.taxone.mapper.entity.DSColumn;
import br.com.lkm.taxone.mapper.entity.DSTable;
import br.com.lkm.taxone.mapper.entity.DataSourceConfiguration;
import br.com.lkm.taxone.mapper.enums.DataSourceType;
import br.com.lkm.taxone.mapper.repository.DSColumnRepository;
import br.com.lkm.taxone.mapper.repository.DSTableRepository;
import br.com.lkm.taxone.mapper.repository.DataSourceConfigRepository;

@Service
public class DataSourceConfigService {

	@Autowired
	private DataSourceConfigRepository dataSourceConfigRepository;
	
	@Autowired
	private DSTableRepository dsTableRepository;

	@Autowired
	private DSColumnRepository dsColumnRepository;

	public List<DataSourceDTO> list() {
		return dataSourceConfigRepository.findAll().stream().map(DataSourceConfigConverter::convert)
				.collect(Collectors.toList());
	}

	public DataSourceDTO get(String dataSourceType) {
		return DataSourceConfigConverter.convert(dataSourceConfigRepository.findByDataSourceType(DataSourceType.valueOf(dataSourceType)));
	}

	public List<DSTableDTO> getDSTables(String dataSourceType) {
		return dsTableRepository.findBydataSourceConfigurationDataSourceType(DataSourceType.valueOf(dataSourceType)).
				stream().map(DSTableConverter::convert).collect(Collectors.toList());
	}

	public int saveDataSourrce(DataSourceDTO dsDTO) {
		DataSourceConfiguration dsc= DataSourceConfigConverter.convert(dsDTO);
		return dataSourceConfigRepository.save(dsc).getId();
	}

	public void saveTablesAndColumns(Integer dataSourceConfigId, DSTableDTO dsTable, List<DSColumnDTO> dsColumnsList) {
		
		DSTable dst = dsTableRepository.findFirstBydataSourceConfigurationIdAndName(dataSourceConfigId, dsTable.getName());
		if (dst == null) {
			dst = new DSTable();
			dst.setDataSourceConfiguration(dataSourceConfigRepository.getOne(dataSourceConfigId));
			dst.setName(dsTable.getName());
			dsTableRepository.save(dst);
		}
		
		for (DSColumnDTO dsColumnDTO : dsColumnsList)  {
			DSColumn dsc = dsColumnRepository.findFirstBydsTableIdAndName(dst.getId(), dsColumnDTO.getName());
			if (dsc == null) {
				dsc = DSColumnConverter.converter(dsColumnDTO);
				dsc.setDsTable(dst);
			}else {
				DSColumnConverter.marge(dsColumnDTO, dsc);
			}
			dsColumnRepository.save(dsc);
		}
	}





}
