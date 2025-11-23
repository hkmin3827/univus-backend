package com.univus.project.dto.board;


import com.univus.project.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class BoardReqDto {
    private Long teamId;
    private String name;
    private String description;
    private User creator;
}
