package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.dto.team.TeamCreateReqDto;
import com.univus.project.dto.team.TeamResDto;
import com.univus.project.entity.Post;
import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.PostRepository;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 팀 생성 / 조회 비즈니스 로직
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 팀 생성
     * - 현재 로그인한 유저를 팀장으로 설정
     * - 동시에 팀장도 TeamMember 에 자동 추가
     */
    @Transactional
    public TeamResDto createTeam(TeamCreateReqDto dto, User leader) {
        if (dto.getTeamName() == null || dto.getTeamName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // 1) Team 엔티티 생성
        Team team = Team.builder()
                .teamName(dto.getTeamName())
                .description(dto.getDescription())
                .leader(leader)
                .build();

        // 2) DB 저장
        Team saved = teamRepository.save(team);

        // 3) 팀장을 팀 멤버로 추가
        TeamMember member = TeamMember.builder()
                .team(saved)
                .user(leader)
                .build();
        teamMemberRepository.save(member);

        // 4) DTO 로 변환해서 반환
        return toDto(saved);
    }
    @Transactional
    public void deleteTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->  new CustomException(ErrorCode.TEAM_NOT_FOUND));

        if (!team.getLeader().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        teamMemberRepository.deleteByTeamId(teamId); // 👈 먼저 삭제
        teamRepository.delete(team);
    }

    @Transactional
    public Long updateTeam(Long teamId,TeamCreateReqDto dto, User user) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->new CustomException(ErrorCode.TEAM_NOT_FOUND));
        if (!team.getLeader().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
        team.setTeamName(dto.getTeamName());
        team.setDescription(dto.getDescription());

        if (dto.getTeamName() != null && !dto.getTeamName().isBlank()) {
            team.setTeamName(dto.getTeamName());
        }
        if (dto.getDescription() != null) {
            team.setDescription(dto.getDescription());
        }

        return team.getId();
    }
    /**
     * 팀 상세 조회
     */
    @Transactional(readOnly = true)
    public TeamResDto getTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
        boolean isMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
        if (!isMember) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        return toDto(team);
    }

    // Team 엔티티 -> TeamResDto 변환
    private TeamResDto toDto(Team team) {
        long memberCount = teamMemberRepository.countByTeam(team);

        return TeamResDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .leaderId(team.getLeader().getId())
                .leaderName(team.getLeader().getName())
                .leaderEmail(team.getLeader().getEmail())
                .memberCount(memberCount)
                .build();
    }
    @Transactional(readOnly = true)
    public List<TeamResDto> getTeamsByUser(User user) {
        return teamMemberRepository.findByUser(user).stream()
                .map(tm -> TeamResDto.fromEntity(tm.getTeam()))
                .collect(Collectors.toList());
    }
}
