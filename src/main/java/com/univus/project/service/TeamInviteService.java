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
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        if (!isLeader(team, inviter)) {
                throw new AccessDeniedException("팀장만 초대 URL을 생성할 수 있습니다.");
        }

        String token = UUID.randomUUID().toString();

        TeamInvite invite = TeamInvite.builder()
                .team(team)
                .inviter(inviter)
                .inviteToken(token)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(3))
                .build();

        TeamInvite saved = teamInviteRepository.save(invite);

        String inviteUrl = frontendBaseUrl + "/teamentry/" + token;

        return toDto(saved, inviteUrl);
    }

    @Transactional(readOnly = true)
    public TeamInviteResDto getInviteInfo(String token, String frontendBaseUrl) {
        TeamInvite invite = teamInviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        String inviteUrl = frontendBaseUrl + "/teamentry/" + token;
        return toDto(invite, inviteUrl);
    }

    @Transactional
    public void acceptInvite(String token, User currentUser) {
        TeamInvite invite = teamInviteRepository.findByInviteToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 링크입니다."));

        // 링크 만료 확인
        if (invite.isExpired()) {
            throw new IllegalStateException("초대 링크가 만료되었습니다.");
        }

        Team team = invite.getTeam();

        if (teamMemberRepository.existsByTeamAndUser(team, currentUser)) {
            throw new IllegalStateException("이미 가입된 팀입니다.");
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(currentUser)
                .build();
        teamMemberRepository.save(member);

        // invite.setStatus(ACCEPTED); 삭제 → 링크는 계속 PENDING 상태 유지
    }


    private boolean isLeader(Team team, User inviter) {
        return team.getLeader() != null
                && team.getLeader().getId().equals(inviter.getId());
    }

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
