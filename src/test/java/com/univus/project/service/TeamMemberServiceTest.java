package com.univus.project.service;


import com.univus.project.entity.Team;
import com.univus.project.entity.TeamMember;
import com.univus.project.entity.User;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private User leader;
    private User member;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        leader = new User();
        leader.setId(1L);
        leader.setName("팀장");

        member = new User();
        member.setId(2L);
        member.setName("팀원");

        team = Team.builder()
                .id(100L)
                .teamName("유니버스")
                .leader(leader)
                .build();

        teamMember = TeamMember.builder()
                .id(10L)
                .team(team)
                .user(member)
                .build();
    }

    @Test
    void 팀_탈퇴_성공() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(100L, 2L)).thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.existsByTeamId(100L)).thenReturn(true);

        boolean result = teamMemberService.leaveTeam(100L, member);

        assertTrue(result);
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void 팀장_탈퇴_실패() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));

        boolean result = teamMemberService.leaveTeam(100L, leader);

        assertFalse(result);
        verify(teamMemberRepository, never()).delete(any());
    }

    @Test
    void 팀_멤버_0명_되면_팀자동삭제() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(100L, 2L)).thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.existsByTeamId(100L)).thenReturn(false);

        boolean result = teamMemberService.leaveTeam(100L, member);

        assertTrue(result);
        verify(teamRepository).deleteById(100L);
    }

    @Test
    void 멤버_강제탈퇴_성공() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.findByTeamIdAndUserId(100L, 2L)).thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.existsByTeamId(100L)).thenReturn(true);

        boolean result = teamMemberService.kickMember(100L, 1L, 2L);

        assertTrue(result);
        verify(teamMemberRepository).delete(teamMember);
    }

    @Test
    void 강제탈퇴_실패_권한없음() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));

        boolean result = teamMemberService.kickMember(100L, 999L, 2L);

        assertFalse(result);
        verify(teamMemberRepository, never()).delete(any());
    }
}