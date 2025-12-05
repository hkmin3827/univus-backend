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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

@ExtendWith(MockitoExtension.class)
class TeamInviteServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamInviteRepository teamInviteRepository;
    @Mock private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamInviteService teamInviteService;

    private Team team;
    private User leader;
    private User user;
    private TeamInvite invite;

    @BeforeEach
    void setUp() {

        leader = new User();
        leader.setId(1L);
        leader.setName("팀장");

        user = new User();
        user.setId(2L);
        user.setName("사용자");

        team = Team.builder()
                .id(100L)
                .teamName("테스트팀")
                .leader(leader)
                .build();

        invite = TeamInvite.builder()
                .team(team)
                .inviter(leader)
                .inviteToken("test-token")
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(3))
                .build();
    }

    @Test
    void 초대_URL_생성_성공() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamInviteRepository.save(any(TeamInvite.class))).thenReturn(invite);

        TeamInviteResDto result = teamInviteService.createInvite(100L, leader, "http://localhost:3000");

        assertEquals("테스트팀", result.getTeamName());
        verify(teamInviteRepository).save(any(TeamInvite.class));
    }

    @Test
    void 팀장이_아닌_사용자가_초대_URL_생성시_예외() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));

        assertThrows(AccessDeniedException.class,
                () -> teamInviteService.createInvite(100L, user, "http://localhost"));
    }

    @Test
    void 초대정보_조회_성공() {
        when(teamInviteRepository.findByInviteToken("test-token"))
                .thenReturn(Optional.of(invite));

        TeamInviteResDto result = teamInviteService.getInviteInfo("test-token", "http://localhost");

        assertEquals("테스트팀", result.getTeamName());
        assertEquals("팀장", result.getInviterName());
    }

    @Test
    void 초대_수락_성공() {
        when(teamInviteRepository.findByInviteToken("test-token")).thenReturn(Optional.of(invite));
        when(teamMemberRepository.existsByTeamAndUser(team, user)).thenReturn(false);

        teamInviteService.acceptInvite("test-token", user);

        verify(teamMemberRepository).save(any(TeamMember.class));
    }

    @Test
    void 초대_토큰_유효하지않음_예외() {
        when(teamInviteRepository.findByInviteToken("wrong-token")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> teamInviteService.acceptInvite("wrong-token", user));
    }

    @Test
    void 이미_가입된_팀이면_예외() {
        when(teamInviteRepository.findByInviteToken("test-token")).thenReturn(Optional.of(invite));
        when(teamMemberRepository.existsByTeamAndUser(team, user)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> teamInviteService.acceptInvite("test-token", user));
    }

    @Test
    void 초대링크_만료시_예외() {
        invite.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(teamInviteRepository.findByInviteToken("test-token")).thenReturn(Optional.of(invite));

        assertThrows(IllegalStateException.class,
                () -> teamInviteService.acceptInvite("test-token", user));
    }
}
