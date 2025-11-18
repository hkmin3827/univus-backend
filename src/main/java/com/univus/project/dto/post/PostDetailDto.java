package com.univus.project.dto.post;

import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 게시글 상세 조회
@Getter @Setter @NoArgsConstructor @ToString
public class PostDetailDto {
    private Long id;
    private String title;
    private String content;
    private String fileUrl;
    private String userName;
    private LocalDateTime createTime;

    private List<CommentResDto> comments;

    public PostDetailDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.fileUrl = post.getFileUrl();
        this.userName = post.getUser().getName();
        this.createTime = post.getCreateTime();

        this.comments = post.getComments()
                .stream()
                .map(CommentResDto::new)
                .collect(Collectors.toList());
    }
}
