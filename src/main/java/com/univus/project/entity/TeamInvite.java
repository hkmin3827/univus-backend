package com.univus.project.entity;

import com.univus.project.constant.InviteStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 팀에서 초대한 것인지
    @ManyToOne(optional = false)
    private Team team;

    // 초대받는 사용자
    @ManyToOne(optional = false)
    private User invitee;

    // 초대한 사람(팀장 userId)
    private String inviter;

    @Enumerated(EnumType.STRING)
    private InviteStatus status = InviteStatus.PENDING;

    private LocalDateTime createAt = LocalDateTime.now();
}
