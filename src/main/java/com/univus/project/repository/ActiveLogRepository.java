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
    Optional<ActivityLog> findByUserAndBoard(User user, Board board);

    List<ActivityLog> findByBoardId(Long boardId);
}
