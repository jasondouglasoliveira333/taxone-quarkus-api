package br.com.lkm.taxone.mapper.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.lkm.taxone.mapper.entity.Schedule;
import br.com.lkm.taxone.mapper.enums.ScheduleStatus;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{

	@Query("select s from Schedule s where (s.days in (:days) and s.lastExecution < :data) or " + 
			"(s.days = :day and s.lastExecution < :data)") //for while
	List<Schedule> findByDaysContainingAndLastExecutionLessThanOrDaysAndLastExecutionLessThan(@Param("days") String days, 
		@Param("data") LocalDateTime data, @Param("day") String wildcard, @Param("data2") LocalDateTime data2);

	@Query("update Schedule s set s.status = :status where s.id = :id")
	@Modifying
	void updateStatus(@Param("id") Integer id, @Param("status") ScheduleStatus status);

	Page<Schedule> findByStatus(ScheduleStatus status, Pageable pageable);

}
