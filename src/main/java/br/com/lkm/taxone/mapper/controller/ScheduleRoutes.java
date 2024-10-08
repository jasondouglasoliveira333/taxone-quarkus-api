package br.com.lkm.taxone.mapper.controller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import br.com.lkm.taxone.mapper.dto.ErrorResponse;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.PeriodeDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleDTO;
import br.com.lkm.taxone.mapper.entity.TaxOneApi;
import br.com.lkm.taxone.mapper.enums.ScheduleStatus;
import br.com.lkm.taxone.mapper.integration.OncoClinicasTaxtOneService;
import br.com.lkm.taxone.mapper.integration.OncoClinicasTaxtOneServiceBuilder;
import br.com.lkm.taxone.mapper.repository.ScheduleRepository;
import br.com.lkm.taxone.mapper.repository.TaxOneApiRepository;
import br.com.lkm.taxone.mapper.service.ScheduleSenderService;
import br.com.lkm.taxone.mapper.service.ScheduleService;

@Component
public class ScheduleRoutes extends RouteBuilder {

	private Logger log = LoggerFactory.getLogger(ScheduleRoutes.class);

	@Inject
	private ScheduleListProcessor scheduleListProcessor;
	
	@Inject
	private ScheduleGetProcessor scheduleGetProcessor;

	@Inject
	private SchedulePeriodsProcessor schedulePeriodsProcessor;
	
	@Inject
	private ScheduleUpdateProcessor scheduleUpdateProcessor;
	
	@Inject
	private ScheduleDeleteProcessor scheduleDeleteProcessor;


    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json).enableCORS(true);
		
        rest("/schedules")
                .get()
				.to("direct:listSchedules")

                .get("{id}")
				.to("direct:getSchedule")
				
                .get("{id}/periodes")
				.to("direct:getPeriods")

                .post()
                .type(ScheduleDTO.class)
				.to("direct:updateSchedule")

                .delete("{id}")
				.to("direct:deleteSchedule");
				
        from("direct:listSchedules")
				.process(scheduleListProcessor);

        from("direct:getSchedule")
				.process(scheduleGetProcessor);

        from("direct:getPeriods")
				.process(schedulePeriodsProcessor);
				
        from("direct:updateSchedule")
				.process(scheduleUpdateProcessor);
				
        from("direct:deleteSchedule")
				.process(scheduleDeleteProcessor);
	}
	
}

@Component
class ScheduleListProcessor implements Processor{
	
	@Inject
	private ScheduleService scheduleService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer page = exchange.getIn().getHeader("page", 0, Integer.class);
		Integer size = exchange.getIn().getHeader("size", 10, Integer.class);
		PageResponse<ScheduleDTO> sPage = scheduleService.list(PageRequest.of(page, size));
		exchange.getMessage().setBody(sPage);
	}
	
}

@Component
class ScheduleGetProcessor implements Processor{
	
	@Inject
	private ScheduleService scheduleService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", Integer.class);
		ScheduleDTO sDTO = scheduleService.get(id);
		exchange.getMessage().setBody(sDTO);
	}
	
}

@Component
class SchedulePeriodsProcessor implements Processor{
	
	@Inject
	private ScheduleService scheduleService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", Integer.class);
		PeriodeDTO pDTO = scheduleService.getPeriode(id);
		exchange.getMessage().setBody(pDTO);
	}
	
}

@Component
class ScheduleUpdateProcessor implements Processor{

	@Inject
	private ScheduleService scheduleService;

	@Override
	public void process(Exchange exchange) throws Exception {
		ScheduleDTO scheduleDTO = (ScheduleDTO)exchange.getIn().getBody();
		scheduleService.save(scheduleDTO);
		exchange.getMessage().setBody("Ok");
	}
}

@Component
class ScheduleDeleteProcessor implements Processor{

	private Logger log = LoggerFactory.getLogger(ScheduleDeleteProcessor.class);

	@Inject
	private ScheduleService scheduleService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer id = exchange.getIn().getHeader("id", Integer.class);
		if (!scheduleService.isWaitingTaxoneResponse(id)) {
			log.info("can delete scheduleId");
			scheduleService.updateStatus(id, ScheduleStatus.INACTIVE);
			exchange.getMessage().setBody("Ok");
		}else {
			ErrorResponse er = new ErrorResponse(1, "Agendamento com retorno do TaxOne pendente");
			exchange.getMessage().setBody(er);
			//return ResponseEntity.badRequest().body(er);
		}
	}
}
