package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.notice.FileResDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.User;
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
@RequestMapping("/team/{teamId}/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<NoticeResDto> createNotice(
            @PathVariable Long teamId,
            @RequestBody NoticeWriteDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        NoticeResDto notice = noticeService.createNotice(teamId, dto, user);
        return ResponseEntity.ok(notice);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<NoticeResDto>> getNoticesByTeam(
            @PathVariable Long teamId,
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<NoticeResDto> notices = noticeService.getNoticesByTeam(teamId, pageable, userDetails.getUser().getId());
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResDto> getNotice(@PathVariable Long teamId,
                                                  @PathVariable Long noticeId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        NoticeResDto notice = noticeService.getNoticeById(teamId, noticeId, userDetails.getUser().getId());
        return ResponseEntity.ok(notice);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {

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

    @PutMapping("/modify/{noticeId}")
    public ResponseEntity<Boolean> modifyNotice(
            @PathVariable Long teamId,
            @PathVariable Long noticeId,
            @RequestBody NoticeModifyDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Boolean result = noticeService.modifyNotice(teamId, noticeId, dto, user);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{noticeId}")
    public ResponseEntity<Boolean> deleteNotice(
            @PathVariable Long teamId,
            @PathVariable Long noticeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        Boolean result = noticeService.deleteNotice(teamId, noticeId, user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recentlist")
    public ResponseEntity<Page<NoticeResDto>> getAllNotices(Pageable pageable) {
        Page<NoticeResDto> notices = noticeService.getAllNotices(pageable);
        return ResponseEntity.ok(notices);
    }
}

