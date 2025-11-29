package com.univus.project.dto.post;

import com.univus.project.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class PostReqDto {
    private String title;
    private String content;
    private Long BoardId;
    private String fileUrl;
    private String fileName;
}