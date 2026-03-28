package com.univus.project.repository;

import com.univus.project.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByTeam_IdOrderByCreateTimeDesc(Long teamId, Pageable pageable);

    Page<Notice> findAllByOrderByCreateTimeDesc(Pageable pageable);

    @Query("SELECT DISTINCT n FROM Notice n " +
            "WHERE n.team.id = :teamId " +
            "AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) " +
            "ORDER BY n.createTime DESC")
    List<Notice> searchNotices(@Param("teamId") Long teamId, @Param("keyword") String keyword);
}