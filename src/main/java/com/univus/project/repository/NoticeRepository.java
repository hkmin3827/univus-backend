package com.univus.project.repository;

import com.univus.project.entity.Notice;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 특정 User가 작성한 공지 조회
    List<Notice> findByUser(User user);

    // 팀별 공지 조회
    Page<Notice> findByTeam_IdOrderByCreateTimeDesc(Long teamId, Pageable pageable);

    // 제목에 특정 단어가 포함된 공지 조회
    List<Notice> findByTitleContaining(String title);
    // 제목이나 내용에 특정 단어가 포함된 공지 조회
    List<Notice> findByTitleContainingOrContentContaining(String title, String content);

    // 최신순으로 공지 조회 -> 페이지네이션 진행
    Page<Notice> findAllByOrderByCreateTimeDesc(Pageable pageable);
}
