package com.univus.project.dto.post;


import com.univus.project.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor
public class PostResDto {
    private Long postId;
    private String name;  // 작성자 이름
    private String title;
    private String content;
    private String fileUrl;
    private String fileName;
    private LocalDateTime createTime;
    private Long boardId;
    private String boardName;

    public PostResDto(Post post){
        this.postId = post.getId();
        this.name = post.getUser() != null ? post.getUser().getName() : "";
        this.title = post.getTitle();
        this.content = post.getContent();
        this.fileUrl = post.getFileUrl();
        this.fileName = post.getFileName();
        this.createTime = post.getCreateTime();
        this.boardId = post.getBoard().getId();
        this.boardName = post.getBoard().getName();
    }
}
