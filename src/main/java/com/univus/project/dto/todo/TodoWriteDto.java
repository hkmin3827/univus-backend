package com.univus.project.dto.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class TodoWriteDto {
    private String email;   // 작성자 이메일
    private Long teamId;    // 할일이 속한 팀
    @JsonProperty("boardId")
    private Long boardId;   // 할일이 속한 게시판
    private String content; // 할일 내용
}
