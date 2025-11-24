package com.univus.project.dto.team;

import lombok.Data;

// 팀 생성 요청 DTO
@Data
public class TeamCreateReqDto {
    private String teamName;    // 팀 이름
    private String description; // 팀 소개
}
