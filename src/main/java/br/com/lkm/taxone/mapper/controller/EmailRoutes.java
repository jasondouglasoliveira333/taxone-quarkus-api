/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.lkm.taxone.mapper.controller;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.ArrayList;

import jakarta.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import br.com.lkm.taxone.mapper.service.EmailService;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.EmailDTO;



/**
 * Camel route definitions.
 */
@Component
public class EmailRoutes extends RouteBuilder {
	
	@Inject
	private EmailListProcessor emailListProcessor;

	@Inject
	private EmailSaveProcessor emailSaveProcessor;

	@Inject
	private EmailDeleteProcessor emailDeleteProcessor;

    public EmailRoutes() {}

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json).enableCORS(true);
		
        rest("/emails")
                .get()
				.to("direct:listEmails")

                .post()
                .type(EmailDTO[].class)
                .to("direct:saveEmails")

                .delete("{id}")
				.to("direct:deleteEmail");
				
        from("direct:listEmails")
				.process(emailListProcessor);

        from("direct:saveEmails")
				.process(emailSaveProcessor);
		
        from("direct:deleteEmail")
				.process(emailDeleteProcessor);

    }

}

@Component
class EmailListProcessor implements Processor{

	@Inject
	private EmailService emailService;

	@Override
	public void process(Exchange exchange) throws Exception {
		
		Integer page = exchange.getIn().getHeader("page", 0, Integer.class);
		Integer size = exchange.getIn().getHeader("size", 10, Integer.class);
		PageResponse<EmailDTO> uPage = emailService.findAll(PageRequest.of(page, size, Direction.DESC, "id"));
		exchange.getMessage().setBody(uPage);
	}
}

@Component
class EmailSaveProcessor implements Processor{

	@Inject
	private EmailService emailService;

	@Override
	public void process(Exchange exchange) throws Exception {
		ArrayList<EmailDTO> emails = (ArrayList<EmailDTO>)exchange.getIn().getBody();
		System.out.println("emails:" + emails);
		emails.forEach((email) -> {
			emailService.save(email);
		});
		
		exchange.getMessage().setBody("OK");
	}
}

@Component
class EmailDeleteProcessor implements Processor{

	@Inject
	private EmailService emailService;

	@Override
	public void process(Exchange exchange) throws Exception {
		Integer emailId = exchange.getIn().getHeader("id", Integer.class);
		emailService.delete(emailId);
		exchange.getMessage().setBody("OK");
	}
}
