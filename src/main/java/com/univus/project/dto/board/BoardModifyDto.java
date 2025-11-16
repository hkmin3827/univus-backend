package com.univus.project.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor
@ToString
public class BoardModifyDto {
    private String email;
    private String name;
    private String description;
}
