package com.univus.project.repository;


import com.univus.project.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    // 게시글 내 공감수 조회

    // 공감수 집계


}
