package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByBoard(Board board);
    List<Todo> findByBoard_Team_IdAndBoard_Id(Long teamId, Long boardId);

    List<Todo> findByDoneAndUser(boolean done, User user);
    List<Todo> findByUserOrderByCreateTimeDesc(User user);
    int countByUserAndBoardAndDone(User user, Board board, boolean done);
    List<Todo> findByBoard_Team_IdAndDoneOrderByCreateTimeDesc(Long teamId, boolean done);
    @Query("select t from Todo t " +
            "join fetch t.user u " +
            "join fetch t.board b " +
            "where b.id = :boardId")
    List<Todo> findAllWithUserAndBoardByBoardId(@Param("boardId") Long boardId);

    List<Todo> findByBoardAndUserOrderByCreateTimeDesc(Board board, User user);
}
