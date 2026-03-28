package com.univus.project.dto.team;

import com.univus.project.entity.Team;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamResDto {
    private Long id;
    private String teamName;
    private String description;

    private Long leaderId;
    private String leaderName;
    private String leaderEmail;

    private long memberCount;
    public static TeamResDto fromEntity(Team team) {
        return TeamResDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .leaderId(team.getLeader().getId())
                .leaderName(team.getLeader().getName())
                .leaderEmail(team.getLeader().getEmail())
                .memberCount(team.getMembers().size())
                .build();
    }
}
