package com.univus.project.dto.team;

import lombok.Data;

@Data
public class TeamCreateReqDto {
    private String teamName;
    private String description;
    private String leaderId;  // 팀 생성자
}
