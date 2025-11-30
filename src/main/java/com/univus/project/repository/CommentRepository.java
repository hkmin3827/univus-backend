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

    // 특정 게시글(post)에 속한 댓글 목록
    List<Comment> findByPost(Post post);

    List<Comment> findByPostOrderByCreateTimeAsc(Post post);

    // 특정 사용자가 특정 게시판에서 작성한 댓글 수를 계산 -> JPQL 쿼리 필요
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.writer = :user AND c.post.board = :board")
    int countByUserAndBoard(@Param("user") User user, @Param("board") Board board);
    
    // 페이지네이션
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Page<Comment> findByPostIdAndContentContaining(Long postId, String keyword, Pageable pageable); // 키워드 검색용 메서드
    Page<Comment> findByPostIdOrderByCreateTimeDesc(Long postId, Pageable pageable);
<<<<<<< HEAD

    List<ActivityTop5Dto> findCommentTop5ByBoardId(@Param("boardId") Long boardId, Pageable pageable);
=======
    Page<Comment> findByContentContaining(String keyword, Pageable pageable);   // 전체 게시글 댓글 검색
>>>>>>> c2c57b2a18df4f22a46c3200895ecab6825e8e52
}
