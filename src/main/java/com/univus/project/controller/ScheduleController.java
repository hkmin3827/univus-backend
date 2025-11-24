package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.schedule.*;
import com.univus.project.entity.User;
import com.univus.project.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 생성
    @PostMapping
    public ResponseEntity<Long> createSchedule(
            @RequestBody ScheduleReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long id = scheduleService.createSchedule(dto, user);
        return ResponseEntity.ok(id);
    }

    // 일정 수정
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long updatedId = scheduleService.updateSchedule(id, dto, user);
        return ResponseEntity.ok(updatedId);
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        scheduleService.deleteSchedule(id, user);
        return ResponseEntity.noContent().build();
    }

    // /home 페이지용 : 오늘 + 7일 이내
    @GetMapping("/home")
    public ResponseEntity<HomeScheduleResDto> getHomeSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        HomeScheduleResDto res = scheduleService.getHomeSchedules(user);
        return ResponseEntity.ok(res);
    }
}
