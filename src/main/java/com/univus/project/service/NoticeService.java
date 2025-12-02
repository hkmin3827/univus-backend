package com.univus.project.service;

import com.univus.project.constant.Role;
import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.Notice;
import com.univus.project.entity.Team;
import com.univus.project.entity.User;
import com.univus.project.repository.NoticeRepository;
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

    // 1) 공지 생성 (교수 권한 체크 진행)
    public NoticeResDto createNotice(NoticeWriteDto dto, User user) {
        try {
            if (user.getRole() != Role.PROFESSOR) { throw new RuntimeException("공지 작성 권한이 없습니다!");}
            Notice notice = new Notice();
            notice.setTitle(dto.getTitle());
            notice.setContent(dto.getContent());
            notice.setUser(user);

            Team team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new RuntimeException("팀이 없습니다."));
            notice.setTeam(team);

            noticeRepository.save(notice);
            return new NoticeResDto(notice);
        } catch (Exception e) {
            log.error("공지 생성 실패: {}", e.getMessage());
            return null;
        }
    }

    // 2) 팀별 공지 목록 조회
    public Page<NoticeResDto> getNoticesByTeam(Long teamId, Pageable pageable) {
        try {
            return noticeRepository.findByTeam_IdOrderByCreateTimeDesc(teamId, pageable)
                    .map(NoticeResDto::new);
        } catch (Exception e) {
            log.error("팀별 공지 조회 실패: {}", e.getMessage());
            return Page.empty();
        }
    }

    public NoticeResDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        return new NoticeResDto(notice);
    }


    // 3) 공지 수정
    public Boolean modifyNotice(Long id, NoticeModifyDto dto, User user) {
        try {
            Notice notice = noticeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("공지가 없습니다!"));
            if (!notice.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }
            notice.setTitle(dto.getTitle());
            notice.setContent(dto.getContent());
            return true;
        } catch(Exception e) {
            log.error("공지 수정 실패: {}", e.getMessage());
            return false;
        }
    }

    // 4) 공지 삭제
    public Boolean deleteNotice(Long id, User user) {
        try {
            Notice notice = noticeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("공지가 없습니다!"));
            if (!notice.getUser().getId().equals(user.getId())) { throw new RuntimeException("삭제 권한이 없습니다!"); }
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
