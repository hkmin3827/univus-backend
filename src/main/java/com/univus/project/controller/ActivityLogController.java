package com.univus.project.controller;

import com.univus.project.dto.activityLog.ActivityLogResDto;
import com.univus.project.entity.ActivityLog;
import com.univus.project.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "[http://localhost:3000](http://localhost:3000)")
@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // 1) 특정 사용자의 게시판 활동 로그 계산 및 조회
    @PostMapping("/recalc/{userId}/{boardId}")
    public ResponseEntity<ActivityLogResDto> recalcActivityLog(
            @PathVariable Long userId,
            @PathVariable Long boardId) {
        try {
            ActivityLog log = activityLogService.recalcActivityLog(userId, boardId);
            if (log == null) throw new RuntimeException("활동 로그 재계산 실패");
            return ResponseEntity.ok(new ActivityLogResDto(log));
        } catch (Exception e) {
            log.error("활동 로그 재계산 오류(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 2) 특정 게시판 전체 사용자 활동 로그 조회
//    @GetMapping("/board/{boardId}")
//    public ResponseEntity<List<ActivityLogResDto>> getAllLogsByBoard(@PathVariable Long boardId) {
//        try {
//            List<ActivityLogResDto> logs = activityLogService.getAllLogsByBoard(boardId)
//                    .stream()
//                    .map(ActivityLogResDto::new)
//                    .toList();
//            return ResponseEntity.ok(logs);
//        } catch (Exception e) {
//            log.error("게시판 전체 활동 로그 조회 실패(boardId:{}): {}", boardId, e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }

    // 3) 특정 사용자 활동 로그 조회
    @GetMapping("/user/{userId}/board/{boardId}")
    public ResponseEntity<ActivityLogResDto> getUserLog(
            @PathVariable Long userId,
            @PathVariable Long boardId) {
        try {
            ActivityLog log = activityLogService.getUserLog(userId, boardId);
            if (log == null) throw new RuntimeException("활동 로그가 없습니다.");
            return ResponseEntity.ok(new ActivityLogResDto(log));
        } catch (Exception e) {
            log.error("사용자 활동 로그 조회 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}

