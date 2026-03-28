package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.dto.board.BoardReqDto;
import com.univus.project.dto.board.BoardResDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.BoardRepository;
import com.univus.project.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private BoardService boardService;

    private User user;
    private Team team;
    private Board board;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("홍길동");

        team = new Team();
        team.setId(100L);
        team.setTeamName("테스트 팀");

        board = new Board();
        board.setId(10L);
        board.setName("프로젝트");
        board.setDescription("테스트용 프로젝트");
        board.setCreator(user);
        board.setTeam(team);
    }

    @Test
    void 게시판_생성_성공() {
        BoardReqDto dto = new BoardReqDto();
        dto.setName("새 게시판");
        dto.setDescription("설명");
        dto.setTeamId(100L);

        when(boardRepository.existsByTeamIdAndName(dto.getTeamId(), dto.getName())).thenReturn(false);
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
                    Board b = invocation.getArgument(0);
                    b.setId(10L);
                    return b;
                });
        Long id = boardService.createBoard(dto, user);

        assertEquals(10L, id);
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    void 게시판_생성_실패_이름없음() {
        BoardReqDto dto = new BoardReqDto();
        dto.setName("");
        dto.setTeamId(100L);

        CustomException ex = assertThrows(CustomException.class,
                () -> boardService.createBoard(dto, user));

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, ex.getErrorCode());
    }

    @Test
    void 게시판_조회_성공() {
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        BoardResDto result = boardService.getBoard(100L, 10L, 1L);

        assertEquals("프로젝트", result.getName());
    }

    @Test
    void 게시판_조회_실패_없음() {
        when(boardRepository.findById(10L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> boardService.getBoard(100L, 10L, 1L));
        assertEquals(ErrorCode.BOARD_NOT_FOUND, ex.getErrorCode());

    }

    @Test
    void 팀별_게시판_조회() {
        when(boardRepository.findByTeamId(100L)).thenReturn(List.of(board));

        List<BoardResDto> result = boardService.getBoardsByTeam(100L);

        assertEquals(1, result.size());
        assertEquals("프로젝트", result.get(0).getName());
    }

    @Test
    void 게시판_수정_성공() {
        BoardReqDto dto = new BoardReqDto();
        dto.setName("수정된 제목");
        dto.setDescription("수정된 설명");

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        Long id = boardService.modifyBoard(10L, dto, user);

        assertEquals(10L, id);
        assertEquals("수정된 제목", board.getName());
        assertEquals("수정된 설명", board.getDescription());
    }

    @Test
    void 게시판_수정_실패_권한없음() {
        User anotherUser = new User();
        anotherUser.setId(999L);

        BoardReqDto dto = new BoardReqDto();
        dto.setName("수정");

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        CustomException ex = assertThrows(CustomException.class,
                () -> boardService.modifyBoard(10L, dto, anotherUser));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }

    @Test
    void 게시판_삭제_성공() {
        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        boardService.deleteBoard(10L, user);

        verify(boardRepository).delete(board);
    }

    @Test
    void 게시판_삭제_실패_권한없음() {
        User anotherUser = new User();
        anotherUser.setId(999L);

        when(boardRepository.findById(10L)).thenReturn(Optional.of(board));

        CustomException ex = assertThrows(CustomException.class,
                () -> boardService.deleteBoard(10L, anotherUser));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }
}
