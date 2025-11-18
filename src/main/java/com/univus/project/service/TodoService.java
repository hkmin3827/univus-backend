package com.univus.project.service;

import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoResDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.Todo;
import com.univus.project.repository.TodoRepository;
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
public class TodoService {
    private final TodoRepository todoRepository;

    // 1) TodoList 생성
    public TodoResDto createTodo(TodoWriteDto dto) {
        try {
            Todo todo = new Todo();
            todo.setContent(dto.getContent());
            todo.setName(dto.getName());
            todo.setDone(false);

            todoRepository.save(todo);
            return new TodoResDto(todo);
        } catch (Exception e) {
            log.error("Todo 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    // 2) Id 조회
    public TodoResDto getTodoById(Long id) {
        try {
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            return new TodoResDto(todo);
        } catch (Exception e) {
            log.error("TodoList 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    // 3) 완료 여부 조회
    public List<TodoResDto> getTodoByDone(boolean done) {
        try {
            return todoRepository.findByDone(done)
                    .stream()
                    .map(TodoResDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("TodoList 완료 여부 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    // 4) TodoList 수정
    public Boolean modifyTodo(Long id, TodoModifyDto dto) {
        try{
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            todo.setContent(dto.getContent());
            todo.setDone(dto.isDone());
            return true;
        } catch (Exception e) {
            log.error("Todo 수정 실패: {}", e.getMessage());
            return false;
        }
    }

    // 5) TodoList 삭제
    public Boolean deleteTodo(Long id) {
        try {
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            todoRepository.delete(todo);
            return true;
        } catch (Exception e) {
            log.error("Todo 삭제 실패: {}", e.getMessage());
            return false;
        }
    }

    // 6) 최신 목록 조회
    public List<TodoResDto> getAllTodo() {
        try {
            return todoRepository.findAllByOrderByCreateTimeDesc()
                    .stream()
                    .map(TodoResDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Todo 목록 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }

}
