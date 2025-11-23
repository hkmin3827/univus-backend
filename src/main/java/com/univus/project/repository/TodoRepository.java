package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 작성자 조회
    List<Todo> findByUser_Email(String email);
    // 완료 여부 기준 조회
    List<Todo> findByDoneAndUser(boolean done, User user);
    // 최신 할일 순 조회
    List<Todo> findByUserOrderByCreateTimeDesc(User user);
    // 할일 완료, 미완료 집계
    int countByUserAndBoardAndDone(User user, Board board, boolean done);
}
