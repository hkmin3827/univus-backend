package com.univus.project.service;

import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoResDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import com.univus.project.repository.BoardRepository;
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
    private final BoardRepository boardRepository;

    // Todo 생성 (User + Board 연계)
    public TodoResDto createTodo(TodoWriteDto dto, User user) {
        if (user == null) {
            log.error("Todo 생성 실패: 사용자 정보가 없습니다.");
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }

        if (dto.getBoardId() == null) {
            log.error("Todo 생성 실패: 게시판 ID가 없습니다.");
            throw new RuntimeException("게시판을 선택해야 합니다.");
        }

        Long boardIdValue;
        try {
            boardIdValue = Long.valueOf(dto.getBoardId());
        } catch (NumberFormatException e) {
            log.error("Todo 생성 실패: 유효하지 않은 게시판 ID 포맷입니다.", e);
            throw new RuntimeException("유효하지 않은 게시판 ID입니다.");
        }

        Board board = boardRepository.findById(boardIdValue)
                .orElseThrow(() -> new RuntimeException("게시판이 없습니다."));

        Todo todo = new Todo();
        todo.setContent(dto.getContent());
        todo.setUser(user);
        todo.setBoard(board);
        todo.setDone(false);

        todoRepository.save(todo);

        return new TodoResDto(todo.getBoard().getName(), todo);
    }

    // 2) Id 조회
    public TodoResDto getTodoById(Long id) {
        try {
            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
            return new TodoResDto(todo.getBoard().getName(), todo);
        } catch (Exception e) {
            log.error("TodoList 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    // 3) Board 기준 TodoList 조회
    public List<TodoResDto> getTodosByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시판이 없습니다."));
        return todoRepository.findByBoard(board).stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                .collect(Collectors.toList());
    }

    // 4) 완료 여부 조회 (로그인 유저 기준)
    public List<TodoResDto> getTodoByDoneForUser(boolean done, User user) {
        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }
        try {
            return todoRepository.findByDoneAndUser(done, user)  // User 기준 추가
                    .stream()
                    .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("TodoList 완료 여부 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }

    // 5) 팀 단위로 완료된 Todo 조회
    public List<TodoResDto> getCompletedTodosForTeam(Long teamId) {
        return todoRepository.findByBoard_Team_IdAndDoneOrderByCreateTimeDesc(teamId, true)
                .stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo)) // Board 이름 포함
                .collect(Collectors.toList());
    }

    // 6) TodoList 수정 (작성자 권한 체크)
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
                    .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Todo 목록 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }
}
