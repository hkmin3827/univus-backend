package com.univus.project.repository;

import com.univus.project.entity.Attendance;
import com.univus.project.entity.Board;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // 특정 사용자 + 특정 게시판 출석 조회
    List<Attendance> findByUserAndBoard(User user, Board board);
    // 사용자의 특정 게시판 출석 여부 확인
    Optional<Attendance> findByUserAndBoardAndDate(User user, Board board, LocalDate date);
}
