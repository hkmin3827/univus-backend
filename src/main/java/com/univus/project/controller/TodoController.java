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
        User user = userDetails.getUser();
        TodoResDto todo = todoService.createTodo(dto, user);
        return ResponseEntity.ok(todo);
    }

    // 2) Todo ID 조회
    @GetMapping("/{id}")
    public ResponseEntity<TodoResDto> getTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    // 3) 작성자 이메일로 Todo 조회
    @GetMapping("/user")
    public ResponseEntity<List<TodoResDto>> getTodoByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(todoService.getTodoByUserEmail(email));
    }

    // 4) 완료 여부 조회
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

    // 7) 최신 Todo 목록 조회 (로그인 유저 기준)
    @GetMapping("/list")
    public ResponseEntity<List<TodoResDto>> getAllTodoForUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(todoService.getAllTodoForUser(user));
    }
}
