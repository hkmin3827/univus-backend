package com.univus.project.dto.todo;
import com.univus.project.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoResDto {
    private Long id;        // todolist id
    private String email;   // 작성자 이메일
    private String content; // 할일 내용
    private boolean done;   // 완료 여부
    private LocalDateTime createTime;

    public TodoResDto(Todo todo) {
        this.id = todo.getId();
        this.email = todo.getUser().getEmail();
        this.content = todo.getContent();
        this.done = todo.isDone();
        this.createTime = todo.getCreateTime();
    }
}
