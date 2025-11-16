package com.univus.project.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class PostReqDto {
    private String title;
    private String content;
    private Long boardId;
}
