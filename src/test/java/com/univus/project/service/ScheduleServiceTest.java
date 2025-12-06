package com.univus.project.service;

import com.univus.project.dto.schedule.HomeScheduleResDto;
import com.univus.project.dto.schedule.ScheduleReqDto;
import com.univus.project.dto.schedule.ScheduleResDto;
import com.univus.project.entity.Schedule;
import com.univus.project.entity.User;
import com.univus.project.repository.ScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private User user;
    private Schedule schedule;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        schedule = new Schedule();
        schedule.setId(100L);
        schedule.setUser(user);
        schedule.setTitle("스터디");
        schedule.setDescription("React 공부");
        schedule.setDateTime(LocalDateTime.of(2025, 1, 1, 10, 0));
    }

    // ====================== 일정 생성 ======================
    @Test
    void createSchedule_success() {
        ScheduleReqDto dto = new ScheduleReqDto();
        dto.setTitle("회의");
        dto.setDescription("팀 미팅");
        dto.setDateTime(LocalDateTime.now());

        when(scheduleRepository.save(any(Schedule.class)))
                .thenAnswer(invocation -> {
                    Schedule s = invocation.getArgument(0);
                    s.setId(999L);
                    return s;
                });

        Long resultId = scheduleService.createSchedule(dto, user);

        assertEquals(999L, resultId);
        verify(scheduleRepository, times(1)).save(any());
    }

    // ====================== 일정 수정 ======================
    @Test
    void updateSchedule_success() {
        ScheduleReqDto dto = new ScheduleReqDto();
        dto.setTitle("수정된 일정");
        dto.setDescription("설명 수정");

        when(scheduleRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(schedule));

        Long resultId = scheduleService.updateSchedule(100L, dto, user);

        assertEquals(100L, resultId);
        assertEquals("수정된 일정", schedule.getTitle());
        assertEquals("설명 수정", schedule.getDescription());
    }

    @Test
    void updateSchedule_notFound_throws() {
        when(scheduleRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> scheduleService.updateSchedule(100L, new ScheduleReqDto(), user));
    }

    // ====================== 일정 삭제 ======================
    @Test
    void deleteSchedule_success() {
        when(scheduleRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(schedule));

        scheduleService.deleteSchedule(100L, user);

        verify(scheduleRepository, times(1)).delete(schedule);
    }

    // ====================== 기간 일정 조회 ======================
    @Test
    void getSchedulesBetween_success() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(scheduleRepository.findByUserIdAndDateTimeBetweenOrderByDateTimeAsc(1L, start, end))
                .thenReturn(List.of(schedule));

        var result = scheduleService.getSchedulesBetween(user, start, end);

        assertEquals(1, result.size());
        assertEquals("스터디", result.get(0).getTitle());
    }

    // ====================== 홈 일정 조회 ======================
    @Test
    void getHomeSchedules_success() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        Schedule today = new Schedule();
        today.setDateTime(now);
        today.setTitle("오늘 일정");

        Schedule future = new Schedule();
        future.setDateTime(now.plusDays(3));
        future.setTitle("다가오는 일정");

        when(scheduleRepository.findByUserIdAndDateTimeBetweenOrderByDateTimeAsc(
                eq(1L), any(), any())
        ).thenReturn(List.of(today, future));

        HomeScheduleResDto result = scheduleService.getHomeSchedules(user);

        // Today
        assertEquals(1, result.getTodaySchedules().size());
        assertEquals("오늘 일정", result.getTodaySchedules().get(0).getTitle());

        // Upcoming
        assertEquals(1, result.getUpcomingSchedules().size());
        assertEquals("다가오는 일정", result.getUpcomingSchedules().get(0).getTitle());
    }

    // ====================== 전체 일정 조회 ======================
    @Test
    void getAllSchedules_success() {
        when(scheduleRepository.findByUserIdOrderByDateTimeAsc(1L))
                .thenReturn(List.of(schedule));

        var result = scheduleService.getAllSchedules(1L);

        assertEquals(1, result.size());
        assertEquals("스터디", result.get(0).getTitle());
    }
}
