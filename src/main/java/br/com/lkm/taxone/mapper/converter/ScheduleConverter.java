package br.com.lkm.taxone.mapper.converter;

import java.util.List;
import java.util.stream.Collectors;

import br.com.lkm.taxone.mapper.dto.CriteriaDTO;
import br.com.lkm.taxone.mapper.dto.SAFXTableDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleDTO;
import br.com.lkm.taxone.mapper.entity.Criteria;
import br.com.lkm.taxone.mapper.entity.SAFXTable;
import br.com.lkm.taxone.mapper.entity.Schedule;
import br.com.lkm.taxone.mapper.entity.User;

public class ScheduleConverter {

	public static ScheduleDTO convert(Schedule schedule) {
		ScheduleDTO sDTO = new ScheduleDTO();
		sDTO.setId(schedule.getId());
		sDTO.setName(schedule.getName());
		sDTO.setDays(schedule.getDays());
		sDTO.setHours(schedule.getHours());
		sDTO.setStatus(schedule.getStatus());
		sDTO.setUserName(schedule.getUser().getName());
		return sDTO;
	}
	
	public static ScheduleDTO convertWithDetail(Schedule schedule) {
		ScheduleDTO sDTO = convert(schedule);
		List<SAFXTableDTO> safxtList = schedule.getSafxTables().stream().map(SAFXTableConverter::convertIdName).collect(Collectors.toList());
		sDTO.setSafxTables(safxtList);
		List<CriteriaDTO> criteriaList = schedule.getCriterias().stream().map(CriteriaConverter::convert).collect(Collectors.toList());
		sDTO.setCriterias(criteriaList);
		return sDTO;
	}

	public static Schedule convert(ScheduleDTO sDTO) {
		Schedule s = new Schedule();
		s.setId(sDTO.getId());
		s.setName(sDTO.getName());
		s.setDays(sDTO.getDays());
		s.setHours(sDTO.getHours());
		s.setStatus(sDTO.getStatus());
		s.setUser(new User(1)); 
		List<SAFXTable> safxtList = sDTO.getSafxTables().stream().map(SAFXTableConverter::convertIdName).collect(Collectors.toList());
		s.setSafxTables(safxtList);
		List<Criteria> criteriaList = sDTO.getCriterias().stream().map(CriteriaConverter::convert).collect(Collectors.toList());
		s.setCriterias(criteriaList);
		return s;
	}

}
