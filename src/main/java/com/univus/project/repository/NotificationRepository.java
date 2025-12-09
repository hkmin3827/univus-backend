package com.univus.project.repository;

import com.univus.project.constant.NotificationType;
import com.univus.project.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdAndTeamIdOrderByCreatedAtDesc(
            Long userId,
            Long teamId,
            Pageable pageable
    );

    /** 미확인 알림 (팀별, 최신순) */
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