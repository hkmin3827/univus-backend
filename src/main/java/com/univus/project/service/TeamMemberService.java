package com.univus.project.service;

import com.univus.project.dto.team.TeamMemberResDto;
import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamMemberService {

    public final TeamMemberRepository teamMemberRepository;
    public final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public List<TeamMemberResDto> getTeamMembers(Long teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamIdWithUser(teamId);

        return members.stream()
                .map(TeamMemberResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean leaveTeam(Long teamId, User user) {

        try {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

            if (team.getLeader().getId().equals(user.getId())) {
                throw new IllegalStateException("팀장은 팀을 탈퇴할 수 없습니다.");
            }
            TeamMember teamMember = teamMemberRepository
                    .findByTeamIdAndUserId(teamId, user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("팀 멤버를 찾을 수 없습니다."));

            teamMemberRepository.delete(teamMember);

            if (!teamMemberRepository.existsByTeamId(teamId)) {
                teamRepository.deleteById(teamId);
            }


            return true;
        } catch(Exception e){
            log.error("팀 탈퇴 실패 : {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean kickMember(Long teamId, Long leaderId, Long targetUserId) {
        try {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found"));

            if (!team.getLeader().getId().equals(leaderId)) {
                throw new IllegalArgumentException("권한이 없습니다. 팀장만 강제 탈퇴가 가능합니다.");
            }

            TeamMember teamMember = teamMemberRepository
                    .findByTeamIdAndUserId(teamId, targetUserId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 멤버를 찾을 수 없습니다."));

            teamMemberRepository.delete(teamMember);

            // 삭제 후 팀원 0명이면 팀 자동 삭제
            if (!teamMemberRepository.existsByTeamId(teamId)) {
                teamRepository.deleteById(teamId);
            }
        return true;
        } catch (Exception e){
            log.error("멤버 강제 탈퇴 실패 : {}", e.getMessage());
            return false;
        }
    }
}
