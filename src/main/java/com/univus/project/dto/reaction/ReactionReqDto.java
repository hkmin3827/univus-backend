package com.univus.project.dto.reaction;

import com.univus.project.constant.ReactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class ReactionReqDto {
   private ReactionType type;
}