package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoResDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.User;
import com.univus.project.service.TodoService;
import com.univus.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    // 1) Todo 생성
    @PostMapping("/create")
    public ResponseEntity<TodoResDto> createTodo(@RequestBody TodoWriteDto dto,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("수신된 DTO 값: {}", dto);
        log.info("boardId: {}", dto.getBoardId());

        User user = userDetails.getUser();
        try {
            TodoResDto todo = todoService.createTodo(dto, user);
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            log.error("Todo 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 2) Todo 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<TodoResDto> getTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    // 3) 게시판 기준 Todo 조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<TodoResDto>> getTodosByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(todoService.getTodosByBoard(boardId));
    }
    @GetMapping("/board/{boardId}/list")
    public ResponseEntity<List<TodoResDto>> getTodosByBoardId(@PathVariable Long boardId) {
        return ResponseEntity.ok(todoService.getTodosByBoardId(boardId));
    }

    // 4) 완료 기준 조회
    @GetMapping("/done/{done}")
    public ResponseEntity<List<TodoResDto>> getTodoByDone(@PathVariable boolean done,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(todoService.getTodoByDoneForUser(done, user));
    }

    // 5) Todo 수정
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyTodo(@PathVariable Long id,
                                              @RequestBody TodoModifyDto dto,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Boolean result = todoService.modifyTodo(id, dto, user);
        return ResponseEntity.ok(result);
    }

    // 6) Todo 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteTodo(@PathVariable Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Boolean result = todoService.deleteTodo(id, user);
        return ResponseEntity.ok(result);
    }

    // 7) 로그인 유저 기준 전체 Todo 목록
    @GetMapping("/list")
    public ResponseEntity<List<TodoResDto>> getAllTodoForUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(todoService.getAllTodoForUser(user));
    }

    // 8) 팀 기준 완료 Todo 조회
    @GetMapping("/team/{teamId}/completed")
    public ResponseEntity<List<TodoResDto>> getCompletedTodosForTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(todoService.getCompletedTodosForTeam(teamId));
    }

    // 9) 팀 + 게시판 기준 Todo 조회
    @GetMapping("/team/{teamId}/board/{boardId}")
    public ResponseEntity<List<TodoResDto>> getTodosByTeamAndBoard(
            @PathVariable Long teamId,
            @PathVariable Long boardId) {
        return ResponseEntity.ok(todoService.getTodosByTeamAndBoard(teamId, boardId));
    }

    // 로그인한 사용자 + 특정 게시판 기준 Todo 조회
    @GetMapping("/board/{boardId}/mine")
    public ResponseEntity<List<TodoResDto>> getMyTodosByBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(todoService.getTodosByUserAndBoard(user, boardId));
    }
}
