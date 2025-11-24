package com.univus.project.repository;

import com.univus.project.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 사용자 + 기간 조회
    List<Schedule> findByUserIdAndDateBetweenOrderByDateTimeAsc(
            Long userId, LocalDateTime start, LocalDateTime end
    );

    // 수정/삭제 시 본인 확인용
    Optional<Schedule> findByIdAndUserId(Long id, Long userId);
}
