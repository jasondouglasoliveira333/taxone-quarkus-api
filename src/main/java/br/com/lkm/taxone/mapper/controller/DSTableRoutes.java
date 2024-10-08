package br.com.lkm.taxone.mapper.controller;

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

import br.com.lkm.taxone.mapper.dto.DSColumnDTO;
import br.com.lkm.taxone.mapper.dto.DSTableDTO;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.service.MatcherService;
import lombok.extern.slf4j.Slf4j;

@Component
public class DSTableRoutes extends RouteBuilder {
	
	private Logger log = LoggerFactory.getLogger(DSTableRoutes.class);
	

	@Autowired
	private DSTableListProcessor dsTableListProcessor;

	@Inject
	private DSColumnListProcessor dsColumnListProcessor;

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json).enableCORS(true);
		
        rest("/dsTables")
                .get()
				.to("direct:listDSTables")

                .get("{id}/dsColumns")
				.to("direct:listDSColumns");

				
        from("direct:listDSTables")
				.process(dsTableListProcessor);

        from("direct:listDSColumns")
				.process(dsColumnListProcessor);
				
    }
}

@Component
class DSTableListProcessor implements Processor{

	@Autowired
	private MatcherService matcherService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		List<DSTableDTO> dsTables = matcherService.getDSTables();
		exchange.getMessage().setBody(dsTables);
	}

}	
			
@Component
class DSColumnListProcessor implements Processor{

	@Autowired
	private MatcherService matcherService; 

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", Integer.class);
		Integer page = exchange.getIn().getHeader("page", 0, Integer.class);
		Integer size = exchange.getIn().getHeader("size", 10, Integer.class);

		PageResponse<DSColumnDTO> dsColumns = matcherService.getDSColumns(id, PageRequest.of(page, size));
		exchange.getMessage().setBody(dsColumns);
	}

}	

