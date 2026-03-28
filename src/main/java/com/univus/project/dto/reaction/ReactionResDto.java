package com.univus.project.dto.reaction;

import com.univus.project.constant.ReactionType;
import com.univus.project.entity.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class ReactionResDto {

    private Long postId;

    private Long userId;

    private ReactionType type;

    private boolean mine;

    public ReactionResDto(Reaction reaction, Long currentUserId) {
        this.userId = reaction.getUser().getId();
        this.postId = reaction.getPost().getId();
        this.type = reaction.getType();
        this.mine = reaction.getUser().getId().equals(currentUserId);



    }
}
