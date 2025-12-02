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
    private final ActivityLogService activityLogService;


    //1) Todo 생성
    public TodoResDto createTodo(TodoWriteDto dto, User user) {

        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }
        if (dto.getBoardId() == null) {
            throw new RuntimeException("게시판을 선택해야 합니다.");
        }

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

        Todo todo = new Todo();
        todo.setContent(dto.getContent());
        todo.setUser(user);
        todo.setBoard(board);
        todo.setDone(false);

        todoRepository.save(todo);

        // 활동 로그 업데이트
        try {
            activityLogService.recalcActivityLog(user.getId(), board.getId());
        } catch (Exception e) {
            log.error("활동 로그 업데이트 실패: {}", e.getMessage());
        }

        return new TodoResDto(board.getName(), todo);
    }


    // 2) Todo Id 조회
    public TodoResDto getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
        return new TodoResDto(todo.getBoard().getName(), todo);
    }


    // 3) Board 기준 조회
    public List<TodoResDto> getTodosByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시판이 없습니다."));

        return todoRepository.findByBoard(board).stream()
                .map(todo -> new TodoResDto(board.getName(), todo))
                .collect(Collectors.toList());
    }

    public List<TodoResDto> getTodosByTeamAndBoard(Long teamId, Long boardId) {
        return todoRepository.findByBoard_Team_IdAndBoard_Id(teamId, boardId)
                .stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                .collect(Collectors.toList());
    }



    // 4) 완료 여부 기준 로그인 사용자 조회
    public List<TodoResDto> getTodoByDoneForUser(boolean done, User user) {
        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }

        return todoRepository.findByDoneAndUser(done, user).stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                .collect(Collectors.toList());
    }


    // 5) 팀 기준 완료된 Todo 조회
    public List<TodoResDto> getCompletedTodosForTeam(Long teamId) {
        return todoRepository.findByBoard_Team_IdAndDoneOrderByCreateTimeDesc(teamId, true)
                .stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                .collect(Collectors.toList());
    }


    // Todo 수정
    public Boolean modifyTodo(Long id, TodoModifyDto dto, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        boolean prevDone = todo.isDone();

        todo.setContent(dto.getContent());
        todo.setDone(dto.isDone());

        // 완료 여부 변경 → 활동 로그 업데이트
        if (todo.getBoard() != null && prevDone != dto.isDone()) {
            try {
                activityLogService.recalcActivityLog(user.getId(), todo.getBoard().getId());
            } catch (Exception e) {
                log.error("Todo 수정 후 활동 로그 계산 실패: {}", e.getMessage());
            }
        }

        return true;
    }


    // 7) Todo 삭제
    public Boolean deleteTodo(Long id, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        Long boardId = todo.getBoard() != null ? todo.getBoard().getId() : null;

        todoRepository.delete(todo);

        // 활동 로그 업데이트
        if (boardId != null) {
            try {
                activityLogService.recalcActivityLog(user.getId(), boardId);
            } catch (Exception e) {
                log.error("Todo 삭제 후 활동 로그 계산 실패: {}", e.getMessage());
            }
        }

        return true;
    }


    //8) 로그인 사용자 전체 Todo 최신순
    public List<TodoResDto> getAllTodoForUser(User user) {
        if (user == null) {
            throw new RuntimeException("사용자 정보가 필요합니다.");
        }

        return todoRepository.findByUserOrderByCreateTimeDesc(user).stream()
                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
                .collect(Collectors.toList());
    }
}
