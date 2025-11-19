package com.univus.project.repository;


import com.univus.project.entity.Board;
import com.univus.project.entity.Comment;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글(post)에 속한 댓글 목록
    List<Comment> findByPost(Post post);

    List<Comment> findByPostOrderByCreateTimeAsc(Post post);

    // 특정 사용자가 특정 게시판에서 작성한 댓글 수를 계산 -> JPQL 쿼리 필요
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.writer = :user AND c.post.board = :board")
    int countByUserAndBoard(@Param("user") User user, @Param("board") Board board);
}
