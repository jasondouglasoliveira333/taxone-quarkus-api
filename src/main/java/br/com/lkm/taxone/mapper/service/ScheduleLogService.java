package br.com.lkm.taxone.mapper.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
import jakarta.transaction.Transactional;

import br.com.lkm.taxone.mapper.converter.ScheduleLogConverter;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.ScheduleLogDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleLogIntergrationErrorDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleLogStatisticDTO;
import br.com.lkm.taxone.mapper.entity.ScheduleLog;
import br.com.lkm.taxone.mapper.entity.ScheduleLogIntergrationError;
import br.com.lkm.taxone.mapper.enums.ScheduleLogStatus;
import br.com.lkm.taxone.mapper.repository.ScheduleLogIntergrationErrorRepository;
import br.com.lkm.taxone.mapper.repository.ScheduleLogRepository;

@Service
public class ScheduleLogService {
	
	@Autowired
	private ScheduleLogRepository scheduleLogRepository;

	@Autowired
	private ScheduleLogIntergrationErrorRepository scheduleLogIntergrationErrorRepository;

	//Just for that
	@Transactional //(propagation = Propagation.REQUIRES_NEW)
	public void save(ScheduleLog sLog) {
		scheduleLogRepository.save(sLog);
	}

	public PageResponse<ScheduleLogDTO> findAll(ScheduleLogStatus status, PageRequest pageable) {
		Page<ScheduleLog> slPage = scheduleLogRepository.findByStatus(status, pageable);
		PageResponse<ScheduleLogDTO> pageResponse = new PageResponse<>();
		pageResponse.setTotalPages(slPage.getTotalPages());
		pageResponse.setContent(slPage.getContent().stream().map(ScheduleLogConverter::convert).collect(Collectors.toList()));
		return pageResponse;
	}

/*
	public List<ScheduleLogStatisticDTO> groupByStatus() {
		return scheduleLogRepository.groupByStatus();
	}
*/

	public ScheduleLogDTO get(Integer id) {
		return ScheduleLogConverter.convert(scheduleLogRepository.getOne(id));
	}

	public PageResponse<ScheduleLogIntergrationErrorDTO> getTaxtOneErrors(Integer id, PageRequest pageable) {
		Page<ScheduleLogIntergrationError> sliePage = scheduleLogIntergrationErrorRepository.findByScheduleLogId(id, pageable);
		PageResponse<ScheduleLogIntergrationErrorDTO> pageResponse = new PageResponse<>();
		pageResponse.setTotalPages(sliePage.getTotalPages());
		pageResponse.setContent(sliePage.getContent().stream().map(ScheduleLogConverter::convert).collect(Collectors.toList()));
		return pageResponse;
	}

}
