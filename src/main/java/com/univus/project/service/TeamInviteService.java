package com.univus.project.service;

import com.univus.project.constant.InviteStatus;
import com.univus.project.dto.team.TeamInviteResDto;
import com.univus.project.entity.Team;
import com.univus.project.entity.TeamInvite;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import com.univus.project.repository.TeamInviteRepository;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// 팀 초대 관련 비즈니스 로직 (초대 URL, 초대 정보, 초대 수락)
@Service
@RequiredArgsConstructor
public class TeamInviteService {

    private final TeamRepository teamRepository;
    private final TeamInviteRepository teamInviteRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 팀장만 초대 URL 생성 가능
     * - 랜덤 토큰 생성 후 TeamInvite 저장
     * - 프론트에서 사용할 전체 URL(프론트 주소 + 토큰) 반환
     */
    @Transactional
    public TeamInviteResDto createInvite(Long teamId, User inviter, String frontendBaseUrl) {
        // 1) 팀 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        // 2) 팀장인지 확인 (팀장만 초대 가능)
        if (!isLeader(team, inviter)) {
            throw new AccessDeniedException("팀장만 초대 URL을 생성할 수 있습니다.");
        }

        // 3) 랜덤 초대 토큰 생성
        String token = UUID.randomUUID().toString();

        // 4) 초대 엔티티 생성 및 저장
        TeamInvite invite = TeamInvite.builder()
                .team(team)
                .inviter(inviter)
                .inviteToken(token)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(3)) // 유효기간 3일
                .build();

        TeamInvite saved = teamInviteRepository.save(invite);

        // 5) 프론트에서 진입할 실제 URL 구성
        String inviteUrl = frontendBaseUrl + "/teamentry/" + token;

        // 6) DTO 로 반환
        return toDto(saved, inviteUrl);
    }

    /**
     * 초대 토큰으로 초대 정보 조회
     * - 초대 페이지 진입 시 팀 이름, 초대한 사람, 만료 여부 등을 보여주기 위함
     */
    @Transactional(readOnly = true)
    public TeamInviteResDto getInviteInfo(String token, String frontendBaseUrl) {
        TeamInvite invite = teamInviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        String inviteUrl = frontendBaseUrl + "/teamentry/" + token;
        return toDto(invite, inviteUrl);
    }

    /**
     * 초대 수락 = 팀 가입
     * - 현재 로그인한 유저를 해당 팀 멤버로 추가
     * - 초대 상태를 ACCEPTED 로 변경
     */
    @Transactional
    public void acceptInvite(String token, User currentUser) {
        // 1) 초대 조회
        TeamInvite invite = teamInviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        // 2) 만료 여부 체크
        if (invite.isExpired()) {
            invite.setStatus(InviteStatus.EXPIRED);
            throw new IllegalStateException("초대 링크가 만료되었습니다.");
        }

        // 3) 이미 처리된 초대인지 체크
        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 초대입니다.");
        }

        Team team = invite.getTeam();

        // 4) 이미 팀 멤버인지 확인
        if (!teamMemberRepository.existsByTeamAndUser(team, currentUser)) {
            // 멤버가 아니라면 새로 팀 멤버 추가
            TeamMember member = TeamMember.builder()
                    .team(team)
                    .user(currentUser)
                    .build();
            teamMemberRepository.save(member);
        }

        // 5) 초대 정보 업데이트 (수락한 유저/시간/상태)
        invite.setInvitee(currentUser);
        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setAcceptedAt(LocalDateTime.now());
    }

    // 팀의 leader 와 현재 유저가 같은지 확인
    private boolean isLeader(Team team, User inviter) {
        return team.getLeader() != null
                && team.getLeader().getId().equals(inviter.getId());
    }

    // TeamInvite 엔티티 -> TeamInviteResDto 변환
    private TeamInviteResDto toDto(TeamInvite invite, String inviteUrl) {
        return TeamInviteResDto.builder()
                .teamId(invite.getTeam().getId())
                .teamName(invite.getTeam().getTeamName())
                .inviterName(invite.getInviter().getName())
                .inviterEmail(invite.getInviter().getEmail())
                .inviteUrl(inviteUrl)
                .status(invite.getStatus().name())
                .expired(invite.isExpired())
                .createdAt(invite.getCreatedAt())
                .expiresAt(invite.getExpiresAt())
                .build();
    }
}
