package com.univus.project.dto.team;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamModifyReqDto {
    private String teamName;
    private String description;
    private String leader;
}