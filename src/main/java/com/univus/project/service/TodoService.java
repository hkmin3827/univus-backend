package com.univus.project.service;

import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoResDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
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

    // 1) TodoList 생성 (작성한 User 확인)
    public TodoResDto createTodo(TodoWriteDto dto, User user) {
        if (user == null) {
            log.error("Todo 생성 실패: 사용자 정보가 없습니다.");
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }
        try {
            Todo todo = new Todo();
            todo.setContent(dto.getContent());
            todo.setUser(user);
            todo.setDone(false);
            todoRepository.save(todo);
            return new TodoResDto(todo);
        } catch (Exception e) {
            log.error("Todo 생성 실패: {}", e.getMessage());
            throw new RuntimeException("Todo 생성 중 오류가 발생했습니다.");
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

    // 3) 작성자 이메일로 TodoList 조회
    public List<TodoResDto> getTodoByUserEmail(String email) {
        try {
            return todoRepository.findByUser_Email(email)
                    .stream()
                    .filter(todo -> todo.getUser() != null)
                    .map(TodoResDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("{} 이메일로 Todo 조회 실패: {}", email, e.getMessage());
            return List.of();
        }
    }

    // 4) 완료 여부 조회 (로그인 유저 기준)
    public List<TodoResDto> getTodoByDoneForUser(boolean done, User user) {
        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }
        try {
            return todoRepository.findByDoneAndUser(done, user)  // User 기준 추가
                    .stream()
                    .map(todo -> new TodoResDto(todo))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("TodoList 완료 여부 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }

    // 5) TodoList 수정 (작성자 권한 체크)
    public Boolean modifyTodo(Long id, TodoModifyDto dto, User user) {
        try{
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            if (!todo.getUser().getId().equals(user.getId())){
                throw new RuntimeException("수정 권한이 없습니다.");
            }
            todo.setContent(dto.getContent());
            todo.setDone(dto.isDone());
            return true;
        } catch (Exception e) {
            log.error("Todo 수정 실패: {}", e.getMessage());
            return false;
        }
    }

    // 6) TodoList 삭제 (작성자 권한 체크)
    public Boolean deleteTodo(Long id, User user) {
        try {
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            if (!todo.getUser().getId().equals(user.getId())){
                throw new RuntimeException("삭제 권한이 없습니다.");
            }
            todoRepository.delete(todo);
            return true;
        } catch (Exception e) {
            log.error("Todo 삭제 실패: {}", e.getMessage());
            return false;
        }
    }

    // 6) 최신 목록 조회
    public List<TodoResDto> getAllTodoForUser(User user) {
        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }
        try {
            return todoRepository.findByUserOrderByCreateTimeDesc(user) // user 기준 정렬
                    .stream()
                    .map(TodoResDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Todo 목록 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }


}
