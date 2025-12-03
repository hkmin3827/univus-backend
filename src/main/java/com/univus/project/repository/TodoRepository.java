package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // board기준 작성자 조회
    List<Todo> findByBoard(Board board);
    List<Todo> findByBoard_Id(Long boardId);
    List<Todo> findByBoardOrderByCreateTimeDesc(Board board);
    List<Todo> findByBoard_Team_IdAndBoard_Id(Long teamId, Long boardId);

    // 완료 여부 기준 조회
    List<Todo> findByDoneAndUser(boolean done, User user);
    // 최신 할일 순 조회
    List<Todo> findByUserOrderByCreateTimeDesc(User user);
    // 할일 완료, 미완료 집계
    int countByUserAndBoardAndDone(User user, Board board, boolean done);
    // 팀 단위, 완료된 Todo만 조회, 최신순
    List<Todo> findByBoard_Team_IdAndDoneOrderByCreateTimeDesc(Long teamId, boolean done);
    @Query("select t from Todo t " +
            "join fetch t.user u " +
            "join fetch t.board b " +
            "where b.id = :boardId")
    List<Todo> findAllWithUserAndBoardByBoardId(@Param("boardId") Long boardId);
}
