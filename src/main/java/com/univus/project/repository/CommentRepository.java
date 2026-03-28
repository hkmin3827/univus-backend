package com.univus.project.repository;

import com.univus.project.dto.activityLog.ActivityTop5Dto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Comment;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.writer = :user AND c.post.board = :board")
    int countByUserAndBoard(@Param("user") User user, @Param("board") Board board);

    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Page<Comment> findByPostIdAndContentContaining(Long postId, String keyword, Pageable pageable); // 키워드 검색용 메서드
    @Query("""
    SELECT new com.univus.project.dto.activityLog.ActivityTop5Dto(
        c.writer, COUNT(c)
    )
    FROM Comment c
    WHERE c.post.board.id = :boardId
    GROUP BY c.writer
    ORDER BY COUNT(c) DESC
""")
    List<ActivityTop5Dto> findCommentTop5ByBoardId(@Param("boardId") Long boardId, Pageable pageable);
    Page<Comment> findByContentContaining(String keyword, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.board.team.id = :teamId AND c.writer.id = :userId ORDER BY c.createTime DESC")
    List<Comment> findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);
    @Query("SELECT DISTINCT c FROM Comment c " +
            "WHERE c.post.board.team.id = :teamId " +
            "AND c.content LIKE %:keyword% " +
            "ORDER BY c.createTime DESC")
    List<Comment> searchComments(@Param("teamId") Long teamId, @Param("keyword") String keyword);
}
