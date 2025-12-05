package com.univus.project.dto.post;


import com.univus.project.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
// 게시글 목록 화면용
public class PostListDto {
    private Long id;
    private String title;
    private String userName;
    private LocalDateTime createTime;
    private String fileUrl;
    private String writerImage;
    private String content;
    private Long boardId;
    private Long teamId;

    public PostListDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userName = post.getUser().getName(); 
        this.createTime = post.getCreateTime();
        this.fileUrl = post.getFileUrl();
        this.writerImage = post.getUser().getImage();
        this.boardId = post.getBoard().getId();
        this.teamId = post.getBoard().getTeam().getId();
    }
}
