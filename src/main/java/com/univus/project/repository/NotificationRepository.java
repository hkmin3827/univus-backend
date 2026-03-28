package com.univus.project.repository;

import com.univus.project.constant.NotificationType;
import com.univus.project.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdAndTeamIdOrderByCreatedAtDesc(
            Long userId,
            Long teamId,
            Pageable pageable
    );

    Page<Notification> findByUserIdAndTeamIdAndCheckedFalseOrderByCreatedAtDesc(
            Long userId,
            Long teamId,
            Pageable pageable
    );

    Page<Notification> findByUserIdAndTeamIdAndTypeOrderByCreatedAtDesc(
            Long userId,
            Long teamId,
            NotificationType type,
            Pageable pageable
    );

    Page<Notification> findByUserIdAndTeamIdAndTypeAndCheckedFalseOrderByCreatedAtDesc(
            Long userId,
            Long teamId,
            NotificationType type,
            Pageable pageable
    );
}