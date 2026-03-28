package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.dto.team.TeamCreateReqDto;
import com.univus.project.dto.team.TeamResDto;
import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TeamResDto createTeam(TeamCreateReqDto dto, User leader) {
        if (dto.getTeamName() == null || dto.getTeamName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Team team = Team.builder()
                .teamName(dto.getTeamName())
                .description(dto.getDescription())
                .leader(leader)
                .build();

        Team saved = teamRepository.save(team);

        TeamMember member = TeamMember.builder()
                .team(saved)
                .user(leader)
                .build();
        teamMemberRepository.save(member);

        return toDto(saved);
    }
    @Transactional
    public void deleteTeam(Long teamId, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->  new CustomException(ErrorCode.TEAM_NOT_FOUND));

        if (!team.getLeader().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }

        teamMemberRepository.deleteByTeamId(teamId);
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
