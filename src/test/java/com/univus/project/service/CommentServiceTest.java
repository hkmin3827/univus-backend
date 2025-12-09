package com.univus.project.service;

import com.univus.project.constant.NotificationType;
import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.*;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private CommentService commentService;

    private User writer;
    private User postOwner;
    private Post post;
    private Board board;
    private Comment createMockComment(Long id, String content) {
        Comment c = new Comment();
        c.setId(id);
        c.setContent(content);
        c.setCreateTime(LocalDateTime.now());
        c.setWriter(writer); // ⭐ writer 설정 중요

        // post 설정
        Board b = new Board();
        b.setId(100L);
        Team t = new Team();
        t.setId(50L);
        b.setTeam(t);

        Post p = new Post();
        p.setId(10L);
        p.setBoard(b);
        p.setUser(postOwner);

        c.setPost(p);
        c.setBoard(b);

        return c;
    }
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        writer = new User();
        writer.setId(1L);
        writer.setName("작성자");

        postOwner = new User();
        postOwner.setId(2L);
        postOwner.setName("게시글 주인");

        board = new Board();
        board.setId(100L);

        post = new Post();
        post.setId(10L);
        post.setTitle("테스트 게시글");
        post.setUser(postOwner);
        post.setBoard(board);
    }

    // -------------------------------------------------------
    // 1) 댓글 작성 테스트
    // -------------------------------------------------------
    @Test
    void createComment_success() {
        CommentReqDto dto = new CommentReqDto();
        dto.setPostId(10L);
        dto.setContent("댓글 내용");

        Team team = new Team();
        team.setId(50L);

        board.setTeam(team);
        post.setBoard(board);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any())).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });

        Long result = commentService.createComment(dto, writer);

        assertThat(result).isEqualTo(99L);
        verify(activityLogService).recalcActivityLog(1L, 100L);
        verify(notificationService).create(any(Notification.class));
    }

    @Test
    void createComment_postNotFound() {
        CommentReqDto dto = new CommentReqDto();
        dto.setPostId(999L);

        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.createComment(dto, writer))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("게시글이 존재하지 않습니다.");
    }

    // -------------------------------------------------------
    // 2) 댓글 목록 조회
    // -------------------------------------------------------
    @Test
    void getComments_success() {
        Comment c1 = createMockComment(1L, "댓글 1");

        Page<Comment> mockPage = new PageImpl<>(List.of(c1));

        when(commentRepository.findByPostId(eq(10L), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<CommentResDto> result = commentService.getComments(10L, 0, 20, null);

        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void getComments_withKeyword() {
        Comment c1 = createMockComment(1L, "키워드 댓글");

        Page<Comment> mockPage = new PageImpl<>(List.of(c1));

        when(commentRepository.findByPostIdAndContentContaining(eq(10L), eq("키워드"), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<CommentResDto> result = commentService.getComments(10L, 0, 20, "키워드");

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getContent()).contains("키워드");
    }

    // -------------------------------------------------------
    // 3) 댓글 수정
    // -------------------------------------------------------
    @Test
    void updateComment_success() {
        Comment comment = new Comment();
        comment.setId(77L);
        comment.setWriter(writer);
        comment.setContent("old");

        when(commentRepository.findById(77L)).thenReturn(Optional.of(comment));

        CommentReqDto dto = new CommentReqDto();
        dto.setContent("new content");

        Long id = commentService.updateComment(77L, dto, writer);

        assertThat(id).isEqualTo(77L);
        assertThat(comment.getContent()).isEqualTo("new content");
    }

    @Test
    void updateComment_unauthorized() {
        Comment comment = new Comment();
        comment.setId(77L);
        comment.setWriter(postOwner);

        when(commentRepository.findById(77L)).thenReturn(Optional.of(comment));

        CommentReqDto dto = new CommentReqDto();
        dto.setContent("수정");

        assertThatThrownBy(() -> commentService.updateComment(77L, dto, writer))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("수정 권한이 없습니다.");
    }

    // -------------------------------------------------------
    // 4) 댓글 삭제
    // -------------------------------------------------------
    @Test
    void deleteComment_success() {
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setWriter(writer);
        comment.setPost(post);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(5L, writer);

        verify(commentRepository).delete(comment);
        verify(activityLogService).recalcActivityLog(1L, 100L);
    }

    @Test
    void deleteComment_unauthorized() {
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setWriter(postOwner);

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(5L, writer))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("작성자만 삭제할 수 있습니다.");
    }

    // -------------------------------------------------------
    // 5) 전체 댓글 검색
    // -------------------------------------------------------
    @Test
    void searchAllComments_success() {
        Comment c = createMockComment(1L, "검색됨");
        Page<Comment> mockPage = new PageImpl<>(List.of(c));

        when(commentRepository.findByContentContaining(eq("검색"), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<CommentResDto> result = commentService.searchAllComments("검색", 0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getContent()).contains("검색");
    }

    // -------------------------------------------------------
    // 6) 내 댓글 목록 조회
    // -------------------------------------------------------
    @Test
    void getMyComments_success() {
        Comment c = createMockComment(1L, "내 댓글");

        when(commentRepository.findByTeamAndUser(1L, 1L))
                .thenReturn(List.of(c));

        List<CommentResDto> list = commentService.getMyComments(1L, 1L);

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getContent()).isEqualTo("내 댓글");
    }
}
