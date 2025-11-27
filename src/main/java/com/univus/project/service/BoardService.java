package com.univus.project.service;

import com.univus.project.dto.board.BoardReqDto;
import com.univus.project.dto.board.BoardResDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
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

        if (boardRepository.existsByTeamIdAndName(dto.getTeamId(), dto.getName())) {
            throw new RuntimeException("같은 이름의 게시판이 이미 존재합니다.");
        }
        // teamId 로 팀 조회
        Team team = teamRepository.findById(dto.getTeamId())
                .orElseThrow(() -> new RuntimeException("해당 팀을 찾을 수 없습니다."));

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
    public Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));
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
                .orElseThrow(() -> new RuntimeException("해당 게시판 id가 존재하지 않습니다."));

        if (!board.getCreator().getId().equals(loginUser.getId())) {
            throw new RuntimeException("생성자만 수정할 수 있습니다.");
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
                .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

        if (!board.getCreator().getId().equals(loginUser.getId())) {
            throw new RuntimeException("생성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }
}
