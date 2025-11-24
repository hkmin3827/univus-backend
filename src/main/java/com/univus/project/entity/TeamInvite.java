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
    private Long id;    // 초대 고유 ID (PK)

    // 어느 팀으로 초대한 것인지
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 초대받은 사람 (초대 수락할 때 설정됨 / 처음에는 null 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    // 초대한 사람 (보통 팀장)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    // 초대 토큰 (URL 안에 들어가는 값, 랜덤 문자열)
    @Column(nullable = false, unique = true, length = 64)
    private String inviteToken;

    // 초대 상태 (대기, 수락, 만료, 취소 등)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InviteStatus status = InviteStatus.PENDING;

    // 초대 만료 시간 (이 시간 지나면 사용 불가)
    private LocalDateTime expiresAt;

    // 초대가 만들어진 시간
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 초대를 수락한 시간
    private LocalDateTime acceptedAt;

    // 처음 저장될 때 createdAt / expiresAt 기본값 세팅
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.expiresAt == null) {
            // 기본 유효기간 3일
            this.expiresAt = this.createdAt.plusDays(3);
        }
    }

    // 현재 기준으로 초대가 만료되었는지 여부
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}
