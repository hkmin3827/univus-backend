package com.univus.project.dto.team;

import com.univus.project.entity.Team;
import lombok.Builder;
import lombok.Data;

// 팀 상세/목록 응답용 DTO
@Data
@Builder
public class TeamResDto {
    private Long id;            // 팀 ID
    private String teamName;    // 팀 이름
    private String description; // 팀 소개

    private Long leaderId;      // 팀장 ID
    private String leaderName;  // 팀장 이름
    private String leaderEmail; // 팀장 이메일

    private long memberCount;   // 팀 멤버 수
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
