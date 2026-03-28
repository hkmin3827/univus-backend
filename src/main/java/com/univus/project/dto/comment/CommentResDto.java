package com.univus.project.dto.comment;

import com.univus.project.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class CommentResDto {
    private Long id;
    private String content;
    private String userName;
    private String userEmail;
    private String writerImage;
    private LocalDateTime createTime;
    private Long postId;
    private String boardName;
    private Long boardId;
    private Long writerId;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getWriter().getName();
        this.userEmail = comment.getWriter().getEmail();
        this.createTime = comment.getCreateTime();
        this.writerImage = comment.getWriter().getImage();
        this.postId = comment.getPost().getId();
        this.boardName = comment.getPost().getBoard().getName();
        this.boardId = comment.getPost().getBoard().getId();
        this.writerId = comment.getWriter().getId();
    }
}
