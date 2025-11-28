package com.univus.project.dto.comment;


import com.univus.project.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

// 댓글 조회
@Getter @Setter @NoArgsConstructor @ToString
public class CommentResDto {
    private Long id;
    private String content;
    private String userName;
    private String userEmail;
    private LocalDateTime createTime;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getWriter().getName(); // Member 엔티티 기준
        this.userEmail = comment.getWriter().getEmail();
        this.createTime = comment.getCreateTime();
    }
}
