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
    private final BoardRepository boardRepository;          // 🔥 추가
    private final ActivityLogService activityLogService;    // 🔥 추가

    // 1) TodoList 생성 (작성한 User + Board 확인)

//    // Todo 생성 (User + Board 연계)
//    public TodoResDto createTodo(TodoWriteDto dto, User user) {
//        if (user == null) {
//            log.error("Todo 생성 실패: 사용자 정보가 없습니다.");
//            throw new RuntimeException("사용자 정보가 필요합니다.");
//        }
//        try {
//            // 🔥 보드 조회 (Todo가 어느 보드에 속하는지)
//            Board board = boardRepository.findById(dto.getBoardId())
//                    .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));
//
//            Todo todo = new Todo();
//            todo.setContent(dto.getContent());
//            todo.setUser(user);
//            todo.setBoard(board);      // 🔥 반드시 보드 세팅
//            todo.setDone(false);
//
//            todoRepository.save(todo);
//
//            // 🔥 Todo 생성 후 활동 로그 재계산
//            try {
//                activityLogService.recalcActivityLog(user.getId(), board.getId());
//            } catch (Exception e) {
//                log.error("Todo 생성 후 활동 로그 계산 실패(userId:{}, boardId:{}): {}",
//                        user.getId(), board.getId(), e.getMessage());
//            }
//
//            return new TodoResDto(todo);
//        } catch (Exception e) {
//            log.error("Todo 생성 실패: {}", e.getMessage());
//            throw new RuntimeException("Todo 생성 중 오류가 발생했습니다.");
//
//        if (dto.getBoardId() == null) {
//            log.error("Todo 생성 실패: 게시판 ID가 없습니다.");
//
//        }
//
//        Long boardIdValue;
//        try {
//            boardIdValue = Long.valueOf(dto.getBoardId());
//        } catch (NumberFormatException e) {
//            log.error("Todo 생성 실패: 유효하지 않은 게시판 ID 포맷입니다.", e);
//            throw new RuntimeException("유효하지 않은 게시판 ID입니다.");
//        }
//
//        Board board = boardRepository.findById(boardIdValue)
//                .orElseThrow(() -> new RuntimeException("게시판이 없습니다."));
//
//        Todo todo = new Todo();
//        todo.setContent(dto.getContent());
//        todo.setUser(user);
//        todo.setBoard(board);
//        todo.setDone(false);
//
//        todoRepository.save(todo);
//
//        return new TodoResDto(todo.getBoard().getName(), todo);
//    }
//
//    // 2) Id 조회
//    public TodoResDto getTodoById(Long id) {
//        try {
//            Todo todo = todoRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
//            return new TodoResDto(todo.getBoard().getName(), todo);
//        } catch (Exception e) {
//            log.error("TodoList 조회 실패: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    // 3) Board 기준 TodoList 조회
//    public List<TodoResDto> getTodosByBoard(Long boardId) {
//        Board board = boardRepository.findById(boardId)
//                .orElseThrow(() -> new RuntimeException("게시판이 없습니다."));
//        return todoRepository.findByBoard(board).stream()
//                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
//                .collect(Collectors.toList());
//    }
//
//    // 4) 완료 여부 조회 (로그인 유저 기준)
//    public List<TodoResDto> getTodoByDoneForUser(boolean done, User user) {
//        if (user == null) {
//            throw new RuntimeException("사용자 정보가 필요합니다.");
//        }
//        try {
//            return todoRepository.findByDoneAndUser(done, user)
//                    .stream()
//                    .map(TodoResDto::new)
//
//                    .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
//
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("TodoList 완료 여부 조회 실패: {}", e.getMessage());
//            return List.of();
//        }
//    }
//
//
//    // 5) TodoList 수정 (작성자 권한 체크 + done 변경 시 기여도 반영)
//
//    // 5) 팀 단위로 완료된 Todo 조회
//    public List<TodoResDto> getCompletedTodosForTeam(Long teamId) {
//        return todoRepository.findByBoard_Team_IdAndDoneOrderByCreateTimeDesc(teamId, true)
//                .stream()
//                .map(todo -> new TodoResDto(todo.getBoard().getName(), todo)) // Board 이름 포함
//                .collect(Collectors.toList());
//    }
//
//    // 6) TodoList 수정 (작성자 권한 체크)
//    public Boolean modifyTodo(Long id, TodoModifyDto dto, User user) {
//        try {
//            Todo todo = todoRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
//            if (!todo.getUser().getId().equals(user.getId())) {
//                throw new RuntimeException("수정 권한이 없습니다.");
//            }
//
//            boolean prevDone = todo.isDone();
//
//            todo.setContent(dto.getContent());
//            todo.setDone(dto.isDone());
//
//            // 🔥 완료 여부가 바뀌었으면 활동 로그 재계산
//            try {
//                if (todo.getBoard() != null && prevDone != dto.isDone()) {
//                    Long boardId = todo.getBoard().getId();
//                    activityLogService.recalcActivityLog(user.getId(), boardId);
//                }
//            } catch (Exception e) {
//                log.error("Todo 수정 후 활동 로그 계산 실패(userId:{}, todoId:{}): {}",
//                        user.getId(), id, e.getMessage());
//            }
//
//            return true;
//        } catch (Exception e) {
//            log.error("Todo 수정 실패: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    // 6) TodoList 삭제 (작성자 권한 체크 + 기여도 반영)
//    public Boolean deleteTodo(Long id, User user) {
//        try {
//            Todo todo = todoRepository.findById(id)
//                    .orElseThrow(() -> new RuntimeException("TodoList가 없습니다!"));
//            if (!todo.getUser().getId().equals(user.getId())) {
//                throw new RuntimeException("삭제 권한이 없습니다.");
//            }
//
//            Long boardId = null;
//            if (todo.getBoard() != null) {
//                boardId = todo.getBoard().getId();
//            }
//
//            todoRepository.delete(todo);
//
//            // 🔥 삭제 후 활동 로그 재계산
//            if (boardId != null) {
//                try {
//                    activityLogService.recalcActivityLog(user.getId(), boardId);
//                } catch (Exception e) {
//                    log.error("Todo 삭제 후 활동 로그 계산 실패(userId:{}, boardId:{}): {}",
//                            user.getId(), boardId, e.getMessage());
//                }
//            }
//
//            return true;
//        } catch (Exception e) {
//            log.error("Todo 삭제 실패: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    // 7) 최신 목록 조회
//    public List<TodoResDto> getAllTodoForUser(User user) {
//        if (user == null) {
//            throw new RuntimeException("사용자 정보가 필요합니다.");
//        }
//        try {
//            return todoRepository.findByUserOrderByCreateTimeDesc(user)
//                    .stream()
//                    .map(todo -> new TodoResDto(todo.getBoard().getName(), todo))
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("Todo 목록 조회 실패: {}", e.getMessage());
//            return List.of();
//        }
//    }
}
