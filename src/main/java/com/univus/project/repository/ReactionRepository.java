package com.univus.project.repository;

import com.univus.project.constant.ReactionType;
import com.univus.project.dto.activityLog.ActivityTop5Dto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import com.univus.project.entity.Reaction;
import com.univus.project.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    long countByPost(Post post);
    Optional<Reaction> findByUserAndPost(User user, Post post);

    List<Reaction> findByPost(Post post);
    int countByUserAndBoard(User user, Board board);

    long countByPostAndType(Post post, ReactionType type);

    @Query("""
    SELECT new com.univus.project.dto.activityLog.ActivityTop5Dto(
        r.user, COUNT(r)
    )
    FROM Reaction r
    WHERE r.post.board.id = :boardId
    GROUP BY r.user
    ORDER BY COUNT(r) DESC
    """)
    List<ActivityTop5Dto> findReactionTop5ByBoardId(@Param("boardId") Long boardId, Pageable pageable);
}
