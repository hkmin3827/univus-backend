package com.univus.project.repository;

import com.univus.project.dto.activityLog.ActivityTop5Dto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);
    List<Post> findByUserEmail(String email);
    List<Post> findByUserName(String name);
    List<Post> findByTitleContaining(String title);

    Page<Post> findAll(Pageable pageable);

    List<Post> findByBoardOrderByCreateTimeDesc(Board board);

    int countByUserAndBoard(User user, Board board);

    Page<Post> findByBoardId(Long boardId, Pageable pageable);
    Page<Post> findByBoardIdAndTitleContaining(Long boardId, String keyword, Pageable pageable);

    @Query("""
    SELECT new com.univus.project.dto.activityLog.ActivityTop5Dto(
        p.user.id,
        p.user.name,
        p.user.image,
        COUNT(p)
    )
    FROM Post p
    WHERE p.board.id = :boardId
    GROUP BY p.user.id, p.user.name, p.user.image
    ORDER BY COUNT(p) DESC
""")
    List<ActivityTop5Dto> findPostTop5ByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.team.id = :teamId AND p.user.id = :userId ORDER BY p.createTime DESC")
    List<Post> findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

}
