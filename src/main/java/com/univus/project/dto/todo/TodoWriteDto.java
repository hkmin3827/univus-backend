package com.univus.project.dto.todo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoWriteDto {
    private String email;
    private String name;    // 작성자 이름
    private String content; // 할일 내용
}
