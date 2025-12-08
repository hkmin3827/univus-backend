package com.univus.project.service;

import com.univus.project.constant.NotificationType;
import com.univus.project.entity.Notification;
import com.univus.project.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /** 팀별 전체 알림 조회 */
    public Page<Notification> getAll(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdOrderByCreatedAtDesc(
                userId, teamId, PageRequest.of(page, size));
    }

    /** 팀별 미확인 알림 조회 */
    public Page<Notification> getUnchecked(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdAndCheckedFalseOrderByCreatedAtDesc(
                userId, teamId, PageRequest.of(page, size));
    }

    /** 팀별 댓글 알림 조회 */
    public Page<Notification> getComment(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdAndTypeOrderByCreatedAtDesc(
                userId, teamId, NotificationType.COMMENT, PageRequest.of(page, size));
    }

    /** 팀별 댓글 미확인 알림 조회 */
    public Page<Notification> getCommentUnchecked(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdAndTypeAndCheckedFalseOrderByCreatedAtDesc(
                userId, teamId, NotificationType.COMMENT, PageRequest.of(page, size));
    }

    /** 팀별 완료과제 알림 조회 */
    public Page<Notification> getTodo(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdAndTypeOrderByCreatedAtDesc(
                userId, teamId, NotificationType.TODO_DONE, PageRequest.of(page, size));
    }

    /** 팀별 완료과제 미확인 알림 조회 */
    public Page<Notification> getTodoUnchecked(Long userId, Long teamId, int page, int size) {
        return notificationRepository.findByUserIdAndTeamIdAndTypeAndCheckedFalseOrderByCreatedAtDesc(
                userId, teamId, NotificationType.TODO_DONE, PageRequest.of(page, size));
    }

    public void markChecked(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알림이 존재하지 않습니다."));

        n.setChecked(true);
        notificationRepository.save(n);
    }

    public Notification create(Notification n) {
        n.setCreatedAt(LocalDateTime.now());
        n.setChecked(false);
        return notificationRepository.save(n);
    }
}