package com.univus.project.service;


import com.univus.project.dto.todo.TodoModifyDto;
import com.univus.project.dto.todo.TodoWriteDto;
import com.univus.project.entity.Board;
import com.univus.project.entity.Todo;
import com.univus.project.entity.User;
import com.univus.project.repository.BoardRepository;
import com.univus.project.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private Board board;
    private Todo todo;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("홍길동");
        user.setEmail("test@test.com");

        board = new Board();
        board.setId(100L);
        board.setName("테스트 게시판");

        todo = new Todo();
        todo.setId(10L);
        todo.setContent("테스트 할 일");
        todo.setUser(user);
        todo.setBoard(board);
        todo.setDone(false);
    }


    @Test
    void TODO_생성_성공() {
        TodoWriteDto dto = new TodoWriteDto();
        dto.setBoardId(100L);
        dto.setContent("새로운 할 일");

        when(boardRepository.findById(100L)).thenReturn(Optional.of(board));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        var result = todoService.createTodo(dto, user);

        assertEquals("테스트 게시판", result.getBoardName());
        assertEquals("새로운 할 일", dto.getContent());
        verify(activityLogService).recalcActivityLog(1L, 100L);
        verify(todoRepository).save(any(Todo.class));
    }


    @Test
    void TODO_완료_성공() {
        TodoModifyDto dto = new TodoModifyDto();
        dto.setContent("할 일 1");
        dto.setDone(true);

        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        boolean result = todoService.modifyTodo(10L, dto, user);

        assertTrue(result);
        assertTrue(todo.isDone());
        assertEquals("할 일 1", todo.getContent());
        verify(activityLogService).recalcActivityLog(1L, 100L);
    }

    @Test
    void TODO_생성_실패_사용자정보없음() {
        TodoWriteDto dto = new TodoWriteDto();
        dto.setBoardId(100L);

        assertThrows(RuntimeException.class,
                () -> todoService.createTodo(dto, null));
    }


    @Test
    void TODO_ID로_조회_성공() {
        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        var result = todoService.getTodoById(10L);

        assertEquals(10L, result.getId());
        assertEquals("테스트 할 일", result.getContent());
    }

    @Test
    void TODO_수정_성공() {
        TodoModifyDto dto = new TodoModifyDto();
        dto.setContent("수정된 할 일");
        dto.setDone(true);

        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        boolean result = todoService.modifyTodo(10L, dto, user);

        assertTrue(result);
        assertEquals("수정된 할 일", todo.getContent());
        assertTrue(todo.isDone());
        verify(activityLogService).recalcActivityLog(1L, 100L);
    }

    @Test
    void TODO_수정_실패_권한없음() {
        TodoModifyDto dto = new TodoModifyDto();
        User anotherUser = new User();
        anotherUser.setId(999L);

        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        assertThrows(RuntimeException.class,
                () -> todoService.modifyTodo(10L, dto, anotherUser));
    }

    @Test
    void TODO_삭제_성공() {
        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        boolean result = todoService.deleteTodo(10L, user);

        assertTrue(result);
        verify(todoRepository).delete(todo);
        verify(activityLogService).recalcActivityLog(1L, 100L);
    }


    @Test
    void TODO_삭제_실패_권한없음() {
        User anotherUser = new User();
        anotherUser.setId(888L);

        when(todoRepository.findById(10L)).thenReturn(Optional.of(todo));

        assertThrows(RuntimeException.class,
                () -> todoService.deleteTodo(10L, anotherUser));
    }
    @Test
    void TODO_없으면_예외발생() {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        TodoModifyDto dto = new TodoModifyDto();
        dto.setContent("test");
        dto.setDone(true);

        assertThrows(RuntimeException.class,
                () -> todoService.modifyTodo(1L, dto, user));
    }
}
