package com.univus.project.dto.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoWriteDto {
    @JsonProperty("teamId")
    private Long teamId;
    @JsonProperty("boardId")
    private Long boardId;
    @JsonProperty("content")
    private String content;
}
