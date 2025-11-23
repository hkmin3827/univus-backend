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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private InviteStatus status = InviteStatus.PENDING;

<<<<<<< HEAD
    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
=======
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
>>>>>>> c9561be053853b8bec99394a48d3065470568288
}
