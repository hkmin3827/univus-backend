package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoard(Board board);
    List<Post> findByUserEmail(String email);
    List<Post> findByUserName(String name);
    List<Post> findByTitleContaining(String title);

    Page<Post> findAll(Pageable pageable);

    List<Post> findByBoardOrderByCreateTimeDesc(Board board);
}
