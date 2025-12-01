package com.univus.project.service;

import com.univus.project.dto.team.TeamMemberResDto;
import com.univus.project.entity.TeamMember;
import com.univus.project.repository.TeamMemberRepository;
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
    @Transactional(readOnly = true)
    public List<TeamMemberResDto> getTeamMembers(Long teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamIdWithUser(teamId);

        return members.stream()
                .map(TeamMemberResDto::new)
                .collect(Collectors.toList());
    }
}
