package com.univus.project.repository;

import com.univus.project.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUserIdAndDateTimeBetweenOrderByDateTimeAsc(
            Long userId, LocalDateTime start, LocalDateTime end);
    List<Schedule> findByUserIdOrderByDateTimeAsc(Long userId);
    Optional<Schedule> findByIdAndUserId(Long id, Long userId);
}
