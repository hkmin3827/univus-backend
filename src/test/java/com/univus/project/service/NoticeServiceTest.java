package com.univus.project.service;

import com.univus.project.constant.ErrorCode;
import com.univus.project.constant.Role;
import com.univus.project.dto.notice.FileResDto;
import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.Notice;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
import com.univus.project.exception.CustomException;
import com.univus.project.repository.NoticeRepository;
import com.univus.project.repository.TeamMemberRepository;
import com.univus.project.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NoticeServiceTest {

    @Mock private NoticeRepository noticeRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private TeamMemberRepository teamMemberRepository;

    @InjectMocks private NoticeService noticeService;

    private User professor;
    private User student;
    private Team team;
    private Notice notice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        professor = new User();
        professor.setId(1L);
        professor.setName("교수");
        professor.setRole(Role.PROFESSOR);

        student = new User();
        student.setId(2L);
        student.setName("학생");
        student.setRole(Role.STUDENT);

        team = new Team();
        team.setId(100L);

        notice = new Notice();
        notice.setId(10L);
        notice.setTitle("공지 제목");
        notice.setContent("내용");
        notice.setCreateTime(LocalDateTime.now());
        notice.setTeam(team);
        notice.setUser(professor);
    }

    // -------------------------------------------------------------
    // 1) 공지 생성 테스트
    // -------------------------------------------------------------
    @Test
    void createNotice_success_professor() {
        NoticeWriteDto dto = new NoticeWriteDto();
        dto.setTitle("새 공지");
        dto.setContent("내용입니다");

        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, professor.getId())).thenReturn(true);
        when(noticeRepository.save(any())).thenAnswer(inv -> {
            Notice n = inv.getArgument(0);
            n.setId(200L);
            return n;
        });

        NoticeResDto result = noticeService.createNotice(100L, dto, professor);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200L);
    }

    @Test
    void createNotice_fail_notProfessor() {
        NoticeWriteDto dto = new NoticeWriteDto();
        dto.setTitle("공지");

        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, student.getId())).thenReturn(true);

        NoticeResDto result = noticeService.createNotice(100L, dto, student);

        assertThat(result).isNull();
    }

    @Test
    void createNotice_fail_emptyTitle() {
        NoticeWriteDto dto = new NoticeWriteDto();
        dto.setTitle("  "); // invalid title

        NoticeResDto result = noticeService.createNotice(100L, dto, professor);

        assertThat(result).isNull();
    }

    @Test
    void createNotice_fail_notMember() {
        NoticeWriteDto dto = new NoticeWriteDto();
        dto.setTitle("공지");

        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.existsByTeamIdAndUserId(anyLong(), anyLong())).thenReturn(false);

        NoticeResDto result = noticeService.createNotice(100L, dto, professor);

        assertThat(result).isNull();
    }

    // -------------------------------------------------------------
    // 2) 팀별 공지 목록 조회
    // -------------------------------------------------------------
    @Test
    void getNoticesByTeam_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> mockPage = new PageImpl<>(List.of(notice));

        when(teamMemberRepository.existsByTeamIdAndUserId(100L, professor.getId())).thenReturn(true);
        when(noticeRepository.findByTeam_IdOrderByCreateTimeDesc(100L, pageable))
                .thenReturn(mockPage);

        Page<NoticeResDto> result = noticeService.getNoticesByTeam(100L, pageable, professor.getId());

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
    }

    @Test
    void getNoticesByTeam_fail_notMember() {
        Pageable pageable = PageRequest.of(0, 10);

        when(teamMemberRepository.existsByTeamIdAndUserId(100L, student.getId())).thenReturn(false);

        Page<NoticeResDto> result = noticeService.getNoticesByTeam(100L, pageable, student.getId());

        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    // -------------------------------------------------------------
    // 3) 공지 상세 조회
    // -------------------------------------------------------------
    @Test
    void getNoticeById_success() {
        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));
        when(teamMemberRepository.existsByTeamIdAndUserId(100L, professor.getId()))
                .thenReturn(true);

        NoticeResDto result = noticeService.getNoticeById(100L, 10L, professor.getId());

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void getNoticeById_fail_invalidTeamRelation() {
        Team otherTeam = new Team();
        otherTeam.setId(200L);
        notice.setTeam(otherTeam);

        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));

        assertThatThrownBy(() -> noticeService.getNoticeById(100L, 10L, professor.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_RELATION.getMessage());
    }

    @Test
    void getNoticeById_fail_notMember() {
        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));
        when(teamMemberRepository.existsByTeamIdAndUserId(anyLong(), anyLong()))
                .thenReturn(false);

        assertThatThrownBy(() -> noticeService.getNoticeById(100L, 10L, student.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.UNAUTHORIZED_MEMBER.getMessage());
    }

    // -------------------------------------------------------------
    // 4) 공지 수정
    // -------------------------------------------------------------
    @Test
    void modifyNotice_success() {
        NoticeModifyDto dto = new NoticeModifyDto();
        dto.setTitle("수정된 제목");
        dto.setContent("수정 내용");

        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));

        Boolean result = noticeService.modifyNotice(100L, 10L, dto, professor);

        assertThat(result).isTrue();
        assertThat(notice.getTitle()).isEqualTo("수정된 제목");
    }

    @Test
    void modifyNotice_fail_notMyNotice() {
        NoticeModifyDto dto = new NoticeModifyDto();
        dto.setTitle("변경");

        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));

        assertThat(notice.getUser().getId()).isEqualTo(1L);

        Boolean result = noticeService.modifyNotice(100L, 10L, dto, student);

        assertThat(result).isFalse();
    }

    // -------------------------------------------------------------
    // 5) 공지 삭제
    // -------------------------------------------------------------
    @Test
    void deleteNotice_success() {
        when(noticeRepository.findById(10L)).thenReturn(Optional.of(notice));

        Boolean result = noticeService.deleteNotice(100L, 10L, professor);

        assertThat(result).isTrue();
        verify(noticeRepository).delete(notice);
    }

    @Test
    void deleteNotice_fail_notOwner() {
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(notice));

        Boolean result = noticeService.deleteNotice(100L, 10L, student);

        assertThat(result).isFalse();
    }

    // -------------------------------------------------------------
    // 6) 전체 공지 조회
    // -------------------------------------------------------------
    @Test
    void getAllNotices_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notice> mockPage = new PageImpl<>(List.of(notice));

        when(noticeRepository.findAllByOrderByCreateTimeDesc(pageable))
                .thenReturn(mockPage);

        Page<NoticeResDto> result = noticeService.getAllNotices(pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllNotices_exception() {
        Pageable pageable = PageRequest.of(0, 10);

        when(noticeRepository.findAllByOrderByCreateTimeDesc(pageable))
                .thenThrow(new RuntimeException("DB 오류"));

        Page<NoticeResDto> result = noticeService.getAllNotices(pageable);

        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
