package br.com.lkm.taxone.mapper.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import br.com.lkm.taxone.mapper.dto.DSColumnDTO;
import br.com.lkm.taxone.mapper.dto.DSTableDTO;
import br.com.lkm.taxone.mapper.dto.DataSourceDTO;
import br.com.lkm.taxone.mapper.dto.POCUser;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.enums.DataSourceType;
import br.com.lkm.taxone.mapper.service.DataSourceConfigService;
import br.com.lkm.taxone.mapper.service.MatcherService;
import lombok.extern.slf4j.Slf4j;

@Component
public class DataSourceConfigRoutes extends RouteBuilder {
	
	public static final POCUser user = new POCUser(1, "WE", "WE", null);
	
	private Logger log = LoggerFactory.getLogger(DataSourceConfigRoutes.class);
	
	public static final Map<Integer, List<DSTableDTO>> dsTableTemporary = new HashMap<>();
	public static final Map<Integer, List<DSColumnDTO>> dsColumnsTemporary = new HashMap<>();


	@Inject
	private DataSourceListProcessor dataSourceListProcessor;
	
	@Inject
	private DataSourceGetProcessor dataSourceGetProcessor;
	
	@Inject
	private DataSourceDSTableProcessor dataSourceDSTableProcessor;
	
	@Inject
	private DataSourceDSColumnProcessor dataSourceDSColumnProcessor;
	
	@Inject
	private DataSourceMetadataProcessor dataSourceMetadataProcessor;
	
	@Inject
	private DataSourceUpdateProcessor dataSourceUpdateProcessor;


    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json).enableCORS(true);
		
        rest("/dataSourceConfigs")
                .get()
				.to("direct:listDSConfigs")

                .get("{dataSourceType}")
				.to("direct:getDSConfig")
				
                .get("{dataSourceType}/dsTables")
				.to("direct:listDsTables")
				
                .get("{dataSourceType}/dsTables/{dsTableId}/dsColumns")
				.to("direct:listDsColumns")

                .post("{dataSourceType}/metadata")
                .type(DataSourceDTO.class)
				.to("direct:getMetadata")

                .post("{dataSourceType}")
                .type(DataSourceDTO.class)
				.to("direct:updateDataSource");
				
        from("direct:listDSConfigs")
				.process(dataSourceListProcessor);

        from("direct:getDSConfig")
				.process(dataSourceGetProcessor);

        from("direct:listDsTables")
				.process(dataSourceDSTableProcessor);
				
        from("direct:listDsColumns")
				.process(dataSourceDSColumnProcessor);
				
        from("direct:getMetadata")
				.process(dataSourceMetadataProcessor);
				
        from("direct:updateDataSource")
				.process(dataSourceUpdateProcessor);
				
	}
	
	public static void loadTableAndColumns(String tableNames, List<DSColumnDTO> dsList) {
		POCUser user = DataSourceConfigRoutes.user;
		List<String> tables = Arrays.asList(tableNames.split(","));
		tables.stream().forEach(tableName -> {
			List<DSColumnDTO> dsCList = dsList.stream().filter(dsc -> dsc.getDsTable().getName().equals(tableName)).collect(Collectors.toList());
			int pseudoId = (int)(Math.random() * 100000);
			DSTableDTO dstDTO = new DSTableDTO();
			dstDTO.setId(pseudoId);
			dstDTO.setName(tableName);
			List<DSTableDTO> dstList = DataSourceConfigRoutes.dsTableTemporary.get(user.getId());
			if (dstList == null) {
				dstList = new ArrayList<>();
				DataSourceConfigRoutes.dsTableTemporary.put(user.getId(), dstList);
			}
			dstList.add(dstDTO);
			DataSourceConfigRoutes.dsColumnsTemporary.put(pseudoId, dsCList);
		});

	}

	public static void clearUserTableAndColumns() {
		POCUser user = DataSourceConfigRoutes.user;
		if (DataSourceConfigRoutes.dsTableTemporary.get(user.getId()) != null) {
			DataSourceConfigRoutes.dsTableTemporary.get(user.getId()).forEach(dsTable -> {
				DataSourceConfigRoutes.dsColumnsTemporary.remove(dsTable.getId());
			});
			DataSourceConfigRoutes.dsTableTemporary.remove(user.getId());
		}
	}

}	

@Component
class DataSourceListProcessor implements Processor{
	
	@Inject
	private DataSourceConfigService dataSourceConfigService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		List<DataSourceDTO> dss = dataSourceConfigService.list();
		exchange.getMessage().setBody(dss);
	}
	
}

@Component
class DataSourceGetProcessor implements Processor{
	
	@Inject
	private DataSourceConfigService dataSourceConfigService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		String dataSourceType = exchange.getIn().getHeader("dataSourceType", String.class);
		DataSourceDTO ds = dataSourceConfigService.get(dataSourceType);
		exchange.getMessage().setBody(ds);
	}
	
}

@Component
class DataSourceDSTableProcessor implements Processor{
	
	@Inject
	private DataSourceConfigService dataSourceConfigService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		POCUser user = DataSourceConfigRoutes.user;
		String dataSourceType = exchange.getIn().getHeader("dataSourceType", String.class);
		List<DSTableDTO> dsTs = DataSourceConfigRoutes.dsTableTemporary.get(user.getId());
		if (dsTs == null) {
			dsTs = dataSourceConfigService.getDSTables(dataSourceType);
		}
		exchange.getMessage().setBody(dsTs);
	}
	
}

@Component
class DataSourceDSColumnProcessor implements Processor{
	
	@Inject
	private MatcherService matcherService;


	@Override
	public void process(Exchange exchange) throws Exception {
		String dataSourceType = exchange.getIn().getHeader("dataSourceType", String.class);
		Integer dsTableId = exchange.getIn().getHeader("dsTableId", Integer.class);
		Integer page = exchange.getIn().getHeader("page", 0, Integer.class);
		Integer size = exchange.getIn().getHeader("size", 10, Integer.class);
		PageResponse<DSColumnDTO> dsCPage = null; 
		List<DSColumnDTO> dscList = DataSourceConfigRoutes.dsColumnsTemporary.get(dsTableId);
		if (dscList != null) {
			dsCPage = new PageResponse<>();
			int lastIdx = page * size + size;
			if (lastIdx > dscList.size()) {
				lastIdx = dscList.size();  
			}
			dsCPage.setContent(dscList.subList(page * size, lastIdx));
			int totalPages = dscList.size() / size + (dscList.size() % size == 0 ? 0 : 1);
			System.out.println("totalPages:" + totalPages);
			dsCPage.setTotalPages(totalPages);
		}else {
			dsCPage = matcherService.getDSColumns(dsTableId, PageRequest.of(page, size));
		}
		exchange.getMessage().setBody(dsCPage);
	}
	
}

@Component
class DataSourceMetadataProcessor implements Processor{
	
	@Inject
	private MatcherService matcherService;


	@Override
	public void process(Exchange exchange) throws Exception {
		String dataSourceType = exchange.getIn().getHeader("dataSourceType", String.class);
		DataSourceDTO dataSourceDTO = (DataSourceDTO)exchange.getIn().getBody();
		dataSourceDTO.setDataSourceType(DataSourceType.valueOf(dataSourceType));
		List<DSColumnDTO> dsList = matcherService.getDSMetadata(dataSourceDTO);
		System.out.println("dsListMetadata.size:" + dsList.size());
		DataSourceConfigRoutes.clearUserTableAndColumns();
		DataSourceConfigRoutes.loadTableAndColumns(dataSourceDTO.getResourceNames(), dsList);
		exchange.getMessage().setBody("OK");
	}
}

@Component
class DataSourceUpdateProcessor implements Processor{
	
	@Inject
	private DataSourceConfigService dataSourceConfigService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		POCUser user = DataSourceConfigRoutes.user;
		String dataSourceType = exchange.getIn().getHeader("dataSourceType", String.class);
		DataSourceDTO dataSourceDTO = (DataSourceDTO)exchange.getIn().getBody();
		dataSourceDTO.setDataSourceType(DataSourceType.valueOf(dataSourceType));
		int dsId = dataSourceConfigService.saveDataSourrce(dataSourceDTO);
		if (DataSourceConfigRoutes.dsTableTemporary.get(user.getId()) != null) {
			DataSourceConfigRoutes.dsTableTemporary.get(user.getId()).forEach(dsTable -> {
				dataSourceConfigService.saveTablesAndColumns(dsId, dsTable, DataSourceConfigRoutes.dsColumnsTemporary.get(dsTable.getId()));
			});
		}
		DataSourceConfigRoutes.clearUserTableAndColumns();
		exchange.getMessage().setBody("OK");
	}
}
	
	
