package com.univus.project.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class CommentReqDto {
    private Long postId;
    private String content;
}
