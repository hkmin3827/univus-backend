package com.univus.project.controller;

import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 1) 공지 생성
    @PostMapping("/create")
    public ResponseEntity<NoticeResDto> createNotice(@RequestBody NoticeWriteDto dto) {
        NoticeResDto notice = noticeService.createNotice(dto);
        return ResponseEntity.ok(notice);
    }

    // 2) 공지 조회
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResDto> getNotice(@PathVariable Long id) {
        NoticeResDto notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    // 3) 공지 수정
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyNotice(@PathVariable Long id, @RequestBody NoticeModifyDto dto) {
        Boolean result = noticeService.modifyNotice(id, dto);
        return ResponseEntity.ok(result);
    }

    // 4) 공지 삭제
    @DeleteMapping("/delete/{id}")
    public  ResponseEntity<Boolean> deleteNotice(@PathVariable Long id) {
        Boolean result = noticeService.deleteNotice(id);
        return ResponseEntity.ok(result);
    }

    // 5) 최신 공지 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<NoticeResDto>> getAllNotices() {
        List<NoticeResDto> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);

    }

}
