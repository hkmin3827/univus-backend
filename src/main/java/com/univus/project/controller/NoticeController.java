package com.univus.project.controller;

import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.User;
import com.univus.project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 1) 공지 생성 - 교수 권한 확인
    @PostMapping("/create")
    public ResponseEntity<NoticeResDto> createNotice(
            @RequestBody NoticeWriteDto dto,
            @AuthenticationPrincipal User user) {

        NoticeResDto notice = noticeService.createNotice(dto, user);
        return ResponseEntity.ok(notice);
    }

    // 2) 공지 조회 - 모든 유저 접근 가능
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResDto> getNotice(@PathVariable Long id) {
        NoticeResDto notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    // 3) 공지 수정 - 작성자 본인만 가능
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyNotice(
            @PathVariable Long id,
            @RequestBody NoticeModifyDto dto,
            @AuthenticationPrincipal User user) {

        Boolean result = noticeService.modifyNotice(id, dto, user);
        return ResponseEntity.ok(result);
    }

    // 4) 공지 삭제 - 작성자 본인만 가능
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Boolean result = noticeService.deleteNotice(id, user);
        return ResponseEntity.ok(result);
    }

    // 5) 최신 공지 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<NoticeResDto>> getAllNotices() {
        List<NoticeResDto> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }
}
