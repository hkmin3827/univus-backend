package com.univus.project.service;

import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Comment;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ActivityLogService activityLogService;

    private User user;
    private Post post;
    private Board board;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);

        board = new Board();
        board.setId(10L);

        post = new Post();
        post.setId(100L);
        post.setBoard(board);
    }

    // ====================== 댓글 생성 테스트 ========================
    @Test
    void createComment_success() {
        CommentReqDto dto = new CommentReqDto();
        dto.setPostId(100L);
        dto.setContent("test comment");

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment c = invocation.getArgument(0);
                    // Mock save return id manually
                    c.setId(999L);
                    return c;
                });

        Long commentId = commentService.createComment(dto, user);

        assertNotNull(commentId);
        assertEquals(999L, commentId);

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(activityLogService, times(1)).recalcActivityLog(user.getId(), board.getId());
    }

    // ====================== 댓글 조회 테스트 ========================
    @Test
    void getComments_success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime"));
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setWriter(user);
        comment.setPost(post);

        Page<Comment> page = new PageImpl<>(Collections.singletonList(comment), pageable, 1);

        when(commentRepository.findByPostId(100L, pageable)).thenReturn(page);

        Page<CommentResDto> result = commentService.getComments(100L, 0, 10, null);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());
        verify(commentRepository, times(1)).findByPostId(100L, pageable);
    }

    // ====================== 댓글 삭제 테스트 ========================
    @Test
    void deleteComment_success() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setWriter(user);
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, user);

        verify(commentRepository, times(1)).delete(comment);
        verify(activityLogService, times(1)).recalcActivityLog(user.getId(), board.getId());
    }

    // ====================== 삭제 권한 실패 ========================
    @Test
    void deleteComment_noPermission_throws() {
        User otherUser = new User();
        otherUser.setId(123L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setWriter(otherUser);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commentService.deleteComment(1L, user));

        assertEquals("작성자만 삭제할 수 있습니다.", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }
}
