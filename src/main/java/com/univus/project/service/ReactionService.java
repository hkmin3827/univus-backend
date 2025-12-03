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
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다!"));
        return reactionRepository.findByUserAndPost(user, post)
                .map(reaction -> {
                    // 이미 공감한 경우 -> 공감 취소
                    if(reaction.getType() == type){
                        reactionRepository.delete(reaction);
                        return null;   //프론트에서는 "반응 없음" 처리
                    } else {
                        //다른 타입을 누르면 -> 타입 변경
                        reaction.setType(type);
                        return reaction.getType();
                    }
                 })
                .orElseGet(() -> {
                    // 공감하지 않은 경우 -> 공감 추가
                    Reaction newReaction = new Reaction();
                    newReaction.setPost(post);
                    newReaction.setBoard(post.getBoard());
                    newReaction.setUser(user);
                    newReaction.setType(type);
                    reactionRepository.save(newReaction);
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
