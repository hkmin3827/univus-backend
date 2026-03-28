package com.univus.project.dto.todo;

import com.univus.project.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoResDto {
    private Long id;
    private String email;
    private String userName;
    private String content;
    private boolean done;
    private LocalDateTime createTime;
    private String boardName;
    private Long boardId;

    public TodoResDto(String boardName, Todo todo) {
        this.boardName = boardName;
        this.id = todo.getId();
        this.email = todo.getUser().getEmail();
        this.userName = todo.getUser().getName();
        this.content = todo.getContent();
        this.done = todo.isDone();
        this.createTime = todo.getCreateTime();
        this.boardId = todo.getBoard().getId();
    }
    public TodoResDto(Todo todo) {
        this.boardName = todo.getBoard().getName();
        this.id = todo.getId();
        this.email = todo.getUser().getEmail();
        this.userName = todo.getUser().getName();
        this.content = todo.getContent();
        this.done = todo.isDone();
        this.createTime = todo.getCreateTime();
        this.boardId = todo.getBoard().getId();
    }
}