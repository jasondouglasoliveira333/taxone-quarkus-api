package br.com.lkm.taxone.mapper.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import br.com.lkm.taxone.mapper.converter.ScheduleConverter;
import br.com.lkm.taxone.mapper.dto.PageResponse;
import br.com.lkm.taxone.mapper.dto.PeriodeDTO;
import br.com.lkm.taxone.mapper.dto.ScheduleDTO;
import br.com.lkm.taxone.mapper.entity.Criteria;
import br.com.lkm.taxone.mapper.entity.Schedule;
import br.com.lkm.taxone.mapper.enums.ScheduleLogStatus;
import br.com.lkm.taxone.mapper.enums.ScheduleStatus;
import br.com.lkm.taxone.mapper.repository.CriteriaRepository;
import br.com.lkm.taxone.mapper.repository.ScheduleLogRepository;
import br.com.lkm.taxone.mapper.repository.ScheduleRepository;

@Service
public class ScheduleService {
	
	private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);
	
	@Autowired
	private ScheduleRepository scheduleRepository; 

	@Autowired
	private CriteriaRepository criteriaRepository;
	
	@Autowired
	private ScheduleLogRepository scheduleLogRepository;

	@Transactional
	public PageResponse<ScheduleDTO> list(Pageable pageable) {
		Page<Schedule> page = scheduleRepository.findByStatus(ScheduleStatus.ACTIVE, pageable);
		PageResponse<ScheduleDTO> sPage = new PageResponse<>();
		sPage.setContent(page.stream().map(ScheduleConverter::convert).collect(Collectors.toList()));
		sPage.setTotalPages(page.getTotalPages());
		return sPage;
	}

	@Transactional
	public ScheduleDTO get(Integer id) {
		Schedule s = scheduleRepository.getOne(id);
		return ScheduleConverter.convertWithDetail(s);
	}

	@Transactional
	public void save(ScheduleDTO sDTO) {
		final List<Integer> cDeleted = new ArrayList<>();
		if (sDTO.getId() != null) {
			cDeleted.addAll(scheduleRepository.getOne(sDTO.getId()).getCriterias().stream().map(Criteria::getId).collect(Collectors.toList()));
		}
		Schedule s = ScheduleConverter.convert(sDTO);
		if (s.getId() == null) {
			s.setLastExecution(LocalDateTime.now());
		}
		s.setStatus(ScheduleStatus.ACTIVE);
		scheduleRepository.save(s);
		s.getCriterias().stream().forEach(c -> {
			cDeleted.remove(c.getId());
			c.setSchedule(s);
			criteriaRepository.save(c);
		});
		System.out.println("cDeleted:" + cDeleted);
		cDeleted.stream().forEach(cId -> {
			criteriaRepository.deleteById(cId);
		});
	}

//	public void delete(Integer id) {
//		Schedule s = scheduleRepository.getOne(id);
//		s.getCriterias().stream().forEach(c -> criteriaRepository.delete(c));
//		scheduleRepository.delete(s);
//	}

	@Transactional
	public PeriodeDTO getPeriode(Integer id) {
		Schedule s = scheduleRepository.getOne(id);
		PeriodeDTO p = new PeriodeDTO();
		p.setDays(s.getDays());
		p.setHours(s.getHours());
		return p;
	}

	@Transactional
	public boolean isWaitingTaxoneResponse(Integer scheduleId) {
		long count = scheduleLogRepository.countByScheduleIdAndStatus(scheduleId, ScheduleLogStatus.SENT);
		log.info(">>>count:" + count);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Transactional
	public void updateStatus(Integer scheduleId, ScheduleStatus status) {
		scheduleRepository.updateStatus(scheduleId, status);
	}

}
