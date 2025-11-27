package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.board.BoardReqDto;
import com.univus.project.dto.board.BoardResDto;
import com.univus.project.entity.User;
import com.univus.project.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")  // 프론트 연결
@RestController
@RequestMapping("/teams/{teamId}/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 생성 (팀 종속)
    @PostMapping("/create")
    public ResponseEntity<Long> createBoard(
            @PathVariable Long teamId,
            @RequestBody BoardReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        dto.setTeamId(teamId);  // DTO에 팀 id 세팅
        Long id = boardService.createBoard(dto, user);
        return ResponseEntity.ok(id);
    }

    // 특정 팀의 게시판 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<BoardResDto>> getBoardsByTeam(
            @PathVariable Long teamId
    ) {
        return ResponseEntity.ok(boardService.getBoardsByTeam(teamId));
    }

    // 게시판 상세 조회 (단일)
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResDto> getBoard(
            @PathVariable Long teamId,
            @PathVariable Long boardId
    ) {
        return ResponseEntity.ok(new BoardResDto(boardService.getBoard(boardId)));
    }

    // 게시판 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<Long> modifyBoard(
            @PathVariable Long teamId,
            @PathVariable Long boardId,
            @RequestBody BoardReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        dto.setTeamId(teamId);
        Long updatedId = boardService.modifyBoard(boardId, dto, user);
        return ResponseEntity.ok(updatedId);
    }

    // 게시판 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long teamId,
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        boardService.deleteBoard(boardId, user);
        return ResponseEntity.noContent().build();
    }
}
