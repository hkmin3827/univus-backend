package com.univus.project.dto.team;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TeamInviteResDto {

    private Long teamId;
    private String teamName;

    private String inviterName;
    private String inviterEmail;

    private String inviteUrl;

    private String status;
    private boolean expired;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
