package com.univus.project.dto.team;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TeamInviteResDto {
    private Long inviteId;
    private String teamName;
    private String inviter;
    private String status;
    private String inviteeEmail;
}