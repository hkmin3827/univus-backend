package com.univus.project.dto.post;


import com.univus.project.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString @NoArgsConstructor
// 게시글 목록 화면용
public class PostListDto {
    private Long id;
    private String title;
    private String userName;
    private LocalDateTime createTime;
    private String fileUrl;

    public PostListDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.userName = post.getUser().getName();  // Member 엔티티에 name이 있다고 가정
        this.createTime = post.getCreateTime();
        this.fileUrl = post.getFileUrl();
    }
}
