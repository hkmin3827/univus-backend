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

    // Post 필드
    private Long postId;

    // 공감 여부
    private boolean press;

    public ReactionResDto(Reaction reaction, boolean press) {
        this.userId = reaction.getUser().getId();
        this.postId = reaction.getPost().getId();
        this.press = press;



    }
}
