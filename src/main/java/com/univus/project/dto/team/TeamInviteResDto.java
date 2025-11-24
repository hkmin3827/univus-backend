package com.univus.project.dto.team;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// 팀 초대 관련 응답 DTO (초대 URL / 초대 정보 둘 다 여기서 처리)
@Data
@Builder
public class TeamInviteResDto {

    private Long teamId;        // 초대 대상 팀 ID
    private String teamName;    // 초대 대상 팀 이름

    private String inviterName; // 초대한 사람 이름
    private String inviterEmail;// 초대한 사람 이메일

    // 초대 URL (초대 생성 시에만 세팅해줘도 됨)
    private String inviteUrl;

    private String status;      // 초대 상태 (PENDING, ACCEPTED, ...)
    private boolean expired;    // 만료 여부

    private LocalDateTime createdAt; // 초대 생성 시각
    private LocalDateTime expiresAt; // 초대 만료 시각
}
