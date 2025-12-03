package com.univus.project.service;

import com.univus.project.constant.ReactionType;
import com.univus.project.dto.reaction.ReactionResDto;
import com.univus.project.entity.Post;
import com.univus.project.entity.Reaction;
import com.univus.project.entity.User;
import com.univus.project.repository.PostRepository;
import com.univus.project.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j @RequiredArgsConstructor
@Service

public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    // 1) 공감 토글 생성
    @Transactional
    public ReactionType toggleReaction(Long postId, User user, ReactionType type) {
        if (type == null) {
            throw new IllegalArgumentException("type은 null일 수 없습니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다! postId=" + postId));

        // 1) 기존 리액션이 있는지 확인
        return reactionRepository.findByUserAndPost(user, post)
                .map(reaction -> {
                    // 이미 같은 타입이면 → 아무 변화 없음 (중복 방지)
                    if (reaction.getType() == type) {
                        log.info("이미 동일한 리액션이 존재합니다. userId={}, postId={}, type={}",
                                user.getId(), postId, type);
                        return reaction.getType();
                    }
                    // 다른 타입이면 → 타입만 변경
                    log.info("리액션 타입 변경: userId={}, postId={}, {} -> {}",
                            user.getId(), postId, reaction.getType(), type);
                    reaction.setType(type); // JPA 더티 체킹으로 자동 업데이트
                    return reaction.getType();
                })
                .orElseGet(() -> {
                    // 2) 기존 리액션이 없으면 새로 생성
                    Reaction newReaction = new Reaction();
                    newReaction.setPost(post);
                    newReaction.setBoard(post.getBoard());
                    newReaction.setUser(user);
                    newReaction.setType(type);

                    reactionRepository.save(newReaction);
                    log.info("새 리액션 생성: userId={}, postId={}, type={}",
                            user.getId(), postId, type);
                    return newReaction.getType();
                });
    }

    // 2) 게시글 공감 리스트
    @Transactional(readOnly = true)
    public List<ReactionResDto> getReactions(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다!"));

        List<Reaction> reactions = reactionRepository.findByPost(post);

        return reactions.stream()
                .map(r -> new ReactionResDto(r, user.getId()))
                .collect(Collectors.toList());
    }

    // 3) 게시글 공감 수
    public long getReactionCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다!"));
        return reactionRepository.countByPost(post);
    }

    // 4) 타입별 매서드

    public long getReactionCountByType(Long postId, ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다!"));
        return reactionRepository.countByPostAndType(post, type);
    }

}
