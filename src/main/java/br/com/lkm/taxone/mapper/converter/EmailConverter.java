package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.EmailDTO;
import br.com.lkm.taxone.mapper.entity.Email;

public class EmailConverter {

	public static EmailDTO convert(Email e) {
		EmailDTO eDTO = new EmailDTO();
		eDTO.setId(e.getId());
		eDTO.setEmail(e.getEmail());
		eDTO.setType(e.getType());
		return eDTO;
	}

	public static Email convert(EmailDTO eDTO) {
		Email e = new Email();
		e.setId(eDTO.getId());
		e.setEmail(eDTO.getEmail());
		e.setType(eDTO.getType());
		return e;
	}
}
