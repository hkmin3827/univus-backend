package com.univus.project.dto.reaction;

import com.univus.project.entity.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class ReactionResDto {
    // User 필드
    private Long userId;
    private String userName;
    private String userEmail;

    // Board 필드
    private Long boardId;
    private String boardName;

    // Post 필드
    private Long postId;
    private String postTitle;

    // 공감 여부
    private boolean press;

    public ReactionResDto(Reaction reaction) {
        this.userId = reaction.getUser().getId();
        this.userName = reaction.getUser().getName();
        this.userEmail = reaction.getUser().getEmail();

        this.boardId = reaction.getBoard().getId();
        this.boardName = reaction.getBoard().getName();

        this.postId = reaction.getPost().getId();
        this.postTitle = reaction.getPost().getTitle();

    }
}
