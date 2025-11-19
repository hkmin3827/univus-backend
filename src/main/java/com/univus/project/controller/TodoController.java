package com.univus.project.controller;

import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoResDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.User;
import com.univus.project.service.TodoService;
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

    // 1) TodoList 생성
    @PostMapping("/create")
    public ResponseEntity<TodoResDto> createTodo(@RequestBody TodoWriteDto dto, @AuthenticationPrincipal User user) {
        TodoResDto todo = todoService.createTodo(dto, user);
        return ResponseEntity.ok(todo);
    }

    // 2) TodoList 조회
    @GetMapping("/{id}")
    public ResponseEntity<TodoResDto> getTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.getTodoById(id));
    }

    // 3) TodoList 완료 여부 조회
    @GetMapping("/done/{done}")
    public ResponseEntity<List<TodoResDto>> getTodoByDone(@PathVariable boolean done) {
        return ResponseEntity.ok(todoService.getTodoByDone(done));
    }

    // 4) TodoList 수정
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyTodo(@PathVariable Long id, @RequestBody TodoModifyDto dto, @AuthenticationPrincipal User user) {
        Boolean result = todoService.modifyTodo(id, dto, user);
        return ResponseEntity.ok(result);
    }

    // 5) TodoList 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteTodo(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Boolean result = todoService.deleteTodo(id, user);
        return ResponseEntity.ok(result);
    }

    // 6) 최신순 TodoList 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<TodoResDto>> getAllTodo() {
        return ResponseEntity.ok(todoService.getAllTodo());
    }
}
