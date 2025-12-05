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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    // 1) 공지 생성 (교수 권한 체크 진행)
    public NoticeResDto createNotice(Long teamId, NoticeWriteDto dto, User user) {
        try {
            if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
                throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
            }

            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
            boolean isMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, user.getId());
            if (!isMember) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
            }
            if (user.getRole() != Role.PROFESSOR) { throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);}
            Notice notice = new Notice();
            notice.setTitle(dto.getTitle());
            notice.setContent(dto.getContent());
            notice.setUser(user);
            notice.setFileUrl(dto.getFileUrl());
            notice.setFileName(dto.getFileName());
            notice.setTeam(team);

            noticeRepository.save(notice);
            return new NoticeResDto(notice);
        } catch (Exception e) {
            log.error("공지 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    // 2) 팀별 공지 목록 조회
    public Page<NoticeResDto> getNoticesByTeam(Long teamId, Pageable pageable, Long userId) {
        try {
            boolean isMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
            if (!isMember) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
            }

            return noticeRepository.findByTeam_IdOrderByCreateTimeDesc(teamId, pageable)
                    .map(NoticeResDto::new);
        } catch (Exception e) {
            log.error("팀별 공지 조회 실패: {}", e.getMessage());
            return Page.empty();
        }
    }

    public NoticeResDto getNoticeById(Long teamId, Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
        Long noticeTeamId = notice.getTeam().getId();
        if (!noticeTeamId.equals(teamId)) {
            throw new CustomException(ErrorCode.INVALID_RELATION);
        }
        boolean isMember = teamMemberRepository.existsByTeamIdAndUserId(teamId, userId);
        if (!isMember) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
        }
        return new NoticeResDto(notice);
    }

    public FileResDto getFileInfo(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        if (notice.getFileUrl() == null) return null;

        return new FileResDto(notice.getFileUrl(), notice.getFileName());
    }


    // 3) 공지 수정
    public Boolean modifyNotice(Long teamId, Long noticeId, NoticeModifyDto dto, User user) {
        try {
            Notice notice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new RuntimeException("공지가 없습니다!"));
            if (!notice.getTeam().getId().equals(teamId)) {
                throw new CustomException(ErrorCode.INVALID_RELATION);
            }

            if (!notice.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER);
            }
            if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
                notice.setTitle(dto.getTitle());
            }
            if (dto.getContent() != null) {
                notice.setContent(dto.getContent());
            }
            notice.setFileUrl(dto.getFileUrl());
            notice.setFileName(dto.getFileName());
            return true;
        } catch(Exception e) {
            log.error("공지 수정 실패: {}", e.getMessage());
            return false;
        }
    }

    // 4) 공지 삭제
    public Boolean deleteNotice(Long teamId, Long noticeId, User user) {
        try {
            Notice notice = noticeRepository.findById(noticeId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
            if (!notice.getTeam().getId().equals(teamId)) {
                throw new CustomException(ErrorCode.INVALID_RELATION);
            }

            if (!notice.getUser().getId().equals(user.getId())) {  throw new CustomException(ErrorCode.UNAUTHORIZED_MEMBER); }
            noticeRepository.delete(notice);
            return true;
        } catch (Exception e) {
            log.error("공지 삭제 실패: {}", e.getMessage());
            return false;
        }
    }

    // 5) 최신순 공지 목록 조회
    public Page<NoticeResDto> getAllNotices(Pageable pageable) {
        try {
            return noticeRepository.findAllByOrderByCreateTimeDesc(pageable)
                    .map(NoticeResDto::new);
        } catch (Exception e) {
            log.error("공지 목록 조회 실패: {}", e.getMessage());
            return Page.empty();
        }
    }


}
