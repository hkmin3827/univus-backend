package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.entity.Notice;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.NoticeRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks NoticeService noticeService;

    @Mock NoticeRepository noticeRepository;
    @Mock TeamRepository teamRepository;
    @Mock TeamMemberRepository teamMemberRepository;

    private User user;
    private Team team;
    private Notice notice;

    @BeforeEach
    void setup() {
        user = new User(); user.setId(1L);
        team = Team.builder().id(100L).teamName("팀이름").leader(user).build();
        notice = new Notice();
        notice.setTitle("공지제목");
        notice.setContent("내용");
        notice.setTeam(team);
        notice.setUser(user);
    }

    @Test
    void 공지_조회_성공() {
        when(noticeRepository.findById(50L)).thenReturn(Optional.of(notice));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(true);

        var result = noticeService.getNoticeById(100L, 50L, 1L);
        assertEquals("공지제목", result.getTitle());
    }

    @Test
    void 공지_조회_실패_존재하지않음() {
        when(noticeRepository.findById(50L)).thenReturn(Optional.empty());

        CustomException ex = assertThrows(CustomException.class,
                () -> noticeService.getNoticeById(100L, 50L, 1L));

        assertEquals(ErrorCode.NOTICE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 공지_조회_실패_권한없음() {
        when(noticeRepository.findById(50L)).thenReturn(Optional.of(notice));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, 1L)).thenReturn(false);

        CustomException ex = assertThrows(CustomException.class,
                () -> noticeService.getNoticeById(100L, 50L, 1L));

        assertEquals(ErrorCode.UNAUTHORIZED_MEMBER, ex.getErrorCode());
    }
}
