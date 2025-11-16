package com.univus.project.repository;

import com.univus.project.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 이메일로 조회
    Optional<Notice> findByEmail(String email);
    // 작성자로 조회
    List<Notice> findByName(String name);
    // 제목의 특정 단어를 포함하는지 조회
    List<Notice> findByTitleContaining(String title);
    // 최신 공지 순 조회
    List<Notice> findAllByOrderByCreateTimeDesc();
}
