package com.univus.project.dto.board;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class BoardReqDto {
    private String name;
    private String description;
}
