package com.univus.project.service;

import com.univus.project.constant.InviteStatus;
import com.univus.project.dto.team.*;
import com.univus.project.entity.*;
import com.univus.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamInviteRepository inviteRepository;
    private final TeamMemberRepository teamMemberRepository;

    // 팀 생성
    @Transactional
    public Boolean createTeam(TeamCreateReqDto dto) {

        Team team = Team.builder()
                .teamName(dto.getTeamName())
                .description(dto.getDescription())
                .leader(dto.getLeaderId())   // userId 저장
                .build();

        teamRepository.save(team);

        // 팀 생성자 팀 멤버로 등록
        User leader = userRepository.findByEmail(dto.getLeaderId())
                .orElseThrow(() -> new RuntimeException("Leader not found"));

        TeamMember leaderMember = TeamMember.builder()
                .team(team)
                .user(leader)
                .build();

        teamMemberRepository.save(leaderMember);

        return true;
    }

    // 팀 전체 조회
    public List<TeamResDto> findAll() {
        return teamRepository.findAll().stream()
                .map(TeamResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 개별 팀 조회
    public TeamResDto findTeam(String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) throw new RuntimeException("팀 존재하지 않음");

        return TeamResDto.fromEntity(team);
    }

    // 팀 수정
    @Transactional
    public Boolean modifyTeam(TeamModifyReqDto dto) {

        Team team = teamRepository.findByTeamName(dto.getTeamName());
        if (team == null) throw new RuntimeException("팀을 찾을 수 없습니다.");

        team.setDescription(dto.getDescription());

        teamRepository.save(team);
        return true;
    }

    // 팀 삭제
    @Transactional
    public Boolean deleteTeam(String teamName) {
        Team team = teamRepository.findByTeamName(teamName);
        if (team == null) throw new RuntimeException("팀을 찾을 수 없습니다.");

        // 팀 멤버 삭제
        teamMemberRepository.deleteByTeam(team);

        // 팀 삭제
        teamRepository.delete(team);

        return true;
    }

    // 팀 초대
    @Transactional
    public Boolean inviteMember(TeamInviteReqDto dto) {

        Team team = teamRepository.findByTeamName(dto.getTeamName());
        if (team == null) throw new RuntimeException("팀을 찾을 수 없습니다.");

        User invitee = userRepository.findByEmail(dto.getInviteEmail())
                .orElseThrow(() -> new RuntimeException("초대 대상 사용자가 존재하지 않습니다."));

        TeamInvite invite = TeamInvite.builder()
                .team(team)
                .invitee(invitee)
                .inviter(team.getLeader())   // 팀장 userId
                .status(InviteStatus.PENDING)
                .build();

        inviteRepository.save(invite);
        return true;
    }

    // 초대 수락
    @Transactional
    public Boolean acceptInvite(Long inviteId) {

        TeamInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("초대가 존재하지 않습니다."));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("이미 처리된 초대입니다.");
        }

        invite.setStatus(InviteStatus.ACCEPTED);

        TeamMember member = TeamMember.builder()
                .team(invite.getTeam())
                .user(invite.getInvitee())
                .build();

        teamMemberRepository.save(member);
        return true;
    }

    // 초대 거절
    @Transactional
    public Boolean declineInvite(Long inviteId) {

        TeamInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new RuntimeException("초대가 존재하지 않습니다."));

        invite.setStatus(InviteStatus.DECLINED);

        return true;
    }
}
