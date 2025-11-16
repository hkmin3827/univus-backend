package com.univus.project.dto.post;
// 게시물 작성

import com.univus.project.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class PostWriteDto {
    private String email;
    private String name;   // 작성자 이름
    private String title;
    private String imagePath;
    private String content;
    private Board board;
}
