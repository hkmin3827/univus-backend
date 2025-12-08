package com.univus.project.controller;
import com.univus.project.constant.NotificationType;
import com.univus.project.entity.Notification;
import com.univus.project.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /** 전체 알림 조회 (팀별, 페이징) */
    @GetMapping("/all/{userId}/{teamId}")
    public Page<Notification> getAll(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getAll(userId, teamId, page, size);
    }

    /** 미확인 알림 조회 */
    @GetMapping("/unchecked/{userId}/{teamId}")
    public Page<Notification> getUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getUnchecked(userId, teamId, page, size);
    }

    /** 댓글 알림 조회 */
    @GetMapping("/comment/{userId}/{teamId}")
    public Page<Notification> getComment(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getComment(userId, teamId, page, size);
    }

    /** 댓글 미확인 알림 조회 */
    @GetMapping("/comment/unchecked/{userId}/{teamId}")
    public Page<Notification> getCommentUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getCommentUnchecked(userId, teamId, page, size);
    }

    /** Todo 완료 알림 조회 */
    @GetMapping("/todo/{userId}/{teamId}")
    public Page<Notification> getTodo(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getTodo(userId, teamId, page, size);
    }

    /** Todo 완료 미확인 알림 조회 */
    @GetMapping("/todo/unchecked/{userId}/{teamId}")
    public Page<Notification> getTodoUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getTodoUnchecked(userId, teamId, page, size);
    }

    /** 알림 확인 처리 */
    @PostMapping("/check/{id}")
    public void markChecked(@PathVariable Long id) {
        notificationService.markChecked(id);
    }
}
