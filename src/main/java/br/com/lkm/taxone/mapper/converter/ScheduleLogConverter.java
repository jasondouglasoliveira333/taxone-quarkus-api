package br.com.lkm.taxone.mapper.converter;

import br.com.lkm.taxone.mapper.dto.ScheduleLogDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleLogIntergrationErrorDTO;
import br.com.lkm.taxone.mapper.entity.ScheduleLog;
import br.com.lkm.taxone.mapper.entity.ScheduleLogIntergrationError;

public class ScheduleLogConverter {

	public static ScheduleLogDTO convert(ScheduleLog sl) {
		ScheduleLogDTO slDTO = new ScheduleLogDTO();
		slDTO.setId(sl.getId());
		slDTO.setScheduleName(sl.getSchedule().getName());
		slDTO.setExecutionDate(sl.getExecutionDate());
		slDTO.setStatus(sl.getStatus());
		return slDTO;
	}

//	public static ScheduleLogDTO convertWithErrors(ScheduleLog sl) {
//		ScheduleLogDTO slDTO = convert(sl);
//		slDTO.setTaxOneErrors(sl.getTaxOneErrors().stream().map(ScheduleLogConverter::convert).collect(Collectors.toList()));
//		return slDTO;
//	}
	
	public static ScheduleLogIntergrationErrorDTO convert(ScheduleLogIntergrationError slie) {
		ScheduleLogIntergrationErrorDTO slieDTO = new ScheduleLogIntergrationErrorDTO();
		slieDTO.setId(slie.getId());
		slieDTO.setNumeroReg(slie.getNumeroReg());
		slieDTO.setNomeCampo(slie.getNomeCampo());
		slieDTO.setCodigoErro(slie.getCodigoErro());
		slieDTO.setDescricaoErro(slie.getDescricaoErro());
		slieDTO.setChaveRegistro(slie.getChaveRegistro());
		return slieDTO;
	}
}
