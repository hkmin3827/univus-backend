package com.univus.project.dto.team;

import com.univus.project.entity.Team;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResDto {
    private Long id;
    private String teamName;
    private String description;
    private String leader;

    public static TeamResDto fromEntity(Team team) {
        return TeamResDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .leader(team.getLeader())
                .build();
    }
}
