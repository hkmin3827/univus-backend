package com.univus.project.repository;

import com.univus.project.entity.Notice;
import com.univus.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 특정 User가 작성한 공지 조회
    List<Notice> findByUser(User user);

    // 제목에 특정 단어가 포함된 공지 조회
    List<Notice> findByTitleContaining(String title);

    // 최신순으로 공지 조회
    List<Notice> findAllByOrderByCreateTimeDesc();
}
