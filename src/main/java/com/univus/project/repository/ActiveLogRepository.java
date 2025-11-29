package com.univus.project.repository;

import com.univus.project.entity.ActivityLog;
import com.univus.project.entity.Board;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveLogRepository extends JpaRepository<ActivityLog, Long> {
    // 특정 사용자, 특정 게시판 조회
    Optional<ActivityLog> findByUserAndBoard(User user, Board board);

    // 특정 게시판 내 모든 활동 로그 조회
    List<ActivityLog> findByBoardId(Long boardId);
}
