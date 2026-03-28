package com.univus.project.controller;
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

    @GetMapping("/all/{userId}/{teamId}")
    public Page<Notification> getAll(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getAll(userId, teamId, page, size);
    }

    @GetMapping("/unchecked/{userId}/{teamId}")
    public Page<Notification> getUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getUnchecked(userId, teamId, page, size);
    }

    @GetMapping("/comment/{userId}/{teamId}")
    public Page<Notification> getComment(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getComment(userId, teamId, page, size);
    }

    @GetMapping("/comment/unchecked/{userId}/{teamId}")
    public Page<Notification> getCommentUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getCommentUnchecked(userId, teamId, page, size);
    }

    @GetMapping("/todo/{userId}/{teamId}")
    public Page<Notification> getTodo(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getTodo(userId, teamId, page, size);
    }

    @GetMapping("/todo/unchecked/{userId}/{teamId}")
    public Page<Notification> getTodoUnchecked(
            @PathVariable Long userId,
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return notificationService.getTodoUnchecked(userId, teamId, page, size);
    }

    @PostMapping("/check/{id}")
    public void markChecked(@PathVariable Long id) {
        notificationService.markChecked(id);
    }
}
