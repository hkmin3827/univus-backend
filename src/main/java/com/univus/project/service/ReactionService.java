package com.univus.project.service;

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
    public boolean toggleReaction(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다!"));
        return reactionRepository.findByUserAndPost(user, post)
                .map(reaction -> {
                    // 이미 공감한 경우 -> 공감 취소
                    reactionRepository.delete(reaction);
                    return false;
                })
                .orElseGet(() -> {
                    // 공감하지 않은 경우 -> 공감 추가
                    Reaction newReaction = new Reaction();
                    newReaction.setPost(post);
                    newReaction.setUser(user);
                    reactionRepository.save(newReaction);
                    return true;
                });
    }

    // 2) 게시글 공감 리스트
    @Transactional(readOnly = true)
    public List<ReactionResDto> getReactions(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다!"));

        List<Reaction> reactions = reactionRepository.findByPost(post);

        return reactions.stream()
                .map(r -> new ReactionResDto(r, r.getUser().getId().equals(user.getId())))
                .collect(Collectors.toList());
    }

    // 3) 게시글 공감 수
    public long getReactionCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다!"));
        return reactionRepository.countByPost(post);
    }


}
