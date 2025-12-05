package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.notice.FileResDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.User;
import com.univus.project.repository.TeamRepository;
import com.univus.project.service.NoticeService;
import com.univus.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;

    // 1) 공지 생성 - 교수 권한 확인
    @PostMapping("/create")
    public ResponseEntity<NoticeResDto> createNotice(
            @RequestBody NoticeWriteDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        NoticeResDto notice = noticeService.createNotice(dto, user);
        return ResponseEntity.ok(notice);
    }

    // 2) 공지 조회 - 모든 유저 접근 가능
    @GetMapping("/list/{teamId}")
    public ResponseEntity<Page<NoticeResDto>> getNoticesByTeam(
            @PathVariable Long teamId,
            Pageable pageable) {

        Page<NoticeResDto> notices = noticeService.getNoticesByTeam(teamId, pageable);
        return ResponseEntity.ok(notices);
    }

    // 공지 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResDto> getNotice(@PathVariable Long id) {
        NoticeResDto notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {

        // NoticeResDto 에 저장된 fileUrl, fileName 등을 service에서 가져옴
        FileResDto fileInfo = noticeService.getFileInfo(id);

        if (fileInfo == null || fileInfo.getFileUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(fileInfo.getFileUrl());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getFileName() + "\"")
                .body(resource);
    }

    // 3) 공지 수정 - 작성자 본인만 가능
    @PutMapping("/modify/{id}")
    public ResponseEntity<Boolean> modifyNotice(
            @PathVariable Long id,
            @RequestBody NoticeModifyDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Boolean result = noticeService.modifyNotice(id, dto, user);
        return ResponseEntity.ok(result);
    }

    // 4) 공지 삭제 - 작성자 본인만 가능
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Boolean result = noticeService.deleteNotice(id, user);
        return ResponseEntity.ok(result);
    }

    // 5) 최신 공지 목록 조회 - 페이지네이션 적용
    @GetMapping("/list")
    public ResponseEntity<Page<NoticeResDto>> getAllNotices(Pageable pageable) {
        Page<NoticeResDto> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(notices);
    }
}
