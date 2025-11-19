package com.univus.project.dto.board;

import com.univus.project.entity.Board;
import com.univus.project.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class BoardResDto {
    private Long id;
    private String name;
    private String description;
    private String creatorName;


    public BoardResDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.description = board.getDescription();
        this.creatorName = board.getCreator().getName();
    }
}
