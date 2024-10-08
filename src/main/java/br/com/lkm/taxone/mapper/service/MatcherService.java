package br.com.lkm.taxone.mapper.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import br.com.lkm.taxone.mapper.converter.DSColumnConverter;
import br.com.lkm.taxone.mapper.converter.DSTableConverter;
import br.com.lkm.taxone.mapper.converter.SAFXColumnConverter;
import br.com.lkm.taxone.mapper.converter.SAFXTableConverter;
import br.com.lkm.taxone.mapper.dto.DSColumnDTO;
import br.com.lkm.taxone.mapper.dto.DSTableDTO;
import br.com.lkm.taxone.mapper.dto.DataSourceDTO;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.SAFXColumnDTO;
import br.com.lkm.taxone.mapper.dto.SAFXColumnUpdateDTO;
import br.com.lkm.taxone.mapper.dto.SAFXTableDTO;
import br.com.lkm.taxone.mapper.entity.DSColumn;
import br.com.lkm.taxone.mapper.entity.SAFXColumn;
import br.com.lkm.taxone.mapper.entity.SAFXTable;
import br.com.lkm.taxone.mapper.enums.DataSourceType;
import br.com.lkm.taxone.mapper.repository.DSColumnRepository;
import br.com.lkm.taxone.mapper.repository.DSTableRepository;
import br.com.lkm.taxone.mapper.repository.SAFXColumnRepository;
import br.com.lkm.taxone.mapper.repository.SAFXTableRepository;
import br.com.lkm.taxone.mapper.util.DatabaseHelper;
import br.com.lkm.taxone.mapper.util.FTPHelper;
import br.com.lkm.taxone.mapper.util.FileHelper;
import br.com.lkm.taxone.mapper.util.StringUtil;

@Service
public class MatcherService {
	
	@Autowired
	private SAFXTableRepository safxTableRepository; 

	@Autowired
	private SAFXColumnRepository safxColumnRepository; 
	
	@Autowired
	private DSTableRepository dsTableRepository;

	@Autowired
	private DSColumnRepository dsColumnRepository;

	@Transactional
	public PageResponse<SAFXTableDTO> findAllSafx(String name, Boolean justAssociated, PageRequest page) {
		Page<SAFXTable> safxPage = safxTableRepository.findByNameAndAssociated(name, justAssociated, page);
		PageResponse<SAFXTableDTO> sfResponse = new PageResponse<>();
		System.out.println("safxPage.getContent().size:" + safxPage.getContent().size());
		sfResponse.setContent(safxPage.getContent().stream().map(SAFXTableConverter::convert).collect(Collectors.toList())); 
		sfResponse.setTotalPages(safxPage.getTotalPages());
		return sfResponse;
	}

	@Transactional
	public SAFXTableDTO getSAFXTable(Integer id) {
		return SAFXTableConverter.convert(safxTableRepository.getOne(id));
	}

	@Transactional
	public List<SAFXColumnDTO> getSAFXColumns(Integer id, Boolean associated) {
		List<SAFXColumn> scList = safxColumnRepository.findBysafxTableId(id);
		return scList.stream().filter(sc -> associated ? sc.getDsColumn() != null : true).
				map(SAFXColumnConverter::convert).collect(Collectors.toList());
	}

	@Transactional
	public PageResponse<DSColumnDTO> getDSColumns(Integer id, Pageable page) {
		Page<DSColumn> dcPage = dsColumnRepository.findBydsTableId(id, page);
		PageResponse<DSColumnDTO> sfResponse = new PageResponse<>();
		sfResponse.setContent(dcPage.getContent().stream().map(DSColumnConverter::convert).collect(Collectors.toList())); 
		sfResponse.setTotalPages(dcPage.getTotalPages());
		return sfResponse;
	}

	@Transactional
	public List<DSTableDTO> getDSTables() {
		return dsTableRepository.findAll().stream().map(DSTableConverter::convert).collect(Collectors.toList());
	}

	public void updateSAFXColumns(List<SAFXColumnUpdateDTO> safxColumns) {
		safxColumns.stream().forEach(safxColumn -> {
			safxColumnRepository.updateSAFXColumn(safxColumn.getId(), safxColumn.getDsColumnId());
		});
	}

	public void updateSAFXTable(Integer id, Integer dsTableId) {
		safxTableRepository.updateDSTable(id, dsTableId);
	}

	@Transactional
	public List<DSColumnDTO> getDSMetadata(DataSourceDTO dataSourceDTO) throws Exception{
		List<DSColumnDTO> dsList = null;
		if (dataSourceDTO.getDataSourceType().equals(DataSourceType.Database)) {
			dsList = DatabaseHelper.getTableMetadata(dataSourceDTO);
		}else if (dataSourceDTO.getDataSourceType().equals(DataSourceType.TXT)) {
			dsList = FileHelper.getFileMetadata(dataSourceDTO);
		}else {
			dsList = FTPHelper.getFileMetadata(dataSourceDTO);
		}
		return dsList;
	}

}
