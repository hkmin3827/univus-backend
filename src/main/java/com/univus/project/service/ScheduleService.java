package com.univus.project.service;

import com.univus.project.dto.schedule.*;
import com.univus.project.entity.Schedule;
import com.univus.project.entity.User;
import com.univus.project.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public Long createSchedule(ScheduleReqDto dto, User user) {
        Schedule s = new Schedule();
        s.setTitle(dto.getTitle());
        s.setDescription(dto.getDescription());
        s.setDateTime(dto.getDateTime());
        s.setUser(user);

        return scheduleRepository.save(s).getId();
    }

    public Long updateSchedule(Long scheduleId, ScheduleReqDto dto, User user) {
        Schedule s = scheduleRepository.findByIdAndUserId(scheduleId, user.getId())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        if (dto.getTitle() != null) s.setTitle(dto.getTitle());
        if (dto.getDescription() != null) s.setDescription(dto.getDescription());
        if (dto.getDateTime() != null) s.setDateTime(dto.getDateTime());

        return s.getId();
    }

    public void deleteSchedule(Long scheduleId, User user) {
        Schedule s = scheduleRepository.findByIdAndUserId(scheduleId, user.getId())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        scheduleRepository.delete(s);
    }

    // 기간으로 일정 조회 (필요 시 일반 목록용)
    public List<ScheduleResDto> getSchedulesBetween(User user, LocalDateTime start, LocalDateTime end) {
        return scheduleRepository
                .findByUserIdAndDateTimeBetweenOrderByDateTimeAsc(user.getId(), start, end)
                .stream()
                .map(ScheduleResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // /home 에서 사용할 오늘 + 7일 이내 일정
    public HomeScheduleResDto getHomeSchedules(User user) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime sevenDaysLater = now.plusDays(7);

        List<ScheduleResDto> all = getSchedulesBetween(user, now, sevenDaysLater);

        List<ScheduleResDto> todayList = all.stream()
                .filter(s -> s.getDateTime().toLocalDate().isEqual(now.toLocalDate()))
                .collect(Collectors.toList());

        List<ScheduleResDto> upcomingList = all.stream()
                .filter(s -> s.getDateTime().isAfter(now))
                .collect(Collectors.toList());

        return new HomeScheduleResDto(todayList, upcomingList);
    }

    public List<ScheduleResDto> getAllSchedules(Long userId) {
        return scheduleRepository.findByUserIdOrderByDateTimeAsc(userId)
                .stream()
                .map(ScheduleResDto::fromEntity)
                .collect(Collectors.toList());
    }

}
