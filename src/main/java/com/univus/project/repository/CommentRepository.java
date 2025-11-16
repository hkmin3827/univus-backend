package com.univus.project.repository;


import com.univus.project.entity.Comment;
import com.univus.project.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글(post)에 속한 댓글 목록
    List<Comment> findByPost(Post post);

    List<Comment> findByPostOrderByCreateTimeAsc(Post post);
}
