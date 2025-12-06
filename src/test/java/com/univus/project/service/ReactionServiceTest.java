package com.univus.project.service;

import com.univus.project.constant.ReactionType;
import com.univus.project.dto.reaction.ReactionResDto;
import com.univus.project.entity.Post;
import com.univus.project.entity.Reaction;
import com.univus.project.entity.User;
import com.univus.project.repository.PostRepository;
import com.univus.project.repository.ReactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ReactionService reactionService;

    private User user;
    private Post post;
    private Reaction reaction;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        post = new Post();
        post.setId(10L);

        reaction = new Reaction();
        reaction.setId(100L);
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setType(ReactionType.POSITIVE);
    }

    // ===== 새 리액션 생성 =====
    @Test
    void toggleReaction_createNewReaction_success() {
        Long postId = 10L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.empty());
        when(reactionRepository.save(any())).thenAnswer(invocation -> {
            Reaction saved = invocation.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        ReactionType result = reactionService.toggleReaction(postId, user, ReactionType.POSITIVE);

        assertEquals(ReactionType.POSITIVE, result);
        verify(reactionRepository, times(1)).save(any());
    }

    // ===== 기존 리액션이 같은 타입 =====
    @Test
    void toggleReaction_sameType_noChange() {
        Long postId = 10L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));

        ReactionType result = reactionService.toggleReaction(postId, user, ReactionType.POSITIVE);

        assertEquals(ReactionType.POSITIVE, result);
        verify(reactionRepository, never()).save(any());
    }

    // ===== 기존 리액션과 다른 타입 =====
    @Test
    void toggleReaction_changeType_success() {
        Long postId = 10L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findByUserAndPost(user, post)).thenReturn(Optional.of(reaction));

        ReactionType result = reactionService.toggleReaction(postId, user, ReactionType.NEGATIVE);

        assertEquals(ReactionType.NEGATIVE, result);
        assertEquals(ReactionType.NEGATIVE, reaction.getType());
        verify(reactionRepository, never()).save(any());
    }

    // ===== 게시글 없음 예외 =====
    @Test
    void toggleReaction_postNotFound_throwException() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> reactionService.toggleReaction(999L, user, ReactionType.POSITIVE));
    }

    // ===== 공감 리스트 조회 =====
    @Test
    void getReactions_success() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(reactionRepository.findByPost(post)).thenReturn(Collections.singletonList(reaction));

        List<ReactionResDto> result = reactionService.getReactions(10L, user);

        assertEquals(1, result.size());
        assertEquals(ReactionType.POSITIVE, result.get(0).getType());
    }

    // ===== 공감 수 조회 =====
    @Test
    void getReactionCount_success() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(reactionRepository.countByPost(post)).thenReturn(4L);

        long count = reactionService.getReactionCount(10L);

        assertEquals(4L, count);
    }

    // ===== 타입별 공감 수 조회 =====
    @Test
    void getReactionCountByType_success() {
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(reactionRepository.countByPostAndType(post, ReactionType.NEUTRAL)).thenReturn(2L);

        long count = reactionService.getReactionCountByType(10L, ReactionType.NEUTRAL);

        assertEquals(2L, count);
    }
}
