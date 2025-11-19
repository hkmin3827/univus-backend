package com.univus.project.controller;

import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import com.univus.project.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {
    private final UserRepository userRepository;

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/create")
    public ResponseEntity<Long> createComment(@RequestBody CommentReqDto dto) {
        User user = getLoggedInMember(); // 로그인한 회원
        Long commentId = commentService.createComment(dto, user);
        return ResponseEntity.ok(commentId);
    }

    // 특정 게시글 댓글 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResDto>> getComments(@PathVariable Long postId) {
        List<CommentResDto> comments = commentService.getCommnets(postId);
        return ResponseEntity.ok(comments);
    }

    private User getLoggedInMember() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
    }
}