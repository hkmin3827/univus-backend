package com.univus.project.dto.todo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoModifyDto {
    private Long id;
    private String content;
    private boolean done;
}
