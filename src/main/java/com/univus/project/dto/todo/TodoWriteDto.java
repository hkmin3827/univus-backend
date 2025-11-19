package com.univus.project.dto.todo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoWriteDto {
    private String content; // 할일 내용
}
