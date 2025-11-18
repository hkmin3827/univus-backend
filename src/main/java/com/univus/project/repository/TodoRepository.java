package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 이메일 조회
    Optional<Todo> findByEmail(String email);
    // 작성자 조회
    List<Todo> findByName(String name);
    // 완료 여부 기준 조회
    List<Todo> findByDone(boolean done);
    // 최신 할일 순 조회
    List<Todo> findAllByOrderByCreateTimeDesc();
    // 할일 완료, 미완료 집계
    int countByUserAndBoardAndDone(User user, Board board, boolean done);
}
