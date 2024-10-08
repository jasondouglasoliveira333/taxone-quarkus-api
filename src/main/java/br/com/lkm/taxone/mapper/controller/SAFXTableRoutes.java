package br.com.lkm.taxone.mapper.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.SAFXColumnDTO;
import br.com.lkm.taxone.mapper.dto.SAFXColumnUpdateDTO;
import br.com.lkm.taxone.mapper.dto.SAFXTableDTO;
import br.com.lkm.taxone.mapper.service.MatcherService;
import lombok.extern.slf4j.Slf4j;



@Component
public class SAFXTableRoutes extends RouteBuilder {
	
	private Logger log = LoggerFactory.getLogger(SAFXTableRoutes.class);
	
	@Inject
	private SAFXTableListProcessor safxTableListProcessor;
	
	@Inject
	private SAFXTableGetProcessor safxTableGetProcessor;
	
	@Inject
	private SAFXColumnListProcessor safxColumnListProcessor;
	
	@Inject
	private SAFXColumnUpdateProcessor safxColumnUpdateProcessor;
	
	@Inject
	private SAFXTableUpdateProcessor safxTableUpdateProcessor;

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json).enableCORS(true);
		
        rest("/safxTables")
                .get()
				.to("direct:listSAFXTables")

                .get("{id}")
				.to("direct:getSAFXTables")
				
                .get("{id}/safxColumns")
				.to("direct:getSAFXColumns")

                .put("{id}/safxColumns")
                .type(SAFXColumnUpdateDTO[].class)
				.to("direct:updateSAFXColumns")

                .put("{id}/dsTables/{dsTableId}")
				.to("direct:updateSAFXTable");
				
        from("direct:listSAFXTables")
				.process(safxTableListProcessor);

        from("direct:getSAFXTables")
				.process(safxTableGetProcessor);

        from("direct:getSAFXColumns")
				.process(safxColumnListProcessor);
				
        from("direct:updateSAFXColumns")
				.process(safxColumnUpdateProcessor);
				
        from("direct:updateSAFXTable")
				.process(safxTableUpdateProcessor);
				

    }
}

@Component
class SAFXTableGetProcessor implements Processor{
	
	@Inject
	private MatcherService matcherService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", Integer.class);
		SAFXTableDTO safxTable = matcherService.getSAFXTable(id);
		exchange.getMessage().setBody(safxTable);
	}
	
}

@Component
class SAFXColumnListProcessor implements Processor{

	@Inject
	private MatcherService matcherService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", 0, Integer.class);
		Boolean associated = exchange.getIn().getHeader("associated", false, Boolean.class);
		List<SAFXColumnDTO> safxColumns = matcherService.getSAFXColumns(id, associated);
		exchange.getMessage().setBody(safxColumns);
	}
}
			
@Component
class SAFXColumnUpdateProcessor implements Processor{

	@Inject
	private MatcherService matcherService;

	@Override
	public void process(Exchange exchange) throws Exception {
		ArrayList<SAFXColumnUpdateDTO> safxColumns = (ArrayList<SAFXColumnUpdateDTO>)exchange.getIn().getBody();
		matcherService.updateSAFXColumns(safxColumns);
		exchange.getMessage().setBody("Ok");
	}
}	

@Component
class SAFXTableUpdateProcessor implements Processor{

	@Inject
	private MatcherService matcherService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", 0, Integer.class);
		Integer dsTableId = exchange.getIn().getHeader("dsTableId", false, Integer.class);
		matcherService.updateSAFXTable(id, dsTableId);
		exchange.getMessage().setBody("Ok");
	}
}

@Component
class SAFXTableListProcessor implements Processor{

	@Inject
	private MatcherService matcherService;

	@Override
	public void process(Exchange exchange) throws Exception {
		String tableName = exchange.getIn().getHeader("tableName", String.class);
		Boolean justAssociated = exchange.getIn().getHeader("justAssociated", false, Boolean.class);
		Integer page = exchange.getIn().getHeader("page", 0, Integer.class);
		Integer size = exchange.getIn().getHeader("size", 10, Integer.class);
		PageResponse<SAFXTableDTO> sPage = matcherService.findAllSafx(tableName, justAssociated, PageRequest.of(page, size));
		exchange.getMessage().setBody(sPage);
	}
}

