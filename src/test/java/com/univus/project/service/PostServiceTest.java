package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.dto.post.PostReqDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.BoardRepository;
import com.univus.project.repository.PostRepository;
import com.univus.project.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks PostService postService;

    @Mock PostRepository postRepository;
    @Mock BoardRepository boardRepository;
    @Mock TeamMemberRepository teamMemberRepository;
    @Mock ActivityLogService activityLogService;

    private User writer;
    private Team team;
    private Board board;
    private Post post;

    @BeforeEach
    void setup() {
        writer = new User(); writer.setId(1L);
        team = Team.builder().id(100L).teamName("팀").leader(writer).build();
        board = new Board();
        board.setId(10L);
        board.setTeam(team);
        board.setCreator(writer);

        post = new Post();
        post.setId(999L);
        post.setTitle("테스트제목");
        post.setContent("내용");
        post.setUser(writer);
        post.setBoard(board);
    }
    @Test
    void 게시글_생성_성공() {
        PostReqDto dto = new PostReqDto();
        dto.setTitle("새 게시글");
        dto.setContent("내용");

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
                    Post p = invocation.getArgument(0);
                    p.setId(999L);  // DB가 id 생성하는 것처럼 시뮬레이션
                    return p;
                });

        when(activityLogService.recalcActivityLog(anyLong(), anyLong())).thenReturn(null);

        Long id = postService.createPost(100L, 10L, dto, writer);

        assertEquals(999L, id);
        verify(postRepository).save(any(Post.class));
    }
    @Test
    void 게시글_생성_실패_제목없음() {
        PostReqDto dto = new PostReqDto();
        dto.setTitle("");

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(100L, 10L, dto, writer));

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, ex.getErrorCode());
    }
    @Test
    void 게시글_생성_실패_보드없음() {
        PostReqDto dto = new PostReqDto();
        dto.setTitle("제목");

        when(boardRepository.findById(10L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(100L, 10L, dto, writer));

        assertEquals(ErrorCode.BOARD_NOT_FOUND, ex.getErrorCode());
    }
    @Test
    void 게시글_생성_실패_팀불일치() {
        PostReqDto dto = new PostReqDto();
        dto.setTitle("제목");

        Team wrongTeam = new Team();
        wrongTeam.setId(999L);
        board.setTeam(wrongTeam);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(100L, 10L, dto, writer));

        assertEquals(ErrorCode.INVALID_RELATION, ex.getErrorCode());
    }
    @Test
    void 게시글_생성_실패_팀멤버아님() {
        PostReqDto dto = new PostReqDto();
        dto.setTitle("제목");

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(100L, 10L, dto, writer));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }

    @Test
    void 게시글_조회_성공() {
        when(postRepository.findById(999L)).thenReturn(Optional.of(post));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(true);

        var result = postService.getPostDetail(100L, 10L, 999L, 1L);
        assertEquals("테스트제목", result.getTitle());
    }

    @Test
    void 게시글_조회_실패_존재하지않음() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.getPostDetail(100L, 10L, 999L, 1L));

        assertEquals(ErrorCode.POST_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 게시글_조회_실패_권한없음() {
        when(postRepository.findById(999L)).thenReturn(Optional.of(post));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.getPostDetail(100L, 10L, 999L, 1L));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }
    @Test
    void 게시글_조회_실패_보드팀불일치() {
        Team wrongTeam = new Team();
        wrongTeam.setId(999L);
        board.setTeam(wrongTeam);

        when(postRepository.findById(999L)).thenReturn(Optional.of(post));

        CustomException ex = assertThrows(CustomException.class,
                () -> postService.getPostDetail(100L, 10L, 999L, 1L));

        assertEquals(ErrorCode.INVALID_RELATION, ex.getErrorCode());
    }
}
