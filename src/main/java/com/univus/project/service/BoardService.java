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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final TeamRepository teamRepository;   // 팀 리포지토리 추가

    // 게시판 생성
    @Transactional
    public Long createBoard(BoardReqDto dto, User user) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (boardRepository.existsByTeamIdAndName(dto.getTeamId(), dto.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_BOARD_NAME);
        }
        // teamId 로 팀 조회
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        Board board = new Board();
        board.setName(dto.getName());
        board.setDescription(dto.getDescription());
        board.setCreator(user);
        board.setTeam(team);      // team 세팅 (중요)

        boardRepository.save(board);
        return board.getId();
    }

    @Transactional(readOnly = true)
    public List<BoardResDto> getAllBoards() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResDto getBoard(Long teamId, Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getTeam().getId().equals(teamId)) {
            throw new CustomException(ErrorCode.INVALID_RELATION);
        }
        return new BoardResDto(board);
    }

    @Transactional(readOnly = true)
    public List<BoardResDto> getBoardsByTeam(Long teamId) {
        return boardRepository.findByTeamId(teamId).stream()
                .map(BoardResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long modifyBoard(Long id, BoardReqDto dto, User loginUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getCreator().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        if (dto.getName() != null) {
            board.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            board.setDescription(dto.getDescription());
        }

        return board.getId();
    }

    @Transactional
    public void deleteBoard(Long boardId, User loginUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() ->  new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getCreator().getId().equals(loginUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        boardRepository.delete(board);
    }
}
