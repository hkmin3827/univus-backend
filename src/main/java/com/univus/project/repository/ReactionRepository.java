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
    // 특정 게시글의 공감 수 확인
    long countByPost(Post post);
    //유저의 공감 여부
    Optional<Reaction> findByUserAndPost(User user, Post post);

    // 특정 게시글의 모든 공감
    List<Reaction> findByPost(Post post);
    // 추가: 특정 유저가 특정 게시판에서 받은 공감 수
    int countByPost_UserAndPost_Board(User user, Board board);

    //타입별 개수
    long countByPostAndType(Post post, ReactionType type);

    @Query("""
    SELECT new com.univus.project.dto.activityLog.ActivityTop5Dto(
        r.user.id,
        r.user.name,
        r.user.image,
        COUNT(r)
    )
    FROM Reaction r
    WHERE r.board.id = :boardId
    GROUP BY r.user.id, r.user.name, r.user.image
    ORDER BY COUNT(r) DESC
""")
    List<ActivityTop5Dto> findReactionTop5ByBoardId(
            @Param("boardId") Long boardId,
            Pageable pageable
    );



}
