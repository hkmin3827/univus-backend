package com.univus.project.controller;

import com.univus.project.dto.activityLog.ActivityLogResDto;
import com.univus.project.dto.activityLog.ActivityTop5Dto;
import com.univus.project.dto.activityLog.BoardUserContributionDto;
import com.univus.project.dto.activityLog.UserContributionDetailDto;
import com.univus.project.entity.ActivityLog;
import com.univus.project.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    // 2) 특정 사용자 활동 로그 조회
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

    // 3) 특정 보드의 팀원별 기여도 리스트 (인사이트 메인 PieChart / TOP5용)
    @GetMapping("/board/{boardId}/contribution")
    public ResponseEntity<List<BoardUserContributionDto>> getBoardContribution(
            @PathVariable Long boardId
    ) {
        try {
            List<BoardUserContributionDto> list = activityLogService.getBoardUserContributions(boardId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error("보드별 팀원 기여도 조회 실패(boardId:{}): {}", boardId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 4) 특정 팀원의 상세 기여도 (팀원 클릭 시 상세 그래프용)
    @GetMapping("/user/{userId}/board/{boardId}/detail")
    public ResponseEntity<UserContributionDetailDto> getUserContributionDetail(
            @PathVariable Long userId,
            @PathVariable Long boardId
    ) {
        try {
            UserContributionDetailDto dto = activityLogService.getUserContributionDetail(userId, boardId);
            if (dto == null) throw new RuntimeException("기여도 정보가 없습니다.");
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("사용자 기여도 상세 조회 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 5) 게시글 TOP5
    @GetMapping("/board/{boardId}/top5/posts")
    public ResponseEntity<List<ActivityTop5Dto>> getPostTop5(
            @PathVariable Long boardId
    ) {
        List<ActivityTop5Dto> list = activityLogService.getPostTop5(boardId);
        return ResponseEntity.ok(list);
    }

    // 6) 댓글 TOP5
    @GetMapping("/board/{boardId}/top5/comments")
    public ResponseEntity<List<ActivityTop5Dto>> getCommentTop5(
            @PathVariable Long boardId
    ) {
        List<ActivityTop5Dto> list = activityLogService.getCommentTop5(boardId);
        return ResponseEntity.ok(list);
    }

    // 7) 리액션 TOP5
    @GetMapping("/board/{boardId}/top5/reactions")
    public ResponseEntity<List<ActivityTop5Dto>> getReactionTop5(
            @PathVariable Long boardId
    ) {
        List<ActivityTop5Dto> list = activityLogService.getReactionTop5(boardId);
        return ResponseEntity.ok(list);
    }



}

