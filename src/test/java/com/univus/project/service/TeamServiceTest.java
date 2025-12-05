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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @InjectMocks TeamService teamService;
    @Mock
    TeamRepository teamRepository;

    @Mock
    TeamMemberRepository teamMemberRepository;

    private User leader;
    private Team team;

    @BeforeEach
    void setUp() {
        leader = new User();
        leader.setId(1L);
        leader.setName("홍길동");
        leader.setEmail("test@test.com");

        team = Team.builder()
                .id(100L)
                .teamName("테스트팀이름")
                .description("테스트설명")
                .leader(leader)
                .build();
    }

    @Test
    void 팀_생성_성공() {
        TeamCreateReqDto dto = new TeamCreateReqDto();
        dto.setTeamName("새로운 팀네임");
        dto.setDescription("새로운 팀설명");

        when(teamRepository.existsByTeamName(dto.getTeamName())).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamResDto result = teamService.createTeam(dto, leader);
        verify(teamRepository).save(any(Team.class));
        verify(teamMemberRepository).save(any(TeamMember.class));
        assertEquals("테스트팀이름", result.getTeamName());

    }


    @Test
    void 팀_생성_실패_팀이름없음(){
        TeamCreateReqDto dto = new TeamCreateReqDto();
        dto.setTeamName("");

        CustomException ex = assertThrows(CustomException.class,
                () -> teamService.createTeam(dto, leader));

        assertEquals(ErrorCode.INVALID_INPUT_VALUE, ex.getErrorCode());
    }

    @Test
    void 팀_생성_실패_팀이름중복(){
        TeamCreateReqDto dto = new TeamCreateReqDto();
        dto.setTeamName("테스트팀이름");
        when(teamRepository.existsByTeamName("테스트팀이름")).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class,
                () -> teamService.createTeam(dto, leader));

        assertEquals(ErrorCode.DUPLICATE_TEAM_NAME, ex.getErrorCode());
    }

    @Test
    void 팀_조회_성공() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.countByTeam(team)).thenReturn(5L);
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(true);

        TeamResDto result = teamService.getTeam(100L, 1L);
        assertEquals("테스트팀이름", result.getTeamName());
        assertEquals(5L, result.getMemberCount());
    }
    @Test
    void 팀_조회_실패_존재하지않음() {
        when(teamRepository.findById(100L)).thenReturn(Optional.empty());
        CustomException ex = assertThrows(CustomException.class,
                () -> teamService.getTeam(100L, 1L));

        assertEquals(ErrorCode.TEAM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 팀_삭제_성공() {
        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));

        teamService.deleteTeam(100L, leader);

        verify(teamRepository).delete(team);
    }

    @Test
    void 팀_삭제_실패_권한없음() {
        User another = new User();
        another.setId(999L);

        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));

        CustomException ex = assertThrows(CustomException.class,
                () -> teamService.deleteTeam(100L, another));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }

}
