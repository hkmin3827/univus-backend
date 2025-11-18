package com.univus.project.dto.post;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor
public class PostResDto {
    private Long postId;
    private String name;
    private String title;
    private String content;
    private String fileUrl;
    private LocalDateTime createTime;

}
