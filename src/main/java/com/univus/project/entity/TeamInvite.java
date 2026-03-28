package com.univus.project.entity;

import com.univus.project.constant.InviteStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 초대받은 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    // 초대한 사람
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @Column(nullable = false, unique = true, length = 64)
    private String inviteToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InviteStatus status = InviteStatus.PENDING;

    // 초대 만료 시간
    private LocalDateTime expiresAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acceptedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusDays(3);
        }
    }

    // 현재 기준으로 초대가 만료되었는지 여부
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
