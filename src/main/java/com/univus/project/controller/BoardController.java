package com.univus.project.controller;

import com.univus.project.dto.board.BoardReqDto;
import com.univus.project.dto.board.BoardResDto;
import com.univus.project.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시판 생성
    @PostMapping("/create")
    public ResponseEntity<Long> createBoard(@RequestBody BoardReqDto dto){
        Long id = boardService.createBoard(dto);
        return ResponseEntity.ok(id);
    }

    // 전체 게시판 조회
    @GetMapping("/list")
    public ResponseEntity<List<BoardResDto>> getAllBoards() {
        List<BoardResDto> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }

    // 특정 게시판 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResDto> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(new BoardResDto(boardService.getBoard(id)));
    }

    // 게시판 수정
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyBoard(@PathVariable Long id, @RequestBody BoardReqDto dto){
        return ResponseEntity.ok(boardService.modifyBoard(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteBoard(@PathVariable Long id){
        return ResponseEntity.ok(boardService.deleteBoard(id));
    }

}
