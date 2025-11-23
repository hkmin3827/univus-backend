package com.univus.project.service;

import com.univus.project.dto.board.BoardReqDto;
import com.univus.project.dto.board.BoardResDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import com.univus.project.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;

    public Long createBoard(BoardReqDto dto, User user) {
            Board board = new Board();
            board.setName(dto.getName());
            board.setDescription(dto.getDescription());
            board.setCreator(user);
            boardRepository.save(board);
            return board.getId();
    }

    public List<BoardResDto> getAllBoards() {
        return boardRepository.findAll()
                .stream()
                .map(BoardResDto::new)
                .collect(Collectors.toList());
    }

    public Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));
    }

    public List<BoardResDto> getBoardsByTeam(Long teamId) {

        List<Board> boards = boardRepository.findByTeam_Id(teamId);

        return boards.stream()
                .map(BoardResDto::new)  // 여기만 변경됨
                .collect(Collectors.toList());
    }

    public Long modifyBoard(Long id, BoardReqDto dto, User loginUser){
        try{
            Board board = boardRepository.findById(id).orElseThrow(()->new RuntimeException("해당 게시판 id가 존재하지 않습니다."));

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
        } catch (Exception e) {
            log.error("게시판 수정 실패 : {}", e.getMessage());
            throw e;
        }

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
