package com.univus.project.repository;

import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByTeamId(Long teamId);
    boolean existsByTeamIdAndName(Long teamId, String name);
}
