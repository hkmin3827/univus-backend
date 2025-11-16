package com.univus.project.dto.todo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoModifyDto {
    private Long id;        // 수정할 todolist Id
    private String content; // 할일 내용
    private boolean done;   // 완료 여부
}
