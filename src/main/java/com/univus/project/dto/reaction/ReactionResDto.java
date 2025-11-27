package com.univus.project.dto.reaction;

import com.univus.project.constant.ReactionType;
import com.univus.project.entity.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class ReactionResDto {

    // Post 필드
    private Long postId;

    // User 필드
    private Long userId;

    //어떤 감정인지 여부
    private ReactionType type;

    // 현재 로그인 유저가 누른 반응인지 여부
    private boolean mine;

    public ReactionResDto(Reaction reaction, Long currentUserId) {
        this.userId = reaction.getUser().getId();
        this.postId = reaction.getPost().getId();
        this.type = reaction.getType();
        this.mine = reaction.getUser().getId().equals(currentUserId);



    }
}
