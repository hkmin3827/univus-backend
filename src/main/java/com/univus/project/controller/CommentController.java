package com.univus.project.controller;
import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.PageResponse;
import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.User;
import com.univus.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/create")
    public ResponseEntity<Long> createComment(
            @RequestBody CommentReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long commentId = commentService.createComment(dto, user);
        return ResponseEntity.ok(commentId);
    }

    // 댓글 목록 + 검색 + 페이징
    @GetMapping("/list")
    public ResponseEntity<PageResponse<CommentResDto>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String keyword
    ) {
        Page<CommentResDto> comments = commentService.getComments(postId, page, size, keyword);
        return ResponseEntity.ok(PageResponse.from(comments));
    }

    // 댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Long> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long id = commentService.updateComment(commentId, dto, user);
        return ResponseEntity.ok(id);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok().build();
    }

    // 전체 게시글 댓글 검색 (topbar 검색용)
    @GetMapping("/search")
    public Page<CommentResDto> searchComments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        return commentService.searchAllComments(keyword, page, size);
    }
    @GetMapping("/my/{teamId}")
    public ResponseEntity<List<CommentResDto>> getMyComments(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<CommentResDto> comments = commentService.getMyComments(teamId, userId);
        return ResponseEntity.ok(comments);
    }
}