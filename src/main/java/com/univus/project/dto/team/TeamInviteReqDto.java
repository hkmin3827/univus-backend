package com.univus.project.dto.team;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamInviteReqDto {
    private String teamName;    // 어느 팀에 초대하는지
    private String inviteEmail; // 초대 받는 useId or email 확인하기
}